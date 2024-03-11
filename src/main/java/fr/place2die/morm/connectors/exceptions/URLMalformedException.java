package fr.place2die.morm.connectors.exceptions;

public class URLMalformedException extends ConnectorException {
    public URLMalformedException(String message) {
        super(message);
    }

    public URLMalformedException(String message, Throwable cause) {
        super(message, cause);
    }

    public URLMalformedException(Throwable cause) {
        super("URL is malformed", cause);
    }

    public URLMalformedException() {
        super("URL is malformed");
    }
}
