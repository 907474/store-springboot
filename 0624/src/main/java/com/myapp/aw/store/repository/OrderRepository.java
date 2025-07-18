package com.myapp.aw.store.repository;

import com.myapp.aw.store.database.DatabaseManager;
import com.myapp.aw.store.model.Order;
import com.myapp.aw.store.model.OrderItem;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    public Order save(Order order) throws SQLException {
        String insertOrderSQL = "INSERT INTO orders(user_id, total_price, created_at, confirmed_at, status) VALUES(?,?,?,?,?)";
        String insertOrderItemSQL = "INSERT INTO order_items(order_id, product_id, quantity, price_at_purchase, product_name) VALUES(?,?,?,?,?)";

        Connection conn = null;
        try {
            conn = DatabaseManager.connect();
            conn.setAutoCommit(false);

            try (PreparedStatement orderPstmt = conn.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS)) {
                orderPstmt.setLong(1, order.getUserId());
                orderPstmt.setDouble(2, order.getTotalPrice());
                // Assuming created_at and confirmed_at are non-null for new orders
                orderPstmt.setString(3, order.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                orderPstmt.setString(4, order.getConfirmedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                orderPstmt.setString(5, order.getStatus());
                orderPstmt.executeUpdate();

                ResultSet generatedKeys = orderPstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    order.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }

            try (PreparedStatement itemPstmt = conn.prepareStatement(insertOrderItemSQL)) {
                for (OrderItem item : order.getItems()) {
                    itemPstmt.setLong(1, order.getId());
                    itemPstmt.setLong(2, item.getProductId());
                    itemPstmt.setInt(3, item.getQuantity());
                    itemPstmt.setDouble(4, item.getPriceAtPurchase());
                    itemPstmt.setString(5, item.getProductName());
                    itemPstmt.addBatch();
                }
                itemPstmt.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
        return order;
    }

    public List<Order> findAll() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY confirmed_at DESC";
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Order order = new Order(
                        rs.getLong("id"),
                        rs.getLong("user_id"),
                        rs.getDouble("total_price"),
                        // ADD THIS LINE
                        rs.getString("order_started_at") != null ? LocalDateTime.parse(rs.getString("order_started_at")) : null,
                        rs.getString("created_at") != null ? LocalDateTime.parse(rs.getString("created_at")) : null,
                        rs.getString("confirmed_at") != null ? LocalDateTime.parse(rs.getString("confirmed_at")) : null,
                        rs.getString("status")
                );
                order.setItems(findItemsByOrderId(order.getId()));
                orders.add(order);
            }
        }
        return orders;
    }


    private List<OrderItem> findItemsByOrderId(long orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                items.add(new OrderItem(
                        rs.getLong("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price_at_purchase")
                ));
            }
        }
        return items;
    }

    public List<OrderItem> findAllItems() throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items";
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                items.add(new OrderItem(
                        rs.getLong("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price_at_purchase")
                ));
            }
        }
        return items;
    }
    /**
     * NEW METHOD
     * Finds all orders created within a specific date and time range.
     * This method is required by the StatisticsService for time-based reporting.
     */
    public List<Order> findAllInDateRange(String startDateTime, String endDateTime) throws SQLException {
        List<Order> orders = new ArrayList<>();
        // Assumes 'created_at' is stored in a TEXT field in ISO_LOCAL_DATE_TIME format
        String sql = "SELECT * FROM orders WHERE created_at BETWEEN ? AND ? ORDER BY confirmed_at DESC";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, startDateTime);
            pstmt.setString(2, endDateTime);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Order order = new Order(
                        rs.getLong("id"),
                        rs.getLong("user_id"),
                        rs.getDouble("total_price"),
                        // ADD THIS LINE
                        rs.getString("order_started_at") != null ? LocalDateTime.parse(rs.getString("order_started_at")) : null,
                        rs.getString("created_at") != null ? LocalDateTime.parse(rs.getString("created_at")) : null,
                        rs.getString("confirmed_at") != null ? LocalDateTime.parse(rs.getString("confirmed_at")) : null,
                        rs.getString("status")
                );
                order.setItems(findItemsByOrderId(order.getId()));
                orders.add(order);
            }
        }
        return orders;
    }
}