package com.example.shopapp.services;

import com.example.shopapp.DTO.CartItemDTO;
import com.example.shopapp.DTO.OrderDTO;
import com.example.shopapp.Responses.OrderResponse;
import com.example.shopapp.exceptions.DataNotFountException;
import com.example.shopapp.model.*;
import com.example.shopapp.repository.OrderDetailRepository;
import com.example.shopapp.repository.OrderRepository;
import com.example.shopapp.repository.ProductRepository;
import com.example.shopapp.repository.UserRepository;
import com.example.shopapp.services.impl.OrderServiceImpl;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
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
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;


    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @Override
    public Order createOrder(OrderDTO orderDTO) throws Exception {
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

        //Tạo ds các đối tượng OrderDetail từ cartItems
        List<OrderDetail> orderDetails=new ArrayList<>();
        for (CartItemDTO cartItemDTO: orderDTO.getCartItems()) {
            // Tạo 1 đối tượng OrderDetail từ CartItemDTO
            OrderDetail orderDetail=new OrderDetail();
            orderDetail.setOrder(order);

            //Lấy thông tin sản phẩm từ cartItemDTO
            Long productId=cartItemDTO.getProductId();
            int quantity=cartItemDTO.getQuantity();

            Product product=productRepository.findById(productId)
                    .orElseThrow(()->new DataNotFountException("Product not found with id: "+productId));
            //Đặt thông tin cho OrderDetail
            orderDetail.setProduct(product);
            orderDetail.setNumberOfProducts(quantity);
            //Các trường khác của OrderDetail nếu cần
            orderDetail.setPrice(product.getPrice());

            orderDetails.add(orderDetail);
        }
        orderDetailRepository.saveAll(orderDetails);
        return order;
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
