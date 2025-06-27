package com.myapp.store;

import com.myapp.aw.store.database.DatabaseManager;
import com.myapp.aw.store.model.Order;
import com.myapp.aw.store.model.Product;
import com.myapp.aw.store.model.Role;
import com.myapp.aw.store.model.User;
import com.myapp.aw.store.repository.OrderRepository;
import com.myapp.aw.store.repository.ProductRepository;
import com.myapp.aw.store.repository.UserRepository;
import com.myapp.aw.store.service.OrderService;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class StoreApplication {

    private static ProductRepository productRepository;
    private static UserRepository userRepository;
    private static OrderService orderService;
    private static Scanner scanner;

    public static void main(String[] args) {
        System.out.println("--- Store Management System Initializing ---");

        DatabaseManager.initializeDatabase();

        productRepository = new ProductRepository();
        userRepository = new UserRepository();
        OrderRepository orderRepository = new OrderRepository();
        orderService = new OrderService(orderRepository, productRepository);
        scanner = new Scanner(System.in);

        try {
            setupInitialData();
        } catch (Exception e) {
            System.err.println("Error during initial data setup: " + e.getMessage());
        }

        runMainLoop();

        scanner.close();
        System.out.println("\n--- Store Management System Shutdown ---");
    }

    private static void runMainLoop() {
        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        handleListProducts();
                        break;
                    case "2":
                        handleCreateUser();
                        break;
                    case "3":
                        handleCreateOrder();
                        break;
                    case "4":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                // Catch any unexpected exceptions during handling to prevent crash
                System.err.println("\nAn error occurred: " + e.getMessage());
            }
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for user to press Enter
        }
    }


    private static void printMenu() {
        System.out.println("\n--- Store Management UI ---");
        System.out.println("1. List all products");
        System.out.println("2. Create a new user");
        System.out.println("3. Place a new order");
        System.out.println("4. Exit");
        System.out.println("---------------------------");
    }


    private static void handleListProducts() throws Exception {
        System.out.println("\n--- Current Product Inventory ---");
        productRepository.findAll().forEach(System.out::println);
    }


    private static void handleCreateUser() throws Exception {
        System.out.println("\n--- Create New User ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter role (ADMIN or EMPLOYEE): ");
        String roleStr = scanner.nextLine().toUpperCase();
        Role role = Role.valueOf(roleStr);

        if (userRepository.findByUsername(username).isPresent()) {
            System.out.println("Error: User with this username already exists.");
            return;
        }

        User newUser = new User(username, password, role);
        userRepository.save(newUser);
        System.out.println("SUCCESS: User '" + username + "' created successfully!");
    }


    private static void handleCreateOrder() throws Exception {
        System.out.println("\n--- Create New Order ---");
        System.out.print("Enter the username of the customer placing the order: ");
        String username = scanner.nextLine();

        User customer = userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("Customer with username '" + username + "' not found."));

        Map<Long, Integer> itemsToOrder = new HashMap<>();

        while (true) {
            System.out.println("\n--- Add item to order ---");
            handleListProducts();
            System.out.print("Enter Product ID to add (or type 'done' to finish): ");
            String input = scanner.nextLine();
            if ("done".equalsIgnoreCase(input)) {
                break;
            }

            try {
                long productId = Long.parseLong(input);
                System.out.print("Enter quantity for product " + productId + ": ");
                int quantity = Integer.parseInt(scanner.nextLine());

                if(quantity <= 0) {
                    System.out.println("Quantity must be positive.");
                    continue;
                }

                // Add to cart, overwriting previous quantity if product is re-added
                itemsToOrder.put(productId, quantity);
                System.out.println("Added " + quantity + " of product " + productId + " to the order.");

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid Product ID or 'done'.");
            }
        }

        if (itemsToOrder.isEmpty()) {
            System.out.println("Order cancelled because no items were added.");
            return;
        }

        System.out.println("\nPlacing order with the following items:");
        System.out.println(itemsToOrder);

        // Call the service to create the order
        Order createdOrder = orderService.createOrder(customer.getId(), itemsToOrder);
        System.out.println("SUCCESS: Order created successfully!");
        System.out.print(createdOrder);
    }


    private static void setupInitialData() throws Exception {
        System.out.println("\n[SETUP] Checking for initial data...");
        // FIX: Replaced .isEmpty() with !.isPresent() for broader Java version compatibility.
        if (!userRepository.findByUsername("admin").isPresent()) {
            userRepository.save(new User("admin", "admin123", Role.ADMIN));
            System.out.println("Created default 'admin' user.");
        }
        if (productRepository.findAll().isEmpty()) {
            productRepository.save(new Product("Laptop Pro", 1200.50, 50));
            productRepository.save(new Product("Wireless Mouse", 25.00, 200));
            productRepository.save(new Product("Mechanical Keyboard", 89.99, 120));
            System.out.println("Populated initial product inventory.");
        }
    }
}
