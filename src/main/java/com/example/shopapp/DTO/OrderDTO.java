package com.example.shopapp.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    @Min(value = 1, message = "User's ID must be >0")
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("fullname")
    private String fullname;
    private String email;
    @JsonProperty("phone_number")
    @NotBlank(message = "phone number is required")
    @Min(value = 5,message = "phone number must be at least 5 characters")
    private String phoneNumber;
    private String address;
    private String note;
    @JsonProperty("total_money")
    @Min(value = 0,message = "Total money must be >=0")
    private Float totalMoney;
    @JsonProperty("shipping_method")
    private String shippingMethod;
    @JsonProperty("shipping_address")
    private String shippingAddress;
    @JsonProperty("shipping_date")
    private LocalDate shippingDate;
    @JsonProperty("payment_method")
    private String paymentMethod;
    @JsonProperty("cart_items")
    private List<CartItemDTO> cartItems;
}
