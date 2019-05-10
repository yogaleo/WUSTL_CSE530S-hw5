package test;

import static org.junit.Assert.*;
//import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import hw5.Document;

class DocumentTester {

	/*
	 * Things to consider testing:
	 * 
	 * Parsing embedded documents Parsing arrays
	 * 
	 * Object to primitive Object to embedded document Object to array
	 */
	@Test
	public void testSimpleParse() {
		String json = "{ \"key\": \"value\" }";
		JsonObject results = new JsonObject();
		results = Document.parse(json);
		assertTrue("simple test wrong", results.getAsJsonPrimitive("key").getAsString().equals("value"));
	}

	@Test
	public void testEmbadedArrayParse() {
		String json = "{\r\n" + " \"firstname\": \"Peter\",\r\n" + " \"lastname\": \"Membrey\",\r\n"
				+ " \"phone_numbers\": [\r\n" + " \"+852 1234 5678\",\r\n" + " \"+44 1234 565 555\"\r\n" + " ]\r\n"
				+ "}";
		JsonObject results = new JsonObject();
		
		results = Document.parse(json);
		
		assertTrue("wrong filed value", results.getAsJsonPrimitive("firstname").getAsString().equals("Peter"));
		assertTrue("wrong field value", results.getAsJsonPrimitive("lastname").getAsString().equals("Membrey"));

		assertTrue("Phone numbers should be a json array", results.get("phone_numbers").isJsonArray());
		assertTrue("Phone number array size incorrect", results.getAsJsonArray("phone_numbers").size() == 2);
		JsonArray array = results.getAsJsonArray("phone_numbers");

		assertTrue("Phone number 0 incorrect", array.get(0).getAsString().equals("+852 1234 5678"));
		assertTrue("Phone number 1 incorrect", array.get(1).getAsString().equals("+44 1234 565 555"));
	}

	@Test
	public void testmbeddedParse() {
		String json = "{\r\n" + "  \"name\":\"John\",\r\n" + "  \"age\":30,\r\n" + "  \"cars\": {\r\n"
				+ "    \"car1\":\"Ford\",\r\n" + "    \"car2\":\"BMW\",\r\n" + "    \"car3\":\"Fiat\"\r\n" + "    }\r\n"
				+ "}";
		JsonObject results = new JsonObject();
		results = Document.parse(json);
		assertTrue("Incorrect object size", results.size() == 3);
		assertTrue("wrong filed value", results.getAsJsonPrimitive("name").getAsString().equals("John"));
		assertTrue("wrong field value", results.getAsJsonPrimitive("age").getAsInt() == 30);

		assertTrue("Test embeded document", results.getAsJsonObject().isJsonObject());

		JsonObject cars = results.getAsJsonObject("cars");
		assertTrue("car1 incorrect", cars.getAsJsonPrimitive("car1").getAsString().equals("Ford"));
		assertTrue("car2 incorrect", cars.getAsJsonPrimitive("car2").getAsString().equals("BMW"));
		assertTrue("Car3 incorrect", cars.getAsJsonPrimitive("car3").getAsString().equals("Fiat"));
	}

	@Test
	public void testComplexParse1() {
		String json = "{\r\n" + " \"type\": \"Book\",\r\n"
				+ " \"Title\": \"Definitive Guide to MongoDB: A complete guide to dealing with Big Data using MongoDB 3rd ed., The\",\r\n"
				+ " \"ISBN\": \"978-1-4842-1183-0\",\r\n" + " \"Publisher\": \"Apress\",\r\n" + " \"Author\": [\r\n"
				+ " \"Hows, David\",\r\n" + " \"Plugge, Eelco\",\r\n" + " \"Membrey, Peter\",\r\n"
				+ " \"Hawkins, Tim\" \r\n" + " ]\r\n" + "}";
		JsonObject results = new JsonObject();
		results = Document.parse(json);
		assertTrue("Incorrect object size", results.size() == 5);

		assertTrue("wrong filed value", results.getAsJsonPrimitive("type").getAsString().equals("Book"));
		assertTrue("wrong field value", results.getAsJsonPrimitive("Title").getAsString().equals(
				"Definitive Guide to MongoDB: A complete guide to dealing with Big Data using MongoDB 3rd ed., The"));
		assertTrue("wrong filed value", results.getAsJsonPrimitive("ISBN").getAsString().equals("978-1-4842-1183-0"));
		assertTrue("wrong filed value", results.getAsJsonPrimitive("Publisher").getAsString().equals("Apress"));

		assertTrue("Author should be a json array", results.get("Author").isJsonArray());
		assertTrue("Author array size incorrect", results.getAsJsonArray("Author").size() == 4);
		JsonArray array = results.getAsJsonArray("Author");

		assertTrue("Author 0 incorrect", array.get(0).getAsString().equals("Hows, David"));
		assertTrue("Author 1 incorrect", array.get(1).getAsString().equals("Plugge, Eelco"));
		assertTrue("Author 2 incorrect", array.get(2).getAsString().equals("Membrey, Peter"));
		assertTrue("Author 3 incorrect", array.get(3).getAsString().equals("Hawkins, Tim"));
	}

