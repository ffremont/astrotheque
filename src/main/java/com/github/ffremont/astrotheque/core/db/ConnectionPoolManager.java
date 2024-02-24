package com.github.ffremont.astrotheque.core.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionPoolManager {
    private static final String JDBC_URL = STR."jdbc:sqlite:\{Optional.ofNullable(System.getenv("DB_FILE")).orElse("dbs/astrotheque.db")}";
    private static final int MAX_POOL_SIZE = 10;

    private static final BlockingQueue<Connection> connectionPool = new ArrayBlockingQueue<>(MAX_POOL_SIZE);

    static {
        try {
            for (int i = 0; i < MAX_POOL_SIZE; i++) {
                Connection connection = DriverManager.getConnection(JDBC_URL);
                connectionPool.offer(connection);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing connection pool", e);
        }
    }

    public Connection getConnection() {
        try {
            return connectionPool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public void releaseConnection(Connection connection) {
        connectionPool.offer(connection);
    }
}

