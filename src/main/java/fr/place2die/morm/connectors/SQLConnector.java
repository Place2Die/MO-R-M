package fr.place2die.morm.connectors;

import fr.place2die.morm.connectors.exceptions.ConnectionIsClosedException;
import fr.place2die.morm.connectors.exceptions.URLMalformedException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLConnector extends AbstractConnector {

    protected final String schema;
    protected Connection connection;

    public SQLConnector(String user, String host, short port) {
        this(user, host, port, "mysql");
    }

    public SQLConnector(String user, String host, short port, String schema) {
        super(user, host, port);
        this.schema = schema;
    }

    public static SQLConnector fromURL(String url) throws URLMalformedException, SQLException {
        Pattern pattern = Pattern.compile("jdbc:(?<driver>\\w+)://(?>(?<user>\\w+):(?<password>\\w+)@)?(?<host>(?>[a-z0-9\\-.]+)|(?>\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})):(?<port>\\d{1,5})(?<schema>/\\w+)?");
        Matcher matcher = pattern.matcher(url);
        if (matcher.matches()) {
            return getSqlConnector(matcher);
        } else {
            throw new URLMalformedException("The URL is malformed");
        }
    }

    public static SQLConnector fromEnvironment() throws URLMalformedException, SQLException {
        String url = System.getenv("DATABASE_URL");
        if (url != null) {
            return fromURL(url);
        } else {
            throw new URLMalformedException("The URL is malformed");
        }
    }

    private static SQLConnector getSqlConnector(Matcher matcher) throws SQLException {
        String host = matcher.group("host");
        short port = Short.parseShort(matcher.group("port"));
        String schema = matcher.group("schema");
        String user = matcher.group("user");
        String password = matcher.group("password");

        schema = schema == null ? "mysql" : schema.substring(1);
        user = user == null ? "root" : user;
        password = password == null ? "" : password;
        SQLConnector connector = new SQLConnector(user, host, port, schema);
        if (!password.isEmpty()) {
            connector.connect(password);
        }
        return connector;
    }

    @Override
    public void connect(String password) throws SQLException {
        if (isConnected()) {
            connection.close();
        }
        connection = DriverManager.getConnection("jdbc:" + schema + "://" + host + ":" + port, user, password);
    }

    @Override
    public void disconnect() throws SQLException, ConnectionIsClosedException {
        if (isConnected()) {
            connection.close();
        } else {
            throw new ConnectionIsClosedException();
        }
    }

    @Override
    public boolean isConnected() {
        return connection != null;
    }

    @Override
    public void createTable(String tableName) throws ConnectionIsClosedException {
        String query = "CREATE TABLE " + tableName + " (id INT PRIMARY KEY AUTO_INCREMENT)";
        execute(query);
    }

    @Override
    public void dropTable(String tableName) throws ConnectionIsClosedException {
        String query = "DROP TABLE " + tableName;
        execute(query);
    }

    @Override
    public void insert(String tableName, String[] columns, Object[] values) throws ConnectionIsClosedException {
        String query = "INSERT INTO " + tableName + " (" + String.join(", ", columns) + ") VALUES (" + String.join(", ", Arrays.stream(values).map(Object::toString).toArray(String[]::new)) + ")";
        execute(query);
    }

    @Override
    public void update(String tableName, String[] columns, Object[] values, String condition) throws ConnectionIsClosedException {
        if (columns.length != values.length) {
            throw new IllegalArgumentException("The number of columns and values must be the same");
        }
        String[] set = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            set[i] = columns[i] + " = " + values[i].toString();
        }
        String query = "UPDATE " + tableName + " SET " + String.join(", ", set) + " WHERE " + condition;
        execute(query);
    }

    @Override
    public void delete(String tableName, String condition) throws ConnectionIsClosedException {
        String query = "DELETE FROM " + tableName + " WHERE " + condition;
        execute(query);
    }

    @Override
    public void select(String tableName, String[] columns, String condition) throws ConnectionIsClosedException {
        String query = "SELECT " + String.join(", ", columns) + " FROM " + tableName + " WHERE " + condition;
        execute(query);
    }

    @Override
    public void selectAll(String tableName) throws ConnectionIsClosedException {
        String query = "SELECT * FROM " + tableName;
        execute(query);
    }

    @Override
    public void execute(String query) throws ConnectionIsClosedException {
        if (!isConnected()) {
            throw new ConnectionIsClosedException();
        }
        try {
            connection.createStatement().execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        this.connection = connection;
    }

    public String getSchema() {
        return schema;
    }
}
