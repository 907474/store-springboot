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
        // 1. Fetch the temporary order
        TemporaryOrder temporaryOrder = temporaryOrderRepository.findById(temporaryOrderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart to be confirmed not found."));

        // 2. Check if it's already finished to prevent duplicate orders
        if (temporaryOrder.getStatus() == OrderStatus.FINISHED) {
            throw new IllegalStateException("This order has already been processed.");
        }

        // 3. Create a new permanent OrderArchive
        OrderArchive orderArchive = new OrderArchive();
        orderArchive.setCustomerId(temporaryOrder.getCustomerId());
        orderArchive.setTotalPrice(temporaryOrder.getTotalPrice());
        orderArchive.setOrderCreationTime(temporaryOrder.getOrderCreationTime());
        orderArchive.setOrderPlacementTime(LocalDateTime.now()); // Set the final placement time

        // 4. Copy all items from the temporary order to the archived order
        for (var tempItem : temporaryOrder.getProductItems()) {
            ArchivedProductItem archivedItem = new ArchivedProductItem();
            archivedItem.setProductId(tempItem.getProductId());
            archivedItem.setQuantity(tempItem.getQuantity());
            archivedItem.setPriceAtPurchase(tempItem.getPriceAtPurchase());
            orderArchive.addProductItem(archivedItem);
        }

        // 5. Save the new archived order
        OrderArchive savedArchivedOrder = orderArchiveRepository.save(orderArchive);

        // 6. Update the temporary order's status to FINISHED
        temporaryOrder.setStatus(OrderStatus.FINISHED);
        temporaryOrderRepository.save(temporaryOrder);

        // 7. Return the new permanent order
        return savedArchivedOrder;
    }
}