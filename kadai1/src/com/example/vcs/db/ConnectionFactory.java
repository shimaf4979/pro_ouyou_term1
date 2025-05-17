package com.example.vcs.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private static final String URL = "jdbc:sqlite:versioncontrol.db";

    private ConnectionFactory() { }

    public static Connection getConnection() throws SQLException {
        try {
            // SQLite JDBC ドライバを明示的にロード
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC ドライバが見つかりません。lib/sqlite-jdbc-3.30.1.jar をクラスパスに含めてください。", e);
        }
        return DriverManager.getConnection(URL);
    }
}