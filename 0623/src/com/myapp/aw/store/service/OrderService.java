package com.myapp.aw.store.service;

import com.myapp.aw.store.model.Order;
import com.myapp.aw.store.model.OrderItem;
import com.myapp.aw.store.model.Product;
import com.myapp.aw.store.repository.OrderRepository;
import com.myapp.aw.store.repository.ProductRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handles business logic for "订单管理" (Order Management).
 * Corresponds to the OrderService class in the UML diagram.
 */
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    /**
     * Creates an order, which includes validating stock and updating inventory.
     * This is a transactional process handled by the service.
     */
    public Order createOrder(long userId, Map<Long, Integer> productQuantities) throws Exception {
        List<OrderItem> orderItems = new ArrayList<>();

        // Step 1: Validate stock and build the list of order items
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            long productId = entry.getKey();
            int quantity = entry.getValue();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new Exception("Validation failed: Product with ID " + productId + " not found."));

            if (product.getStock() < quantity) {
                throw new Exception("Validation failed: Insufficient stock for '" + product.getName() +
                        "'. Requested: " + quantity + ", Available: " + product.getStock());
            }
            orderItems.add(new OrderItem(product, quantity));
        }

        // Step 2: Create the Order object
        Order newOrder = new Order(userId, orderItems);

        // Step 3: Save the order to the database (this is transactional)
        orderRepository.save(newOrder);

        // Step 4: If order saving was successful, update the stock for each product
        for (OrderItem item : newOrder.getItems()) {
            // Safely retrieve the product again to prevent errors and ensure data consistency.
            Product productToUpdate = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalStateException("Data consistency error: Product with ID " + item.getProductId() + " was not found after validation."));

            productToUpdate.setStock(productToUpdate.getStock() - item.getQuantity());
            productRepository.save(productToUpdate);
        }

        return newOrder;
    }
}
