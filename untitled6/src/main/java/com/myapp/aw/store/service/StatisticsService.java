package com.myapp.aw.store.service;

import com.myapp.aw.store.model.ArchivedProductItem;
import com.myapp.aw.store.model.Customer;
import com.myapp.aw.store.model.OrderArchive;
import com.myapp.aw.store.model.Product;
import com.myapp.aw.store.model.dto.ProductRevenueDTO;
import com.myapp.aw.store.model.dto.RevenueReportDTO;
import com.myapp.aw.store.model.dto.StatisticsDTO;
import com.myapp.aw.store.model.dto.TopCustomerDTO;
import com.myapp.aw.store.model.dto.TopProductDTO;
import com.myapp.aw.store.repository.CustomerRepository;
import com.myapp.aw.store.repository.OrderArchiveRepository;
import com.myapp.aw.store.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final OrderArchiveRepository orderArchiveRepository;

    public StatisticsService(
            ProductRepository productRepository,
            CustomerRepository customerRepository,
            OrderArchiveRepository orderArchiveRepository
    ) {
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.orderArchiveRepository = orderArchiveRepository;
    }

    public StatisticsDTO getDashboardStats() {
        long totalProducts = productRepository.count();
        long totalCustomers = customerRepository.count();
        long totalFinishedOrders = orderArchiveRepository.count();
        long lowStockProducts = productRepository.countByProductQuantityLessThan(10);

        BigDecimal totalRevenue = orderArchiveRepository.findAll().stream()
                .map(OrderArchive::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new StatisticsDTO(
                totalProducts,
                totalCustomers,
                totalFinishedOrders,
                totalRevenue,
                lowStockProducts
        );
    }

    public RevenueReportDTO generateRevenueReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        List<OrderArchive> orders = orderArchiveRepository.findByOrderPlacementTimeBetween(startDateTime, endDateTime);

        BigDecimal totalRevenue = orders.stream()
                .map(OrderArchive::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<Long, ProductRevenueDTO> productRevenueMap = orders.stream()
                .flatMap(order -> order.getProductItems().stream())
                .collect(Collectors.groupingBy(
                        ArchivedProductItem::getProductId,
                        Collectors.collectingAndThen(Collectors.toList(), items -> {
                            int totalQuantity = items.stream().mapToInt(ArchivedProductItem::getQuantity).sum();
                            BigDecimal revenue = items.stream()
                                    .map(item -> item.getPriceAtPurchase().multiply(new BigDecimal(item.getQuantity())))
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            return new ProductRevenueDTO(null, totalQuantity, revenue);
                        })
                ));

        List<Long> productIds = new ArrayList<>(productRevenueMap.keySet());
        Map<Long, String> productNameMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getProductId, Product::getProductName));

        List<ProductRevenueDTO> productRevenueList = productRevenueMap.entrySet().stream()
                .map(entry -> new ProductRevenueDTO(
                        productNameMap.getOrDefault(entry.getKey(), "Unknown Product"),
                        entry.getValue().quantitySold(),
                        entry.getValue().totalRevenue()
                ))
                .sorted((a, b) -> b.totalRevenue().compareTo(a.totalRevenue()))
                .collect(Collectors.toList());

        return new RevenueReportDTO(totalRevenue, productRevenueList);
    }

    public Page<TopProductDTO> getTopProducts(Pageable pageable) {
        // This method is now corrected to handle deleted products
        Map<Long, Integer> quantityMap = orderArchiveRepository.findAll().stream()
                .flatMap(order -> order.getProductItems().stream())
                .collect(Collectors.groupingBy(ArchivedProductItem::getProductId, Collectors.summingInt(ArchivedProductItem::getQuantity)));

        List<Long> productIds = new ArrayList<>(quantityMap.keySet());
        Map<Long, Product> productDetailsMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));

        List<TopProductDTO> topProducts = quantityMap.entrySet().stream()
                .map(entry -> {
                    Product product = productDetailsMap.get(entry.getKey());
                    if (product == null) return null; // Check for deleted products
                    return new TopProductDTO(product.getProductName(), product.getProductSku(), entry.getValue());
                })
                .filter(Objects::nonNull) // Remove null entries before sorting
                .sorted(Comparator.comparingInt(TopProductDTO::totalQuantitySold).reversed())
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), topProducts.size());
        return new PageImpl<>(topProducts.subList(start, end), pageable, topProducts.size());
    }

    public Page<TopCustomerDTO> getTopCustomers(Pageable pageable) {
        // This method is now corrected to handle deleted customers
        Map<Long, BigDecimal> revenueMap = orderArchiveRepository.findAll().stream()
                .filter(order -> order.getCustomerId() != null && order.getCustomerId() != 0L)
                .collect(Collectors.groupingBy(
                        OrderArchive::getCustomerId,
                        Collectors.reducing(BigDecimal.ZERO, OrderArchive::getTotalPrice, BigDecimal::add)
                ));

        if (revenueMap.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Long> customerIds = new ArrayList<>(revenueMap.keySet());
        Map<Long, String> customerNameMap = customerRepository.findAllById(customerIds).stream()
                .collect(Collectors.toMap(Customer::getId, Customer::getUsername));

        List<TopCustomerDTO> topCustomers = revenueMap.entrySet().stream()
                .filter(entry -> customerNameMap.containsKey(entry.getKey())) // Ensure customer exists
                .map(entry -> new TopCustomerDTO(
                        entry.getKey(),
                        customerNameMap.get(entry.getKey()),
                        entry.getValue()
                ))
                .sorted(Comparator.comparing(TopCustomerDTO::totalSpent).reversed())
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), topCustomers.size());
        List<TopCustomerDTO> pageContent = (start >= topCustomers.size()) ? List.of() : topCustomers.subList(start, end);

        return new PageImpl<>(pageContent, pageable, topCustomers.size());
    }
}