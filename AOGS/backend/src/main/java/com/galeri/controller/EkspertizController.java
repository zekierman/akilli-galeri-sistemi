package com.galeri.controller;

import com.galeri.model.Ekspertiz;
import com.galeri.service.EkspertizService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ekspertizler")
public class EkspertizController {

    private final EkspertizService ekspertizService;

    public EkspertizController(EkspertizService ekspertizService) {
        this.ekspertizService = ekspertizService;
    }

    @GetMapping
    public List<Ekspertiz> tumEkspertizler() {
        return ekspertizService.tumEkspertizler();
    }

    @GetMapping("/son")
    public Optional<Ekspertiz> sonEkspertiz() {
        return ekspertizService.sonEkspertiz();
    }

    @GetMapping("/arac/{aracId}")
    public List<Ekspertiz> aracaGore(@PathVariable String aracId) {
        return ekspertizService.aracaGoreEkspertizler(aracId);
    }

    @PostMapping
    public ResponseEntity<Ekspertiz> ekspertizEkle(@RequestBody Ekspertiz ekspertiz) {
        return ResponseEntity.ok(ekspertizService.ekspertizEkle(ekspertiz));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ekspertiz> ekspertizGuncelle(@PathVariable String id, @RequestBody Ekspertiz ekspertiz) {
        return ResponseEntity.ok(ekspertizService.ekspertizGuncelle(id, ekspertiz));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> ekspertizSil(@PathVariable String id) {
        ekspertizService.ekspertizSil(id);
        return ResponseEntity.noContent().build();
    }
}
