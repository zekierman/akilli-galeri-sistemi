package com.galeri.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS (Cross-Origin Resource Sharing) yapılandırması.
 * <p>
 * Client-Server mimarisinde frontend (React, port 5173) ve backend
 * (Spring Boot, port 8080) farklı originlerde çalışır. Tarayıcının
 * Same-Origin Policy'sini aşmak için backend tarafında izin verilen
 * origin'ler tanımlanmalıdır.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "http://localhost:5173",   // Vite dev server
                    "http://localhost:3000",   // Alternatif dev port
                    "http://127.0.0.1:5173"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
