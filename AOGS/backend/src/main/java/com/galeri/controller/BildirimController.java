package com.galeri.controller;

import com.galeri.model.MuayeneBilgisi;
import com.galeri.model.SigortaBilgisi;
import com.galeri.service.BildirimService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bildirimler")
public class BildirimController {

    private final BildirimService bildirimService;

    public BildirimController(BildirimService bildirimService) {
        this.bildirimService = bildirimService;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboardOzeti() {
        return bildirimService.dashboardOzeti();
    }

    @GetMapping("/sigorta/yaklasan")
    public List<SigortaBilgisi> yaklasanSigortalar(@RequestParam(defaultValue = "30") int gun) {
        return bildirimService.yaklasanSigortalar(gun);
    }

    @GetMapping("/muayene/yaklasan")
    public List<MuayeneBilgisi> yaklasanMuayeneler(@RequestParam(defaultValue = "30") int gun) {
        return bildirimService.yaklasanMuayeneler(gun);
    }

    @PostMapping("/sigorta")
    public ResponseEntity<SigortaBilgisi> sigortaEkle(@RequestBody SigortaBilgisi sigorta) {
        return ResponseEntity.ok(bildirimService.sigortaEkle(sigorta));
    }

    @PutMapping("/sigorta/{id}")
    public ResponseEntity<SigortaBilgisi> sigortaGuncelle(@PathVariable String id, @RequestBody SigortaBilgisi sigorta) {
        return ResponseEntity.ok(bildirimService.sigortaGuncelle(id, sigorta));
    }

    @DeleteMapping("/sigorta/{id}")
    public ResponseEntity<Void> sigortaSil(@PathVariable String id) {
        bildirimService.sigortaSil(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/muayene")
    public ResponseEntity<MuayeneBilgisi> muayeneEkle(@RequestBody MuayeneBilgisi muayene) {
        return ResponseEntity.ok(bildirimService.muayeneEkle(muayene));
    }

    @PutMapping("/muayene/{id}")
    public ResponseEntity<MuayeneBilgisi> muayeneGuncelle(@PathVariable String id, @RequestBody MuayeneBilgisi muayene) {
        return ResponseEntity.ok(bildirimService.muayeneGuncelle(id, muayene));
    }

    @DeleteMapping("/muayene/{id}")
    public ResponseEntity<Void> muayeneSil(@PathVariable String id) {
        bildirimService.muayeneSil(id);
        return ResponseEntity.noContent().build();
    }
}
