package org.ever.sd.controller;

import lombok.RequiredArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ever.sd.service.OrderService;

@RestController
@RequestMapping("/api/sd/sales-orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Void> registerOrder(@RequestBody RegisterOrderRequest request) {
        orderService.registerOrder(request.getId(), request.getName(), request.getCount());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterOrderRequest {
        private Long id;
        private String name;
        private Integer count;
    }

}
