package hw5;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class DBCursor implements Iterator<JsonObject> {

	private final DBCollection collection;
	private JsonObject query;
	private JsonObject projection;

	private List<JsonObject> list;
	private Iterator<JsonObject> jsonIterator;

	/**
	 * 
	 * @param collection DBcollection
	 * @param query      query json
	 * @param fields     project json
	 */
	public DBCursor(DBCollection collection, JsonObject query, JsonObject fields) {
		this.collection = collection;
		this.query = query;
		this.projection = fields;
		list = this.collection.deepCopy();

		preProcess();

		jsonIterator = list.iterator();
	}

	/**
	 * Returns true if there are more documents to be seen
	 */
	public boolean hasNext() {
		if (this.jsonIterator.hasNext())
			return true;
		return false;
	}

	/**
	 * Returns the next document
	 */
	public JsonObject next() {

		return this.jsonIterator.next();
	}

	/**
	 * Returns the total number of documents
	 */
	public long count() {
		return this.list.size();
	}

	private void preProcess() {
		// process query
		if (this.query != null) {
			Iterator<JsonObject> iter1 = this.list.iterator();
			while (iter1.hasNext()) {
				JsonObject object = iter1.next();
				if (!matchQuery(object, this.query)) {
					iter1.remove();
				}
			}
		}

		// process query
		if (this.projection != null) {
			Iterator<JsonObject> iter2 = this.list.iterator();
			while (iter2.hasNext()) {
				JsonObject object = iter2.next();
				projectQuery(object, this.projection);
			}
		}

	}

	private boolean matchQuery(JsonObject object, JsonObject inputQuery) {
		Set<String> keySet = inputQuery.keySet();
		Iterator<String> iter = keySet.iterator();
		while (iter.hasNext()) {
			JsonObject tempObject = object;

			String queryString = iter.next();
			String[] splited = queryString.split("\\.");
			for (int i = 0; i < splited.length - 1; i++) {
				if (tempObject.has(splited[i])) {
					tempObject = tempObject.getAsJsonObject(splited[i]);
				} else {
					return false;
				}
			}

			JsonElement element;
			if (tempObject.has(splited[splited.length - 1])) {
				element = tempObject.getAsJsonPrimitive(splited[splited.length - 1]);
			} else {
				return false;
			}

			JsonElement queryPart = inputQuery.get(queryString);
			if (!matchComparisionQuery(element, queryPart)) {
				return false;
			}
		}

		return true;
	}

	private void projectQuery(JsonObject object, JsonObject inputProject) {
		Set<String> keySet = inputProject.keySet();
		Iterator<String> iter = keySet.iterator();
		while (iter.hasNext()) {
			JsonObject tempObject = object;
			String queryString = iter.next();
			String[] splited = queryString.split("\\.");
			for (int i = 0; i < splited.length - 1; i++) {
				tempObject = tempObject.getAsJsonObject(splited[i]);
				if (tempObject.has(splited[i])) {
					tempObject = tempObject.getAsJsonObject(splited[i]);
				}
			}

			if (tempObject.has(splited[splited.length - 1])) {
				if (inputProject.get(queryString).getAsJsonPrimitive().getAsInt() == 0) {
					tempObject.remove(splited[splited.length - 1]);
				}
			}
		}
	}

	private boolean matchComparisionQuery(JsonElement element, JsonElement inputQuery) {
		if (inputQuery.isJsonPrimitive()) {
			// if query final is a primitive, the json object must be a primitive at the end
			JsonPrimitive primitive = element.getAsJsonPrimitive();
			if (!primitive.getAsJsonPrimitive().equals(inputQuery.getAsJsonPrimitive())) {
				return false;
			}
		} else if (inputQuery.isJsonObject()) { // : {$xx: xxx}
			JsonObject comparison = inputQuery.getAsJsonObject();
			Set<String> keySet = comparison.keySet();
			Iterator<String> iter = keySet.iterator();

			while (iter.hasNext()) {
				String comp = iter.next(); // $xx
				JsonElement compValue = inputQuery.getAsJsonObject().get(comp); // :xxx
				
				if (compValue.isJsonPrimitive()) {
					switch (comp) {
					case "$eq":
						if (!element.getAsJsonPrimitive().equals(compValue.getAsJsonPrimitive())) {
							return false;
						}
						break;
					case "$gt":
						if (element.getAsJsonPrimitive().isNumber() && element.getAsJsonPrimitive().getAsNumber()
								.doubleValue() <= compValue.getAsJsonPrimitive().getAsNumber().doubleValue()) {
							return false;
						}

						if (element.getAsJsonPrimitive().isString() && element.getAsJsonPrimitive().getAsString()
								.compareTo(compValue.getAsJsonPrimitive().getAsString()) <= 0) {
							return false;
						}
						break;
					case "$gte":
						if (element.getAsJsonPrimitive().isNumber() && element.getAsJsonPrimitive().getAsNumber()
								.doubleValue() < compValue.getAsJsonPrimitive().getAsNumber().doubleValue()) {
							return false;
						}

						if (element.getAsJsonPrimitive().isString() && element.getAsJsonPrimitive().getAsString()
								.compareTo(compValue.getAsJsonPrimitive().getAsString()) < 0) {
							return false;
						}
						break;
					case "$lt":
						if (element.getAsJsonPrimitive().isNumber() && element.getAsJsonPrimitive().getAsNumber()
								.doubleValue() >= compValue.getAsJsonPrimitive().getAsNumber().doubleValue()) {
							return false;
						}

						if (element.getAsJsonPrimitive().isString() && element.getAsJsonPrimitive().getAsString()
								.compareTo(compValue.getAsJsonPrimitive().getAsString()) >= 0) {
							return false;
						}
						break;
					case "$lte":
						if (element.getAsJsonPrimitive().isNumber() && element.getAsJsonPrimitive().getAsNumber()
								.doubleValue() > compValue.getAsJsonPrimitive().getAsNumber().doubleValue()) {
							return false;
						}

						if (element.getAsJsonPrimitive().isString() && element.getAsJsonPrimitive().getAsString()
								.compareTo(compValue.getAsJsonPrimitive().getAsString()) > 0) {
							return false;
						}
						break;
					case "$ne":
						if (element.getAsJsonPrimitive().equals(compValue.getAsJsonPrimitive())) {
							return false;
						}
						break;
					default:
						break;
					}

				} else if (compValue.isJsonArray()) {
					JsonArray array = compValue.getAsJsonArray();
					switch (comp) {
					case "$in":
						boolean exist = false;
						for (int i = 0; i < array.size(); i++) {
							JsonElement item = array.get(i);
							if (item.equals(compValue)) {
								exist = true;
							}
						}
						if (!exist)
							return false;
						break;
					case "$nin":
						boolean noExist = true;
						for (int i = 0; i < array.size(); i++) {
							JsonElement item = array.get(i);
							if (item.equals(compValue)) {
								noExist = false;
							}
						}
						if (!noExist)
							return false;
						break;
					default:
						break;
					}
				}

			}
		}

		return true;
	}
}
