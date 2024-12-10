package com.example.shopapp.services.impl;

import com.example.shopapp.DTO.OrderDTO;
import com.example.shopapp.exceptions.DataNotFountException;
import com.example.shopapp.model.Order;

import java.util.List;

public interface OrderServiceImpl {
    Order createOrder(OrderDTO orderDTO) throws Exception;
    Order getOrder(Long id);
    Order updateOrder(Long id,OrderDTO orderDTO);
    void deleteOrder(Long id) throws DataNotFountException;
    List<Order> findByUserId(Long userId);
}
