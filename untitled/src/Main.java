import java.util.*;

public class Main {
    public static void main(String[] args) {

        System.out.println("--- Advanced String Operations ---");
        String longSentence = "The quick brown fox jumps over the lazy dog. This is a demo sentence.";
        System.out.println("Original Sentence: " + longSentence);

        // Loop through characters and count vowels
        int vowelCount = 0;
        char[] charsInSentence = longSentence.toLowerCase().toCharArray();
        for (char c : charsInSentence) {
            if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') {
                vowelCount++;
            }
        }
        System.out.println("Vowel Count: " + vowelCount);

        // Splitting and rejoining with a different delimiter based on a condition
        String[] words = longSentence.split(" ");
        StringBuilder modifiedSentence = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (words[i].length() > 3 && !words[i].contains(".")) {
                modifiedSentence.append(words[i].toUpperCase());
            } else {
                modifiedSentence.append(words[i].toLowerCase());
            }
            if (i < words.length - 1) {
                modifiedSentence.append("-"); // Rejoin with hyphens
            }
        }
        System.out.println("Modified Sentence (hyphenated, conditional case): " + modifiedSentence.toString());

        // Using while loop for substring extraction with a condition
        String searchString = "demo";
        int startIndex = longSentence.indexOf(searchString);
        System.out.println("Index of '" + searchString + "': " + startIndex);
        while (startIndex != -1) {
            int endIndex = startIndex + searchString.length();
            if (endIndex <= longSentence.length()) {
                System.out.println("Found '" + searchString + "' at: " + longSentence.substring(startIndex, endIndex));
            }
            startIndex = longSentence.indexOf(searchString, startIndex + 1); // Search for next occurrence
        }

        System.out.println("\n--- Complex Array Manipulations ---");
        int[][] matrix = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };

        System.out.println("Matrix Contents (nested for loop):");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.println();
        }

        // Searching for an element with a while loop and breaking
        int target = 5;
        boolean found = false;
        int row = 0;
        while (row < matrix.length && !found) {
            int col = 0;
            while (col < matrix[row].length && !found) {
                if (matrix[row][col] == target) {
                    System.out.println("Target " + target + " found at [" + row + "][" + col + "]");
                    found = true;
                }
                col++;
            }
            row++;
        }

        // Conditional array element modification
        int[] numbers = {10, 25, 30, 45, 50, 65, 70, 85, 90};
        System.out.print("Original Numbers: ");
        for (int num : numbers) {
            System.out.print(num + " ");
        }
        System.out.println();

        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i] % 2 == 0) { // If even
                numbers[i] *= 2; // Double it
            } else if (numbers[i] % 5 == 0) { // If odd and divisible by 5
                numbers[i] += 10; // Add 10
            } else {
                numbers[i] -= 1; // Subtract 1
            }
        }
        System.out.print("Modified Numbers: ");
        for (int num : numbers) {
            System.out.print(num + " ");
        }
        System.out.println();

        System.out.println("\n--- In-Depth Collection Operations ---");

        // List of custom objects with conditional processing
        List<Product> products = new ArrayList<>();
        products.add(new Product("Laptop", 1200.00, 5));
        products.add(new Product("Mouse", 25.00, 20));
        products.add(new Product("Keyboard", 75.00, 10));
        products.add(new Product("Monitor", 300.00, 3));
        products.add(new Product("Webcam", 50.00, 0)); // Out of stock

        System.out.println("Initial Product List:");
        for (Product p : products) {
            System.out.println(p);
        }

        // Removing items based on a condition using an iterator (to avoid ConcurrentModificationException)
        Iterator<Product> productIterator = products.iterator();
        while (productIterator.hasNext()) {
            Product p = productIterator.next();
            if (p.getQuantity() == 0) {
                System.out.println("Removing out of stock product: " + p.getName());
                productIterator.remove();
            } else if (p.getPrice() > 100) {
                p.applyDiscount(0.10); // Apply 10% discount to expensive items
                System.out.println("Applied discount to " + p.getName() + ". New price: " + p.getPrice());
            }
        }
        System.out.println("\nProduct List after processing:");
        for (Product p : products) {
            System.out.println(p);
        }

        // Set operations with nested loops and conditional additions
        Set<Integer> primeNumbers = new HashSet<>(Arrays.asList(2, 3, 5, 7, 11, 13, 17, 19));
        Set<Integer> evenNumbers = new HashSet<>(Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20));
        Set<Integer> uniqueNumbers = new HashSet<>();

        System.out.println("\nPrime Numbers: " + primeNumbers);
        System.out.println("Even Numbers: " + evenNumbers);

        // Add numbers that are either prime or even, but not both (XOR like behavior)
        for (int p : primeNumbers) {
            if (!evenNumbers.contains(p)) {
                uniqueNumbers.add(p);
            }
        }
        for (int e : evenNumbers) {
            if (!primeNumbers.contains(e)) {
                uniqueNumbers.add(e);
            }
        }
        System.out.println("Numbers unique to either prime or even set: " + uniqueNumbers);

        // Map with nested conditional logic for updating values
        Map<String, Integer> inventory = new HashMap<>();
        inventory.put("Apples", 100);
        inventory.put("Oranges", 50);
        inventory.put("Bananas", 75);
        inventory.put("Grapes", 120);

        System.out.println("\nInitial Inventory: " + inventory);

        // Simulate sales and restocking
        String[] dailyTransactions = {"Apples: -10", "Oranges: -5", "Bananas: +20", "Grapes: -15", "Pears: +30"};

        for (String transaction : dailyTransactions) {
            String[] parts = transaction.split(": ");
            String itemName = parts[0];
            int quantityChange = Integer.parseInt(parts[1]);

            if (inventory.containsKey(itemName)) {
                int currentQuantity = inventory.get(itemName);
                int newQuantity = currentQuantity + quantityChange;
                if (newQuantity < 0) {
                    System.out.println("Warning: Not enough " + itemName + " to complete transaction. Current: " + currentQuantity + ", Attempted change: " + quantityChange);
                    inventory.put(itemName, 0); // Set to 0 if goes negative
                } else {
                    inventory.put(itemName, newQuantity);
                    System.out.println("Updated " + itemName + ": " + newQuantity);
                }
            } else {
                if (quantityChange > 0) {
                    inventory.put(itemName, quantityChange);
                    System.out.println("Added new item " + itemName + " with quantity: " + quantityChange);
                } else {
                    System.out.println("Item " + itemName + " not found in inventory for removal.");
                }
            }
        }
        System.out.println("Final Inventory: " + inventory);
    }
}

// Simple Product class for demonstration
class Product {
    private String name;
    private double price;
    private int quantity;

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void applyDiscount(double percentage) {
        this.price *= (1 - percentage);
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price=" + String.format("%.2f", price) +
                ", quantity=" + quantity +
                '}';
    }
}