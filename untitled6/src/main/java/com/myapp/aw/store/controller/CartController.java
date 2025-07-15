package com.myapp.aw.store.controller;

import com.myapp.aw.store.model.Customer;
import com.myapp.aw.store.model.OrderStatus;
import com.myapp.aw.store.model.Product;
import com.myapp.aw.store.model.TemporaryOrder;
import com.myapp.aw.store.model.TemporaryProductItem;
import com.myapp.aw.store.repository.CustomerRepository;
import com.myapp.aw.store.repository.ProductRepository;
import com.myapp.aw.store.repository.TemporaryOrderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class CartController {

    private final ProductRepository productRepository;
    private final TemporaryOrderRepository temporaryOrderRepository;
    private final CustomerRepository customerRepository;

    public CartController(
            ProductRepository productRepository,
            TemporaryOrderRepository temporaryOrderRepository,
            CustomerRepository customerRepository
    ) {
        this.productRepository = productRepository;
        this.temporaryOrderRepository = temporaryOrderRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        Long temporaryOrderId = (Long) session.getAttribute("temporaryOrderId");
        if (temporaryOrderId == null) {
            model.addAttribute("isCartEmpty", true);
            return "cart";
        }
        Optional<TemporaryOrder> temporaryOrderOpt = temporaryOrderRepository.findById(temporaryOrderId);
        if (temporaryOrderOpt.isEmpty() || temporaryOrderOpt.get().getProductItems().isEmpty()) {
            model.addAttribute("isCartEmpty", true);
            return "cart";
        }
        TemporaryOrder temporaryOrder = temporaryOrderOpt.get();
        Map<Long, Product> productMap = new HashMap<>();
        List<Long> productIds = temporaryOrder.getProductItems().stream().map(TemporaryProductItem::getProductId).toList();
        productRepository.findAllById(productIds).forEach(p -> productMap.put(p.getProductId(), p));
        model.addAttribute("order", temporaryOrder);
        model.addAttribute("productMap", productMap);
        model.addAttribute("isCartEmpty", false);
        return "cart";
    }

    @PostMapping("/cart")
    public String recoverCart(@RequestParam Long cartId, HttpSession session) {
        // Find an order only if the ID is valid AND its status is IN_PROGRESS
        Optional<TemporaryOrder> orderToRecover = temporaryOrderRepository.findByOrderIdAndStatus(cartId, OrderStatus.IN_PROGRESS);

        if (orderToRecover.isPresent()) {
            session.setAttribute("temporaryOrderId", cartId);
            return "redirect:/cart";
        } else {
            // Fail if the ID is invalid OR the order is already finished
            return "redirect:/cart?recover_error=true";
        }
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId, @RequestParam int quantity, HttpSession session, Authentication authentication) {
        Long temporaryOrderId = (Long) session.getAttribute("temporaryOrderId");
        TemporaryOrder temporaryOrder;

        if (temporaryOrderId == null) {
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                Customer customer = customerRepository.findByUsername(username)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Logged in customer not found"));
                temporaryOrder = new TemporaryOrder();
                temporaryOrder.setCustomerId(customer.getId());
                temporaryOrder.setStatus(OrderStatus.IN_PROGRESS);
            } else {
                return "redirect:/cart/associate?productId=" + productId + "&quantity=" + quantity;
            }
        } else {
            temporaryOrder = temporaryOrderRepository.findById(temporaryOrderId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        updateOrAddItem(temporaryOrder, product, quantity);
        TemporaryOrder savedOrder = recalculateTotalAndSave(temporaryOrder);
        session.setAttribute("temporaryOrderId", savedOrder.getOrderId());
        return "redirect:/cart";
    }

    @GetMapping("/cart/associate")
    public String showAssociatePage(@RequestParam Long productId, @RequestParam int quantity, Model model) {
        model.addAttribute("productId", productId);
        model.addAttribute("quantity", quantity);
        return "associate-customer";
    }

    @PostMapping("/cart/create")
    public String createCart(@RequestParam Long productId, @RequestParam int quantity, @RequestParam(required = false) Long customerId, HttpSession session) {
        TemporaryOrder temporaryOrder = new TemporaryOrder();
        temporaryOrder.setCustomerId(customerId != null ? customerId : 0L);
        temporaryOrder.setStatus(OrderStatus.IN_PROGRESS);
        temporaryOrder.setOrderCreationTime(LocalDateTime.now());
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        updateOrAddItem(temporaryOrder, product, quantity);
        TemporaryOrder savedOrder = recalculateTotalAndSave(temporaryOrder);
        session.setAttribute("temporaryOrderId", savedOrder.getOrderId());
        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    public String updateCart(@RequestParam Long productId, @RequestParam int quantity, HttpSession session) {
        Long temporaryOrderId = (Long) session.getAttribute("temporaryOrderId");
        if (temporaryOrderId != null) {
            TemporaryOrder order = temporaryOrderRepository.findById(temporaryOrderId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
            if (quantity <= 0) {
                order.getProductItems().removeIf(item -> item.getProductId().equals(productId));
            } else {
                order.getProductItems().stream()
                        .filter(item -> item.getProductId().equals(productId))
                        .findFirst()
                        .ifPresent(item -> item.setQuantity(quantity));
            }
            recalculateTotalAndSave(order);
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long productId, HttpSession session) {
        Long temporaryOrderId = (Long) session.getAttribute("temporaryOrderId");
        if (temporaryOrderId != null) {
            TemporaryOrder order = temporaryOrderRepository.findById(temporaryOrderId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
            order.getProductItems().removeIf(item -> item.getProductId().equals(productId));
            recalculateTotalAndSave(order);
        }
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        Long temporaryOrderId = (Long) session.getAttribute("temporaryOrderId");
        if (temporaryOrderId == null) {
            return "redirect:/cart";
        }
        TemporaryOrder temporaryOrder = temporaryOrderRepository.findById(temporaryOrderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
        if (temporaryOrder.getProductItems().isEmpty()) {
            return "redirect:/cart";
        }
        Map<Long, Product> productMap = new HashMap<>();
        List<Long> productIds = temporaryOrder.getProductItems().stream().map(TemporaryProductItem::getProductId).toList();
        productRepository.findAllById(productIds).forEach(p -> productMap.put(p.getProductId(), p));
        model.addAttribute("order", temporaryOrder);
        model.addAttribute("productMap", productMap);
        return "checkout";
    }

    private void updateOrAddItem(TemporaryOrder order, Product product, int quantity) {
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

    private TemporaryOrder recalculateTotalAndSave(TemporaryOrder order) {
        BigDecimal total = order.getProductItems().stream()
                .map(item -> item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(total);
        return temporaryOrderRepository.save(order);
    }
}