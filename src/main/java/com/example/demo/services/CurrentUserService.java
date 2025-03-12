//package com.example.demo.services;
//
//import com.example.demo.models.User;
//import com.example.demo.repositories.UserRepository;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class CurrentUserService {
//
//    UserRepository userRepository;
//
//    public String getCurrentUsername() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            return userDetails.getUsername();
//        }
//        return null; // Không có người dùng đăng nhập
//    }
//
//    public User getCurrentUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            String username = userDetails.getUsername();
//            return userRepository.findByUsername(username).orElseThrow();
//        }
//        return null; // Không có người dùng đăng nhập
//    }
//
//    public UserDetails getUserDetails() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            return userDetails;
//        }
//        return null; // Không có người dùng đăng nhập
//    }
//}
