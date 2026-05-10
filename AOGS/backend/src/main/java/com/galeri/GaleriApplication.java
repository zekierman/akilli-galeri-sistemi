package com.galeri;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Akıllı Oto Galeri Sistemi - Backend Giriş Noktası.
 * <p>
 * PDR v1.0'da {@code GaleriUygulamasi} (JavaFX) sınıfının üstlendiği
 * yaşam döngüsü sorumluluğunu burada Spring Boot devralır:
 * <ul>
 *   <li>Tomcat gömülü web sunucusu başlatılır (port 8080).</li>
 *   <li>JPA EntityManager ile PostgreSQL bağlantı havuzu kurulur.</li>
 *   <li>{@code @RestController} sınıfları otomatik olarak REST endpoint'lerine dönüşür.</li>
 *   <li>{@code @Service} sınıfları DI konteynerine kaydedilir (Singleton scope).</li>
 * </ul>
 * Bu sayede eski PDR'daki Singleton {@code getInstance()} kalıbı, modern
 * bağımlılık enjeksiyonu (constructor injection) ile değiştirilmiş olur.
 */
@SpringBootApplication
public class GaleriApplication {

    public static void main(String[] args) {
        SpringApplication.run(GaleriApplication.class, args);
    }
}
