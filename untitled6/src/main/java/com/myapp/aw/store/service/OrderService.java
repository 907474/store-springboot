package com.myapp.aw.store.service;

import com.myapp.aw.store.model.ArchivedProductItem;
import com.myapp.aw.store.model.OrderArchive;
import com.myapp.aw.store.model.OrderStatus;
import com.myapp.aw.store.model.TemporaryOrder;
import com.myapp.aw.store.repository.OrderArchiveRepository;
import com.myapp.aw.store.repository.TemporaryOrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class OrderService {

    private final TemporaryOrderRepository temporaryOrderRepository;
    private final OrderArchiveRepository orderArchiveRepository;

    public OrderService(TemporaryOrderRepository temporaryOrderRepository, OrderArchiveRepository orderArchiveRepository) {
        this.temporaryOrderRepository = temporaryOrderRepository;
        this.orderArchiveRepository = orderArchiveRepository;
    }

    @Transactional
    public OrderArchive finalizeOrder(Long temporaryOrderId) {
        TemporaryOrder temporaryOrder = temporaryOrderRepository.findById(temporaryOrderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart to be confirmed not found."));

        if (temporaryOrder.getStatus() == OrderStatus.FINISHED) {
            throw new IllegalStateException("This order has already been processed.");
        }

        OrderArchive orderArchive = new OrderArchive();
        orderArchive.setCustomerId(temporaryOrder.getCustomerId());
        orderArchive.setTotalPrice(temporaryOrder.getTotalPrice());
        orderArchive.setOrderCreationTime(temporaryOrder.getOrderCreationTime());
        orderArchive.setOrderPlacementTime(LocalDateTime.now()); // Set the final placement time

        for (var tempItem : temporaryOrder.getProductItems()) {
            ArchivedProductItem archivedItem = new ArchivedProductItem();
            archivedItem.setProductId(tempItem.getProductId());
            archivedItem.setQuantity(tempItem.getQuantity());
            archivedItem.setPriceAtPurchase(tempItem.getPriceAtPurchase());
            orderArchive.addProductItem(archivedItem);
        }

        OrderArchive savedArchivedOrder = orderArchiveRepository.save(orderArchive);

        temporaryOrder.setStatus(OrderStatus.FINISHED);
        temporaryOrderRepository.save(temporaryOrder);

        return savedArchivedOrder;
    }
}