package com.example.demo.global.filter;


import com.example.demo.global.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtTokenProvider.getHeaderToken(request, HttpHeaders.AUTHORIZATION);
        String refreshToken = jwtTokenProvider.getHeaderToken(request, "Refresh_token");

        if(accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            setAuthentication(jwtTokenProvider.getEmailFromToken(accessToken));
            filterChain.doFilter(request,response);
            return;
        }

        if (refreshToken != null && jwtTokenProvider.refreshTokenValidation(refreshToken)) {
            String email = jwtTokenProvider.getEmailFromToken(refreshToken);
            String newAccessToken = jwtTokenProvider.createToken(email, "Access");
            jwtTokenProvider.setHeaderAccessToken(response, newAccessToken);
            setAuthentication(jwtTokenProvider.getEmailFromToken(newAccessToken));
            filterChain.doFilter(request,response);
            return;
        }

        filterChain.doFilter(request,response);
    }

    public void setAuthentication(String email) {
        Authentication authentication = jwtTokenProvider.createAuthentication(email);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
