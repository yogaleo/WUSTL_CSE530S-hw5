package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import hw5.DB;
import hw5.DBCollection;
import hw5.DBCursor;
import hw5.Document;

class CursorTester {
	
	private DB db;
	/*
	 *Queries:
	 * 	Find all (done?)
	 * 	Find with relational select
	 * 	Find with projection
	 * 	Conditional operators
	 * 	Embedded Documents and arrays
	 */
	
	@BeforeAll
	public static void setup() {
		
		DB db = new DB("query");
		DBCollection collection1 = db.getCollection("test1");
		
		// origin & embedded
		String s1 = "{ item: \"journal\", qty: 25, size: { h: 14, w: 21, uom: \"cm\" }, status: \"A\" }";
		String s2 = "{ item: \"notebook\", qty: 50, size: { h: 8.5, w: 11, uom: \"in\" }, status: \"A\" }";
		String s3 = "{ item: \"paper\", qty: 100, size: { h: 8.5, w: 11, uom: \"in\" }, status: \"D\" }";
		String s4 = "{ item: \"planner\", qty: 75, size: { h: 22.85, w: 30, uom: \"cm\" }, status: \"D\" }";
		String s5 = "{ item: \"postcard\", qty: 45, size: { h: 10, w: 15.25, uom: \"cm\" }, status: \"A\" }";
		
		collection1.insert(Document.parse(s1), Document.parse(s2), Document.parse(s3), Document.parse(s4), Document.parse(s5));
		
		DBCollection collection2 = db.getCollection("test2");
		
		// array
		s1 = "{ item: \"journal\", qty: 25, tags: [\"blank\", \"red\"], dim_cm: [ 14, 21 ] }";
		s2 = "{ item: \"notebook\", qty: 50, tags: [\"red\", \"blank\"], dim_cm: [ 14, 21 ] }";
		s3 = "{ item: \"paper\", qty: 100, tags: [\"red\", \"blank\", \"plain\"], dim_cm: [ 14, 21 ] }";
		s4 = "{ item: \"planner\", qty: 75, tags: [\"blank\", \"red\"], dim_cm: [ 22.85, 30 ] }";
		s5 = "{ item: \"postcard\", qty: 45, tags: [\"blue\"], dim_cm: [ 10, 15.25 ] }";
		
		collection2.insert(Document.parse(s1), Document.parse(s2), Document.parse(s3), Document.parse(s4), Document.parse(s5));
		
		
	}

	@Test
	public void testFindAll() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test");
		DBCursor results = test.find();
		assertTrue(results.count() == 3);
		assertTrue(results.hasNext());
		JsonObject d1 = results.next(); //verify contents?
		assertTrue(results.hasNext());
		JsonObject d2 = results.next();//verify contents?
		assertTrue(results.hasNext());
		JsonObject d3 = results.next();//verify contents?
		assertTrue(!results.hasNext());
	}
	
	@Test
	public void testFindAllDetail() {
		DB db = new DB("query");
		DBCollection collection = new DBCollection(db, "test1");
		
		DBCursor cursor = collection.find();
		
		assertTrue(cursor.count() == 5);
		assertTrue(cursor.hasNext());
		
		JsonObject d1 = cursor.next();
		assertTrue(cursor.hasNext());
		JsonObject d2 = cursor.next();
		assertTrue(cursor.hasNext());
		JsonObject d3 = cursor.next();
		assertTrue(cursor.hasNext());
		JsonObject d4 = cursor.next();
		assertTrue(cursor.hasNext());
		JsonObject d5 = cursor.next();
		assertTrue(!cursor.hasNext());
	}
	
	@Test
	public void testFindQuery() {
		DB db = new DB("query");
		DBCollection collection = new DBCollection(db, "test1");
		
		String query = "{ status: \"A\" }";
		
		DBCursor cursor = collection.find(Document.parse(query));
		
		assertTrue(cursor.count() == 3);
		assertTrue(cursor.hasNext());
		
		JsonObject obj1 = cursor.next();
		System.out.println(obj1.toString());
		assertTrue(cursor.hasNext());
		JsonObject obj2 = cursor.next();
		System.out.println(obj2.toString());
		assertTrue(cursor.hasNext());
		JsonObject obj3 = cursor.next();
		System.out.println(obj3.toString());
		assertTrue(!cursor.hasNext());
	}
	
	@Test
	public void testFindProject() {
		DB db = new DB("query");
		DBCollection collection = new DBCollection(db, "test1");
		
		String query = "{ status: \"D\" }";
		String projection = "{ \"item\": 0, \"status\": 0 }";
		
		DBCursor cursor = collection.find(Document.parse(query), Document.parse(projection));
		
		assertTrue(cursor.count() == 2);
		assertTrue(cursor.hasNext());
		
		JsonObject obj1 = cursor.next();
//		System.out.println(obj1.toString());
		assertTrue(cursor.hasNext());
		JsonObject obj2 = cursor.next();
//		System.out.println(obj2.toString());
		assertTrue(!cursor.hasNext());
	}
	
	@Test
	public void testFindComparision1() {
		DB db = new DB("query");
		DBCollection collection = new DBCollection(db, "test1");
		
		String query = "{ \"size.h\": { $lt: 15 } }";
		
		DBCursor cursor = collection.find(Document.parse(query));
		
		System.out.println(cursor.count());
		assertTrue(cursor.count() == 4);
		assertTrue(cursor.hasNext());
		
		JsonObject obj1 = cursor.next();
		System.out.println(obj1.toString());
		assertTrue(cursor.hasNext());
		JsonObject obj2 = cursor.next();
		System.out.println(obj2.toString());
		assertTrue(cursor.hasNext());
		JsonObject obj3 = cursor.next();
		System.out.println(obj3.toString());
		assertTrue(cursor.hasNext());
		JsonObject obj4 = cursor.next();
		System.out.println(obj4.toString());
		assertTrue(!cursor.hasNext());
		
	}
	
	
	
	@AfterAll
	public static void clean() {
		DB db = new DB("query");
		db.dropDatabase();
	}

}
