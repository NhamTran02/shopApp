package com.example.shopapp.Responses;
import com.example.shopapp.model.Product;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse extends BaseResponse {
    private String name;
    private Float price;
    private String url;
    private String description;
    private Long categoryId;

    public static ProductResponse fromProduct(Product product) {
        ProductResponse productResponse=ProductResponse.builder()
                .name(product.getName())
                .price(product.getPrice())
                .url(product.getUrl())
                .description(product.getDescription())
                .categoryId(product.getCategory().getId())
                .build();
        productResponse.setCreateAt(product.getCreateAt());
        productResponse.setUpdateAt(product.getUpdateAt());
        return productResponse;
    }
}
