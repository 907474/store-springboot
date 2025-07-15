package com.myapp.aw.store.controller;

import com.myapp.aw.store.model.OrderStatus;
import com.myapp.aw.store.model.ProductStatus;
import com.myapp.aw.store.model.dto.BulkUpdateResultDTO;
import com.myapp.aw.store.model.dto.RevenueReportDTO;
import com.myapp.aw.store.repository.AdminUserRepository;
import com.myapp.aw.store.repository.CustomerRepository;
import com.myapp.aw.store.repository.OrderArchiveRepository;
import com.myapp.aw.store.repository.ProductRepository;
import com.myapp.aw.store.repository.TemporaryOrderRepository;
import com.myapp.aw.store.service.AdminOrderService;
import com.myapp.aw.store.service.AdminUserService;
import com.myapp.aw.store.service.ProductService;
import com.myapp.aw.store.service.StatisticsService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final AdminUserService adminUserService;
    private final AdminUserRepository adminUserRepository;
    private final AdminOrderService adminOrderService;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final CustomerRepository customerRepository;
    private final StatisticsService statisticsService;
    private final OrderArchiveRepository orderArchiveRepository;
    private final TemporaryOrderRepository temporaryOrderRepository;

    public AdminController(
            AdminUserService adminUserService,
            AdminUserRepository adminUserRepository,
            AdminOrderService adminOrderService,
            ProductRepository productRepository,
            ProductService productService,
            CustomerRepository customerRepository,
            StatisticsService statisticsService,
            OrderArchiveRepository orderArchiveRepository,
            TemporaryOrderRepository temporaryOrderRepository
    ) {
        this.adminUserService = adminUserService;
        this.adminUserRepository = adminUserRepository;
        this.adminOrderService = adminOrderService;
        this.productRepository = productRepository;
        this.productService = productService;
        this.customerRepository = customerRepository;
        this.statisticsService = statisticsService;
        this.orderArchiveRepository = orderArchiveRepository;
        this.temporaryOrderRepository = temporaryOrderRepository;
    }

    @GetMapping("/admin/users")
    public String listAdminUsers(Model model) {
        log.info("GET /admin/users - Request received for admin user list page.");
        model.addAttribute("admins", adminUserRepository.findAll());
        return "admin-list";
    }

    @GetMapping("/admin/customers")
    public String listCustomers(Model model, @RequestParam(defaultValue = "0") int page) {
        log.info("GET /admin/customers - Request received for customer list page {}.", page);
        int pageSize = 15;
        model.addAttribute("customersPage", customerRepository.findAll(PageRequest.of(page, pageSize)));
        return "admin-customer-list";
    }

    @GetMapping("/admin/all-orders")
    public String showAllOrders(
            Model model,
            @RequestParam(defaultValue = "0") int unfinishedPage,
            @RequestParam(defaultValue = "0") int finishedPage
    ) {
        log.info("GET /admin/all-orders - Request received for all orders page.");
        int pageSize = 5;
        model.addAttribute("unfinishedOrdersPage",
                temporaryOrderRepository.findAllByStatus(OrderStatus.IN_PROGRESS, PageRequest.of(unfinishedPage, pageSize)));

        model.addAttribute("finishedOrdersPage",
                orderArchiveRepository.findAll(PageRequest.of(finishedPage, pageSize)));

        return "admin-all-orders";
    }

    @GetMapping("/admin/order-details/{displayId}")
    public String getOrderDetails(@PathVariable String displayId, Model model) {
        log.info("GET /admin/order-details/{} - Fetching details for order.", displayId);
        model.addAttribute("items", adminOrderService.getOrderDetails(displayId));
        return "admin-all-orders :: orderDetailsFragment";
    }

    @GetMapping("/admin/edit-products")
    public String showEditProductsPage(Model model, @RequestParam(defaultValue = "0") int page) {
        log.info("GET /admin/edit-products - Request received for edit products page {}.", page);
        int pageSize = 15;
        model.addAttribute("productsPage", productRepository.findAll(PageRequest.of(page, pageSize)));
        return "admin-edit-products";
    }

    @GetMapping("/admin/search-products")
    public String searchAdminProducts(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword
    ) {
        log.info("GET /admin/search-products - Live search for keyword: '{}', page: {}", keyword, page);
        int pageSize = 15;
        Pageable pageable = PageRequest.of(page, pageSize);
        if (keyword != null && !keyword.isEmpty()) {
            model.addAttribute("productsPage", productRepository.findByNameOrSkuContainingIgnoreCase(keyword, pageable));
        } else {
            model.addAttribute("productsPage", productRepository.findAll(pageable));
        }
        return "admin-edit-products :: productTable";
    }

    @GetMapping("/admin/statistics")
    public String showStatistics(Model model) {
        log.info("GET /admin/statistics - Request received for statistics page.");
        model.addAttribute("stats", statisticsService.getDashboardStats());
        return "admin-statistics";
    }

    @GetMapping("/admin/revenue")
    public String showRevenueReport(@RequestParam Optional<Integer> year, @RequestParam Optional<Integer> month, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> date, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> startDate, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> endDate, Model model) {
        log.info("GET /admin/revenue - Request received for revenue report.");
        LocalDate start, end;
        String reportTitle;
        if (startDate.isPresent() && endDate.isPresent()) {
            start = startDate.get();
            end = endDate.get();
            reportTitle = "Custom Range: " + start + " to " + end;
        } else if (date.isPresent()) {
            start = date.get();
            end = date.get();
            reportTitle = "Single Day: " + start;
        } else if (year.isPresent() && month.isPresent()) {
            YearMonth yearMonth = YearMonth.of(year.get(), month.get());
            start = yearMonth.atDay(1);
            end = yearMonth.atEndOfMonth();
            reportTitle = "Month: " + yearMonth;
        } else if (year.isPresent()) {
            start = LocalDate.of(year.get(), 1, 1);
            end = LocalDate.of(year.get(), 12, 31);
            reportTitle = "Year: " + year.get();
        } else {
            start = LocalDate.now().withDayOfYear(1);
            end = LocalDate.now();
            reportTitle = "Year to Date: " + start.getYear();
        }
        log.debug("Generating revenue report for period: {} to {}", start, end);
        RevenueReportDTO report = statisticsService.generateRevenueReport(start, end);
        model.addAttribute("report", report);
        model.addAttribute("reportTitle", reportTitle);
        return "admin-revenue";
    }

    @GetMapping("/admin/low-stock-products")
    public String showLowStockProducts(Model model) {
        log.info("GET /admin/low-stock-products - Request received for low stock products page.");
        model.addAttribute("lowStockProducts", productRepository.findByProductQuantityLessThan(10));
        return "admin-low-stock";
    }

    @GetMapping("/admin/top-products")
    public String showTopProducts(Model model, @RequestParam(defaultValue = "0") int page) {
        log.info("GET /admin/top-products - Request received for top products page {}.", page);
        model.addAttribute("productPage", statisticsService.getTopProducts(PageRequest.of(page, 15)));
        return "admin-top-products";
    }

    @GetMapping("/admin/top-customers")
    public String showTopCustomers(Model model, @RequestParam(defaultValue = "0") int page) {
        log.info("GET /admin/top-customers - Request received for top customers page {}.", page);
        Objects.requireNonNull(statisticsService, "StatisticsService is null in AdminController. Check constructor.");
        model.addAttribute("customerPage", statisticsService.getTopCustomers(PageRequest.of(page, 15)));
        return "admin-top-customers";
    }

    @GetMapping("/admin/customer-orders/{customerId}")
    public String getCustomerOrders(@PathVariable Long customerId, @RequestParam(defaultValue = "0") int page, Model model) {
        log.info("GET /admin/customer-orders/{} - Fetching order history for customer, page {}.", customerId, page);
        int pageSize = 5;
        model.addAttribute("ordersPage", orderArchiveRepository.findByCustomerId(customerId, PageRequest.of(page, pageSize)));
        model.addAttribute("customerId", customerId);
        return "admin-top-customers :: customerOrdersFragment";
    }

    @GetMapping("/admin/create-user")
    public String showCreateAdminForm() {
        log.info("GET /admin/create-user - Displaying form to create new admin.");
        return "create-admin";
    }

    @PostMapping("/admin/create-user")
    public String createAdminUser(@RequestParam String username, @RequestParam String password, @RequestParam String role, RedirectAttributes redirectAttributes) {
        log.info("POST /admin/create-user - Attempting to create admin user '{}' with role {}", username, role);
        try {
            adminUserService.createAdmin(username, password, role);
            redirectAttributes.addFlashAttribute("successMessage", "Admin user '" + username + "' created successfully!");
        } catch (IllegalStateException e) {
            log.warn("Failed to create admin user '{}': {}", username, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("username", username);
            redirectAttributes.addFlashAttribute("role", role);
        }
        return "redirect:/admin/create-user";
    }

    @PostMapping("/admin/disable")
    public String disableAdminUser(@RequestParam Long adminId) {
        log.info("POST /admin/disable - Request to disable admin with ID: {}", adminId);
        adminUserService.disableAdmin(adminId);
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/edit-product")
    public String updateProduct(@RequestParam Long productId, @RequestParam String productName, @RequestParam String productSku, @RequestParam BigDecimal productPrice, @RequestParam int productQuantity, @RequestParam String type, @RequestParam ProductStatus status, RedirectAttributes redirectAttributes) {
        log.info("POST /admin/edit-product - Request to update product with ID: {}", productId);
        try {
            productService.updateProduct(productId, productName, productSku, productPrice, productQuantity, type, status);
            redirectAttributes.addFlashAttribute("updateSuccessMessage", "Successfully updated '" + productName + "' (ID: " + productId + ")");
        } catch (IllegalStateException e) {
            log.warn("Failed to update product ID {}: {}", productId, e.getMessage());
            redirectAttributes.addFlashAttribute("updateErrorMessage", "Could not update product ID " + productId + ": " + e.getMessage());
        }
        return "redirect:/admin/edit-products";
    }

    @PostMapping("/admin/add-product")
    public String addProduct(@RequestParam String productName, @RequestParam String productSku, @RequestParam BigDecimal productPrice, @RequestParam int productQuantity, @RequestParam String type, @RequestParam ProductStatus status, RedirectAttributes redirectAttributes) {
        log.info("POST /admin/add-product - Attempting to create new product with SKU: {}", productSku);
        try {
            productService.createProduct(productName, productSku, productPrice, productQuantity, type, status);
            redirectAttributes.addFlashAttribute("successMessage", "Product '" + productName + "' created successfully!");
        } catch (IllegalStateException e) {
            log.warn("Failed to create product with SKU {}: {}", productSku, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/edit-products";
    }

    @GetMapping("/admin/bulk-edit")
    public String showBulkEditPage() {
        log.info("GET /admin/bulk-edit - Displaying bulk product management page.");
        return "admin-bulk-edit";
    }

    @GetMapping("/admin/products/download")
    public void downloadProducts(HttpServletResponse response) throws IOException {
        log.info("GET /admin/products/download - Processing request to download products Excel sheet.");
        byte[] data = productService.generateProductsExcel();
        String fileName = URLEncoder.encode("products.xlsx", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
        response.getOutputStream().flush();
    }

    @PostMapping("/admin/products/upload")
    public String uploadProducts(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        log.info("POST /admin/products/upload - Received file '{}' for bulk update.", file.getOriginalFilename());
        if (file.isEmpty()) {
            log.warn("Upload request failed: No file was selected.");
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload.");
            return "redirect:/admin/bulk-edit";
        }
        try {
            BulkUpdateResultDTO result = productService.processExcelUpload(file);
            log.info("Excel upload processed. Updates: {}, Creations: {}, Errors: {}", result.updatedDetails().size(), result.createdProductNames().size(), result.errors().size());
            StringBuilder successMessage = new StringBuilder("Upload complete. ");
            if (!result.updatedDetails().isEmpty()) { successMessage.append("Updated ").append(result.updatedDetails().size()).append(" products. "); }
            if (!result.createdProductNames().isEmpty()) { successMessage.append("Created ").append(result.createdProductNames().size()).append(" new products. "); }
            if (result.updatedDetails().isEmpty() && result.createdProductNames().isEmpty() && result.errors().isEmpty()) { successMessage.append("No changes or new products were found in the file."); }
            redirectAttributes.addFlashAttribute("successMessage", successMessage.toString());
            redirectAttributes.addFlashAttribute("updateDetails", result.updatedDetails());
            redirectAttributes.addFlashAttribute("creationDetails", result.createdProductNames());
            redirectAttributes.addFlashAttribute("errorDetails", result.errors());
        } catch (Exception e) {
            log.error("Failed to process uploaded Excel file '{}'.", file.getOriginalFilename(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to process the file. Please ensure it's a valid Excel format and data types are correct.");
        }
        return "redirect:/admin/bulk-edit";
    }
}