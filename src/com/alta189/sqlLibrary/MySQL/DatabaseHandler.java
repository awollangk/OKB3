package com.alta189.sqlLibrary.MySQL;

import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler {
	private mysqlCore core;
	private Connection connection;
	private String dblocation;
	private String username;
	private String password;
	private String database;

	public DatabaseHandler(mysqlCore core, String dbLocation, String database, String username, String password) {
		this.core = core;
		dblocation = dbLocation;
		this.database = database;
		this.username = username;
		this.password = password;
	}

	private boolean openConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + dblocation + "/" + database, username, password);
		} catch (ClassNotFoundException e) {
			core.writeError("ClassNotFoundException! " + e.getMessage(), true);
			return false;
		} catch (SQLException e) {
			core.writeError("SQLException! " + e.getMessage(), true);
			return false;
		}
		return true;
	}

	public boolean checkConnection() {
		if (connection == null) {
			if (!openConnection()) {
				return false;
			}
		}
		return true;
	}

	public void closeConnection() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (Exception e) {
			core.writeError("Failed to close database connection! " + e.getMessage(), true);
		}
	}

	public Connection getConnection() throws MalformedURLException, InstantiationException, IllegalAccessException {
		if (connection == null) {
			openConnection();
		}
		return connection;
	}

	public ResultSet sqlQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			Connection connection = getConnection();
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(query);
			return result;
		} catch (SQLException e) {
			closeConnection();
			openConnection();
			try {
				Connection connection = getConnection();
				Statement statement = connection.createStatement();
				ResultSet result;
				result = statement.executeQuery(query);
				return result;
			} catch (SQLException e1) {
				core.writeError("Error at SQL Query: " + e.getMessage(), false);
			}
		}
		return null;
	}

	public void insertQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			Connection connection = getConnection();
			Statement statement = connection.createStatement();
			statement.executeUpdate(query);
		} catch (SQLException e) {
			core.writeError("Error at SQL Query: " + e.getMessage(), false);
		}
	}

	public void updateQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			Connection connection = getConnection();
			Statement statement = connection.createStatement();
			statement.executeUpdate(query);
		} catch (SQLException e) {
			closeConnection();
			openConnection();
			try {
				Connection connection = getConnection();
				Statement statement = connection.createStatement();
				statement.executeUpdate(query);
			} catch (SQLException e1) {
				core.writeError("Error at SQL Query: " + e.getMessage(), false);
			}
		}
	}

	public void deleteQuery(String query) throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			Connection connection = getConnection();
			Statement statement = connection.createStatement();
			statement.executeUpdate(query);
		} catch (SQLException ex) {
			if (!ex.toString().contains("not return ResultSet")) {
				core.writeError("Error at SQL DELETE Query: " + ex, false);
			}
		}
	}

	public Boolean checkTable(String table) throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			Connection connection = getConnection();
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM " + table);
			if (result == null) {
				return false;
			}
			if (result != null) {
				return true;
			}
		} catch (SQLException ex) {
			if (ex.getMessage().contains("exist")) {
				return false;
			} else {
				core.writeError("Error at SQL Query: " + ex.getMessage(), false);
			}
		}
		if (sqlQuery("SELECT * FROM " + table) == null) {
			return true;
		}
		return false;
	}

	public Boolean wipeTable(String table) throws MalformedURLException, InstantiationException, IllegalAccessException {
		try {
			if (!core.checkTable(table)) {
				core.writeError("Error at Wipe Table: table, " + table + ", does not exist", true);
				return false;
			}
			Connection connection = getConnection();
			Statement statement = connection.createStatement();
			String query = "DELETE FROM " + table + ";";
			statement.executeUpdate(query);
			return true;
		} catch (SQLException ex) {
			if (!ex.toString().contains("not return ResultSet")) {
				core.writeError("Error at SQL WIPE TABLE Query: " + ex, false);
			}
			return false;
		}
	}

	public Boolean createTable(String query) {
		try {
			if (query == null) {
				core.writeError("SQL Create Table query empty.", true);
				return false;
			}
			Statement statement = connection.createStatement();
			statement.execute(query);
			return true;
		} catch (SQLException ex) {
			core.writeError(ex.getMessage(), true);
			return false;
		}
	}
}
