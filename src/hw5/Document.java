package hw5;

import com.google.gson.*;

public class Document {
	
	/**
	 * Parses the given json string and returns a JsonObject
	 * This method should be used to convert text data from
	 * a file into an object that can be manipulated.
	 */
	public static JsonObject parse(String json) {
		JsonObject document = new JsonObject();
		JsonParser parser = new  JsonParser();
		JsonElement element = parser.parse(json);
		if(element.isJsonObject()) {
			document = element.getAsJsonObject();
		}
		
		return document;
	}
	
	/**
	 * Takes the given object and converts it into a
	 * properly formatted json string. This method should
	 * be used to convert JsonObjects to strings
	 * when writing data to disk.
	 */
	public static String toJsonString(JsonObject json) {
		return json.toString();
	}
}
