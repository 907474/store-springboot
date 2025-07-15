package com.myapp.aw.store.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DATABASE_URL = "jdbc:sqlite:store.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Fatal Error: SQLite JDBC driver not found.");
            throw new RuntimeException("Failed to load SQLite JDBC driver", e);
        }
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    public static void initializeDatabase() {
        String createUserTable = "CREATE TABLE IF NOT EXISTS users (" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " username TEXT NOT NULL UNIQUE," +
                " password TEXT," +
                " role TEXT NOT NULL" +
                ");";

        String createProductTable = "CREATE TABLE IF NOT EXISTS products (" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " sku TEXT NOT NULL UNIQUE," +
                " name TEXT NOT NULL," +
                " price REAL NOT NULL," +
                " stock INTEGER NOT NULL" +
                ");";

        String createOrdersTable = "CREATE TABLE IF NOT EXISTS orders (" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " user_id INTEGER NOT NULL," +
                " total_price REAL NOT NULL," +
                " order_started_at TEXT," + // NEW COLUMN
                " created_at TEXT," +
                " confirmed_at TEXT," +
                " status TEXT NOT NULL," +
                " FOREIGN KEY (user_id) REFERENCES users(id)" +
                ");";

        String createOrderItemsTable = "CREATE TABLE IF NOT EXISTS order_items (" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " order_id INTEGER NOT NULL," +
                " product_id INTEGER NOT NULL," +
                " quantity INTEGER NOT NULL," +
                " price_at_purchase REAL NOT NULL," +
                " product_name TEXT NOT NULL," +
                " FOREIGN KEY (order_id) REFERENCES orders(id)" +
                ");";

        String createCustomersTable = "CREATE TABLE IF NOT EXISTS customers (" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " total_spent REAL NOT NULL DEFAULT 0," +
                " amount_of_orders INTEGER NOT NULL DEFAULT 0" +
                ");";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createUserTable);
            stmt.execute(createProductTable);
            stmt.execute(createOrdersTable);
            stmt.execute(createOrderItemsTable);
            stmt.execute(createCustomersTable);
        } catch (SQLException e) {
            System.err.println("Database schema initialization failed: " + e.getMessage());
        }
    }
}