package com.example.shopapp.services.impl;

import com.example.shopapp.DTO.OrderDetailDTO;
import com.example.shopapp.exceptions.DataNotFountException;
import com.example.shopapp.model.OrderDetail;

import java.util.List;

public interface OrderDetailServiceImpl {
    OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws DataNotFountException;
    OrderDetail getOrderDetail(Long id) throws DataNotFountException;
    OrderDetail updateOrderDetail(Long id,OrderDetailDTO orderDetailDTO) throws DataNotFountException;
    void deleteOrderDetail(Long id);
    List<OrderDetail> findByOrderId(Long orderId);
}
