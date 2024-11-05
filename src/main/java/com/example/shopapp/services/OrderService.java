package com.example.shopapp.services;

import com.example.shopapp.DTO.OrderDTO;
import com.example.shopapp.Responses.OrderResponse;
import com.example.shopapp.exceptions.DataNotFountException;
import com.example.shopapp.model.Order;
import com.example.shopapp.model.OrderStatus;
import com.example.shopapp.model.User;
import com.example.shopapp.repositories.OrderRepository;
import com.example.shopapp.repositories.UserRepository;
import com.example.shopapp.services.impl.OrderServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class OrderService implements OrderServiceImpl {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;


    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public OrderResponse createOrder(OrderDTO orderDTO) throws Exception {
        //tìm xem user_id có tồn tại kh
        User user =userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFountException("Cannot find user with id: "+orderDTO.getUserId()));
        //convert orderDTO -> Order
        //dùng thư viện Model Mapper
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        //Cập nhật các trường của đơn hàng từ DTO
        Order order=new Order();
        modelMapper.map(orderDTO,order);
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.PENDING);
        //Ktra  phải>= ngày hôm
        LocalDate shippingDate=orderDTO.getShippingDate() == null ? LocalDate.now() : orderDTO.getShippingDate();
        if(shippingDate.isBefore(LocalDate.now())){
            throw new DataNotFountException("Shipping Date cannot be before Order Date");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        orderRepository.save(order);
        return modelMapper.map(order, OrderResponse.class);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @Override
    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public Order updateOrder(Long id, OrderDTO orderDTO) {
        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(()->new DataNotFountException("Cannot find order with id: "+id));
            User existingUser = userRepository.findById(orderDTO.getUserId())
                    .orElseThrow(()->new DataNotFountException("Cannot find user with id: "+id));
            //Tạo 1 luồng bằng ánh xạ riêng để kiểm soát việc ánh xạ
            modelMapper.typeMap(OrderDTO.class, Order.class)
                    .addMappings(mapper -> mapper.skip(Order::setId));
            //Cập nhật các trường của đơn hàng bằng OrderDTO
            modelMapper.map(orderDTO,order);
            order.setUser(existingUser);
            orderRepository.save(order);
            return order;
        } catch (DataNotFountException e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        //no hard-delete, => please soft-delete
        if (order != null) {
            order.setActive(false);
            orderRepository.save(order);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @Override
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
