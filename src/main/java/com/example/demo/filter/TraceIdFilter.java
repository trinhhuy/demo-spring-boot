package com.example.demo.filter;

import java.io.IOException;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.demo.config.TraceIdConfig;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@WebFilter(filterName = "traceIdFilter", urlPatterns = "/*")
public class TraceIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            TraceIdConfig.setTraceId();
            chain.doFilter(request, response);
        } finally {
            TraceIdConfig.clearTraceId();
        }
    }
}
