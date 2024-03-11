package fr.place2die.morm.connectors;

import fr.place2die.morm.connectors.exceptions.ConnectionIsClosedException;

public interface IConnector {
    void connect(String password) throws Exception;

    void disconnect() throws ConnectionIsClosedException, Exception;

    void createTable(String tableName) throws ConnectionIsClosedException;

    void dropTable(String tableName) throws ConnectionIsClosedException;

    void insert(String tableName, String[] columns, Object[] values) throws ConnectionIsClosedException;

    void update(String tableName, String[] columns, Object[] values, String condition) throws ConnectionIsClosedException;

    void delete(String tableName, String condition) throws ConnectionIsClosedException;

    void select(String tableName, String[] columns, String condition) throws ConnectionIsClosedException;

    void selectAll(String tableName) throws ConnectionIsClosedException;

    void execute(String query) throws ConnectionIsClosedException;

    boolean isConnected();
}
