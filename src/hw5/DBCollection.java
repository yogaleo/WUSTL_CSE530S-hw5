package hw5;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class DBCollection {

	private final String JSON_SUFFIX = ".json";
	private final String ID_FIELD_NAME = "_id";

	private DB db;
	private String name; // collection name
	private String collectionPath;
	private File collectionFile;
	private List<JsonObject> documents; // all the modification must first happen in memory

	/**
	 * Constructs a collection for the given database with the given name. If that
	 * collection doesn't exist it will be created.
	 */
	public DBCollection(DB database, String name) {
		this.db = database;
		this.name = name;
		this.collectionPath = this.db.getDBPath() + "/" + this.name + JSON_SUFFIX;
		this.collectionFile = new File(collectionPath);
		this.documents = new ArrayList<JsonObject>();

		preProcess();
	}

	/**
	 * Returns a cursor for all of the documents in this collection.
	 */
	public DBCursor find() {
		DBCursor cusor = new DBCursor(this, null, null);
		return cusor;
	}

	/**
	 * Finds documents that match the given query parameters.
	 * 
	 * @param query relational select
	 * @return
	 */
	public DBCursor find(JsonObject query) {
		DBCursor cusor = new DBCursor(this, query, null);
		return cusor;
	}

	/**
	 * Finds documents that match the given query parameters.
	 * 
	 * @param query      relational select
	 * @param projection relational project
	 * @return
	 */
	public DBCursor find(JsonObject query, JsonObject projection) {
		DBCursor cusor = new DBCursor(this, query, projection);
		return cusor;
	}

	/**
	 * Inserts documents into the collection Must create and set a proper id before
	 * insertion When this method is completed, the documents should be permanently
	 * stored on disk.
	 * 
	 * @param documents
	 */
	public void insert(JsonObject... documents) {
		for (JsonObject object : documents) {
			if (!object.has("_id")) {
				UUID uuid = UUID.randomUUID();
				object.addProperty(this.ID_FIELD_NAME, uuid.toString());
			}
			this.documents.add(object);
			writeJsonObject(object, true);
		}
	}

	/**
	 * Locates one or more documents and replaces them with the update document.
	 * 
	 * @param query  relational select for documents to be updated
	 * @param update the document to be used for the update
	 * @param multi  true if all matching documents should be updated false if only
	 *               the first matching document should be updated
	 */
	public void update(JsonObject query, JsonObject update, boolean multi) {
		List<Integer> indexList = new ArrayList<>();
		List<JsonObject> objList = new ArrayList<>();

		for (int i = 0; i < this.documents.size(); i++) {
			JsonObject object = this.documents.get(i);
			if (matchQuery(object, query)) {
				indexList.add(i);
				objList.add(updateJson(object, update));
			}
		}

		for (int i = 0; i < indexList.size(); i++) {
			this.documents.set(indexList.get(i), objList.get(i));
			if (!multi)
				break;
		}

		this.clearCollection();
		for (JsonObject obj : this.documents) {
			this.writeJsonObject(obj, true);
		}
	}

	/**
	 * Removes one or more documents that match the given query parameters
	 * 
	 * @param query relational select for documents to be removed
	 * @param multi true if all matching documents should be updated false if only
	 *              the first matching document should be updated
	 */
	public void remove(JsonObject query, boolean multi) {
		Iterator<JsonObject> iterator = this.documents.iterator();
		while (iterator.hasNext()) {
			JsonObject object = iterator.next();
			if (matchQuery(object, query)) {
				iterator.remove();
				if (!multi)
					break;
			}
		}

		this.clearCollection();
		for (JsonObject obj : this.documents) {
			this.writeJsonObject(obj, true);
		}
	}

	/**
	 * Returns the number of documents in this collection
	 */
	public long count() {
		return this.documents.size();
	}

	public String getName() {
		return this.name;
	}

	public String getCollectionPath() {
		return this.collectionPath;
	}

	/**
	 * Returns the ith document in the collection. Documents are separated by a line
	 * that contains only a single tab (\t) Use the parse function from the document
	 * class to create the document object
	 */
	public JsonObject getDocument(int i) {
		if (i < this.documents.size()) {
			return this.documents.get(i);
		}
		return null;
	}

	/**
	 * Drops this collection, removing all of the documents it contains from the DB
	 */
	public void drop() {
		if (this.collectionFile.exists()) {
			this.collectionFile.delete();
		}
	}

	
	public List<JsonObject> deepCopy(){
		List<JsonObject> list = new ArrayList<>();
		for(JsonObject object: this.documents) {
			list.add(object.deepCopy());
		}
		
		return list;
	}
	
	private void preProcess() {
		if (!this.collectionFile.exists()) {
			try {
				this.collectionFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		List<String> temp = this.readAllJsonObjects();
		for (String s : temp) {
			this.documents.add(Document.parse(s));
		}

	}

	private void clearCollection() {
		PrintWriter writer;
		try {
			writer = new PrintWriter(this.collectionPath);
			writer.print("");
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writeJsonObject(JsonObject object, boolean append) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(this.collectionFile, append));
			writer.write(gson.toJson(object));
			writer.newLine();
			writer.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private List<String> readAllJsonObjects() {
		List<String> list = new ArrayList<>();
		StringBuilder builder = new StringBuilder();
		try {
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(this.collectionPath));
			String line = reader.readLine();

			while (line != null) {
				if (line.trim().length() == 0) {
					list.add(new String(builder));
					builder = new StringBuilder();
				} else {
					builder.append(line);
				}
				line = reader.readLine();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (builder.length() != 0)
			list.add(new String(builder));

		return list;
	}

	private boolean matchQuery(JsonObject object, JsonObject query) {
		Set<String> keySet = query.keySet();
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
			JsonPrimitive primitive = tempObject.getAsJsonPrimitive(splited[splited.length - 1]);
			if (!query.getAsJsonPrimitive(queryString).getAsString().equals(primitive.getAsString())) {
				return false;
			}
		}
		return true;
	}

	private JsonObject updateJson(JsonObject object, JsonObject update) {
		JsonObject newObject = object.deepCopy();

		Set<String> keySet = update.keySet();
		Iterator<String> iter = keySet.iterator();
		while (iter.hasNext()) {
			JsonObject tempObject = newObject;

			String queryString = iter.next();
			String[] splited = queryString.split("\\.");
			for (int i = 0; i < splited.length - 1; i++) {
				tempObject = tempObject.getAsJsonObject(splited[i]);
			}

			tempObject.remove(splited[splited.length - 1]);
			tempObject.add(splited[splited.length - 1], update.get(queryString));
		}

		return newObject;
	}
}
