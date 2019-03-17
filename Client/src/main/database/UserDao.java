package main.database;

import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import javafx.scene.control.Alert.AlertType;
import main.util.DialogUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//TODO kiedy catch exception to wyœwietliæ okienko o b³êdzie

public class UserDao {

	public User getUser(int id) {
		try {
			Connection connection = DatabaseConnection.getInstance().getConnection();
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM [User] WHERE UserId=" + id);
			if (rs.next()) {
				return extractUserFromResultSet(rs);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			DialogUtils.showDialog("Error", ex.getMessage(), AlertType.ERROR);
		}
		return null;
	}
	
	public User getUserByLogin(String login) {
		try {
			Connection connection = DatabaseConnection.getInstance().getConnection();
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM [User] WHERE Login=?");
			ps.setString(1, login);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return extractUserFromResultSet(rs);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			DialogUtils.showDialog("Error", ex.getMessage(), AlertType.ERROR);
		}
		return null;
	}

	private User extractUserFromResultSet(ResultSet rs) throws SQLException {
		User user = new User();
		user.setId(rs.getInt("UserId"));
		user.setLogin(rs.getString("Login"));
		user.setPassword(rs.getString("Password"));
		return user;
	}

	public Set<User> getAllUsers() {
		try {
			Connection connection = DatabaseConnection.getInstance().getConnection();
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM [User]");
			Set<User> users = new HashSet<User>();
			while (rs.next()) {
				User user = extractUserFromResultSet(rs);
				users.add(user);
			}
			return users;
		} catch (SQLException ex) {
			ex.printStackTrace();
			DialogUtils.showDialog("Error", ex.getMessage(), AlertType.ERROR);
		}
		return null;
	}

	public boolean insertUser(User user) {
		try {
			Connection connection = DatabaseConnection.getInstance().getConnection();
			PreparedStatement ps = connection.prepareStatement("INSERT INTO [User] (Login, Password) VALUES (?, ?)");
			ps.setString(1, user.getLogin());
			ps.setString(2, user.getPassword());
			int i = ps.executeUpdate();
			if (i == 1) {
				return true;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			DialogUtils.showDialog("Error", ex.getMessage(), AlertType.ERROR);
		}
		return false;
	}
	
	public boolean insertUser(String login, String password) {
		try {
			Connection connection = DatabaseConnection.getInstance().getConnection();
			PreparedStatement ps = connection.prepareStatement("INSERT INTO [User] (Login, Password) VALUES (?, ?)");
			ps.setString(1, login);
			ps.setString(2, password);
			int i = ps.executeUpdate();
			if (i == 1) {
				return true;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			DialogUtils.showDialog("Error", ex.getMessage(), AlertType.ERROR);
		}
		return false;
	}
	
}