package org.banka1.exchangeservice.controllers;

import lombok.AllArgsConstructor;
import org.banka1.exchangeservice.domains.dtos.order.OrderFilterRequest;
import org.banka1.exchangeservice.domains.dtos.order.OrderRequest;
import org.banka1.exchangeservice.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class  OrderController {

    private OrderService orderService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/all")
    public ResponseEntity<?> getOrders(@RequestBody OrderFilterRequest orderFilterRequest) {
        return ResponseEntity.ok(orderService.getAllOrders(orderFilterRequest));
    }

    @PostMapping(value = "/by-user")
    public ResponseEntity<?> getOrdersByUser(@RequestBody OrderFilterRequest orderFilterRequest, @RequestAttribute("userId") Long userId) {
        orderFilterRequest.setUserId(userId);
        return ResponseEntity.ok(orderService.getOrdersByUser(orderFilterRequest));
    }

    @PostMapping(value = "/make-order")
    public ResponseEntity<?> makeOrder(@RequestHeader("Authorization") String token, @RequestBody OrderRequest orderRequest){
        return ResponseEntity.ok(orderService.makeOrder(orderRequest, token));
    }

    @PostMapping(value = "/approve/{orderId}")
    public ResponseEntity<?> approveOrder(@RequestHeader("Authorization") String token, @PathVariable Long orderId){
        orderService.approveOrder(token, orderId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(value = "/reject/{orderId}")
    public ResponseEntity<?> rejectOrder(@RequestHeader("Authorization") String token, @PathVariable Long orderId){
        orderService.rejectOrder(token, orderId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
