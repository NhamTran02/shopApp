package com.example.shopapp.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @JsonProperty("fullname")
    private String fullName;
    @JsonProperty("phone_number")
    @NotBlank(message = "phone number is required")
    private String phoneNumber;
    private String address;
    @NotBlank(message = "password can't be blank")
    private String password;
    @JsonProperty("retype_password")
    private String retypePassword;
    @JsonProperty("date_of_birth")
    private Date dateOfBirth;
    @JsonProperty("facebook_account_id")
    private String facebookAccountId;
    @JsonProperty("google_account_id")
    private String googleAccountId;
    @NotNull(message = "role ID is required")
    @JsonProperty("role_id")
    private Long roleId;
}
