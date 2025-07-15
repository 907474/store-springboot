package com.myapp.aw.store.webserver.handlers;

import com.myapp.aw.store.model.Role;
import com.myapp.aw.store.model.User;
import com.myapp.aw.store.webserver.SessionManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.Optional;

public class RootHandler implements HttpHandler {
    private final SessionManager sessionManager;

    public RootHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Optional<User> userOpt = sessionManager.getSessionUser(exchange);

        // Check if an admin or employee is already logged in
        if (userOpt.isPresent() && (userOpt.get().getRole() == Role.ADMIN || userOpt.get().getRole() == Role.EMPLOYEE)) {
            // If so, redirect them directly to their dashboard
            HandlerUtils.redirect(exchange, "/admin-dashboard");
        } else {
            // Otherwise, show the default customer-facing homepage
            String response = generateCustomerHomepage();
            HandlerUtils.sendResponse(exchange, 200, response, "text/html");
        }
    }

    private String generateCustomerHomepage() {
        return "<html><head><title>Welcome</title><style>" +
                "body { font-family: sans-serif; text-align: center; padding-top: 5em; background-color: #f8f9fa; }" +
                "h1 { color: #333; }" +
                "p { color: #555; }" +
                ".order-btn { display: inline-block; background-color: #28a745; color: white; padding: 20px 40px; margin: 20px; border-radius: 5px; text-decoration: none; font-size: 1.5em; }" +
                ".order-btn:hover { background-color: #218838; }" +
                ".admin-login-link { position: absolute; top: 15px; right: 15px; font-size: 0.9em; }" +
                "</style></head><body>" +
                "<a href='/admin-login' class='admin-login-link'>Admin Login</a>" +
                "<h1>Welcome to Our Store!</h1>" +
                "<p>Ready to shop? Click the button below to start your order.</p>" +
                "<a href='/order-form' class='order-btn'>Place an Order</a>" +
                "</body></html>";
    }
}
