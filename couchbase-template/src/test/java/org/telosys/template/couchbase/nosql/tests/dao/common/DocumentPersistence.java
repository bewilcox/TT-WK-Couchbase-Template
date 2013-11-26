package org.telosys.template.couchbase.nosql.tests.dao.common;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.telosys.template.couchbase.nosql.tests.db.DatabaseConnectionProvider;

public abstract class DocumentPersistence<T> {

	private final DatabaseConnectionProvider connectionProvider;
	private final ObjectMapper mapper;
	
	public DocumentPersistence(DatabaseConnectionProvider provider) {
		super();
		this.mapper = new ObjectMapper();
		mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		this.connectionProvider = provider;
	}

	public Collection<T> findAll() {
		Collection<T> entities = new ArrayList<>();
		
		try {
			Collection<String> resultsDoc = this.connectionProvider.getAllDocByType(this.getGenericClass().getSimpleName());
			for (String doc : resultsDoc) {
				if(doc != null) {
					entities.add(this.mapper.readValue(doc, this.getGenericClass()));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return entities;
	}


	public void removeAll() {
		Collection<String> ids = this.connectionProvider.getAllIDByType(this.getGenericClass().getSimpleName());
		for (String id : ids) {
			this.connectionProvider.deleteDocument(id);
		}
	}

	public T findById(String id)  {
		try {
			return this.mapper.readValue(
					this.connectionProvider.getDocument(id),
					this.getGenericClass());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	
	public void saveOrUpdate(T entity) {
		String json = this.convertAndAddTypeProperty(entity);
		try {
			this.connectionProvider.saveOrUpdateDocument(this.getDocumentID(entity),json);
		} catch (Exception e) {
			throw new RuntimeException("Error while saving entity of type " 
											+ entity.getClass().getSimpleName() 
											+ " :" +e.getMessage());
		}
	}
	

	public void delete(T entity) throws Exception {
		this.connectionProvider.deleteDocument(this.getDocumentID(entity));
	}
	
	public Collection<T> findByQueryName(String queryName) {
		return this.findByQueryName(queryName, null);
	}
	 
	public Collection<T> findByQueryName(String queryName, Properties parameters) {
		Collection<T> entities = new ArrayList<>();
		try {
			
			Collection<String> docs =  this.connectionProvider.executeQuery(queryName, parameters);
			for (String doc : docs) {
				entities.add(this.mapper.readValue(doc, this.getGenericClass()));
			}
		} catch (Exception e) {
			throw new RuntimeException("Error while executing query " 
					+ queryName 
					+ " :" +e.getMessage());
		}
		return entities;
	}
	
	public abstract String getDocumentID(T entity);
	
	@SuppressWarnings("unchecked")
	protected Class<T> getGenericClass() {
		ParameterizedType superClass = (ParameterizedType)getClass().getGenericSuperclass();
		return (Class<T>)superClass.getActualTypeArguments()[0];
	}

	private String convertAndAddTypeProperty(T entity) {
		try {
			ObjectNode jsonNode = (ObjectNode)this.mapper.readTree(this.mapper.writeValueAsString(entity));
			jsonNode.put("__type", getGenericClass().getCanonicalName());
			return jsonNode.toString();
		} catch (Exception e) {
			throw new RuntimeException("Error while adding type property to the json value for " 
					+ entity.getClass().getSimpleName() 
					+ " :" +e.getMessage());
		}
	}

}
