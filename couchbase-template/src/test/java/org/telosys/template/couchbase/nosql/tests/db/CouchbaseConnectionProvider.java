package org.telosys.template.couchbase.nosql.tests.db;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import net.spy.memcached.PersistTo;

import org.telosys.template.couchbase.nosql.tests.dao.common.QueryParameters;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.protocol.views.Query;
import com.couchbase.client.protocol.views.View;
import com.couchbase.client.protocol.views.ViewResponse;
import com.couchbase.client.protocol.views.ViewRow;

/**
 * ConnectionProvider for Couchbase.
 * @author bewilcox
 *
 */
public class CouchbaseConnectionProvider implements DatabaseConnectionProvider{
	
	private CouchbaseClient client;
	
    private final static String COUCHBASE_URI = "couchbase.uris";
    private final static String COUCHBASE_BUCKET = "couchbase.bucket";
    private final static String COUCHBASE_PASSWORD = "couchbase.password";
    private final static String WAIT_PERSIST= "couchbase.waitPersist";
        
    private final static String DESIGN_NAME_DOMAIN = "domain";
    
    private final static String UNICODE_END = "\\uefff";
    
    private List<URI> uris = new ArrayList<>();
    private String bucket = null;
    private String password = null;
    private PersistTo persistTo;
    	

	@Override
	public String getDocument(String docId) throws Exception {
		return (String)this.getClient().get(docId);
	}

	@Override
	public void saveOrUpdateDocument(String docId, String document)  {
		try {
			this.getClient().set(docId, document, persistTo).get();
		} catch (Exception e) {
			throw new RuntimeException("Enable to save the document " + docId, e);
		}
	}
	

	@Override
	public void deleteDocument(String docId) {
		this.getClient().delete(docId);
	}
	
	/**
	 * init connection to the cluster couchbase
	 * @throws Exception
	 */
	@Override
	public void initConnection() {
		if (this.client == null) {
			// params
			this.loadConfiguration();
			try {
				client = new CouchbaseClient(uris, bucket, password);
			} catch (IOException e) {
				throw new RuntimeException("Enabled to connect to the Couchbase cluster", e);
			}
		}
	}
	
	/**
	 * Close the connection.
	 */
	@Override
	public void closeConnection() {
		if (client != null) {
			client.shutdown();
			client = null;
		}
	}
	
	/**
	 * Increment a counter.
	 * @param keyName
	 * @return
	 */
	@Override
	public long increment(String keyName) {
		return this.getClient().incr(keyName, 1, 1);
	}
	
	
	@Override
	public Collection<String> getAllDocByType(String startkeydocid) {
		String viewName = "all_" + startkeydocid.toLowerCase() + "s";
		Properties properties = new Properties();
		properties.put(QueryParameters.RANGE_START, startkeydocid);
		properties.put(QueryParameters.RANGE_END, startkeydocid);
		properties.put(QueryParameters.INCLUDE_DOCS, true);
		return this.executeQuery(viewName, properties);
	}


	@Override
	public Collection<String> getAllIDByType(String startkeydocid) {
		String viewName = "all_" + startkeydocid.toLowerCase() + "s";
		Properties properties = new Properties();
		properties.put(QueryParameters.INCLUDE_DOCS, false);
		return this.executeQuery(viewName, properties);
	}
	
	
	/**
	 * Load configuration for the couchbase connection.
	 * @throws Exception
	 */
	private void loadConfiguration() {
        InputStream in = getClass().getClassLoader().getResourceAsStream("couchbase.properties");
        Properties p = new Properties();
        try {
			p.load(in);
			
	        // load the list of URI
	        String urisAsString = (String)p.get(COUCHBASE_URI);
	        List<String> x = Arrays.asList(  urisAsString.split(",") );
	        for ( String u : x ) {
	            URI uri = new URI(u);
	            uris.add( uri );
	        }

	        bucket = (String)p.get(COUCHBASE_BUCKET);
	        password = (String)p.get(COUCHBASE_PASSWORD);
	        
	        if (Boolean.valueOf((String)p.get(WAIT_PERSIST))) {
	        	persistTo = PersistTo.ONE;
	        } else {
	        	persistTo = PersistTo.ZERO;
	        }
	        
		} catch (Exception e) {
			throw new RuntimeException("Enable to load configuration of the couchbase server : " +e.getStackTrace());
		} 
	}

	/**
	 * Get the couchbase client.
	 * @return
	 */
	private CouchbaseClient getClient() {
		if (client == null) {
			this.initConnection();
		}
		return client;
	}

	@Override
	public Collection<String> executeQuery(String queryName, Properties parameters) {
		List<String> docs = new ArrayList<>();
		View view = this.getClient().getView(DESIGN_NAME_DOMAIN, queryName);
		Query query = new Query();
		boolean includeDocs = (boolean)parameters.get(QueryParameters.INCLUDE_DOCS);
		query.setIncludeDocs(includeDocs);
		if(parameters.containsKey(QueryParameters.CRITERION)) {
			query.setRangeStart(parameters.getProperty(QueryParameters.CRITERION));
			query.setRangeEnd(parameters.getProperty(QueryParameters.CRITERION)+UNICODE_END);
		} else if (parameters.containsKey(QueryParameters.RANGE_START) &&  parameters.containsKey(QueryParameters.RANGE_END)) {
			query.setRangeStart(parameters.getProperty(QueryParameters.RANGE_START));
			query.setRangeEnd(parameters.getProperty(QueryParameters.RANGE_END)+UNICODE_END);
		}
		ViewResponse viewResponse = this.getClient().query(view, query);
		for (ViewRow viewRow : viewResponse) {
			if(includeDocs) {
				docs.add((String)viewRow.getDocument());
			} else {
				docs.add((String)viewRow.getKey());
			}
		}
		return docs;
	}

}
