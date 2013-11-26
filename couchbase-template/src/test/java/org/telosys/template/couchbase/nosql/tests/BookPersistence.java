package org.telosys.template.couchbase.nosql.tests;

import java.util.Collection;
import java.util.Properties;

import org.telosys.template.couchbase.nosql.bean.Book;
import org.telosys.template.couchbase.nosql.tests.dao.common.DocumentPersistence;
import org.telosys.template.couchbase.nosql.tests.dao.common.QueryParameters;
import org.telosys.template.couchbase.nosql.tests.db.DatabaseConnectionProvider;

public class BookPersistence extends DocumentPersistence<Book> {

	// VIEW NAME FOR QUERY LINKS
	public static final String BY_AUTHOR_QUERY_NAME = "books_by_author";
	
	public BookPersistence(DatabaseConnectionProvider provider) {
		super(provider);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getDocumentID(Book entity) {
		return entity.getClass().getSimpleName() + ":" + entity.getId();
	}

	/**
	 * 
	 * @param authorId
	 * @return
	 */
	public Collection<Book> getBooksByAuthor(String authorId) {
		Properties properties = new Properties();
		properties.put(QueryParameters.CRITERION, authorId);
		properties.put(QueryParameters.INCLUDE_DOCS, true);
		return this.findByQueryName(BY_AUTHOR_QUERY_NAME, properties);
	}

}
