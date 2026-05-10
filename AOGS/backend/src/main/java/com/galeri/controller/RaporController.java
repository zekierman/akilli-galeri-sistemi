package com.galeri.controller;

import com.galeri.dto.AylikSatisDTO;
import com.galeri.dto.CalisanPerformansDTO;
import com.galeri.dto.DashboardOzetDTO;
import com.galeri.dto.GelirGiderDTO;
import com.galeri.dto.MarkaSatisDTO;
import com.galeri.dto.OdemeSekliDTO;
import com.galeri.dto.StokDegerDTO;
import com.galeri.service.RaporlayiciService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Raporlama REST API.
 * <p>
 * <b>Sorumlu:</b> Emre Kuzal — Raporlama Modülü.
 * <p>
 * <b>v2 değişiklik:</b> Tüm endpoint'ler artık tip güvenli DTO döndürür;
 * önceki sürümdeki {@code Map<String, Object>} yapısının yerine sözleşme
 * netleştirilmiştir. Bu sayede:
 * <ul>
 *   <li>OpenAPI/Swagger şeması tam dokümante edilir.</li>
 *   <li>Frontend TypeScript tarafına çevrildiğinde otomatik tip üretilebilir.</li>
 *   <li>Alan adı değişikliği derleme hatası olarak yakalanır (regresyon önleme).</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/raporlar")
public class RaporController {

    private final RaporlayiciService raporlayici;

    public RaporController(RaporlayiciService raporlayici) {
        this.raporlayici = raporlayici;
    }

    /** Dashboard kartları için tek istekte özet. */
    @GetMapping("/ozet")
    public DashboardOzetDTO genelOzet() {
        return raporlayici.genelOzet();
    }

    /** Tarih aralığında gelir-gider raporu. */
    @GetMapping("/gelir-gider")
    public GelirGiderDTO gelirGider(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime baslangic,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bitis) {
        return raporlayici.gelirGiderRaporu(baslangic, bitis);
    }

    /** Aylık satış grafiği için yıl bazlı veri. */
    @GetMapping("/aylik-satis")
    public List<AylikSatisDTO> aylikSatis(@RequestParam int yil) {
        return raporlayici.aylikSatisGrafigi(yil);
    }

    /** En çok satılan markalar. */
    @GetMapping("/marka-satis")
    public List<MarkaSatisDTO> markaSatis() {
        return raporlayici.markaBazliSatis();
    }

    /** Ödeme şekli analizi. */
    @GetMapping("/odeme-sekli")
    public List<OdemeSekliDTO> odemeSekli() {
        return raporlayici.odemeSekliAnalizi();
    }

    /** Çalışan performans tablosu. */
    @GetMapping("/calisan-performans")
    public List<CalisanPerformansDTO> calisanPerformansi() {
        return raporlayici.calisanPerformansi();
    }

    /** Stoktaki araçların toplam değer raporu. */
    @GetMapping("/stok-deger")
    public StokDegerDTO stokDeger() {
        return raporlayici.stokDegerRaporu();
    }
}
