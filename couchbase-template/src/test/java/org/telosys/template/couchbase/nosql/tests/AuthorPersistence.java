package org.telosys.template.couchbase.nosql.tests;

import org.telosys.template.couchbase.nosql.bean.Author;
import org.telosys.template.couchbase.nosql.tests.dao.common.DocumentPersistence;
import org.telosys.template.couchbase.nosql.tests.db.DatabaseConnectionProvider;

public class AuthorPersistence extends DocumentPersistence<Author> {

	public AuthorPersistence(DatabaseConnectionProvider provider) {
		super(provider);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getDocumentID(Author entity) {
		return entity.getClass().getSimpleName() + ":" + entity.getId();
	}

}
