package fr.place2die.morm.connectors.exceptions;

public class ConnectionIsClosedException extends ConnectorException {
    public ConnectionIsClosedException(String message) {
        super(message);
    }

    public ConnectionIsClosedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionIsClosedException(Throwable cause) {
        super("The connection is already closed", cause);
    }

    public ConnectionIsClosedException() {
        super("The connection is already closed");
    }
}
