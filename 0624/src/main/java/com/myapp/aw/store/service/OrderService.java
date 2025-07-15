package com.myapp.aw.store.service;

import com.myapp.aw.store.model.Order;
import com.myapp.aw.store.model.OrderItem;
import com.myapp.aw.store.model.Product;
import com.myapp.aw.store.repository.OrderRepository;
import com.myapp.aw.store.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    /**
     * Creates an order, checks stock, and updates stock levels.
     * @param userId The ID of the user placing the order.
     * @param productQuantities A map of Product ID to quantity desired.
     * @param orderStartedAt The timestamp when the cart was first initiated.
     * @return The created Order.
     * @throws Exception if stock is insufficient or a product doesn't exist.
     */
    public Order createOrder(long userId, Map<Long, Integer> productQuantities, LocalDateTime orderStartedAt) throws Exception {
        List<OrderItem> orderItems = new ArrayList<>();

        // 1. Validate stock and create OrderItems
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            long productId = entry.getKey();
            int quantity = entry.getValue();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new Exception("Product with ID " + productId + " not found."));

            if (product.getStock() < quantity) {
                throw new Exception("Insufficient stock for product '" + product.getName() +
                        "'. Requested: " + quantity + ", Available: " + product.getStock());
            }
            orderItems.add(new OrderItem(product, quantity));
        }

        // 2. All products have enough stock, now create the order.
        // This passes the start time to the Order constructor.
        // The constructor should handle setting the completion time (createdAt) to now().
        Order newOrder = new Order(userId, orderItems, orderStartedAt);

        // 3. Save the order to the database (this is transactional)
        orderRepository.save(newOrder);

        // 4. Update the stock for each product
        for (OrderItem item : newOrder.getItems()) {
            Product product = productRepository.findById(item.getProductId()).get(); // We know it exists
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        return newOrder;
    }
}