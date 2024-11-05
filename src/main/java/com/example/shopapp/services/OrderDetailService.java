package com.example.shopapp.services;

import com.example.shopapp.DTO.OrderDetailDTO;
import com.example.shopapp.exceptions.DataNotFountException;
import com.example.shopapp.model.Order;
import com.example.shopapp.model.OrderDetail;
import com.example.shopapp.model.Product;
import com.example.shopapp.repositories.OrderDetailRepository;
import com.example.shopapp.repositories.OrderRepository;
import com.example.shopapp.repositories.ProductRepository;
import com.example.shopapp.services.impl.OrderDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class OrderDetailService implements OrderDetailServiceImpl {
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ProductRepository productRepository;

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws DataNotFountException {
        //Tìm xem order có tồn tại kh
        Order order =orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(()-> new DataNotFountException("Cannot find order with id: "+orderDetailDTO.getOrderId()));
        //tìm xem product có tồn tại kh
        Product product=productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(()-> new DataNotFountException("Cannot find product with id: "+orderDetailDTO.getProductId()));
        OrderDetail orderDetail=OrderDetail.builder()
                .order(order)
                .product(product)
                .price(orderDetailDTO.getPrice())
                .numberOfProducts(orderDetailDTO.getNumberOfProducts())
                .totalMoney(orderDetailDTO.getTotalMoney())
                .color(orderDetailDTO.getColor())
                .build();
        return orderDetailRepository.save(orderDetail);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @Override
    public OrderDetail getOrderDetail(Long id) throws DataNotFountException {
        return orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFountException("Cannot find orderdetail with id: "+id));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) throws DataNotFountException {
        //Tìn xem order detail có tồn tại kh
        OrderDetail existingOrderDetail=orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFountException("Cannot find orderdetail with id: "+id));
        Order existingOrder=orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFountException("Cannot find order with id: "+orderDetailDTO.getOrderId()));
        Product existingProduct=productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(()-> new DataNotFountException("Cannot find product with id: "+orderDetailDTO.getProductId()));

        existingOrderDetail.setProduct(existingProduct);
        existingOrderDetail.setProduct(existingProduct);
        existingOrderDetail.setPrice(orderDetailDTO.getPrice());
        existingOrderDetail.setNumberOfProducts(orderDetailDTO.getNumberOfProducts());
        existingOrderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
        existingOrderDetail.setColor(orderDetailDTO.getColor());
        return orderDetailRepository.save(existingOrderDetail);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public void deleteOrderDetail(Long id) {
        orderDetailRepository.deleteById(id);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @Override
    public List<OrderDetail> findByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
}
