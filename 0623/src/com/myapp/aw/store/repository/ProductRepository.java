package com.myapp.aw.store.repository;

import com.myapp.aw.store.database.DatabaseManager;
import com.myapp.aw.store.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ProductRepository {

    public Product save(Product product) throws SQLException {
        // Use UPSERT logic for SQLite: Insert or replace on conflict.
        String sql = "INSERT INTO products (id, name, price, stock) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT(id) DO UPDATE SET name=excluded.name, price=excluded.price, stock=excluded.stock";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // If ID is 0, it's a new product, let the DB assign it. Otherwise, use existing ID.
            if (product.getId() == 0) {
                pstmt.setNull(1, Types.INTEGER);
            } else {
                pstmt.setLong(1, product.getId());
            }
            pstmt.setString(2, product.getName());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setInt(4, product.getStock());
            pstmt.executeUpdate();

            // Get the ID if it was newly generated
            if (product.getId() == 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getLong(1));
                }
            }
        }
        return product;
    }

    public Optional<Product> findById(long id) throws SQLException {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Product product = new Product(rs.getString("name"), rs.getDouble("price"), rs.getInt("stock"));
                product.setId(rs.getLong("id"));
                return Optional.of(product);
            }
        }
        return Optional.empty();
    }

    public List<Product> findAll() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY id";
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Product product = new Product(rs.getString("name"), rs.getDouble("price"), rs.getInt("stock"));
                product.setId(rs.getLong("id"));
                products.add(product);
            }
        }
        return products;
    }
}
