package me.kalmanolah.okb3;

import java.io.File;
import java.net.MalformedURLException;
import java.sql.ResultSet;

import com.alta189.sqlLibrary.MySQL.mysqlCore;
import com.alta189.sqlLibrary.SQLite.sqlCore;

/**
 * A class managing the database managing internal OKB3 functionality
 * 
 * @author kalmanolah
 */
public class OKDB {
	/**
	 * The internal field holding an object managing mySQL interactions
	 */
	private static mysqlCore dbmysql  = null;
	/**
	 * The internal field holding an object managing mySQL interactions
	 */
	private static sqlCore   dbsqlite = null;
	/**
	 * An enumeration to hold the different database engines that OKB3 can use
	 * to store its data.
	 *
	 * @author awollangk
	 */
	private enum   DBTYPE {
		SQLITE,
		MYSQL
	}
	/**
	 * Initialize the OKB3 Database.
	 *
	 * @param  instance    A reference to the main plugin class
	 * @throws OKException If an invalid database type is specified in the configuration file
	 */
	public static void initialize(OKmain instance) throws OKException {
		String dbTypeName = (String) OKFunctions.getConfig("general.dbtype");
		DBTYPE dbType = null;
		try {
			dbType = DBTYPE.valueOf(dbTypeName);
		}
		catch (IllegalArgumentException e) {
			throw new OKException("Unknown OKDB general.db-type of " + dbTypeName + " (expected SQLITE or MYSQL.)", e);
		}
		switch (dbType) {
		case SQLITE:
			String dbPath = (String) OKFunctions.getConfig("gen.sqlite.path");
			String dbFile = (String) OKFunctions.getConfig("gen.sqlite.filename");
			File dbDir = new File(dbPath);
			if (!dbDir.exists()) {
				dbDir.mkdir();
			}
			OKDB.dbsqlite = new sqlCore(OKLogger.getLog(), OKLogger.getPrefix(), dbFile, dbDir.getPath());
			break;
		case MYSQL:
			String dbHost = (String) OKFunctions.getConfig("gen.mysql.host");
			String dbUser = (String) OKFunctions.getConfig("gen.mysql.user");
			String dbPass = (String) OKFunctions.getConfig("gen.mysql.password");
			String dbDb   = (String) OKFunctions.getConfig("gen.mysql.dbname");
			OKDB.dbmysql = new mysqlCore(OKLogger.getLog(), OKLogger.getPrefix(), dbHost, dbDb, dbUser, dbPass);
			break;
		}
		OKLogger.dbinfo("Loading database...");
		OKDB.init();
		if (!OKDB.checkTable("players")) {
			OKLogger.dbinfo("Creating table 'players'...");
			String query = "CREATE TABLE players (id INT AUTO_INCREMENT PRIMARY_KEY, player VARCHAR(255), user VARCHAR(255), encpass VARCHAR(255));";
			OKDB.createTable(query);
		}
		if (!OKDB.checkTable("bans")) {
			OKLogger.dbinfo("Creating table 'bans'...");
			String query = "CREATE TABLE bans (id INT AUTO_INCREMENT PRIMARY_KEY, player VARCHAR(255), reason VARCHAR(255));";
			OKDB.createTable(query);
		}
		if (!OKDB.checkTable("posts")) {
			OKLogger.dbinfo("Creating table 'posts'...");
			String query = "CREATE TABLE posts (id INT AUTO_INCREMENT PRIMARY_KEY, name VARCHAR(255), postcount INT(10));";
			OKDB.createTable(query);
		}
	}
	/**
	 * Run a select query against the OKB3 database.
	 *
	 * @param  query The SQL statement to execute
	 * @return The results of the SQL query
	 *
	 * @throws MalformedURLException  Bubbles up from mysqlCore.sqlQuery which in turn bubbles from DatabaseHandler.sqlQuery
	 * @throws InstantiationException Bubbles up from mysqlCore.sqlQuery which in turn bubbles from DatabaseHandler.sqlQuery
	 * @throws IllegalAccessException Bubbles up from mysqlCore.sqlQuery which in turn bubbles from DatabaseHandler.sqlQuery
	 * @throws OKException            If neither mySQL nor SQLite databases are configured.
	 */
	public static ResultSet sqlQuery(String query)
		throws MalformedURLException,
		       InstantiationException,
		       IllegalAccessException,
		       OKException {
		ResultSet retVal = null;

		if (dbmysql != null)
			retVal = dbmysql.sqlQuery(query);
		else if (dbsqlite != null)
			retVal = dbsqlite.sqlQuery(query);
		else
			throw new OKException("Neither mySQL nor SQLite databases initialized when accessing sqlQuery.");

		return retVal;
	}
	/**
	 * Insert data into the OKB3 database.
	 *
	 * @param  query                  The query containing the insert statement.
	 * @throws MalformedURLException  Bubbles up from mysqlCore.insertQuery which in turn bubbles from DatabaseHandler.insertQuery
	 * @throws InstantiationException Bubbles up from mysqlCore.insertQuery which in turn bubbles from DatabaseHandler.insertQuery
	 * @throws IllegalAccessException Bubbles up from mysqlCore.insertQuery which in turn bubbles from DatabaseHandler.insertQuery
	 * @throws OKException            If neither mySQL nor SQLite databases are configured.
	 */
	public static void insertQuery(String query)
		throws MalformedURLException,
		       InstantiationException,
		       IllegalAccessException,
		       OKException {
		if (dbmysql != null)
			dbmysql.insertQuery(query);
		else if (dbsqlite != null)
			dbsqlite.insertQuery(query);
		else
			throw new OKException("Neither mySQL nor SQLite databases initialized when accessing insertQuery.");
	}
	/**
	 * Delete data from the OKB3 database.
	 *
	 * @param  query                  The query containing the insert statement.
	 * @throws MalformedURLException  Bubbles up from mysqlCore.insertQuery which in turn bubbles from DatabaseHandler.insertQuery
	 * @throws InstantiationException Bubbles up from mysqlCore.insertQuery which in turn bubbles from DatabaseHandler.insertQuery
	 * @throws IllegalAccessException Bubbles up from mysqlCore.insertQuery which in turn bubbles from DatabaseHandler.insertQuery
	 * @throws OKException            If neither mySQL nor SQLite databases are configured.
	 */
	public static void deleteQuery(String query)
		throws MalformedURLException,
		       InstantiationException,
		       IllegalAccessException,
		       OKException {
		if (dbmysql != null)
			dbmysql.deleteQuery(query);
		else if (dbsqlite != null)
			dbsqlite.deleteQuery(query);
		else
			throw new OKException("Neither mySQL nor SQLite databases initialized when accessing deleteQuery.");
	}
	/**
	 * Update data in the OKB3 database.
	 *
	 * @param  query                  The query containing the update statement.
	 * @throws MalformedURLException  Bubbles up from mysqlCore.updateQuery which in turn bubbles from DatabaseHandler.updateQuery
	 * @throws InstantiationException Bubbles up from mysqlCore.updateQuery which in turn bubbles from DatabaseHandler.updateQuery
	 * @throws IllegalAccessException Bubbles up from mysqlCore.updateQuery which in turn bubbles from DatabaseHandler.updateQuery
	 * @throws OKException            If neither mySQL nor SQLite databases are configured.
	 */
	public static void updateQuery(String query)
		throws MalformedURLException,
		       InstantiationException,
		       IllegalAccessException,
		       OKException {
		if (dbmysql != null)
			dbmysql.updateQuery(query);
		else if (dbsqlite != null)
			dbsqlite.updateQuery(query);
		else
			throw new OKException("Neither mySQL nor SQLite databases initialized when accessing updateQuery.");
	}
	/**
	 * Disable the OKB3 database.
	 */
	public static void disable() {
		try {
			if (dbmysql != null)
				dbmysql.close();
			else if (dbsqlite != null)
				dbsqlite.close();
		} catch (Exception e) {}
	}
	/**
	 * Initialize the database handler.
	 *
	 * @throws OKException If neither mySQL nor SQLite databases are configured.
	 */
	private static void init() throws OKException {
		if (dbmysql != null)
			dbmysql.initialize();
		else if (dbsqlite != null)
			dbsqlite.initialize();
		else
			throw new OKException("Neither mySQL nor SQLite databases initialized when accessing initialize.");
	}
	/**
	 * Check whether a table exists in the OKB3 database.
	 *
	 * @param  table       The name of the table to look for
	 * @return             Whether the table exists
	 * @throws OKException If neither mySQL nor SQLite databases are configured.
	 */
	private static Boolean checkTable(String table) throws OKException {
		Boolean retVal = false;

		try {
			if (dbmysql != null)
				retVal = dbmysql.checkTable(table);
			else if (dbsqlite != null)
				retVal = dbsqlite.checkTable(table);
			else
				throw new OKException("Neither mySQL nor SQLite databases initialized when accessing checkTable.");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return retVal;
	}
	/**
	 * Create a table in the OKB3 database.
	 *
	 * @param  query       The query to use to create the table
	 * @return             Whether the table was successfully created
	 * @throws OKException If neither mySQL nor SQLite databases are configured.
	 */
	private static Boolean createTable(String query) throws OKException {
		Boolean retVal = false;

		if (dbmysql != null)
			retVal = dbmysql.createTable(query);
		else if (dbsqlite != null)
			retVal = dbsqlite.createTable(query);
		else
			throw new OKException("Neither mySQL nor SQLite databases initialized when accessing checkTable.");

		return retVal;
	}
}