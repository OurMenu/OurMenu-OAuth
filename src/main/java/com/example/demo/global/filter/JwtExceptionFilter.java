package com.example.demo.global.filter;

import com.example.demo.domain.user.exception.TokenExpiredExcpetion;
import com.example.demo.global.exception.ErrorCode;
import com.example.demo.global.exception.ErrorResponse;
import com.example.demo.global.response.ApiResponse;
import com.example.demo.global.response.util.ApiUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            if(e instanceof TokenExpiredExcpetion) {
                setResponse(response, ApiUtil.error(ErrorResponse.of(ErrorCode.TOKEN_EXPIRED)));
            }
            else {
                setResponse(response, ApiUtil.error(ErrorResponse.of(ErrorCode.INVALID_TOKEN)));
            }
        }
    }

    private void setResponse(HttpServletResponse response, ApiResponse<?> errorMessage) throws RuntimeException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        String result = objectMapper.writeValueAsString(errorMessage);
        response.getWriter().print(result);
    }

}