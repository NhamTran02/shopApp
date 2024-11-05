package com.example.shopapp.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    @NotBlank(message = "name kh đc trống")
    @Size(min = 3,max = 200,message = "name phải từ 3 đến 200 kí tự")
    private String name;
    @Min(value = 0,message = " Giá phải >=0")
    private Float price;
    private String url;
    private String description;
    @JsonProperty("category_id")
    private Long categoryId;
}
