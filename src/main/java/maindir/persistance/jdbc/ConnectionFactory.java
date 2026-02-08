package maindir.persistance.jdbc;


import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionFactory {
    private static Connection connection;
    private static final Logger LOGGER = Logger.getLogger(ConnectionFactory.class.getName());
    private ConnectionFactory() {
    }

    static {
        try {
            InputStream input = ConnectionFactory.class.getResourceAsStream("/db.properties");
            if (input == null) {
                LOGGER.log(Level.SEVERE, "File properties del db non trovato");
            }

            Properties properties = new Properties();
            properties.load(input);
            input.close(); // Chiudi manualmente l'InputStream

            String connectionUrl = properties.getProperty("CONNECTION_URL");
            String user = properties.getProperty("LOGIN_USER");
            String pass = properties.getProperty("LOGIN_PASS");

            connection = DriverManager.getConnection(connectionUrl, user, pass);


        } catch (IOException | SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore nella connessione al db");
        }
    }


    public static Connection getConnection(){
        return connection;
    }
}

