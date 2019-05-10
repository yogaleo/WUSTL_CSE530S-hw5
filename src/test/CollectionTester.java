package test;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import hw5.DB;
import hw5.DBCollection;
import hw5.Document;

class CollectionTester {

	/*
	 * Things to be tested:
	 * 
	 * Document access (done?) Document insert/update/delete
	 */
	@Test
	public void testGetDocument1() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test");
		JsonObject primitive = test.getDocument(0);
		assertTrue(primitive.getAsJsonPrimitive("key").getAsString().equals("value"));
	}

	@Test
	public void testGetDocument2() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test");
		JsonObject object = test.getDocument(1);
		assertTrue(object.getAsJsonObject("embedded").isJsonObject());
		assertTrue(!object.getAsJsonObject("embedded").isJsonPrimitive());

		JsonObject embedded = object.getAsJsonObject("embedded");
		assertTrue(embedded.getAsJsonPrimitive("key2").getAsString().equals("value2"));

	}

	@Test
	public void testGetDocument3() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test");
		JsonObject object = test.getDocument(2);

		assertTrue(object.get("array").isJsonArray());
		assertTrue(object.getAsJsonArray("array").size() == 3);
		JsonArray array = object.getAsJsonArray("array");

		assertTrue(array.get(0).getAsString().equals("one"));
		assertTrue(array.get(1).getAsString().equals("two"));
		assertTrue(array.get(2).getAsString().equals("three"));
	}

	@Test
	public void testSingleInsert() {
		DB db = new DB("insert");
		DBCollection test = db.getCollection("test1");

		String json = "{ item: \"canvas\", qty: 100, tags: [\"cotton\"], size: { h: 28, w: 35.5, uom: \"cm\" } }";
		test.insert(Document.parse(json));

		assertTrue(test.getName().equals("test1"));
		assertTrue(test.count() == 1);

		JsonObject object = test.getDocument(0);

		assertTrue(object.getAsJsonPrimitive("item").getAsString().equals("canvas"));
		assertTrue(object.getAsJsonPrimitive("qty").getAsInt() == 100);

		assertTrue(object.getAsJsonArray("tags").isJsonArray());

		JsonArray array = object.getAsJsonArray("tags");

		assertTrue(array.size() == 1);
		assertTrue(array.get(0).getAsString().equals("cotton"));

		assertTrue(object.getAsJsonObject("size").isJsonObject());

		JsonObject size = object.getAsJsonObject("size");

		assertTrue(size.getAsJsonPrimitive("h").getAsInt() == 28);
		assertTrue(size.getAsJsonPrimitive("w").getAsDouble() == 35.5);
		assertTrue(size.getAsJsonPrimitive("uom").getAsString().equals("cm"));

		File file = new File(test.getCollectionPath());
		file.delete();

	}

	@Test
	public void testMultileInsert() {
		DB db = new DB("insert");
		DBCollection test = db.getCollection("test2");

		String json1 = "{ item: \"journal\", qty: 25, tags: [\"blank\", \"red\"], size: { h: 14, w: 21, uom: \"cm\" } }";
		String json2 = "{ item: \"mat\", qty: 85, tags: [\"gray\"], size: { h: 27.9, w: 35.5, uom: \"cm\" } }";
		String json3 = "{ item: \"mousepad\", qty: 25, tags: [\"gel\", \"blue\"], size: { h: 19, w: 22.85, uom: \"cm\" } }";

		test.insert(Document.parse(json1), Document.parse(json2), Document.parse(json3));

		assertTrue(test.getName().equals("test2"));
		assertTrue(test.count() == 3);

		JsonObject object = test.getDocument(0);

		assertTrue(object.getAsJsonPrimitive("item").getAsString().equals("journal"));
		assertTrue(object.getAsJsonPrimitive("qty").getAsInt() == 25);

		assertTrue(object.getAsJsonArray("tags").isJsonArray());

		JsonArray array = object.getAsJsonArray("tags");

		assertTrue(array.size() == 2);
		assertTrue(array.get(0).getAsString().equals("blank"));
		assertTrue(array.get(1).getAsString().equals("red"));

		assertTrue(object.getAsJsonObject("size").isJsonObject());

		JsonObject size = object.getAsJsonObject("size");

		assertTrue(size.getAsJsonPrimitive("h").getAsInt() == 14);
		assertTrue(size.getAsJsonPrimitive("w").getAsInt() == 21);
		assertTrue(size.getAsJsonPrimitive("uom").getAsString().equals("cm"));

		object = test.getDocument(1);

		assertTrue(object.getAsJsonPrimitive("item").getAsString().equals("mat"));
		assertTrue(object.getAsJsonPrimitive("qty").getAsInt() == 85);

		assertTrue(object.getAsJsonArray("tags").isJsonArray());

		array = object.getAsJsonArray("tags");

		assertTrue(array.size() == 1);
		assertTrue(array.get(0).getAsString().equals("gray"));

		assertTrue(object.getAsJsonObject("size").isJsonObject());

		size = object.getAsJsonObject("size");

		assertTrue(size.getAsJsonPrimitive("h").getAsDouble() == 27.9);
		assertTrue(size.getAsJsonPrimitive("w").getAsDouble() == 35.5);
		assertTrue(size.getAsJsonPrimitive("uom").getAsString().equals("cm"));

		object = test.getDocument(2);

		assertTrue(object.getAsJsonPrimitive("item").getAsString().equals("mousepad"));
		assertTrue(object.getAsJsonPrimitive("qty").getAsInt() == 25);

		assertTrue(object.getAsJsonArray("tags").isJsonArray());

		array = object.getAsJsonArray("tags");

		assertTrue(array.size() == 2);
		assertTrue(array.get(0).getAsString().equals("gel"));
		assertTrue(array.get(1).getAsString().equals("blue"));

		assertTrue(object.getAsJsonObject("size").isJsonObject());

		size = object.getAsJsonObject("size");

		assertTrue(size.getAsJsonPrimitive("h").getAsDouble() == 19);
		assertTrue(size.getAsJsonPrimitive("w").getAsDouble() == 22.85);
		assertTrue(size.getAsJsonPrimitive("uom").getAsString().equals("cm"));

		File file = new File(test.getCollectionPath());
		file.delete();
	}

	@Test
	public void testUpdate1() {
		DB db = new DB("update");
		DBCollection test = db.getCollection("test1");
		String json = "{ item: \"paper\", qty: 100, size: { h: 8.5, w: 11, uom: \"in\" }, status: \"D\" }";

		test.insert(Document.parse(json));

		String query = "{ item: \"paper\" }";
		String update = "{ \"size.uom\": \"cm\", status: \"P\" }";

		test.update(Document.parse(query), Document.parse(update), false);

		assertTrue(test.getName().equals("test1"));
		assertTrue(test.count() == 1);

		JsonObject object = test.getDocument(0);

		assertTrue(object.getAsJsonPrimitive("status").getAsString().equals("P"));

		JsonObject size = object.getAsJsonObject("size");
		assertTrue(size.getAsJsonPrimitive("uom").getAsString().equals("cm"));

		File file = new File(test.getCollectionPath());
		file.delete();

	}

	@Test
	public void testUpdate2() {
		DB db = new DB("update");
		DBCollection test = db.getCollection("test2");
		String json = "{ item: \"paper\", qty: 100, size: { h: 8.5, w: 11, uom: \"in\" }, status: \"D\" }";

		test.insert(Document.parse(json));

		String query = "{ item: \"other\" }";
		String update = "{ \"size.uom\": \"cm\", status: \"P\" }";

		test.update(Document.parse(query), Document.parse(update), false);

		assertTrue(test.getName().equals("test2"));
		assertTrue(test.count() == 1);

		JsonObject object = test.getDocument(0);

		assertTrue(object.getAsJsonPrimitive("status").getAsString().equals("D"));

		JsonObject size = object.getAsJsonObject("size");
		assertTrue(size.getAsJsonPrimitive("uom").getAsString().equals("in"));

		File file = new File(test.getCollectionPath());
		file.delete();

	}

	@Test
	public void testUpdate3() {
		DB db = new DB("update");
		DBCollection test = db.getCollection("test3");
		String json1 = "{ item: \"canvas\", qty: 100, size: { h: 28, w: 35.5, uom: \"in\" }, status: \"A\" }";
		String json2 = "{ item: \"journal\", qty: 25, size: { h: 14, w: 21, uom: \"cm\" }, status: \"A\" }";
		String json3 = "{ item: \"mat\", qty: 85, size: { h: 27.9, w: 35.5, uom: \"cm\" }, status: \"A\" }";
		String json4 = "{ item: \"mousepad\", qty: 25, size: { h: 19, w: 22.85, uom: \"in\" }, status: \"P\" }";

		test.insert(Document.parse(json1), Document.parse(json2), Document.parse(json3), Document.parse(json4));

		assertTrue(test.count() == 4);

		String query = "{\r\n" + "	\"size.uom\": \"cm\"\r\n" + "}";
		String update = "{\r\n" + "	\"size.w\": 15, \"status\": \"D\"\r\n" + "}";

		test.update(Document.parse(query), Document.parse(update), true);

		assertTrue(test.getName().equals("test3"));
		assertTrue(test.count() == 4);

		JsonObject object = test.getDocument(0);

		assertTrue(object.getAsJsonPrimitive("status").getAsString().equals("A"));
		
		JsonObject size = object.getAsJsonObject("size");
		assertTrue(size.getAsJsonPrimitive("uom").getAsString().equals("in"));
		assertTrue(size.getAsJsonPrimitive("w").getAsDouble() == 35.5);
		
		object = test.getDocument(1);

		assertTrue(object.getAsJsonPrimitive("status").getAsString().equals("D"));
		
		size = object.getAsJsonObject("size");
		assertTrue(size.getAsJsonPrimitive("uom").getAsString().equals("cm"));
		assertTrue(size.getAsJsonPrimitive("w").getAsInt() == 15);
		
		object = test.getDocument(2);

		assertTrue(object.getAsJsonPrimitive("status").getAsString().equals("D"));
		
		size = object.getAsJsonObject("size");
		assertTrue(size.getAsJsonPrimitive("uom").getAsString().equals("cm"));
		assertTrue(size.getAsJsonPrimitive("w").getAsInt() == 15);
		
		object = test.getDocument(3);

		assertTrue(object.getAsJsonPrimitive("status").getAsString().equals("P"));
		
		size = object.getAsJsonObject("size");
		assertTrue(size.getAsJsonPrimitive("uom").getAsString().equals("in"));
		assertTrue(size.getAsJsonPrimitive("w").getAsDouble() == 22.85);

		File file = new File(test.getCollectionPath());
		file.delete();

	}

	@Test
	public void testRemoveSingle() {
		DB db = new DB("remove");
		DBCollection test = db.getCollection("test1");

		String json1 = "{ item: \"journal\", qty: 25, size: { h: 14, w: 21, uom: \"cm\" }, status: \"A\" }";
		String json2 = "{ item: \"notebook\", qty: 50, size: { h: 8.5, w: 11, uom: \"in\" }, status: \"P\" }";
		String json3 = "{ item: \"paper\", qty: 100, size: { h: 8.5, w: 11, uom: \"in\" }, status: \"D\" }";
		String json4 = "{ item: \"planner\", qty: 75, size: { h: 22.85, w: 30, uom: \"cm\" }, status: \"D\" }";
		String json5 = "{ item: \"postcard\", qty: 45, size: { h: 10, w: 15.25, uom: \"cm\" }, status: \"A\" }";

		test.insert(Document.parse(json1), Document.parse(json2), Document.parse(json3), Document.parse(json4),
				Document.parse(json5));

		assertTrue(test.count() == 5);

		String query = "{ status : \"A\" }";

		test.remove(Document.parse(query), false);

		assertTrue(test.count() == 4);

		JsonObject object = test.getDocument(0);
		assertTrue(object.getAsJsonPrimitive("status").getAsString().equals("P"));

		object = test.getDocument(1);
		assertTrue(object.getAsJsonPrimitive("status").getAsString().equals("D"));

		object = test.getDocument(2);
		assertTrue(object.getAsJsonPrimitive("status").getAsString().equals("D"));

		object = test.getDocument(3);
		assertTrue(object.getAsJsonPrimitive("status").getAsString().equals("A"));

		File file = new File(test.getCollectionPath());
		file.delete();
	}

	@Test
	public void testRemoveMulti() {
		DB db = new DB("remove");
		DBCollection test = db.getCollection("test2");

		String json1 = "{ item: \"journal\", qty: 25, size: { h: 14, w: 21, uom: \"cm\" }, status: \"A\" }";
		String json2 = "{ item: \"notebook\", qty: 50, size: { h: 8.5, w: 11, uom: \"in\" }, status: \"P\" }";
		String json3 = "{ item: \"paper\", qty: 100, size: { h: 8.5, w: 11, uom: \"in\" }, status: \"D\" }";
		String json4 = "{ item: \"planner\", qty: 75, size: { h: 22.85, w: 30, uom: \"cm\" }, status: \"D\" }";
		String json5 = "{ item: \"postcard\", qty: 45, size: { h: 10, w: 15.25, uom: \"cm\" }, status: \"A\" }";

		test.insert(Document.parse(json1), Document.parse(json2), Document.parse(json3), Document.parse(json4),
				Document.parse(json5));

		assertTrue(test.count() == 5);

		String query = "{ status : \"A\" }";

		test.remove(Document.parse(query), true);
		assertTrue(test.count() == 3);

		JsonObject object = test.getDocument(0);
		assertTrue(object.getAsJsonPrimitive("status").getAsString().equals("P"));

		object = test.getDocument(1);
		assertTrue(object.getAsJsonPrimitive("status").getAsString().equals("D"));

		object = test.getDocument(2);
		assertTrue(object.getAsJsonPrimitive("status").getAsString().equals("D"));

		query = "{\r\n" + "	\"status\": \"B\"\r\n" + "}";

		test.remove(Document.parse(query), true);
		assertTrue(test.count() == 3);

		query = "{\r\n" + "	\"size.h\": 22.85\r\n" + "}";

		test.remove(Document.parse(query), true);
		assertTrue(test.count() == 2);

		object = test.getDocument(0);

		assertTrue(object.getAsJsonPrimitive("status").getAsString().equals("P"));

		JsonObject size = object.getAsJsonObject("size");
		assertTrue(size.getAsJsonPrimitive("h").getAsDouble() == 8.5);

		object = test.getDocument(1);

		assertTrue(object.getAsJsonPrimitive("status").getAsString().equals("D"));

		size = object.getAsJsonObject("size");
		assertTrue(size.getAsJsonPrimitive("h").getAsDouble() == 8.5);

		File file = new File(test.getCollectionPath());
		file.delete();
	}

	@Test
	public void testDrop() {
		DB db = new DB("drop");
		DBCollection test = db.getCollection("test1");

		File file = new File(test.getCollectionPath());
		assertTrue(file.exists());

		test.drop();

		assertTrue(!file.exists());
	}
	
	@AfterAll
	public static void clean() {
		DB db1 = new DB("insert");
		db1.dropDatabase();
		
		DB db2 = new DB("remove");
		db2.dropDatabase();
		
		DB db3 = new DB("update");
		db3.dropDatabase();
		
		DB db4 = new DB("drop");
		db4.dropDatabase();
	}
}
