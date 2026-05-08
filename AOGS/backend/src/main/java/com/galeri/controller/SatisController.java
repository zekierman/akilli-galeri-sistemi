package com.galeri.controller;

import com.galeri.dto.SatisIstegi;
import com.galeri.dto.SatisYanitDTO;
import com.galeri.service.SatisService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Satış REST API.
 * <p>
 * <b>Sorumlu:</b> Emre Kuzal — Satış Modülü.
 * <p>
 * Tüm endpoint'ler {@link SatisYanitDTO} ile yanıt verir; {@code Satis}
 * entity'si controller katmanından dışarı sızdırılmaz (proje kuralı:
 * "DTO ile API arasında veri taşınır").
 * <p>
 * <b>v2 değişiklikleri:</b>
 * <ul>
 *   <li>{@code SatisIstegi} artık {@code com.galeri.dto} paketinde — controller
 *       içindeki inner class kaldırıldı (tek sorumluluk, paylaşılabilirlik).</li>
 *   <li>{@code @Valid} ile Bean Validation aktif edildi.</li>
 *   <li>{@code POST} 201 Created + {@code Location} header döndürüyor (REST best practice).</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/satislar")
public class SatisController {

    private final SatisService satisService;

    public SatisController(SatisService satisService) {
        this.satisService = satisService;
    }

    /** Tüm satışları en yeniden eskiye listele. */
    @GetMapping
    public List<SatisYanitDTO> tumSatislar() {
        return satisService.tumSatislar();
    }

    /** ID ile tek satış getir. */
    @GetMapping("/{id}")
    public SatisYanitDTO satisBul(@PathVariable String id) {
        return satisService.satisBul(id);
    }

    /** Bir müşterinin TC kimliğine göre tüm satışlarını listele. */
    @GetMapping("/musteri/{tc}")
    public List<SatisYanitDTO> musteriSatislari(@PathVariable String tc) {
        return satisService.musteriSatislari(tc);
    }

    /**
     * Yeni satış oluşturur. {@code @Valid} ile request gövdesi otomatik
     * doğrulanır; ihlaller {@code GlobalExceptionHandler} tarafından
     * 400 Bad Request olarak çevrilir.
     */
    @PostMapping
    public ResponseEntity<SatisYanitDTO> satisYap(@Valid @RequestBody SatisIstegi istek) {
        SatisYanitDTO yanit = satisService.satisYap(istek);
        return ResponseEntity
            .created(URI.create("/api/satislar/" + yanit.satisId()))
            .body(yanit);
    }

    /** Satışı iptal eder (PDR §2.4 — 24 saat kuralı). */
    @PostMapping("/{id}/iptal")
    public ResponseEntity<Void> satisIptal(@PathVariable String id) {
        satisService.satisIptal(id);
        return ResponseEntity.noContent().build();
    }

    /** Fatura metnini düz string olarak döndürür (frontend PDF'e dönüştürür). */
    @GetMapping("/{id}/fatura")
    public String fatura(@PathVariable String id) {
        return satisService.fatura(id);
    }
}
