import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MyUtils {
	private Connection connection;
	private Statement statement;
	private String schemaName;

	public Connection createConnection() throws SQLException {
		DriverManager.registerDriver(new org.h2.Driver());
		connection = DriverManager.getConnection("jdbc:h2:mem:test", "", "");
		return connection;
	}

	public void closeConnection() throws SQLException {
		//your code
	}
	
	public Statement createStatement() throws SQLException {
		statement = connection.createStatement();
		return statement;
	}

	public void closeStatement() throws SQLException {
		//your code
	}
	
	public void createSchema(String schemaName) throws SQLException {
		//your code
	}

	public void dropSchema() throws SQLException {
		//your code
	}
	
	public void useSchema() throws SQLException {
		//your code
	}

	public void createTableRoles() throws SQLException {
		//your code
	}
	
	public void createTableDirections() throws SQLException {
		//your code
	}
	
	public void createTableProjects() throws SQLException {
		//your code
	}
	
	public void createTableEmployee() throws SQLException {
		//your code
	}
	
	public void dropTable(String tableName) throws SQLException {
		//your code
	}
	
	public void insertTableRoles(String roleName) throws SQLException {
		//your code
	}

	public void insertTableDirections(String directionName) throws SQLException {
		//your code
	}

	public void insertTableProjects(String projectName, String directionName) throws SQLException {
		//your code
	}

	public void insertTableEmployee(String firstName, String roleName, String projectName) throws SQLException {
		//your code
	}

	public int getRoleId(String roleName) throws SQLException {
		//your code
		return 0; // return not 0, write your code
	}
	
	public int getDirectionId(String directionName) throws SQLException {

		return 0; // return not 0, write your code
	}
	
	public int getProjectId(String projectName) throws SQLException {

		return 0; // return not 0, write your code
	}
	
	public int getEmployeeId(String firstName) throws SQLException {

		return 0; // return not 0, write your code
	}
	
	public List<String> getAllRoles() throws SQLException {

		return null; // return not null, write your code
	}

	public List<String> getAllDirestion() throws SQLException {

		return null; // return not null, write your code
	}

	public List<String> getAllProjects() throws SQLException {

		return null; // return not null, write your code
	}

	public List<String> getAllEmployee() throws SQLException {

		return null; // return not null, write your code
	}
	
	public List<String> getAllDevelopers() throws SQLException {

		return null; // return not null, write your code
	}

	public List<String> getAllJavaProjects() throws SQLException {

		return null; // return not null, write your code
	}

	public List<String> getAllJavaDevelopers() throws SQLException {

		return null; // return not null, write your code
	}

}
