package db.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DBConnectionManager {
    private static DBConnectionManager instance;
    private Properties props;
    private static BlockingQueue<Connection> pool;

    private static final String URL_KEY = "url";
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static final String DRIVER_KEY = "driver";
    private static final String POOL_SIZE_KEY = "pool.size";
    private static final int DEFAULT_POOL_SIZE = 3;

    DBConnectionManager() {
        propertyInitialization();
        initConnectionPool();
    }

    private void initConnectionPool() {
        String poolSize = props.getProperty(POOL_SIZE_KEY);
        int size = poolSize == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolSize);

        pool = new ArrayBlockingQueue<>(size);

        for (int i = 0; i < size; i++) {
            pool.add(connect());
        }
    }

    public Connection get() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized DBConnectionManager getInstance() {
        if (instance == null)
            instance = new DBConnectionManager();
        return instance;
    }

    private void propertyInitialization() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            props = new Properties();
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection connect() {
        try {
            Class.forName(props.getProperty(DRIVER_KEY));
            return DriverManager.getConnection(
                    props.getProperty(URL_KEY),
                    props.getProperty(USERNAME_KEY),
                    props.getProperty(PASSWORD_KEY)
            );
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    pool.offer(connection);
                } else {
                    pool.add(connect());
                }
            } catch (SQLException e) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    throw new RuntimeException(e);
                }
                pool.add(connect());
            }
        }
    }

}
