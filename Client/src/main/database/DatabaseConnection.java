package main.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	
	//TODO user i haslo nie powinny byc podane jawnie
	public static String connectionUrl = "jdbc:sqlserver://192.168.56.1:1433;databaseName=BskDB;integratedSecurity=false;user=linux;password=linux;";
	private static DatabaseConnection instance;
	private Connection connection;
	
	 private DatabaseConnection() throws SQLException {
		 try {
	          this.connection = DriverManager.getConnection(connectionUrl);
	      } catch (SQLException ex) {
	          throw new RuntimeException("Error connecting to the database", ex);
	      }
	 }
	 
	 public Connection getConnection() {
	        return connection;
	 }

	 public static DatabaseConnection getInstance() throws SQLException {
		 if (instance == null) {
			 instance = new DatabaseConnection();
		 } else if (instance.getConnection().isClosed()) {
			 instance = new DatabaseConnection();
		 }
		 return instance;
	}

}
