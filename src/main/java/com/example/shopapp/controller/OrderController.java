package com.example.shopapp.controller;

import com.example.shopapp.DTO.OrderDTO;
import com.example.shopapp.DTO.UserDTO;
import com.example.shopapp.Responses.OrderResponse;
import com.example.shopapp.exceptions.DataNotFountException;
import com.example.shopapp.model.Order;
import com.example.shopapp.model.User;
import com.example.shopapp.services.OrderService;
import com.example.shopapp.services.impl.OrderServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}oders")
public class OrderController {
    @Autowired
    private OrderServiceImpl orderServiceImpl;

    @PostMapping("")
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderDTO orderDTO,
                                         BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errors = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errors);
            }
            Order order=orderServiceImpl.createOrder(orderDTO);
            return ResponseEntity.ok().body(order);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/user/{user_id}")
    public ResponseEntity<?> getOrders(@Valid @PathVariable("user_id") Long userId) {
        try {
            List<Order> orders=orderServiceImpl.findByUserId(userId);
            return  ResponseEntity.ok().body(orders);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //việc của admin
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@Valid @PathVariable Long id,
                                            @Valid @RequestBody OrderDTO orderDTO) {
        try {
            Order order=orderServiceImpl.updateOrder(id, orderDTO);
            return ResponseEntity.ok().body("Update successfully order with id: "+id);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@Valid @PathVariable Long id) throws DataNotFountException {
        //xóa mềm => cập nhật trường active=false
        orderServiceImpl.deleteOrder(id);
        return ResponseEntity.ok().body("Order deleted sucessfully");
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@Valid @PathVariable("id") Long OrderId) {
        try {
            Order existingOrder =orderServiceImpl.getOrder(OrderId);
            OrderResponse orderResponse=OrderResponse.from(existingOrder);
            return ResponseEntity.ok().body(orderResponse);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
