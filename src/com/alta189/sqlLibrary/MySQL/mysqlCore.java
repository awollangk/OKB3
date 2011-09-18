package com.alta189.sqlLibrary.MySQL;

import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.logging.Logger;

public class mysqlCore {
	private Logger log;
	private String logPrefix;
	public String host;
	private DatabaseHandler manageDB;
	public String username;
	public String password;
	public String database;

	public mysqlCore(Logger log, String logPrefix, String host, String database, String username, String password) {
		this.log = log;
		this.logPrefix = logPrefix;
		this.database = database;
		this.host = host;
		this.username = username;
		this.password = password;
	}

	public Boolean initialize() {
		manageDB = new DatabaseHandler(this, host, database, username, password);
		return false;
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

	public ResultSet sqlQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException {
		return manageDB.sqlQuery(query);
	}

	public Boolean createTable(String query) {
		return manageDB.createTable(query);
	}

	public void insertQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException {
		manageDB.insertQuery(query);
	}

	public void updateQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException {
		manageDB.updateQuery(query);
	}

	public void deleteQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException {
		manageDB.deleteQuery(query);
	}

	public Boolean checkTable(String table) throws MalformedURLException, InstantiationException, IllegalAccessException {
		return manageDB.checkTable(table);
	}

	public Boolean wipeTable(String table) throws MalformedURLException, InstantiationException, IllegalAccessException {
		return manageDB.wipeTable(table);
	}

	public Connection getConnection() throws MalformedURLException, InstantiationException, IllegalAccessException {
		return manageDB.getConnection();
	}

	public void close() {
		manageDB.closeConnection();
	}

	public Boolean checkConnection() throws MalformedURLException, InstantiationException, IllegalAccessException {
		return manageDB.checkConnection();
	}
}
