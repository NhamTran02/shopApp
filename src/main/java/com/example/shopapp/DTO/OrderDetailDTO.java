package com.example.shopapp.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.jmx.export.annotation.ManagedAttribute;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDTO {
    @JsonProperty("order_id")
    @Min(value = 1,message = "order's ID must be >0")
    private Long orderId;
    @Min(value = 1,message = "product's ID must be >0")
    @JsonProperty("product_id")
    private Long productId;
    @Min(value = 0,message = "price must be >0")
    @JsonProperty("price")
    private Float price;
    @JsonProperty("number_of_products")
    private Integer numberOfProducts;
    @JsonProperty("total_money")
    @Min(value = 0,message = "total money must be >0")
    private Float totalMoney;
    private String color;

}
