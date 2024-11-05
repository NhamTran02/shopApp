package com.example.shopapp.controller;

import com.example.shopapp.DTO.OrderDetailDTO;
import com.example.shopapp.Responses.OrderDetailResponse;
import com.example.shopapp.exceptions.DataNotFountException;
import com.example.shopapp.model.OrderDetail;
import com.example.shopapp.services.impl.OrderDetailServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}order-details")
public class OrderDetailController {
    @Autowired
    private OrderDetailServiceImpl orderDetailServiceImpl;
    //tạo 1 orderdetail
    @PostMapping("")
    public ResponseEntity<?> createOrderDetail(@Valid @RequestBody OrderDetailDTO orderDetailDTO) {
        try {
            OrderDetail orderDetail =orderDetailServiceImpl.createOrderDetail(orderDetailDTO);
            return ResponseEntity.ok().body(OrderDetailResponse.fromOrderDetail(orderDetail));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
//    lấy orderdetail theo id
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(@Valid @PathVariable Long id) throws DataNotFountException {
        OrderDetail orderDetail=orderDetailServiceImpl.getOrderDetail(id);
        return ResponseEntity.ok().body(OrderDetailResponse.fromOrderDetail(orderDetail));
//        return ResponseEntity.ok().body(orderDetail);
    }
    //lấy ds các orderdetail của 1 order nào đó
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> findByOrderId(@Valid @PathVariable("orderId") Long orderId) {
        List<OrderDetail> orderDetails =orderDetailServiceImpl.findByOrderId(orderId);
        List<OrderDetailResponse> orderDetailResponses=orderDetails
                .stream()
                .map(orderDetail -> OrderDetailResponse.fromOrderDetail(orderDetail) )
                .toList();
        return ResponseEntity.ok().body(orderDetailResponses);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail(@PathVariable("id") Long id,
                                               @Valid @RequestBody OrderDetailDTO orderDetailDTO) {
        try {
            OrderDetail orderDetail=orderDetailServiceImpl.updateOrderDetail(id, orderDetailDTO);
            return ResponseEntity.ok().body(orderDetail);
        } catch (DataNotFountException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetail(@PathVariable("id") Long id) {
        orderDetailServiceImpl.deleteOrderDetail(id);
        return ResponseEntity.ok().body("delete order detail with id: " + id+" successfully");
    }

}
