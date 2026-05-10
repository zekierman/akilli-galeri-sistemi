package com.galeri.service;

import com.galeri.dto.AylikSatisDTO;
import com.galeri.dto.CalisanPerformansDTO;
import com.galeri.dto.DashboardOzetDTO;
import com.galeri.dto.GelirGiderDTO;
import com.galeri.dto.MarkaSatisDTO;
import com.galeri.dto.OdemeSekliDTO;
import com.galeri.dto.StokDegerDTO;
import com.galeri.model.enums.AracDurumu;
import com.galeri.repository.SatisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Raporlama / iş zekâsı servisi.
 * <p>
 * <b>Sorumlu:</b> Emre Kuzal — Raporlama Modülü.
 * <p>
 * <b>Performans devrimi:</b> PDR v1.0'da raporlar uygulama belleğinde
 * tüm listeyi gezerek (O(n)) hesaplanıyordu. Yeni mimaride PostgreSQL
 * SQL agregasyonları ({@code SUM, COUNT, GROUP BY}) ile hesaplama
 * veritabanı motoruna delegasyon edilir; indeksli sütunlar üzerinde
 * milisaniyelik yanıt süreleri elde edilir.
 * <p>
 * <b>v2 değişiklik:</b> Tüm metotlar artık tip güvenli DTO döndürür —
 * önceki sürümde {@code Map<String, Object>} dönülüyordu, bu hem
 * frontend tarafında dokümantasyonu zorlaştırıyor hem de derleme zamanı
 * tip kontrolünden mahrum bırakıyordu.
 */
@Service
public class RaporlayiciService {

    private final SatisRepository satisRepository;
    private final AracService     aracService;

    public RaporlayiciService(SatisRepository satisRepository, AracService aracService) {
        this.satisRepository = satisRepository;
        this.aracService     = aracService;
    }

    /** Genel özet (Dashboard üst kartları için). */
    @Transactional(readOnly = true)
    public DashboardOzetDTO genelOzet() {
        return new DashboardOzetDTO(
            satisRepository.toplamCiro(),
            satisRepository.toplamKar(),
            satisRepository.count(),
            aracService.toplamStokDegeri(),
            aracService.aracSayisi(AracDurumu.SATISTA)
        );
    }

    /** Tarih aralığında gelir-gider raporu. */
    @Transactional(readOnly = true)
    public GelirGiderDTO gelirGiderRaporu(LocalDateTime baslangic, LocalDateTime bitis) {
        Object[] satir = satisRepository.gelirGiderOzeti(baslangic, bitis);
        return new GelirGiderDTO(
            baslangic,
            bitis,
            toDouble(satir[0]),  // gelir
            toDouble(satir[1]),  // gider
            toDouble(satir[2]),  // kar
            toLong(satir[3])     // adet
        );
    }

    /** Aylık satış grafiği — verilen yıl için ay bazlı dağılım. */
    @Transactional(readOnly = true)
    public List<AylikSatisDTO> aylikSatisGrafigi(int yil) {
        return satisRepository.aylikSatislar(yil).stream()
            .map(s -> new AylikSatisDTO(
                toInt(s[0]),     // ay
                toLong(s[1]),    // adet
                toDouble(s[2])   // tutar
            ))
            .toList();
    }

    /** En çok satılan markalar (azalan sırada). */
    @Transactional(readOnly = true)
    public List<MarkaSatisDTO> markaBazliSatis() {
        return satisRepository.markaBazliSatis().stream()
            .map(r -> new MarkaSatisDTO(
                (String) r[0],   // marka
                toLong(r[1])     // adet
            ))
            .toList();
    }

    /** Ödeme şekli analizi. */
    @Transactional(readOnly = true)
    public List<OdemeSekliDTO> odemeSekliAnalizi() {
        return satisRepository.odemeSekliAnalizi().stream()
            .map(r -> new OdemeSekliDTO(
                (String) r[0],   // odemeSekli
                toLong(r[1]),    // adet
                toDouble(r[2])   // tutar
            ))
            .toList();
    }

    /** Çalışan performans sıralaması (ciroya göre azalan). */
    @Transactional(readOnly = true)
    public List<CalisanPerformansDTO> calisanPerformansi() {
        return satisRepository.calisanPerformansi().stream()
            .map(r -> new CalisanPerformansDTO(
                (String) r[0],   // calisanId
                (String) r[1],   // ad
                (String) r[2],   // soyad
                toLong(r[3]),    // adet
                toDouble(r[4]),  // ciro
                toDouble(r[5])   // kar
            ))
            .toList();
    }

    /** Stok değer raporu — kalan stoğun toplam liste değeri. */
    @Transactional(readOnly = true)
    public StokDegerDTO stokDegerRaporu() {
        return new StokDegerDTO(
            aracService.toplamStokDegeri(),
            aracService.aracSayisi(AracDurumu.SATISTA)
        );
    }

    // ==================================================================
    // JPQL Object[] satırlarını tip güvenli dönüştürme yardımcıları
    // ==================================================================

    private static double toDouble(Object o) {
        return o == null ? 0.0 : ((Number) o).doubleValue();
    }
    private static long toLong(Object o) {
        return o == null ? 0L : ((Number) o).longValue();
    }
    private static int toInt(Object o) {
        return o == null ? 0 : ((Number) o).intValue();
    }
}
