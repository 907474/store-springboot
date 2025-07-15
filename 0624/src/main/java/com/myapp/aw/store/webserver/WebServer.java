package com.myapp.aw.store.webserver;

import com.myapp.aw.store.repository.CustomerRepository;
import com.myapp.aw.store.repository.OrderRepository;
import com.myapp.aw.store.repository.ProductRepository;
import com.myapp.aw.store.repository.UserRepository;
import com.myapp.aw.store.service.OrderService;
import com.myapp.aw.store.service.ProductService;
import com.myapp.aw.store.service.StatisticsService;
import com.myapp.aw.store.webserver.handlers.*;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class WebServer {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderService orderService;
    private final ProductService productService;
    private final SessionManager sessionManager;
    private final StatisticsService statisticsService;

    public WebServer(ProductRepository pr, UserRepository ur, OrderRepository or, CustomerRepository cr, OrderService os, ProductService ps, StatisticsService ss, SessionManager sm) {
        this.productRepository = pr;
        this.userRepository = ur;
        this.orderRepository = or;
        this.customerRepository = cr;
        this.orderService = os;
        this.productService = ps;
        this.sessionManager = sm;
        this.statisticsService = ss;
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // --- Register all page handlers ---
        server.createContext("/", new RootHandler(this.sessionManager));
        server.createContext("/admin-login", new AdminLoginHandler());
        server.createContext("/admin-dashboard", new AdminDashboardHandler(this.sessionManager));
        server.createContext("/users", new HtmlUsersHandler(this.userRepository, this.sessionManager));
        server.createContext("/orders", new HtmlOrdersHandler(this.orderRepository, this.sessionManager));

        // UPDATED: Now passes sessionManager to the handler.
        server.createContext("/restock", new HtmlRestockHandler(this.productRepository, this.sessionManager));

        server.createContext("/order-form", new HtmlOrderFormHandler(this.productRepository));
        server.createContext("/checkout", new HtmlCheckoutHandler(this.productRepository));
        server.createContext("/order-confirmed", new OrderConfirmedHandler());
        server.createContext("/statistics", new HtmlStatisticsHandler(this.statisticsService));

        // --- Register all API handlers ---
        server.createContext("/api/login", new LoginHandler(this.userRepository, this.sessionManager));
        server.createContext("/api/orders/add", new AddOrderHandler(this.orderService, this.userRepository, this.customerRepository));

        // This is your existing handler for single-item restocks. It remains untouched.
        server.createContext("/api/products/restock", new RestockHandler(this.productService));

        // NEW: This is the new handler for updating all stock levels from the /restock page.
        server.createContext("/api/products/batch-restock", new BatchRestockApiHandler(this.productService));

        server.createContext("/logout", new LogoutHandler(this.sessionManager));

        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8080. Press Ctrl+C to stop.");
    }
}