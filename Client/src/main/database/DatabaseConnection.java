package main.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	
	// TODO Tymczasowe rozwi¹zanie, trzeba bêdzie dostosowaæ do maszyny wirtualnej
	public static String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=BskDB;integratedSecurity=true";
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
