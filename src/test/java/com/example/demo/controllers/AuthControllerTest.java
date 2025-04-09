package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    UserService userService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Khởi tạo MockMvc với controller đã được inject
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void register() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("username");
        registerRequest.setPassword("password");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(registerRequest);

        // Giả sử register sẽ không làm gì trong service mock
        doNothing().when(userService).register(any(RegisterRequest.class));

        // Act: Gửi POST request đến /register
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))

                // Assert: Kiểm tra response trả về từ controller
                .andExpect(status().isCreated()) // Kiểm tra status code
                .andExpect(jsonPath("$.code").value(200)) // Kiểm tra code trong response body
                .andExpect(jsonPath("$.message")
                        .value("User registered successfully!")) // Kiểm tra message trong response body
                .andExpect(jsonPath("$.data")
                        .doesNotExist()) // Kiểm tra phần data trong body, đây là kiểu `Void` nên không có dữ liệu
        ;
    }
}
