package com.inventaris.core.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DatabaseConnection {
    private static String URL;
    private static String USER;
    private static String PASSWORD;
    private static String DRIVER;
    private static Connection connection;

    static {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(".env")) {
            props.load(in);
            URL = props.getProperty("DB_URL", "jdbc:mysql://localhost:3306/db_inventaris");
            USER = props.getProperty("DB_USER", "root");
            PASSWORD = props.getProperty("DB_PASSWORD", "");
            DRIVER = props.getProperty("DB_DRIVER", "com.mysql.cj.jdbc.Driver");
        } catch (IOException e) {
            System.err.println("Gagal membaca file .env, menggunakan konfigurasi default. " + e.getMessage());
            URL = "jdbc:mysql://localhost:3306/db_inventaris";
            USER = "root";
            PASSWORD = "";
            DRIVER = "com.mysql.cj.jdbc.Driver";
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName(DRIVER);
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver JDBC tidak ditemukan: " + DRIVER, e);
            }
        }
        return connection;
    }
}
