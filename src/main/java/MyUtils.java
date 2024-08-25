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
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public Statement createStatement() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Connection is not established.");
        }
        statement = connection.createStatement();
        return statement;
    }

    public void closeStatement() throws SQLException {
        if (statement != null && !statement.isClosed()) {
            statement.close();
        }
    }

    public void createSchema(String schemaName) throws SQLException {
        this.schemaName = schemaName;
        createStatement().execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);
    }

    public void dropSchema() throws SQLException {
        if (schemaName != null) {
            createStatement().execute("DROP SCHEMA IF EXISTS " + schemaName + " CASCADE");
        }
    }

    public void useSchema() throws SQLException {
        if (schemaName != null) {
            createStatement().execute("SET SCHEMA " + schemaName);
        }
    }

    public void createTableRoles() throws SQLException {
        createStatement().execute("CREATE TABLE IF NOT EXISTS Roles (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) UNIQUE)");
    }

    public void createTableDirections() throws SQLException {
        createStatement().execute("CREATE TABLE IF NOT EXISTS Directions (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) UNIQUE)");
    }

    public void createTableProjects() throws SQLException {
        createStatement().execute("CREATE TABLE IF NOT EXISTS Projects (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) UNIQUE, direction_id INT, FOREIGN KEY (direction_id) REFERENCES Directions(id))");
    }

    public void createTableEmployee() throws SQLException {
        createStatement().execute("CREATE TABLE IF NOT EXISTS Employee (id INT AUTO_INCREMENT PRIMARY KEY, first_name VARCHAR(255), role_id INT, project_id INT, FOREIGN KEY (role_id) REFERENCES Roles(id), FOREIGN KEY (project_id) REFERENCES Projects(id))");
    }

    public void dropTable(String tableName) throws SQLException {
        createStatement().execute("DROP TABLE IF EXISTS " + tableName);
    }

    public void insertTableRoles(String roleName) throws SQLException {
        createStatement().executeUpdate("INSERT INTO Roles (name) VALUES ('" + roleName + "')");
    }

    public void insertTableDirections(String directionName) throws SQLException {
        createStatement().executeUpdate("INSERT INTO Directions (name) VALUES ('" + directionName + "')");
    }

    public void insertTableProjects(String projectName, String directionName) throws SQLException {
        int directionId = getDirectionId(directionName);
        createStatement().executeUpdate("INSERT INTO Projects (name, direction_id) VALUES ('" + projectName + "', " + directionId + ")");
    }

    public void insertTableEmployee(String firstName, String roleName, String projectName) throws SQLException {
        int roleId = getRoleId(roleName);
        int projectId = getProjectId(projectName);
        createStatement().executeUpdate("INSERT INTO Employee (first_name, role_id, project_id) VALUES ('" + firstName + "', " + roleId + ", " + projectId + ")");
    }

    public int getRoleId(String roleName) throws SQLException {
        ResultSet rs = createStatement().executeQuery("SELECT id FROM Roles WHERE name = '" + roleName + "'");
        return rs.next() ? rs.getInt("id") : -1;
    }

    public int getDirectionId(String directionName) throws SQLException {
        ResultSet rs = createStatement().executeQuery("SELECT id FROM Directions WHERE name = '" + directionName + "'");
        return rs.next() ? rs.getInt("id") : -1;
    }

    public int getProjectId(String projectName) throws SQLException {
        ResultSet rs = createStatement().executeQuery("SELECT id FROM Projects WHERE name = '" + projectName + "'");
        return rs.next() ? rs.getInt("id") : -1;
    }

    public int getEmployeeId(String firstName) throws SQLException {
        ResultSet rs = createStatement().executeQuery("SELECT id FROM Employee WHERE first_name = '" + firstName + "'");
        return rs.next() ? rs.getInt("id") : -1;
    }

    public List<String> getAllRoles() throws SQLException {
        List<String> roles = new ArrayList<>();
        ResultSet rs = createStatement().executeQuery("SELECT name FROM Roles");
        while (rs.next()) {
            roles.add(rs.getString("name"));
        }
        return roles;
    }

    public List<String> getAllDirestion() throws SQLException {
        List<String> directions = new ArrayList<>();
        ResultSet rs = createStatement().executeQuery("SELECT name FROM Directions");
        while (rs.next()) {
            directions.add(rs.getString("name"));
        }
        return directions;
    }

    public List<String> getAllProjects() throws SQLException {
        List<String> projects = new ArrayList<>();
        ResultSet rs = createStatement().executeQuery("SELECT name FROM Projects");
        while (rs.next()) {
            projects.add(rs.getString("name"));
        }
        return projects;
    }

    public List<String> getAllEmployee() throws SQLException {
        List<String> employees = new ArrayList<>();
        ResultSet rs = createStatement().executeQuery("SELECT first_name FROM Employee");
        while (rs.next()) {
            employees.add(rs.getString("first_name"));
        }
        return employees;
    }

    public List<String> getAllDevelopers() throws SQLException {
        List<String> developers = new ArrayList<>();
        ResultSet rs = createStatement().executeQuery("SELECT e.first_name FROM Employee e JOIN Roles r ON e.role_id = r.id WHERE r.name = 'Developer'");
        while (rs.next()) {
            developers.add(rs.getString("first_name"));
        }
        return developers;
    }

    public List<String> getAllJavaProjects() throws SQLException {
        List<String> javaProjects = new ArrayList<>();
        ResultSet rs = createStatement().executeQuery("SELECT p.name FROM Projects p JOIN Directions d ON p.direction_id = d.id WHERE d.name = 'Java'");
        while (rs.next()) {
            javaProjects.add(rs.getString("name"));
        }
        return javaProjects;
    }

    public List<String> getAllJavaDevelopers() throws SQLException {
        List<String> javaDevelopers = new ArrayList<>();
        ResultSet rs = createStatement().executeQuery("SELECT e.first_name FROM Employee e JOIN Roles r ON e.role_id = r.id JOIN Projects p ON e.project_id = p.id JOIN Directions d ON p.direction_id = d.id WHERE r.name = 'Developer' AND d.name = 'Java'");
        while (rs.next()) {
            javaDevelopers.add(rs.getString("first_name"));
        }
        return javaDevelopers;
    }
}
