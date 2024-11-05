package com.example.shopapp.controller;

import com.example.shopapp.DTO.ProductDTO;
import com.example.shopapp.DTO.ProductImageDTO;
import com.example.shopapp.Responses.ProductListResponse;
import com.example.shopapp.Responses.ProductResponse;
import com.example.shopapp.exceptions.DataNotFountException;
import com.example.shopapp.model.Product;
import com.example.shopapp.model.ProductImage;
import com.example.shopapp.services.ProductService;
import com.github.javafaker.Faker;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("")
    public ResponseEntity<?> getAllProducts(@RequestParam("page") int page,
                                              @RequestParam("limit") int limit) {
        //Tạo page từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("createAt").descending());
        Page<ProductResponse> productPage= productService.getAllProducts(pageRequest);
        //Lấy tổng số
        int totalPages = productPage.getTotalPages();
        List<ProductResponse> products=productPage.getContent();
        return ResponseEntity.ok().body(ProductListResponse.builder()
                .products(products)
                .totalPages(totalPages)
                .build());
    }

    @PostMapping(value = "")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO,
                                           BindingResult bindingResult) throws DataNotFountException{
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errors);
        }
        Product newProduct =productService.createProduct(productDTO);
        return ResponseEntity.ok().body(newProduct);
    }

    @PostMapping(value = "upload/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    private ResponseEntity<?> uploadImage(
            @PathVariable("id") Long productId,
            @ModelAttribute("files") List<MultipartFile> files){
        try {
            Product existingProduct = productService.getProductById(productId);

            files = files == null ? new ArrayList<MultipartFile>() : files;
            if(files.size()>ProductImage.MAX_IMAGES){
                return ResponseEntity.badRequest().body("You can only upload less than 5 images");
            }
            List<ProductImage> productImages = new ArrayList<>();
            for (MultipartFile file : files) {
                if (file.getSize() == 0) {
                    continue;
                }
                //ktra kích thước và định dạng file
                if (file.getSize() > 10 * 1024 * 1024) {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("file kh đc quá 10MB");
                }
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("file kh phải là ảnh");
                }
                //Lưu file và cập nhật
                String filename = storeFile(file);
                ProductImage productImage = productService.createProductImage(existingProduct.getId(), ProductImageDTO.builder()
                        .imageUrl(filename)
                        .build());

                productImages.add(productImage);
            }

            return ResponseEntity.ok().body(productImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType == null || !contentType.startsWith("image/");
    }

    private String storeFile(MultipartFile file) throws IOException {
        if(!isImageFile(file) || file.getOriginalFilename() ==null){
            throw new IOException("Invalid image format");
        }
        String filename= StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        //Thêm UUID và trc tên file để đảm bảo tên file là duy nhất
        String uniqueFilename= UUID.randomUUID().toString()+"_"+filename;
        //Đường dẫn đến thư mục muốn lưu file
        Path uploadDir= Paths.get("uploads");
        //Ktra và tạo thư mục nếu nó kh tồn tại
        if(!Files.exists(uploadDir)){
            Files.createDirectory(uploadDir);
        }
        //Đường dẫn đến file
        Path destination=Paths.get(uploadDir.toString(),uniqueFilename);
        //Sao chép đường dẫn vào thư mục đích
        Files.copy(file.getInputStream(),destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long id){
        try {
            Product existingProduct=productService.getProductById(id);
            return ResponseEntity.ok().body(ProductResponse.fromProduct(existingProduct));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id,
                                           @RequestBody ProductDTO productDTO) {
        try {
            Product updateProduct=productService.update(id,productDTO);
            return ResponseEntity.ok().body("update Product successfully with id: "+id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok().body("delete successfully with id: "+id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/generateFakeProducts")
    public ResponseEntity<?> generateFakeProducts() {
        Faker faker = new Faker();
        for (int i = 0; i < 1000000; i++) {
            String productName = faker.commerce().productName();
            if (productService.existsName(productName)) {
                continue;
            }
            ProductDTO productDTO=ProductDTO.builder()
                    .name(productName)
                    .price((float) faker.number().numberBetween(10,100))
                    .description(faker.lorem().sentence())
                    .categoryId((long) faker.number().numberBetween(2,4))
                    .build();
            try {
                productService.createProduct(productDTO);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok().body("generateFakeProducts");
    }
}
