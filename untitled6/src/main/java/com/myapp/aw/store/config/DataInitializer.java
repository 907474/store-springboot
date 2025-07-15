package com.myapp.aw.store.config;

import com.myapp.aw.store.model.*;
import com.myapp.aw.store.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class DataInitializer {

    private record SampleProduct(String name, String type, double price) {}

    @Bean
    CommandLineRunner initDatabase(
            ProductRepository productRepository,
            AdminUserRepository adminUserRepository,
            CustomerRepository customerRepository,
            TemporaryOrderRepository temporaryOrderRepository,
            OrderArchiveRepository orderArchiveRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // --- Step 1: Create Admins ---
            if (adminUserRepository.count() == 0) {
                System.out.println("Creating admin users...");
                AdminUser adminT3 = new AdminUser();
                adminT3.setUsername("admin");
                adminT3.setPassword(passwordEncoder.encode("admin"));
                adminT3.setRole("ROLE_ADMIN_T3");
                adminT3.setStatus(UserStatus.ENABLED);
                adminUserRepository.save(adminT3);

                AdminUser adminT2 = new AdminUser();
                adminT2.setUsername("t2admin");
                adminT2.setPassword(passwordEncoder.encode("t2admin"));
                adminT2.setRole("ROLE_ADMIN_T2");
                adminT2.setStatus(UserStatus.ENABLED);
                adminUserRepository.save(adminT2);

                AdminUser adminT1 = new AdminUser();
                adminT1.setUsername("t1admin");
                adminT1.setPassword(passwordEncoder.encode("t1admin"));
                adminT1.setRole("ROLE_ADMIN_T1");
                adminT1.setStatus(UserStatus.ENABLED);
                adminUserRepository.save(adminT1);
            }

            // --- Step 2: Create Customers ---
            List<Customer> customers = new ArrayList<>();
            if (customerRepository.count() == 0) {
                System.out.println("Creating default customers...");
                for (int i = 1; i <= 10; i++) {
                    Customer customer = new Customer();
                    customer.setUsername("customer" + i);
                    customer.setPassword(passwordEncoder.encode("customer" + i));
                    customer.setRole("ROLE_CUSTOMER");
                    customer.setStatus(UserStatus.ENABLED);
                    customers.add(customer);
                }
                customers = customerRepository.saveAll(customers);
            } else {
                customers = customerRepository.findAll();
            }

            // --- Step 3: Create Products ---
            List<Product> products; // Declare the list here
            if (productRepository.count() == 0) {
                System.out.println("Creating 100 sample products...");
                List<SampleProduct> sampleProducts = List.of(
                        new SampleProduct("Milk", "Groceries", 3.99),
                        new SampleProduct("Eggs", "Groceries", 2.99),
                        new SampleProduct("Bread", "Groceries", 2.49),
                        new SampleProduct("Butter", "Groceries", 4.50),
                        new SampleProduct("Cheddar Cheese", "Groceries", 5.25),
                        new SampleProduct("Yogurt", "Groceries", 1.25),
                        new SampleProduct("Chicken Breast", "Groceries", 9.99),
                        new SampleProduct("Ground Beef", "Groceries", 7.50),
                        new SampleProduct("Bacon", "Groceries", 6.50),
                        new SampleProduct("Salmon Fillet", "Groceries", 12.99),
                        new SampleProduct("Tofu", "Groceries", 2.80),
                        new SampleProduct("Apples", "Groceries", 3.10),
                        new SampleProduct("Bananas", "Groceries", 1.50),
                        new SampleProduct("Strawberries", "Groceries", 4.20),
                        new SampleProduct("Blueberries", "Groceries", 4.80),
                        new SampleProduct("Oranges", "Groceries", 2.50),
                        new SampleProduct("Grapes", "Groceries", 5.00),
                        new SampleProduct("Lettuce", "Groceries", 2.00),
                        new SampleProduct("Tomatoes", "Groceries", 2.50),
                        new SampleProduct("Carrots", "Groceries", 1.99),
                        new SampleProduct("Broccoli", "Groceries", 2.10),
                        new SampleProduct("Onions", "Groceries", 1.80),
                        new SampleProduct("Potatoes", "Groceries", 4.00),
                        new SampleProduct("Avocado", "Groceries", 1.75),
                        new SampleProduct("Cucumber", "Groceries", 0.99),
                        new SampleProduct("Bell Pepper", "Groceries", 1.10),
                        new SampleProduct("Lemon", "Groceries", 0.75),
                        new SampleProduct("Sourdough Bread", "Groceries", 4.49),
                        new SampleProduct("Baguette", "Groceries", 3.49),
                        new SampleProduct("White Rice", "Groceries", 3.80),
                        new SampleProduct("Spaghetti", "Groceries", 1.99),
                        new SampleProduct("Marinara Sauce", "Groceries", 2.20),
                        new SampleProduct("Black Beans", "Groceries", 1.19),
                        new SampleProduct("Canned Tuna", "Groceries", 1.50),
                        new SampleProduct("Olive Oil", "Groceries", 8.99),
                        new SampleProduct("Flour", "Groceries", 4.20),
                        new SampleProduct("Sugar", "Groceries", 3.80),
                        new SampleProduct("Peanut Butter", "Groceries", 3.50),
                        new SampleProduct("Strawberry Jam", "Groceries", 3.20),
                        new SampleProduct("Cereal", "Groceries", 3.75),
                        new SampleProduct("Oatmeal", "Groceries", 4.10),
                        new SampleProduct("Ground Coffee", "Groceries", 8.99),
                        new SampleProduct("Tea Bags", "Groceries", 3.50),
                        new SampleProduct("Potato Chips", "Groceries", 4.25),
                        new SampleProduct("Cookies", "Groceries", 3.80),
                        new SampleProduct("Almonds", "Groceries", 9.50),
                        new SampleProduct("Honey", "Groceries", 5.50),
                        new SampleProduct("Orange Juice", "Groceries", 4.50),
                        new SampleProduct("Apple Juice", "Groceries", 3.99),
                        new SampleProduct("Toilet Paper", "Household", 12.00),
                        new SampleProduct("Paper Towels", "Household", 9.50),
                        new SampleProduct("Trash Bags", "Household", 7.80),
                        new SampleProduct("Dish Soap", "Household", 3.25),
                        new SampleProduct("Laundry Detergent", "Household", 14.00),
                        new SampleProduct("All-Purpose Cleaner", "Household", 4.50),
                        new SampleProduct("Sponges", "Household", 2.50),
                        new SampleProduct("Aluminum Foil", "Household", 3.80),
                        new SampleProduct("Light Bulbs", "Household", 6.99),
                        new SampleProduct("AA Batteries", "Household", 7.50),
                        new SampleProduct("Tissues", "Household", 2.20),
                        new SampleProduct("Hand Soap", "Household", 1.99),
                        new SampleProduct("Bar Soap", "Personal Care", 2.80),
                        new SampleProduct("Shampoo", "Personal Care", 5.99),
                        new SampleProduct("Conditioner", "Personal Care", 5.99),
                        new SampleProduct("Toothpaste", "Personal Care", 3.10),
                        new SampleProduct("Toothbrush", "Personal Care", 2.50),
                        new SampleProduct("Deodorant", "Personal Care", 4.80),
                        new SampleProduct("Hand Sanitizer", "Personal Care", 2.99),
                        new SampleProduct("Sunscreen", "Personal Care", 9.99),
                        new SampleProduct("Pain Reliever", "Personal Care", 6.50),
                        new SampleProduct("Bandages", "Personal Care", 3.20),
                        new SampleProduct("Cotton Balls", "Personal Care", 2.10),
                        new SampleProduct("Face Wash", "Personal Care", 7.50),
                        new SampleProduct("Gaming Laptop", "Electronics", 1499.99),
                        new SampleProduct("4K Monitor", "Electronics", 349.00),
                        new SampleProduct("Wireless Earbuds", "Electronics", 129.00),
                        new SampleProduct("Smartwatch", "Electronics", 299.00),
                        new SampleProduct("Portable Charger", "Electronics", 25.50),
                        new SampleProduct("USB-C Cable", "Electronics", 12.99),
                        new SampleProduct("Tablet", "Electronics", 450.00),
                        new SampleProduct("E-Reader", "Electronics", 130.00),
                        new SampleProduct("Bluetooth Speaker", "Electronics", 65.00),
                        new SampleProduct("Smart Home Hub", "Electronics", 99.00),
                        new SampleProduct("White T-Shirt", "Apparel", 20.00),
                        new SampleProduct("Socks", "Apparel", 15.00),
                        new SampleProduct("Blue Jeans", "Apparel", 59.99),
                        new SampleProduct("Hoodie", "Apparel", 45.00),
                        new SampleProduct("Baseball Cap", "Apparel", 18.00),
                        new SampleProduct("Printer Paper", "Office", 7.99),
                        new SampleProduct("Pens", "Office", 5.50),
                        new SampleProduct("Notebook", "Office", 2.50),
                        new SampleProduct("Sticky Notes", "Office", 3.00),
                        new SampleProduct("Stapler", "Office", 8.00)
                );

                // Use a new list inside this block to avoid the error
                List<Product> productsToSave = new ArrayList<>();
                AtomicInteger skuCounter = new AtomicInteger(1);

                sampleProducts.forEach(p -> {
                    Product product = new Product();
                    product.setProductName(p.name());
                    product.setProductSku("SKU-" + String.format("%04d", skuCounter.getAndIncrement()));
                    product.setProductPrice(BigDecimal.valueOf(p.price()));
                    product.setProductQuantity( (int) (Math.random() * 91) + 10);
                    product.setType(p.type());
                    product.setStatus(ProductStatus.DISPLAY);
                    productsToSave.add(product);
                });

                // Save the new list and assign it to the main 'products' list
                products = productRepository.saveAll(productsToSave);
                System.out.println(productsToSave.size() + " sample products created.");
            } else {
                products = productRepository.findAll();
            }

            Random random = new Random();

            // --- Step 4: Create Temporary Orders (Carts) ---
            if (temporaryOrderRepository.count() == 0 && !products.isEmpty() && !customers.isEmpty()) {
                System.out.println("Creating 10 temporary orders (carts)...");
                for (int i = 0; i < 10; i++) {
                    TemporaryOrder tempOrder = new TemporaryOrder();
                    tempOrder.setCustomerId((i % 3 == 0) ? 0L : customers.get(random.nextInt(customers.size())).getId());
                    tempOrder.setStatus(OrderStatus.IN_PROGRESS);

                    int itemsInCart = random.nextInt(3) + 1;
                    for (int j = 0; j < itemsInCart; j++) {
                        Product randomProduct = products.get(random.nextInt(products.size()));
                        TemporaryProductItem item = new TemporaryProductItem();
                        item.setProductId(randomProduct.getProductId());
                        item.setPriceAtPurchase(randomProduct.getProductPrice());
                        item.setQuantity(random.nextInt(2) + 1);
                        tempOrder.addProductItem(item);
                    }
                    BigDecimal total = tempOrder.getProductItems().stream().map(item -> item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                    tempOrder.setTotalPrice(total);
                    temporaryOrderRepository.save(tempOrder);
                }
            }

            // --- Step 5: Create Archived Orders (Finished) ---
            if (orderArchiveRepository.count() == 0 && !products.isEmpty() && !customers.isEmpty()) {
                System.out.println("Creating 10 archived orders...");
                for (int i = 0; i < 10; i++) {
                    OrderArchive archive = new OrderArchive();
                    archive.setCustomerId((i % 4 == 0) ? 0L : customers.get(random.nextInt(customers.size())).getId());
                    archive.setOrderPlacementTime(LocalDateTime.now().minusDays(random.nextInt(30)));

                    int itemsInOrder = random.nextInt(4) + 1;
                    for (int j = 0; j < itemsInOrder; j++) {
                        Product randomProduct = products.get(random.nextInt(products.size()));
                        ArchivedProductItem item = new ArchivedProductItem();
                        item.setProductId(randomProduct.getProductId());
                        item.setPriceAtPurchase(randomProduct.getProductPrice());
                        item.setQuantity(random.nextInt(2) + 1);
                        archive.addProductItem(item);
                    }
                    BigDecimal total = archive.getProductItems().stream().map(item -> item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                    archive.setTotalPrice(total);
                    orderArchiveRepository.save(archive);
                }
            }
        };
    }
}