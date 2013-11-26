package org.telosys.template.couchbase.nosql.tests;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Collection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.telosys.template.couchbase.nosql.bean.Author;
import org.telosys.template.couchbase.nosql.bean.Book;
import org.telosys.template.couchbase.nosql.tests.db.CouchbaseConnectionProvider;

public class TemplateTest {

	private static CouchbaseConnectionProvider couchbaseConnectionProvider;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		couchbaseConnectionProvider = new CouchbaseConnectionProvider();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * 
	 */
	@Test
	public void testAuthorPersistence()  {
		AuthorPersistence authorPersistence = this.populateAuthors();

		int nbAuthors = authorPersistence.findAll().size();
		assertEquals("Wrong number of Authors",3,nbAuthors);
		
		authorPersistence.removeAll();

	}
	
	/**
	 * 
	 */
	@Test
	public void testBookPersistence()  {

		BookPersistence bookPersistence = this.populateBooks();		
		AuthorPersistence authorPersistence = this.populateAuthors();
		
		// Test links
		// All books from an author
		Collection<Book> books = bookPersistence.getBooksByAuthor("12");
		assertEquals("The test should found 2 books for the author 12", 2,books.size());
		bookPersistence.removeAll();
		authorPersistence.removeAll();
	}

	@Test
	public void testRemoveAll() throws Exception {
		BookPersistence bookPersistence = this.populateBooks();
		bookPersistence.removeAll();
		assertEquals("Should persist no book doc", 0, bookPersistence.findAll().size());
	}
	
	//################################################################################################################
	// PRIVATE METHODS
	//################################################################################################################
	private AuthorPersistence populateAuthors() {
		
		AuthorPersistence authorPersistence = new AuthorPersistence(couchbaseConnectionProvider);
		
		Author author1 = new Author();
		author1.setFirstName("John");
		author1.setLastName("Doe");
		author1.setId(12);
		
		Author author2 = new Author();
		author2.setFirstName("Jane");
		author2.setLastName("Doe");
		author2.setId(11);
		
		Author author3 = new Author();
		author3.setFirstName("Foo");
		author3.setLastName("Bar");
		author3.setId(8);
		
		authorPersistence.saveOrUpdate(author1);
		authorPersistence.saveOrUpdate(author2);
		authorPersistence.saveOrUpdate(author3);
		
		return authorPersistence;
	}
	
	private BookPersistence populateBooks() {
		BookPersistence bookPersistence = new BookPersistence(couchbaseConnectionProvider);
		
		Book book1 = new Book();
		book1.setAuthor("12");
		book1.setId(100);
		book1.setIsbn("ISBN0000000001");
		book1.setPrice(new BigDecimal(12.25));
		book1.setAvailability(new Short("1"));
		book1.setBestSeller(new Short("1"));
		book1.setPublisher("789");
		book1.setTitle("Title ok book1");
		
		Book book2 = new Book();
		book2.setAuthor("12");
		book2.setId(110);
		book2.setIsbn("ISBN0000001234");
		book2.setPrice(new BigDecimal(12.25));
		book2.setAvailability(new Short("1"));
		book2.setBestSeller(new Short("1"));
		book2.setPublisher("789");
		book2.setTitle("Title ok book2");
		
		Book book3 = new Book();
		book3.setAuthor("99");
		book3.setId(120);
		book3.setIsbn("ISBN0000000001");
		book3.setPrice(new BigDecimal(12.25));
		book3.setAvailability(new Short("1"));
		book3.setBestSeller(new Short("1"));
		book3.setPublisher("789");
		book3.setTitle("Title ok book3");
		
		bookPersistence.saveOrUpdate(book1);
		bookPersistence.saveOrUpdate(book2);
		bookPersistence.saveOrUpdate(book3);
		
		return bookPersistence;
	}
}
