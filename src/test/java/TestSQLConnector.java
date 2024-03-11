import fr.place2die.morm.connectors.SQLConnector;
import fr.place2die.morm.connectors.exceptions.URLMalformedException;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class TestSQLConnector {

    @Test
    public void testValidURL() {
        try {
            SQLConnector connector = SQLConnector.fromURL("jdbc:mysql://localhost:3306/test");
            assertEquals("localhost", connector.getHost());
            assertEquals(3306, connector.getPort());
            assertEquals("test", connector.getSchema());
        } catch (SQLException | URLMalformedException e) {
            fail();
        }
    }
}
