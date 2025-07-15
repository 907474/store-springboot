package com.myapp.aw.store.service;

import com.myapp.aw.store.model.OrderArchive;
import com.myapp.aw.store.model.OrderStatus;
import com.myapp.aw.store.model.Product;
import com.myapp.aw.store.model.TemporaryOrder;
import com.myapp.aw.store.model.dto.OrderDisplayDTO;
import com.myapp.aw.store.repository.OrderArchiveRepository;
import com.myapp.aw.store.repository.ProductRepository;
import com.myapp.aw.store.repository.TemporaryOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AdminOrderService {

    private static final Logger log = LoggerFactory.getLogger(AdminOrderService.class);

    private final TemporaryOrderRepository temporaryOrderRepository;
    private final OrderArchiveRepository orderArchiveRepository;
    private final ProductRepository productRepository;

    public record OrderItemDetailDTO(String productName, int quantity, BigDecimal priceAtPurchase) {}

    public AdminOrderService(TemporaryOrderRepository temporaryOrderRepository, OrderArchiveRepository orderArchiveRepository, ProductRepository productRepository) {
        this.temporaryOrderRepository = temporaryOrderRepository;
        this.orderArchiveRepository = orderArchiveRepository;
        this.productRepository = productRepository;
    }

    public Page<OrderDisplayDTO> getPaginatedCombinedOrders(String statusFilter, Pageable pageable) {
        log.info("Fetching combined orders with filter: {} and page: {}", statusFilter, pageable.getPageNumber());

        List<OrderDisplayDTO> combinedList = new ArrayList<>();

        if (statusFilter == null || "IN_PROGRESS".equals(statusFilter)) {
            // Corrected to use a find all method that doesn't require pagination for the initial fetch
            temporaryOrderRepository.findAllByStatus(OrderStatus.IN_PROGRESS)
                    .forEach(order -> combinedList.add(toDto(order)));
        }

        if (statusFilter == null || "FINISHED".equals(statusFilter)) {
            orderArchiveRepository.findAll().forEach(order -> combinedList.add(toDto(order)));
        }

        combinedList.sort(Comparator.comparing(OrderDisplayDTO::orderDate).reversed());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), combinedList.size());

        List<OrderDisplayDTO> pageContent = (start > combinedList.size()) ? List.of() : combinedList.subList(start, end);
        log.debug("Returning page {} with {} orders.", pageable.getPageNumber(), pageContent.size());
        return new PageImpl<>(pageContent, pageable, combinedList.size());
    }

    public List<OrderItemDetailDTO> getOrderDetails(String displayId) {
        log.info("Fetching details for order display ID: {}", displayId);
        char type = displayId.charAt(0);
        Long id = Long.parseLong(displayId.substring(2));
        List<Long> productIds;
        List<OrderItemDetailDTO> items = new ArrayList<>();

        if (type == 'T') {
            TemporaryOrder order = temporaryOrderRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
            productIds = order.getProductItems().stream().map(item -> item.getProductId()).toList();
            Map<Long, String> productNames = getProductNames(productIds);
            order.getProductItems().forEach(item -> items.add(new OrderItemDetailDTO(productNames.get(item.getProductId()), item.getQuantity(), item.getPriceAtPurchase())));
        } else if (type == 'F') {
            OrderArchive order = orderArchiveRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
            productIds = order.getProductItems().stream().map(item -> item.getProductId()).toList();
            Map<Long, String> productNames = getProductNames(productIds);
            order.getProductItems().forEach(item -> items.add(new OrderItemDetailDTO(productNames.get(item.getProductId()), item.getQuantity(), item.getPriceAtPurchase())));
        }
        log.debug("Found {} items for order {}", items.size(), displayId);
        return items;
    }

    private Map<Long, String> getProductNames(List<Long> productIds) {
        return productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getProductId, Product::getProductName));
    }

    private OrderDisplayDTO toDto(TemporaryOrder order) { return new OrderDisplayDTO("T-" + order.getOrderId(), order.getCustomerId(), order.getTotalPrice(), order.getOrderCreationTime(), "In Progress"); }
    private OrderDisplayDTO toDto(OrderArchive order) { return new OrderDisplayDTO("F-" + order.getOrderId(), order.getCustomerId(), order.getTotalPrice(), order.getOrderPlacementTime(), "Finished"); }
}