package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    UserService userService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("username");
        registerRequest.setPassword("password");
    }

    @Test
    void register_success() throws JsonProcessingException {
        // Arrange: Giả lập username chưa tồn tại
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");

        // Act: Gọi phương thức register
        userService.register(registerRequest);

        // Assert: Kiểm tra rằng phương thức save đã được gọi để lưu user
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_UserAlreadyExists() {
        // Arrange: Giả lập username đã tồn tại trong cơ sở dữ liệu
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        // Act & Assert: Kiểm tra rằng khi username đã tồn tại, sẽ ném ra exception
        AppException thrown = assertThrows(AppException.class, () -> userService.register(registerRequest));
        assertEquals(ErrorCode.USERNAME_EXISTED, thrown.getErrorCode());

        // Verify: Kiểm tra rằng không có lưu user nào vào repository
        verify(userRepository, times(0)).save(any(User.class));
    }
}
