package com.sms.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Central place for creating JDBC connections. Settings come from db.properties. */
public final class DBConnection {

    private static final String CONFIG_FILE = "db.properties";
    private static Properties props;

    private DBConnection() { }

    private static synchronized Properties config() throws SQLException {
        if (props == null) {
            Properties p = new Properties();
            try (InputStream in = new FileInputStream(CONFIG_FILE)) {
                p.load(in);
            } catch (IOException e) {
                throw new SQLException("Cannot read " + CONFIG_FILE
                    + " from the current folder. Run the app from the project folder. (" + e.getMessage() + ")");
            }
            props = p;
        }
        return props;
    }

    public static Connection getConnection() throws SQLException {
        Properties p = config();
        try {
            return DriverManager.getConnection(
                p.getProperty("db.url"), p.getProperty("db.user"), p.getProperty("db.password"));
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("No suitable driver")) {
                throw new SQLException("MySQL JDBC driver not found. Put mysql-connector-j-*.jar in the lib/ "
                    + "folder and run with: java -cp \"out:lib/*\" com.sms.Main", e);
            }
            throw e;
        }
    }
}
