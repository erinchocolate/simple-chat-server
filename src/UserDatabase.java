

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
   
public class UserDatabase {
	private Connection connection;
	private PreparedStatement preparedStatement;
	private ResultSet resultSet;
	private String url = "jdbc:mysql://localhost:3306/user";
	private String databaseUser = "root";
	private String databasePassword = "qb686994";
	
	public UserDatabase() {
		connection = null;
		preparedStatement = null;
		resultSet = null;
	}

	public void insertIntoDB(String username, String password) throws SQLException{		
		String insert = "INSERT INTO users (username, password)" + "VALUES (?,?)";
		try {		
			connection = DriverManager.getConnection(url,databaseUser,databasePassword);
			preparedStatement = connection.prepareStatement(insert);			
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			preparedStatement.executeUpdate();		
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	
	public boolean isUsernameExsit(String username) throws SQLException {
		String search = "SELECT * FROM users WHERE username = ?";
		try {
			connection = DriverManager.getConnection(url,databaseUser,databasePassword);
			preparedStatement = connection.prepareStatement(search);
			preparedStatement.setString(1, username);
			resultSet = preparedStatement.executeQuery();		
			if(resultSet.isBeforeFirst()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return false;
	}
	
	// Check user input with password stored in database
	public boolean isPasswordMatch(String username, String password) throws SQLException {
		String check = "SELECT password FROM users WHERE username = ?";
		try {
			connection = DriverManager.getConnection(url,databaseUser,databasePassword);
			preparedStatement = connection.prepareStatement(check);
			preparedStatement.setString(1, username);
			resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				String retrievedPassword = resultSet.getString("password");
				if(retrievedPassword.equals(password)) {
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return false;
		
	}
	
	// Close database connection to avoid memory leakage
	public void close() throws SQLException {
		if(resultSet!=null) {
			resultSet.close();
		}
		if(preparedStatement!=null) {
			preparedStatement.close();
		}
		if(connection!=null) {
			connection.close();
		}
	}
}
