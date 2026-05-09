package com.galeri.controller;

import com.galeri.model.Arac;
import com.galeri.model.enums.AracDurumu;
import com.galeri.service.AracService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Araç REST API (Client-Server mimarisi köprüsü).
 * <p>
 * Endpoint'ler RESTful kurallara uygun şekilde tanımlanmıştır:
 * <pre>
 *   GET    /api/araclar              - Tüm araçlar
 *   GET    /api/araclar/{id}         - Tek araç
 *   GET    /api/araclar/ara?q=...    - Arama
 *   POST   /api/araclar              - Yeni araç
 *   PUT    /api/araclar/{id}         - Güncelleme
 *   DELETE /api/araclar/{id}         - Silme
 * </pre>
 */
@RestController
@RequestMapping("/api/araclar")
public class AracController {

    private final AracService aracService;

    public AracController(AracService aracService) {
        this.aracService = aracService;
    }

    @GetMapping
    public List<Arac> tumAraclar(@RequestParam(required = false) AracDurumu durum) {
        return durum != null ? aracService.durumaGore(durum) : aracService.tumAraclar();
    }

    @GetMapping("/{id}")
    public Arac aracBul(@PathVariable String id) {
        return aracService.aracBul(id);
    }

    @GetMapping("/ara")
    public List<Arac> arama(@RequestParam(name = "q") String q) {
        return aracService.arama(q);
    }

    @PostMapping
    public ResponseEntity<Arac> aracEkle(@Valid @RequestBody Arac arac) {
        return ResponseEntity.ok(aracService.aracEkle(arac));
    }

    @PutMapping("/{id}")
    public Arac aracGuncelle(@PathVariable String id, @Valid @RequestBody Arac arac) {
        return aracService.aracGuncelle(id, arac);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> aracSil(@PathVariable String id) {
        aracService.aracSil(id);
        return ResponseEntity.noContent().build();
    }
}
