package com.galeri.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Tüm REST controller'lardan fırlayan istisnaları yakalar ve
 * standardize edilmiş JSON hata yanıtlarına dönüştürür.
 * PDR §2.6.3'teki "beklenen iş hataları" yaklaşımının web mimarisi
 * eşdeğeridir — eski JavaFX Alert popup'ları yerine HTTP status kodu
 * + JSON body kullanılır.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** İş kuralı ihlali (örn: TC zaten kayıtlı, plaka mevcut). */
    @ExceptionHandler(IsKuraliException.class)
    public ResponseEntity<Map<String, Object>> handleIsKurali(IsKuraliException ex) {
        return hata(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /** Bulunamayan kayıtlar için 404. */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(EntityNotFoundException ex) {
        return hata(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /** Form/JSON doğrulama hataları (TC 11 hane değil, fiyat negatif vb.). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> alanHatalari = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
            alanHatalari.put(err.getField(), err.getDefaultMessage())
        );
        Map<String, Object> govde = new HashMap<>();
        govde.put("zaman", LocalDateTime.now());
        govde.put("durum", HttpStatus.BAD_REQUEST.value());
        govde.put("mesaj", "Doğrulama hatası");
        govde.put("alanlar", alanHatalari);
        return ResponseEntity.badRequest().body(govde);
    }

    /** Beklenmeyen tüm hatalar için 500 — PDR §2.6.3 "beklenmeyen sistem hataları". */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenel(Exception ex) {
        return hata(HttpStatus.INTERNAL_SERVER_ERROR, "Sunucu hatası: " + ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> hata(HttpStatus status, String mesaj) {
        Map<String, Object> govde = new HashMap<>();
        govde.put("zaman", LocalDateTime.now());
        govde.put("durum", status.value());
        govde.put("mesaj", mesaj);
        return ResponseEntity.status(status).body(govde);
    }
}
