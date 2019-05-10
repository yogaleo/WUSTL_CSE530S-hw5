package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

import hw5.DB;

class DBTester {

	/*
	 * Things to consider testing:
	 * 
	 * Properly returns collections
	 * Properly creates new collection
	 * Properly creates new DB (done)
	 * Properly drops db
	 */
	@Test
	public void testCreateNewDB() {
		DB db = new DB("hw5");
		assertTrue(new File("testfiles/hw5").exists());
		new File("testfiles/hw5").delete();
	}
	
	@Test
	public void testDropDB() {
		DB db = new DB("testDelete");
		assertTrue(new File("testfiles/testDelete").exists());
		
		db.dropDatabase();
		assertTrue(!new File("testfiles/testDelete").exists());
	}
	
	@Test
	public void testCreateMultipleDB() {
		DB db1 = new DB("TestDB1");
		DB db2 = new DB("TestDB2");
		DB db3 = new DB("TestDB3");
		DB db4 = new DB("TestDB4");
		DB db5 = new DB("TestDB5");
		
		assertTrue(new File("testfiles/TestDB1").exists());
		assertTrue(new File("testfiles/TestDB2").exists());
		assertTrue(new File("testfiles/TestDB3").exists());
		assertTrue(new File("testfiles/TestDB4").exists());
		assertTrue(new File("testfiles/TestDB5").exists());
		
		new File("testfiles/TestDB1").delete();
		new File("testfiles/TestDB2").delete();
		new File("testfiles/TestDB3").delete();
		new File("testfiles/TestDB4").delete();
		new File("testfiles/TestDB5").delete();
	}
	
	@Test
	public void testCreateExistingDB() {
		DB db = new DB("TestDB");
		assertTrue(new File("testfiles/TestDB").exists());
		
		DB anotherDB = new DB("TestDB");
		assertTrue(new File("testfiles/TestDB").exists());
		
		new File("testfiles/TestDB").delete();
	}

}
