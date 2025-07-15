package com.myapp.aw.store.service;

import com.myapp.aw.store.model.Customer;
import com.myapp.aw.store.model.Product;
import com.myapp.aw.store.model.TemporaryOrder;
import com.myapp.aw.store.model.TemporaryProductItem;
import com.myapp.aw.store.repository.CustomerRepository;
import com.myapp.aw.store.repository.ProductRepository;
import com.myapp.aw.store.repository.TemporaryOrderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CartService {

    private final TemporaryOrderRepository temporaryOrderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public record CartDisplayInfo(TemporaryOrder order, Map<Long, Product> productMap, boolean isCartEmpty) {}

    public CartService(
            TemporaryOrderRepository temporaryOrderRepository,
            ProductRepository productRepository,
            CustomerRepository customerRepository
    ) {
        this.temporaryOrderRepository = temporaryOrderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public TemporaryOrder addItemToCart(Long temporaryOrderId, Long productId, int quantity) {
        TemporaryOrder order = findOrderById(temporaryOrderId);
        Product product = findProductById(productId);

        Optional<TemporaryProductItem> existingItemOpt = order.getProductItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            TemporaryProductItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            TemporaryProductItem newItem = new TemporaryProductItem();
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            newItem.setPriceAtPurchase(product.getProductPrice());
            order.addProductItem(newItem);
        }
        return recalculateTotalAndSave(order);
    }

    @Transactional
    public TemporaryOrder createCartForUser(Long customerId, Long productId, int quantity) {
        TemporaryOrder temporaryOrder = new TemporaryOrder();
        temporaryOrder.setCustomerId(customerId);
        temporaryOrder.setOrderCreationTime(LocalDateTime.now());

        return addItemToCart(temporaryOrder, productId, quantity);
    }

    @Transactional
    public TemporaryOrder createGuestCart(Long productId, int quantity, Long customerId) {
        TemporaryOrder temporaryOrder = new TemporaryOrder();
        temporaryOrder.setCustomerId(customerId != null ? customerId : 0L);
        temporaryOrder.setOrderCreationTime(LocalDateTime.now());

        return addItemToCart(temporaryOrder, productId, quantity);
    }

    @Transactional
    public void updateItemQuantity(Long temporaryOrderId, Long productId, int newQuantity) {
        TemporaryOrder order = findOrderById(temporaryOrderId);
        if (newQuantity <= 0) {
            order.getProductItems().removeIf(item -> item.getProductId().equals(productId));
        } else {
            order.getProductItems().stream()
                    .filter(item -> item.getProductId().equals(productId))
                    .findFirst()
                    .ifPresent(item -> item.setQuantity(newQuantity));
        }
        recalculateTotalAndSave(order);
    }

    @Transactional
    public void removeItemFromCart(Long temporaryOrderId, Long productId) {
        TemporaryOrder order = findOrderById(temporaryOrderId);
        order.getProductItems().removeIf(item -> item.getProductId().equals(productId));
        recalculateTotalAndSave(order);
    }

    public CartDisplayInfo getCartForDisplay(HttpSession session) {
        Long temporaryOrderId = (Long) session.getAttribute("temporaryOrderId");
        if (temporaryOrderId == null) {
            return new CartDisplayInfo(null, null, true);
        }
        Optional<TemporaryOrder> optOrder = temporaryOrderRepository.findById(temporaryOrderId);
        if (optOrder.isEmpty() || optOrder.get().getProductItems().isEmpty()) {
            return new CartDisplayInfo(null, null, true);
        }
        TemporaryOrder order = optOrder.get();
        Map<Long, Product> productMap = new HashMap<>();
        List<Long> productIds = order.getProductItems().stream().map(TemporaryProductItem::getProductId).toList();
        productRepository.findAllById(productIds).forEach(p -> productMap.put(p.getProductId(), p));
        return new CartDisplayInfo(order, productMap, false);
    }

    public Long getTemporaryOrderId(HttpSession session, Authentication authentication) {
        Long temporaryOrderId = (Long) session.getAttribute("temporaryOrderId");
        if (temporaryOrderId != null) {
            return temporaryOrderId;
        }
        if (authentication != null && authentication.isAuthenticated()) {
            Customer customer = customerRepository.findByUsername(authentication.getName()).orElse(null);
            // Here you could also check if the customer has an existing temporary order in the DB
            // For now, we'll create a new one if nothing is in the session.
            return null;
        }
        return null;
    }

    private TemporaryOrder recalculateTotalAndSave(TemporaryOrder order) {
        BigDecimal total = order.getProductItems().stream()
                .map(item -> item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(total);
        return temporaryOrderRepository.save(order);
    }

    private TemporaryOrder addItemToCart(TemporaryOrder order, Long productId, int quantity) {
        Product product = findProductById(productId);
        updateOrAddItem(order, product, quantity);
        return recalculateTotalAndSave(order);
    }

    private void updateOrAddItem(TemporaryOrder order, Product product, int quantity) {
        // This is a private helper, logic is the same as before
        Optional<TemporaryProductItem> existingItemOpt = order.getProductItems().stream()
                .filter(item -> item.getProductId().equals(product.getProductId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            TemporaryProductItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            TemporaryProductItem newItem = new TemporaryProductItem();
            newItem.setProductId(product.getProductId());
            newItem.setQuantity(quantity);
            newItem.setPriceAtPurchase(product.getProductPrice());
            order.addProductItem(newItem);
        }
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    private TemporaryOrder findOrderById(Long orderId) {
        return temporaryOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
    }
}