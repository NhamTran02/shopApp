package com.example.shopapp.Responses;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponse {
    private List<ProductResponse> products;
    private int totalPages;
    private long totalElements;
    private int currentPage;
}
