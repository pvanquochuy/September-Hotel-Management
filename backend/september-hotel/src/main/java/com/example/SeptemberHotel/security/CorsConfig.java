package com.example.SeptemberHotel.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @pvanquochuy
 * cấu hình CORS
 */
@Configuration
public class CorsConfig {
    @Bean //  Đánh dấu phương thức này để Spring tạo và quản lý bean WebMvcConfigurer.
    public WebMvcConfigurer webMvcConfigurer(){
        return new WebMvcConfigurer(){
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") //  Áp dụng quy tắc CORS cho tất cả các endpoint trong ứng dụn
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // Chỉ định các phương thức HTTP được phép từ các nguồn gốc khác.
                        .allowedOrigins("*");  //  Chỉ định các nguồn gốc (origins) được phép truy cập tài nguyên từ ứng dụng.
            }
        };
    }
}
