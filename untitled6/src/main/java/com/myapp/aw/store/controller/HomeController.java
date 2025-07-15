package com.myapp.aw.store.controller;

import com.myapp.aw.store.model.Product;
import com.myapp.aw.store.model.ProductStatus;
import com.myapp.aw.store.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final ProductRepository productRepository;

    public HomeController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/")
    public String home(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(required = false) String keyword
    ) {
        loadProducts(model, page, size, keyword);
        return "index";
    }

    @GetMapping("/search-products")
    public String searchProducts(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(required = false) String keyword
    ) {
        loadProducts(model, page, size, keyword);
        return "index :: productGridContent";
    }

    private void loadProducts(Model model, int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage;

        if (keyword != null && !keyword.isEmpty()) {
            // Use the new method that searches both name and SKU
            productPage = productRepository.findByStatusAndNameOrSku(ProductStatus.DISPLAY, keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            productPage = productRepository.findByStatus(ProductStatus.DISPLAY, pageable);
        }
        model.addAttribute("productPage", productPage);
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable("id") Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        return "product-detail";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "registered", required = false) String registered, Model model) {
        if (registered != null) {
            model.addAttribute("registrationSuccess", "Registration successful! Please log in.");
        }
        return "login";
    }
}