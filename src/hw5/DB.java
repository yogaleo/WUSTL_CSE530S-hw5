package hw5;

import java.io.File;

public class DB {

	private final String FILE_PREFIX = "testfiles/";

	private String name; // database name
	private File file;
	private String filePath;

	/**
	 * Creates a database object with the given name. The name of the database will
	 * be used to locate where the collections for that database are stored. For
	 * example if my database is called "library", I would expect all collections
	 * for that database to be in a directory called "library".
	 * 
	 * If the given database does not exist, it should be created.
	 */
	public DB(String name) {
		this.name = name;
		filePath = this.FILE_PREFIX + name;
		file = new File(filePath);

		if (this.dirExist()) {

		} else {
			this.dirCreate();
		}
	}

	/**
	 * Retrieves the collection with the given name from this database. The
	 * collection should be in a single file in the directory for this database.
	 * 
	 * Note that it is not necessary to read any data from disk at this time. Those
	 * methods are in DBCollection.
	 */
	public DBCollection getCollection(String name) {
		DBCollection collection = new DBCollection(this, name);
		return collection;
	}

	/**
	 * Drops this database and all collections that it contains
	 */
	public void dropDatabase() {
		if(dirExist()) {
			this.dirDelete();
		}
	}
	
	public String getDBPath() {
		return this.filePath;
	}
	
	public String getDBName() {
		return this.name;
	}

	private boolean dirExist() {
		return this.file.exists();
	}

	private void dirCreate() {
		this.file.mkdir();
	}

	private void dirDelete() {
		String[] contents = this.file.list();
		for (String f : contents) {
			File currentFile = new File(this.file.getPath(), f);
			currentFile.delete();
		}
		this.file.delete();
	}
}
