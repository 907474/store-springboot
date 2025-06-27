package com.myapp.aw.store.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages the connection to the SQLite database and initializes the schema.
 * This class corresponds to the DatabaseManager in the UML diagram.
 */
public class DatabaseManager {
    private static final String DATABASE_URL = "jdbc:sqlite:store.db";

    // Static initializer block to load the JDBC driver.
    // This runs once when the class is loaded into memory.
    static {
        try {
            // FIX: Explicitly load the SQLite JDBC driver class.
            // This ensures the driver is registered with DriverManager.
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            // This error is critical, as no database operations can proceed.
            System.err.println("Fatal Error: SQLite JDBC driver not found.");
            throw new RuntimeException("Failed to load SQLite JDBC driver", e);
        }
    }

    /**
     * Establishes a connection to the database.
     * @return A Connection object.
     */
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    /**
     * Initializes the database by creating tables if they do not already exist,
     * based on the UML design.
     */
    public static void initializeDatabase() {
        // SQL for creating tables, derived directly from the UML models
        String createUserTable = "CREATE TABLE IF NOT EXISTS users (" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " username TEXT NOT NULL UNIQUE," +
                " password TEXT NOT NULL," +
                " role TEXT NOT NULL" +
                ");";

        String createProductTable = "CREATE TABLE IF NOT EXISTS products (" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " name TEXT NOT NULL UNIQUE," +
                " price REAL NOT NULL," +
                " stock INTEGER NOT NULL" +
                ");";

        String createOrdersTable = "CREATE TABLE IF NOT EXISTS orders (" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " user_id INTEGER NOT NULL," +
                " total_price REAL NOT NULL," +
                " order_date TEXT NOT NULL," +
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

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            // Execute all table creation statements
            stmt.execute(createUserTable);
            stmt.execute(createProductTable);
            stmt.execute(createOrdersTable);
            stmt.execute(createOrderItemsTable);
            System.out.println("[Database] Schema initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Database schema initialization failed: " + e.getMessage());
        }
    }
}
