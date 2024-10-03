package com.example.SeptemberHotel.security;

import com.example.SeptemberHotel.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.CachingUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * pvanquochuy
 * JWTAuthFilter để xử lý xác thực dựa trên JSON Web Tokens (JWT).
 * 1. Nhận Yêu Cầu HTTP:
 * 2. Trích Xuất Header
 * 3. Trích Xuất Token và Email:
 * 4. Xác Thực Token:
 * 5. Tiếp Tục Yêu Cầu:
 */
@Component
public class JWTAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtils jwtUtils; //  sử dụng để tạo và xác thực token JWT.
    @Autowired
    private CachingUserDetailsService cachingUserDetailsService; // : Được sử dụng để tải thông tin người dùng dựa trên email (hoặc username)

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization"); // Trích xuất giá trị của header Authorization từ yêu cầu HTTP
        final String jwtToken;
        final String userEmail;

        if(authHeader == null || authHeader.isBlank()){ //  Kiểm tra xem header Authorization có tồn tại và không trống.
            filterChain.doFilter(request, response);
            return;
        }

        jwtToken = authHeader.substring(7); // Loại bỏ tiền tố "Bearer " (độ dài 7 ký tự) để lấy token JWT thực sự.
        userEmail = jwtUtils.extractUsername(jwtToken); //  Sử dụng JWTUtils để trích xuất email người dùng từ token JWT.

        // Kiểm Tra và Xác Thực Token
        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = cachingUserDetailsService.loadUserByUsername(userEmail);
            if(jwtUtils.isValidToken(jwtToken, userDetails)){
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                securityContext.setAuthentication(token);
                SecurityContextHolder.setContext(securityContext);
            }
        }
        filterChain.doFilter(request, response); //  Cho phép yêu cầu tiếp tục đi qua các bộ lọc khác trong chuỗi hoặc tới các endpoint của ứng dụng.
    }
}
