package org.banka1.exchangeservice.controllers;

import lombok.AllArgsConstructor;
import org.banka1.exchangeservice.domains.dtos.order.OrderRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderController {

    // NAPOMENA: IZMENITI RUTE

    @GetMapping(value = "/order/{status}/{done}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getOrders(@PathVariable(required = false) String status, @PathVariable(required = false) Boolean done,
                                       @RequestAttribute("userId") Long userId) {

//        List<Order> orders = orderService.getOrders(token, status, done);
//        return ResponseEntity.ok(orders);
        return null;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getOrders(@RequestAttribute("userId") Long userId) {
        System.out.println("USER ID: " + userId);
//        List<Order> orders = orderService.getOrders(token);
//        return ResponseEntity.ok(orders);
        return null;
    }

    @PostMapping(value = "/make-order", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> makeOrder(@RequestHeader("Authorization") String token, @RequestBody OrderRequest orderRequest){
        orderService.makeOrder(orderRequest, token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(value = "/order/approve/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> approveOrder(@RequestAttribute("userId") Long userId, @PathVariable Long orderId){
//        ApproveRejectOrderResponse resp = orderService.approveOrder(userService.getUserRoleByToken(token), id);
//        if(resp.getMessage().equals(MessageUtils.ORDER_APPROVED)) {
//            return ResponseEntity.ok(resp);
//        }
//        return ResponseEntity.internalServerError().body(resp);
        return null;
    }

    @PostMapping(value = "/order/reject/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> rejectOrder(@RequestAttribute("userId") Long userId, @PathVariable Long id){
//        ApproveRejectOrderResponse resp = orderService.rejectOrder(userService.getUserRoleByToken(token), id);
//        if(resp.getMessage().equals(MessageUtils.ORDER_REJECTED)) {
//            return ResponseEntity.ok(resp);
//        }
//        return ResponseEntity.internalServerError().body(resp);
        return null;
    }
}
