package com.alta189.sqlLibrary.SQLite;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.logging.Logger;

public class sqlCore {
	/*
	 * @author: alta189
	 */
	private Logger log;
	private String logPrefix;
	public String dbLocation;
	public String dbName;
	private DatabaseHandler manageDB;

	public sqlCore(Logger log, String logPrefix, String dbName, String dbLocation) {
		this.log = log;
		this.logPrefix = logPrefix;
		this.dbName = dbName;
		this.dbLocation = dbLocation;
	}

	public void writeInfo(String toWrite) {
		if (toWrite != null) {
			log.info(logPrefix + toWrite);
		}
	}

	public void writeError(String toWrite, Boolean severe) {
		if (severe) {
			if (toWrite != null) {
				log.severe(logPrefix + toWrite);
			}
		} else {
			if (toWrite != null) {
				log.warning(logPrefix + toWrite);
			}
		}
	}

	public Boolean initialize() {
		File dbFolder = new File(dbLocation);
		if (dbName.contains("/") || dbName.contains("\\") || dbName.endsWith(".db")) {
			writeError("The database name can not contain: /, \\, or .db", true);
			return false;
		}
		if (!dbFolder.exists()) {
			dbFolder.mkdir();
		}
		File SQLFile = new File(dbFolder.getAbsolutePath() + "/" + dbName);
		manageDB = new DatabaseHandler(this, SQLFile);
		return manageDB.initialize();
	}

	public ResultSet sqlQuery(String query) {
		return manageDB.sqlQuery(query);
	}

	public Boolean createTable(String query) {
		return manageDB.createTable(query);
	}

	public void insertQuery(String query) {
		manageDB.insertQuery(query);
	}

	public void updateQuery(String query) {
		manageDB.updateQuery(query);
	}

	public void deleteQuery(String query) {
		manageDB.deleteQuery(query);
	}

	public Boolean checkTable(String table) {
		return manageDB.checkTable(table);
	}

	public Boolean wipeTable(String table) {
		return manageDB.wipeTable(table);
	}

	public Connection getConnection() {
		return manageDB.getConnection();
	}

	public void close() {
		manageDB.closeConnection();
	}

	public Boolean checkConnection() {
		Connection con = manageDB.getConnection();
		if (con != null) {
			return true;
		}
		return false;
	}
}
