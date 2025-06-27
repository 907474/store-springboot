package com.myapp.aw.store.repository;

import com.myapp.aw.store.database.DatabaseManager;
import com.myapp.aw.store.model.Order;
import com.myapp.aw.store.model.OrderItem;

import java.sql.*;
import java.time.format.DateTimeFormatter;

public class OrderRepository {

    public Order save(Order order) throws SQLException {
        String insertOrderSQL = "INSERT INTO orders(user_id, total_price, order_date, status) VALUES(?,?,?,?)";
        String insertOrderItemSQL = "INSERT INTO order_items(order_id, product_id, quantity, price_at_purchase, product_name) VALUES(?,?,?,?,?)";

        Connection conn = null;
        try {
            conn = DatabaseManager.connect();
            // Critical for data integrity: perform operations as a single transaction.
            conn.setAutoCommit(false);

            // 1. Insert the parent 'orders' record
            try (PreparedStatement orderPstmt = conn.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS)) {
                orderPstmt.setLong(1, order.getUserId());
                orderPstmt.setDouble(2, order.getTotalPrice());
                orderPstmt.setString(3, order.getOrderDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                orderPstmt.setString(4, order.getStatus());
                orderPstmt.executeUpdate();

                ResultSet generatedKeys = orderPstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    order.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }

            // 2. Insert all child 'order_items' records
            try (PreparedStatement itemPstmt = conn.prepareStatement(insertOrderItemSQL)) {
                for (OrderItem item : order.getItems()) {
                    itemPstmt.setLong(1, order.getId());
                    itemPstmt.setLong(2, item.getProductId());
                    itemPstmt.setInt(3, item.getQuantity());
                    itemPstmt.setDouble(4, item.getPriceAtPurchase());
                    itemPstmt.setString(5, item.getProductName());
                    itemPstmt.addBatch(); // Add statement to batch for efficiency
                }
                itemPstmt.executeBatch(); // Execute all statements in the batch
            }

            conn.commit(); // If all steps succeed, commit the transaction
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // If any step fails, undo all changes
            }
            throw e; // Re-throw the exception to let the service layer know
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); // Restore default behavior
                conn.close();
            }
        }
        return order;
    }
}