	@Test
	public void testComplexParse2() {
		String json = "{\r\n" + " \"Type\": \"CD\",\r\n" + " \"Artist\": \"Nirvana\",\r\n"
				+ " \"Title\": \"Nevermind\",\r\n" + " \"Genre\": \"Grunge\",\r\n"
				+ " \"Releasedate\": \"1991.09.24\",\r\n" + " \"Tracklist\": [\r\n" + " 	{\r\n"
				+ " 		\"Track\": 1,\r\n" + " 		\"Title\": \"Smells Like Teen Spirit\",\r\n"
				+ " 		\"Length\": \"5:02\"\r\n" + " 	},\r\n" + "\r\n" + "	{\r\n" + "		 \"Track\": 2,\r\n"
				+ "		 \"Title\": \"In Bloom\",\r\n" + "		 \"Length\": \"4:15\"\r\n" + " 	}\r\n" + " ]\r\n"
				+ " }";
		JsonObject results = new JsonObject();
		results = Document.parse(json);
		assertTrue("Incorrect object size", results.size() == 6);

		assertTrue("wrong filed value", results.getAsJsonPrimitive("Type").getAsString().equals("CD"));
		assertTrue("wrong field value", results.getAsJsonPrimitive("Artist").getAsString().equals("Nirvana"));
		assertTrue("wrong filed value", results.getAsJsonPrimitive("Title").getAsString().equals("Nevermind"));
		assertTrue("wrong filed value", results.getAsJsonPrimitive("Genre").getAsString().equals("Grunge"));
		assertTrue("wrong filed value", results.getAsJsonPrimitive("Releasedate").getAsString().equals("1991.09.24"));

		assertTrue("Tracklist should be a json array", results.get("Tracklist").isJsonArray());
		assertTrue("Tracklist array size incorrect", results.getAsJsonArray("Tracklist").size() == 2);
		JsonArray array = results.getAsJsonArray("Tracklist");

		assertTrue("Tracklist 0 incorrect", array.get(0).isJsonObject());
		assertTrue("Tracklist 1 incorrect", array.get(1).isJsonObject());

		assertTrue("Tracklist 0 Inteegr test",
				array.get(0).getAsJsonObject().getAsJsonPrimitive("Track").isJsonPrimitive());
		assertTrue("Tracklist 0 Inteegr test",
				array.get(0).getAsJsonObject().getAsJsonPrimitive("Track").getAsInt() == 1);
		assertTrue("Tracklist 0 String test", array.get(0).getAsJsonObject().getAsJsonPrimitive("Title").getAsString()
				.equals("Smells Like Teen Spirit"));
		assertTrue("Tracklist 0 Sring test",
				array.get(0).getAsJsonObject().getAsJsonPrimitive("Length").getAsString().equals("5:02"));

		assertTrue("Tracklist 1 Inteegr test",
				array.get(1).getAsJsonObject().getAsJsonPrimitive("Track").isJsonPrimitive());
		assertTrue("Tracklist 1 Inteegr test",
				array.get(1).getAsJsonObject().getAsJsonPrimitive("Track").getAsInt() == 2);
		assertTrue("Tracklist 1 String test",
				array.get(1).getAsJsonObject().getAsJsonPrimitive("Title").getAsString().equals("In Bloom"));
		assertTrue("Tracklist 1 Sring test",
				array.get(1).getAsJsonObject().getAsJsonPrimitive("Length").getAsString().equals("4:15"));
	}
}
