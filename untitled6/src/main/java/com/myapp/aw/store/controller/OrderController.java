package com.myapp.aw.store.controller;

import com.myapp.aw.store.model.OrderArchive;
import com.myapp.aw.store.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/order/place")
    public String placeOrder(HttpSession session) {
        Long temporaryOrderId = (Long) session.getAttribute("temporaryOrderId");

        if (temporaryOrderId == null) {
            // Can't place an order without a cart
            return "redirect:/cart";
        }

        OrderArchive finalOrder = orderService.finalizeOrder(temporaryOrderId);

        // Clear the cart from the session after successful checkout
        session.removeAttribute("temporaryOrderId");

        // Redirect to a confirmation page with the new permanent order ID
        return "redirect:/order/confirmed?id=" + finalOrder.getOrderId();
    }

    @GetMapping("/order/confirmed")
    public String orderConfirmed(@RequestParam Long id, Model model) {
        model.addAttribute("orderId", id);
        return "order-confirmed";
    }
}