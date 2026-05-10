package com.galeri.controller;

import com.galeri.model.Musteri;
import com.galeri.service.MusteriService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Müşteri REST API denetleyicisi.
 *
 * <p>Temel uç noktalar:
 * <ul>
 *   <li>{@code GET  /api/musteriler}         — tüm müşteriler</li>
 *   <li>{@code GET  /api/musteriler/tc/{tc}} — TC ile arama</li>
 *   <li>{@code GET  /api/musteriler/ara?q=X} — ad/soyad arama</li>
 *   <li>{@code POST /api/musteriler}          — yeni müşteri ekle</li>
 *   <li>{@code PUT  /api/musteriler/{id}}     — müşteri güncelle</li>
 *   <li>{@code DELETE /api/musteriler/{id}}   — müşteri sil</li>
 * </ul>
 *
 * <p>Doğrulama hataları ve iş kuralı ihlalleri
 * {@code GlobalExceptionHandler} tarafından JSON formatında döndürülür.
 */
@RestController
@RequestMapping("/api/musteriler")
public class MusteriController {

    private final MusteriService musteriService;

    public MusteriController(MusteriService musteriService) {
        this.musteriService = musteriService;
    }

    /**
     * Sistemdeki tüm müşterileri listeler.
     *
     * @return müşteri listesi (HTTP 200)
     */
    @GetMapping
    public List<Musteri> tumMusteriler() {
        return musteriService.tumMusteriler();
    }

    /**
     * TC kimlik numarasıyla tek müşteri getirir.
     *
     * @param tc 11 haneli TC kimlik numarası
     * @return bulunan müşteri (HTTP 200) ya da 404
     */
    @GetMapping("/tc/{tc}")
    public Musteri tcIleAra(@PathVariable String tc) {
        return musteriService.tcIleAra(tc);
    }

    /**
     * Ad veya soyadda arama yapar.
     *
     * @param q aranacak kelime parçası (örn. {@code ?q=ahmet})
     * @return eşleşen müşteri listesi (HTTP 200)
     */
    @GetMapping("/ara")
    public List<Musteri> adaGoreAra(@RequestParam(name = "q") String q) {
        return musteriService.adaGoreAra(q);
    }

    /**
     * Yeni müşteri kaydı oluşturur.
     *
     * @param musteri istek gövdesindeki müşteri verisi (@Valid ile doğrulanır)
     * @return kaydedilen müşteri (HTTP 200)
     */
    @PostMapping
    public ResponseEntity<Musteri> musteriEkle(@Valid @RequestBody Musteri musteri) {
        return ResponseEntity.ok(musteriService.musteriEkle(musteri));
    }

    /**
     * Mevcut müşteri bilgilerini günceller.
     *
     * @param id      güncellenecek müşteri ID'si
     * @param musteri yeni değerleri taşıyan istek gövdesi
     * @return güncellenmiş müşteri (HTTP 200)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Musteri> musteriGuncelle(@PathVariable String id, @Valid @RequestBody Musteri musteri) {
        return ResponseEntity.ok(musteriService.musteriGuncelle(id, musteri));
    }

    /**
     * Müşteriyi sistemden siler.
     *
     * @param id silinecek müşteri ID'si
     * @return boş yanıt (HTTP 204)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> musteriSil(@PathVariable String id) {
        musteriService.musteriSil(id);
        return ResponseEntity.noContent().build();
    }
}
