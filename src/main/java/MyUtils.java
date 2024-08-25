import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MyUtils {
    private Connection connection;
    private Statement statement;
    private String schemaName;

    public Connection createConnection() throws SQLException {
        DriverManager.registerDriver(new org.postgresql.Driver());
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "Philips105s";
        connection = DriverManager.getConnection(url, user, password);
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
        statement = createConnection().createStatement();
        return statement;
    }

    public void closeStatement() throws SQLException {
        if (statement != null && !statement.isClosed()) {
            statement.close();
        }
    }

    public void createSchema(String schemaName) throws SQLException {
        if (createStatement() == null || createStatement().isClosed()) {
            throw new SQLException("Statement is not created.");
        }
        this.schemaName = schemaName;
        String sql = "CREATE SCHEMA IF NOT EXISTS " + schemaName;
        createStatement().executeUpdate(sql);
    }

    public void dropSchema() throws SQLException {
        if (createStatement() == null || createStatement().isClosed()) {
            throw new SQLException("Statement is not created.");
        }
        String sql = "DROP SCHEMA IF EXISTS " + schemaName + " CASCADE";
        createStatement().executeUpdate(sql);
    }

    public void useSchema() throws SQLException {
        if (createStatement() == null || createStatement().isClosed()) {
            throw new SQLException("Statement is not created.");
        }
        String sql = "SET SCHEMA '" + schemaName + "'";
        createStatement().executeUpdate(sql);
    }

    public void createTableRoles() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS Roles ("
                + "id SERIAL PRIMARY KEY, "
                + "roleName VARCHAR(50) NOT NULL, "
                + "CONSTRAINT unique_role_name UNIQUE (roleName)"
                + ");";
        createStatement().executeUpdate(sql);
    }



    public void createTableDirections() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS Directions ("
                + "id SERIAL PRIMARY KEY, "
                + "directionName VARCHAR(50) NOT NULL, "
                + "CONSTRAINT unique_direction_name UNIQUE (directionName)"
                + ");";
        createStatement().executeUpdate(sql);
    }

    public void createTableProjects() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS Projects ("
                + "id SERIAL PRIMARY KEY, "
                + "projectName VARCHAR(100) NOT NULL, "
                + "directionId INT NOT NULL REFERENCES Directions(id), "
                + "CONSTRAINT unique_project_name UNIQUE (projectName)"
                + ");";
        createStatement().executeUpdate(sql);
    }

    public void createTableEmployee() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS Employee ("
                + "id SERIAL PRIMARY KEY, "
                + "firstName VARCHAR(50) NOT NULL, "
                + "roleId INT NOT NULL REFERENCES Roles(id), "
                + "projectId INT NOT NULL REFERENCES Projects(id), "
                + "CONSTRAINT unique_employee_name UNIQUE (firstName)"
                + ");";
        createStatement().executeUpdate(sql);
    }

    public void dropTable(String tableName) throws SQLException {
        String sql = "DROP TABLE IF EXISTS " + tableName + " CASCADE";
        createStatement().executeUpdate(sql);
    }

    public void insertTableRoles(String roleName) throws SQLException {
        String sql = "INSERT INTO Roles (roleName) VALUES (?) ON CONFLICT (roleName) DO NOTHING";
        try (PreparedStatement preparedStatement = createConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, roleName);
            preparedStatement.executeUpdate();
        }
    }

    public void insertTableDirections(String directionName) throws SQLException {
        String sql = "INSERT INTO Directions (directionName) VALUES (?) ON CONFLICT (directionName) DO NOTHING";
        try (PreparedStatement preparedStatement = createConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, directionName);
            preparedStatement.executeUpdate();
        }
    }

    public void insertTableProjects(String projectName, String directionName) throws SQLException {
        int directionId = getDirectionId(directionName); // Предполагается, что у вас есть метод для получения ID направления
        String sql = "INSERT INTO Projects (projectName, directionId) VALUES (?, ?) " +
                "ON CONFLICT (projectName) DO UPDATE SET directionId = EXCLUDED.directionId";
        try (PreparedStatement preparedStatement = createConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, projectName);
            preparedStatement.setInt(2, directionId);
            preparedStatement.executeUpdate();
        }
    }

    public void insertTableEmployee(String firstName, String roleName, String projectName) throws SQLException {
        int roleId = getRoleId(roleName);
        int projectId = getProjectId(projectName);
        String sql = "INSERT INTO Employee (firstName, roleId, projectId) VALUES (?, ?, ?) " +
                "ON CONFLICT (firstName) DO UPDATE SET roleId = EXCLUDED.roleId, projectId = EXCLUDED.projectId";
        try (Connection connection = createConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // Устанавливаем параметры в подготовленный запрос
            preparedStatement.setString(1, firstName);
            preparedStatement.setInt(2, roleId);
            preparedStatement.setInt(3, projectId);

            // Выполняем запрос
            preparedStatement.executeUpdate();
        }
    }

    public int getRoleId(String roleName) throws SQLException {
        String sql = "SELECT id FROM Roles WHERE roleName = ?";
        try (PreparedStatement preparedStatement = createConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, roleName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
            throw new SQLException("Role not found");
        }
    }

    public int getDirectionId(String directionName) throws SQLException {
        String sql = "SELECT id FROM Directions WHERE directionName = ?";
        try (PreparedStatement preparedStatement = createConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, directionName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
            throw new SQLException("Direction not found");
        }
    }

    public int getProjectId(String projectName) throws SQLException {
        String sql = "SELECT id FROM Projects WHERE projectName = ?";
        try (PreparedStatement preparedStatement = createConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, projectName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
            throw new SQLException("Project not found");
        }
    }

    public int getEmployeeId(String firstName) throws SQLException {
        String sql = "SELECT id FROM Employee WHERE firstName = ?";
        try (PreparedStatement preparedStatement = createConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, firstName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
            throw new SQLException("Employee not found");
        }
    }

    public List<String> getAllRoles() throws SQLException {
        String sql = "SELECT roleName FROM Roles";
        try (Statement statement = createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            List<String> roles = new ArrayList<>();
            while (resultSet.next()) {
                roles.add(resultSet.getString("roleName"));
            }
            return roles;
        }
    }

    public List<String> getAllDirestion() throws SQLException {
        String sql = "SELECT directionName FROM Directions";
        try (Statement statement = createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            List<String> directions = new ArrayList<>();
            while (resultSet.next()) {
                directions.add(resultSet.getString("directionName"));
            }
            return directions;
        }
    }

    public List<String> getAllProjects() throws SQLException {
        String sql = "SELECT projectName FROM Projects";
        try (Statement statement = createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            List<String> projects = new ArrayList<>();
            while (resultSet.next()) {
                projects.add(resultSet.getString("projectName"));
            }
            return projects;
        }
    }

    public List<String> getAllEmployee() throws SQLException {
        String sql = "SELECT firstName FROM Employee";
        try (Statement statement = createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            List<String> employees = new ArrayList<>();
            while (resultSet.next()) {
                employees.add(resultSet.getString("firstName"));
            }
            return employees;
        }
    }

    public List<String> getAllDevelopers() throws SQLException {
        String sql = "SELECT firstName FROM Employee WHERE roleId = (SELECT id FROM Roles WHERE roleName = 'Developer')";
        try (Statement statement = createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            List<String> developers = new ArrayList<>();
            while (resultSet.next()) {
                developers.add(resultSet.getString("firstName"));
            }
            return developers;
        }
    }

    public List<String> getAllJavaProjects() throws SQLException {
        String sql = "SELECT projectName FROM Projects WHERE directionId = (SELECT id FROM Directions WHERE directionName = 'Java')";
        try (Statement statement = createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            List<String> javaProjects = new ArrayList<>();
            while (resultSet.next()) {
                javaProjects.add(resultSet.getString("projectName"));
            }
            return javaProjects;
        }
    }

    public List<String> getAllJavaDevelopers() throws SQLException {
        String sql = "SELECT firstName FROM Employee WHERE roleId = (SELECT id FROM Roles WHERE roleName = 'Developer') AND projectId IN (SELECT id FROM Projects WHERE directionId = (SELECT id FROM Directions WHERE directionName = 'Java'))";
        try (Statement statement = createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            List<String> javaDevelopers = new ArrayList<>();
            while (resultSet.next()) {
                javaDevelopers.add(resultSet.getString("firstName"));
            }
            return javaDevelopers;
        }
    }
}


class DatabaseSetup {

    MyUtils myUtils = new MyUtils();

    public static void main(String[] args) {
        MyUtils myUtils = new MyUtils();

        try {
            // Создаем соединение с базой данных
            myUtils.createConnection();

            // Создаем схему и таблицы
            String schemaName = "your_schema_name";  // Замените на желаемое имя схемы
            myUtils.createSchema(schemaName);
            myUtils.useSchema();

            myUtils.createTableRoles();
            myUtils.createTableDirections();
            myUtils.createTableProjects();
            myUtils.createTableEmployee();

            System.out.println("Schema and tables created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                // Закрываем соединение и statement
                myUtils.closeStatement();
                myUtils.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}



