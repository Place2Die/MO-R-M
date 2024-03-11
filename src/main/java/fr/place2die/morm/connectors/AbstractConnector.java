package fr.place2die.morm.connectors;

public abstract class AbstractConnector implements IConnector {

    protected final String user;
    protected final String host;
    protected final short port;

    public AbstractConnector(String user, String host, short port) {
        this.user = user;
        this.host = host;
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public String getHost() {
        return host;
    }

    public short getPort() {
        return port;
    }
}
