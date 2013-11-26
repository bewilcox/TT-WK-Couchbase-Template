package org.telosys.template.couchbase.nosql.tests.db;

import java.util.Collection;
import java.util.Properties;

public interface DatabaseConnectionProvider {
	
	public String getDocument(String docId) throws Exception;
	
	void saveOrUpdateDocument(String docId, String document)  throws Exception;
	
	void deleteDocument(String docId);

	void closeConnection();

	void initConnection() throws Exception;

	long increment(String keyName);

	Collection<String> getAllDocByType(String startKeyIdDoc);
	
	Collection<String> getAllIDByType(String startKeyIdDoc);

	Collection<String> executeQuery(String queryName,Properties parameters);

}
