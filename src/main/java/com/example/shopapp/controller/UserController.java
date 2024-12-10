package com.example.shopapp.controller;

import com.example.shopapp.DTO.UserDTO;
import com.example.shopapp.DTO.UserLoginDTO;
import com.example.shopapp.Responses.UserResponse;
import com.example.shopapp.model.User;
import com.example.shopapp.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}users")
@Validated
public class UserController {
    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO,
                                        BindingResult result){
        try {
            if (result.hasErrors()) {
                List<String> errors = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errors);
            }
            if(!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().body("Passwords do not match");
            }
            User user =userService.createUser(userDTO);
//            return ResponseEntity.ok().body("Register successful");
            return ResponseEntity.ok(user);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {

//        SecretKey secretKey= Keys.secretKeyFor(SignatureAlgorithm.HS256);
//        String encrypted= Encoders.BASE64.encode(secretKey.getEncoded());
//        System.out.println(encrypted);

        //ktra thông tin đăng nhập và sinh token
        try {
            String token = userService.login(userLoginDTO.getPhoneNumber(),
                    userLoginDTO.getPassword());
            //Trả về token trong response
            return ResponseEntity.ok().body(token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/details")
    public ResponseEntity<?> getUserDetails(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String extractedToken = authorizationHeader.substring(7);
            User user=userService.getUserDetailsFromToken(extractedToken);
            return ResponseEntity.ok(UserResponse.fromUser(user));

        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/details/{userId}")
    public ResponseEntity<?> updateUserDetails(
            @PathVariable Long userId,
            @RequestBody UserDTO userDTO,
            @RequestHeader("Authorization") String authorizationHeader
    ){
        try {
            String extractedToken = authorizationHeader.substring(7);
            User user=userService.getUserDetailsFromToken(extractedToken);

            if (user.getId() != userId) {
                return ResponseEntity.status(403).build();
            }

            User updateUser=userService.updateUser(userId, userDTO);
            return ResponseEntity.ok(UserResponse.fromUser(updateUser));
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
