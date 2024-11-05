package com.example.shopapp.services;

import com.example.shopapp.DTO.ProductDTO;
import com.example.shopapp.DTO.ProductImageDTO;
import com.example.shopapp.Responses.ProductResponse;
import com.example.shopapp.exceptions.DataNotFountException;
import com.example.shopapp.exceptions.InvalidParamException;
import com.example.shopapp.model.Category;
import com.example.shopapp.model.Product;
import com.example.shopapp.model.ProductImage;
import com.example.shopapp.repositories.CategoryRepository;
import com.example.shopapp.repositories.ProductImageRepository;
import com.example.shopapp.repositories.ProductRepository;
import com.example.shopapp.services.impl.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService implements ProductServiceImpl {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductImageRepository productImageRepository;

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public Product createProduct(ProductDTO productDTO) throws DataNotFountException {
        Category exitsingCategory =categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(()->new DataNotFountException("Cannot find category with id: "+ productDTO.getCategoryId()));

        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .url(productDTO.getUrl())
                .description(productDTO.getDescription())
                .category(exitsingCategory)
                .build();
        return productRepository.save(newProduct);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @Override
    public Product getProductById(Long id) throws DataNotFountException {
        return productRepository.findById(id)
                .orElseThrow(()-> new DataNotFountException("Cannot find product with id: "+ id));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @Override
    public Page<ProductResponse> getAllProducts(PageRequest pageRequest) {
        //Lấy danh sách sp theo page và limit(giới hạn)
        return productRepository.findAll(pageRequest)
                .map(ProductResponse::fromProduct);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public Product update(Long id, ProductDTO productDTO) throws DataNotFountException {
        Product exitingProduct=getProductById(id);

        Category exitsingCategory =categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(()->new DataNotFountException("Cannot find category with id: "+ productDTO.getCategoryId()));

        exitingProduct.setName(productDTO.getName());
        exitingProduct.setPrice(productDTO.getPrice());
        exitingProduct.setUrl(productDTO.getUrl());
        exitingProduct.setDescription(productDTO.getDescription());
        exitingProduct.setCategory(exitsingCategory);

        return productRepository.save(exitingProduct);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public void deleteProduct(Long id) throws DataNotFountException {
        Optional<Product> optionalProduct =productRepository.findById(id);
        if (!optionalProduct.isPresent()) {
            throw new DataNotFountException("Cannot find product with id: "+ id);
        }
        productRepository.deleteById(optionalProduct.get().getId());
    }

    @Override
    public boolean existsName(String name) {
        return productRepository.existsByName(name);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws DataNotFountException, InvalidParamException {
        Product existingProduct=productRepository.findById(productId)
                .orElseThrow(()-> new DataNotFountException("Cannot find product with id: "+ productId));
        ProductImage newProductImage=ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();
        //kh cho insert quá 5 ảnh trên 1 sản phẩm
        int size = productImageRepository.findByProductId(productId).size();
        if (size>=ProductImage.MAX_IMAGES) {
            throw new InvalidParamException("number of image must be <= 5");
        }
        return productImageRepository.save(newProductImage);
    }
}
