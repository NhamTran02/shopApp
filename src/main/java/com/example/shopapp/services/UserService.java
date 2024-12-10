package com.example.shopapp.services;

import com.example.shopapp.DTO.UserDTO;
import com.example.shopapp.components.JWTTokenUtil;
import com.example.shopapp.exceptions.DataNotFountException;
import com.example.shopapp.exceptions.PermissionDenyException;
import com.example.shopapp.model.Role;
import com.example.shopapp.model.User;
import com.example.shopapp.repository.RoleRepository;
import com.example.shopapp.repository.UserRepository;
import com.example.shopapp.services.impl.UserServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserService implements UserServiceImpl {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTTokenUtil jwtTokenUtil;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public User createUser(UserDTO userDTO) throws Exception {
        String phoneNumber = userDTO.getPhoneNumber();
        //ktra xem sđt có tồn tại chưa
        Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);
        if (userOptional.isPresent()) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }
        Role role=roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFountException("Role not found"));
        if (role.getName().toUpperCase().equals("ADMIN")) {
            throw new PermissionDenyException("You cannot register a admin account");
        }
        //convert from UserDTO => user
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(phoneNumber)
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .build();
        newUser.setRole(role);

        //Ktra nếu có accountId() thì kh yêu cầu password
        if ((userDTO.getFacebookAccountId()==null ||userDTO.getFacebookAccountId().isEmpty())
                && (userDTO.getGoogleAccountId()==null)||userDTO.getGoogleAccountId().isEmpty()) {
            String password=userDTO.getPassword();
            String encodedPassword=passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        }
        return userRepository.save(newUser);

    }

    @Override
    public String login(String phoneNumber, String password) throws Exception {
        Optional<User> optionalUser=userRepository.findByPhoneNumber(phoneNumber);
        if (optionalUser.isEmpty()) {
            throw new DataNotFountException("Invalid phonenumber / password");
        }
        User user=optionalUser.get();
        //check password
        if ((user.getFacebookAccountId() == null || user.getFacebookAccountId().isEmpty())
                && (user.getGoogleAccountId() == null || user.getGoogleAccountId().isEmpty())) {
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new BadCredentialsException("Invalid password");
            }
        }
        if (!optionalUser.get().getActive()){
            throw new DataNotFountException("User not active");
        }
        //authenticate with Java spring security
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(phoneNumber, password,user.getAuthorities()));
        return jwtTokenUtil.generateToken(user);
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if (jwtTokenUtil.isTokenExpired(token)) {
            throw new Exception("Token is expired");
        }
        String phoneNumber=jwtTokenUtil.extractPhoneNumber(token);
        Optional<User> optionalUser=userRepository.findByPhoneNumber(phoneNumber);

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }else {
            throw new DataNotFountException("User not found");
        }
    }

    @Transactional
    @Override
    public User updateUser(Long userId, UserDTO userDTO) throws Exception {
        // Tìm User dựa trên userId
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFountException("User not found"));

        // Kiểm tra nếu số điện thoại đã tồn tại và không thuộc về user hiện tại
        String phoneNumber = userDTO.getPhoneNumber();
        Optional<User> userWithPhoneNumber = userRepository.findByPhoneNumber(phoneNumber);
        if (userWithPhoneNumber.isPresent() && !userWithPhoneNumber.get().getId().equals(userId)) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }

        // Kiểm tra role mới (nếu có)
        if (userDTO.getRoleId() != null) {
            Role role = roleRepository.findById(userDTO.getRoleId())
                    .orElseThrow(() -> new DataNotFountException("Role not found"));
            if (role.getName().toUpperCase().equals("ADMIN")) {
                throw new PermissionDenyException("You cannot assign the ADMIN role");
            }
            existingUser.setRole(role);
        }

        // Cập nhật thông tin User
        existingUser.setFullName(userDTO.getFullName() != null ? userDTO.getFullName() : existingUser.getFullName());
        existingUser.setPhoneNumber(userDTO.getPhoneNumber() != null ? userDTO.getPhoneNumber() : existingUser.getPhoneNumber());
        existingUser.setAddress(userDTO.getAddress() != null ? userDTO.getAddress() : existingUser.getAddress());
        existingUser.setDateOfBirth(userDTO.getDateOfBirth() != null ? userDTO.getDateOfBirth() : existingUser.getDateOfBirth());
        existingUser.setFacebookAccountId(userDTO.getFacebookAccountId() != null ? userDTO.getFacebookAccountId() : existingUser.getFacebookAccountId());
        existingUser.setGoogleAccountId(userDTO.getGoogleAccountId() != null ? userDTO.getGoogleAccountId() : existingUser.getGoogleAccountId());

        // Cập nhật mật khẩu nếu người dùng cung cấp
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        // Kiểm tra nếu có accountId() thì không yêu cầu password
        if ((userDTO.getFacebookAccountId() == null || userDTO.getFacebookAccountId().isEmpty())
                && (userDTO.getGoogleAccountId() == null || userDTO.getGoogleAccountId().isEmpty())
                && (userDTO.getPassword() == null || userDTO.getPassword().isEmpty())) {
            throw new RuntimeException("Password is required if no social account is provided");
        }

        // Lưu user đã được cập nhật
        return userRepository.save(existingUser);
    }

}
