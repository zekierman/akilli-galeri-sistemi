package com.galeri.controller;

import com.galeri.model.Musteri;
import com.galeri.service.MusteriService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/musteriler")
public class MusteriController {

    private final MusteriService musteriService;

    public MusteriController(MusteriService musteriService) {
        this.musteriService = musteriService;
    }

    @GetMapping
    public List<Musteri> tumMusteriler() {
        return musteriService.tumMusteriler();
    }

    @GetMapping("/tc/{tc}")
    public Musteri tcIleAra(@PathVariable String tc) {
        return musteriService.tcIleAra(tc);
    }

    @GetMapping("/ara")
    public List<Musteri> adaGoreAra(@RequestParam(name = "q") String q) {
        return musteriService.adaGoreAra(q);
    }

    @PostMapping
    public ResponseEntity<Musteri> musteriEkle(@Valid @RequestBody Musteri musteri) {
        return ResponseEntity.ok(musteriService.musteriEkle(musteri));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Musteri> musteriGuncelle(@PathVariable String id, @Valid @RequestBody Musteri musteri) {
        return ResponseEntity.ok(musteriService.musteriGuncelle(id, musteri));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> musteriSil(@PathVariable String id) {
        musteriService.musteriSil(id);
        return ResponseEntity.noContent().build();
    }
}
