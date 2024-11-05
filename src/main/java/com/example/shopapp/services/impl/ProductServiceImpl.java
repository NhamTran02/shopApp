package com.example.shopapp.services.impl;

import com.example.shopapp.DTO.ProductDTO;
import com.example.shopapp.DTO.ProductImageDTO;
import com.example.shopapp.Responses.ProductResponse;
import com.example.shopapp.exceptions.DataNotFountException;
import com.example.shopapp.exceptions.InvalidParamException;
import com.example.shopapp.model.Product;
import com.example.shopapp.model.ProductImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ProductServiceImpl {
    public Product createProduct(ProductDTO productDTO) throws DataNotFountException;
    Product getProductById(Long id) throws DataNotFountException;
    Page<ProductResponse> getAllProducts(PageRequest pageRequest);
    Product update(Long id, ProductDTO productDTO) throws DataNotFountException;
    void deleteProduct(Long id) throws DataNotFountException;
    boolean existsName(String name);

    ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws DataNotFountException, InvalidParamException;
}
