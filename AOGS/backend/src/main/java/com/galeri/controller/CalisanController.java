package com.galeri.controller;

import com.galeri.model.Calisan;
import com.galeri.service.CalisanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Çalışan Modülü REST API.
 */
@RestController
@RequestMapping("/api/calisanlar")
public class CalisanController {

    private final CalisanService calisanService;

    public CalisanController(CalisanService calisanService) {
        this.calisanService = calisanService;
    }

    @GetMapping
    public List<Calisan> tumCalisanlar() {
        return calisanService.tumCalisanlar();
    }

    @GetMapping("/{id}")
    public Calisan calisanBul(@PathVariable String id) {
        return calisanService.calisanBul(id);
    }

    @PostMapping
    public ResponseEntity<Calisan> calisanEkle(@RequestBody Calisan calisan) {
        return ResponseEntity.ok(calisanService.calisanEkle(calisan));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Calisan> calisanGuncelle(@PathVariable String id, @RequestBody Calisan calisan) {
        return ResponseEntity.ok(calisanService.calisanGuncelle(id, calisan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> calisanSil(@PathVariable String id) {
        calisanService.calisanSil(id);
        return ResponseEntity.noContent().build();
    }
}
