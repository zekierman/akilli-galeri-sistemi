package com.galeri.service;

import com.galeri.dto.SatisIstegi;
import com.galeri.dto.SatisYanitDTO;
import com.galeri.exception.IsKuraliException;
import com.galeri.model.Arac;
import com.galeri.model.Calisan;
import com.galeri.model.Musteri;
import com.galeri.model.Satis;
import com.galeri.model.enums.AracDurumu;
import com.galeri.repository.CalisanRepository;
import com.galeri.repository.SatisRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Satış işlemlerini yöneten servis.
 * <p>
 * <b>Sorumlu:</b> Emre Kuzal — Satış Modülü.
 * <p>
 * <b>Mimari değişiklik:</b>
 * <ul>
 * <li>PDR v1.0'da {@code SatisYonetimi} Singleton, içinde
 * {@code SatisKuyrugu} (FIFO) tutuyordu. Yeni mimaride veriler
 * PostgreSQL'de saklanır; {@code @Transactional} ile <b>ACID</b>
 * garantisi sağlanır — eski mimaride satış sırasında uygulama
 * çökerse veri tutarsızlığı oluşabilirdi, artık imkânsız.</li>
 * <li>{@code satisYap()} metodu artık tek bir veritabanı transaction'ı
 * içinde: araç durumunu SATILDI yapar, satışı kaydeder ve çalışanın
 * satış sayacını günceller — biri başarısız olursa hepsi geri alınır.</li>
 * </ul>
 * <p>
 * <b>v2 değişiklik:</b> Public API artık entity yerine {@link SatisYanitDTO}
 * döndürür — proje kuralı gereği entity'ler controller katmanına sızdırılmaz.
 * Lazy ilişkilere ({@code arac, musteri, calisan}) yapılan erişim, DTO'ya
 * çevirme işleminin bu transaction içinde yapılmasını gerektirir; bu yüzden
 * read metotları da {@code @Transactional(readOnly = true)} ile sarılır.
 */
@Service
public class SatisService {

    private final SatisRepository satisRepository;
    private final AracService aracService;
    private final MusteriService musteriService;
    private final CalisanRepository calisanRepository;

    public SatisService(SatisRepository satisRepository,
            AracService aracService,
            MusteriService musteriService,
            CalisanRepository calisanRepository) {
        this.satisRepository = satisRepository;
        this.aracService = aracService;
        this.musteriService = musteriService;
        this.calisanRepository = calisanRepository;
    }

    // ==================================================================
    // Yazma işlemleri
    // ==================================================================

    /**
     * Yeni satış gerçekleştirir. Tek atomik transaction içinde:
     * <ol>
     * <li>Araç ve müşteri varlığı doğrulanır.</li>
     * <li>Aracın {@code SATISTA} olduğu kontrol edilir.</li>
     * <li>Satış kaydı oluşturulur, kâr otomatik hesaplanır.</li>
     * <li>Aracın durumu {@code SATILDI}'ya çevrilir.</li>
     * <li>Çalışanın {@code toplamSatis} sayacı artırılır.</li>
     * </ol>
     * Adımlardan biri patlarsa hepsi rollback olur (ACID).
     *
     * @param istek frontend'den gelen, doğrulanmış satış isteği DTO'su
     * @return oluşturulan satışın yanıt DTO'su
     * @throws IsKuraliException       araç satışta değilse
     * @throws EntityNotFoundException araç/müşteri/çalışan bulunamazsa
     */
    @Transactional
    public SatisYanitDTO satisYap(SatisIstegi istek) {

        Arac arac = aracService.aracBul(istek.aracId());
        if (arac.getDurum() != AracDurumu.SATISTA) {
            throw new IsKuraliException(
                    "Araç şu anda satışta değil. Mevcut durum: " + arac.getDurum());
        }

        Musteri musteri = musteriService.tcIleAra(istek.musteriTc());

        Calisan calisan = (istek.calisanId() == null || istek.calisanId().isBlank())
                ? null
                : calisanRepository.findById(istek.calisanId()).orElseThrow(
                        () -> new EntityNotFoundException(
                                "Çalışan bulunamadı: " + istek.calisanId()));

        Satis satis = new Satis();
        satis.setSatisId("SAT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        satis.setArac(arac);
        satis.setMusteri(musteri);
        satis.setCalisan(calisan);
        satis.setSatisFiyati(istek.satisFiyati());
        satis.setAlisFiyati(arac.getAlisFiyati());
        satis.setSatisTarihi(LocalDateTime.now());
        satis.setOdemeSekli(istek.odemeSekli());
        satis.karHesapla();

        Satis kaydedilen = satisRepository.save(satis);

        // Yan etkiler — aynı transaction içinde
        arac.setDurum(AracDurumu.SATILDI);
        if (calisan != null) {
            calisan.setToplamSatis(calisan.getToplamSatis() + 1);
        }

        return SatisYanitDTO.from(kaydedilen);
    }

    /**
     * Satışı iptal eder ve aracı tekrar satışa çıkarır. PDR §2.4'teki
     * "24 saat içinde iptal" iş kuralı uygulanır.
     */
    @Transactional
    public void satisIptal(String satisId) {
        Satis satis = satisRepository.findById(satisId).orElseThrow(
                () -> new EntityNotFoundException("Satış bulunamadı: " + satisId));

        if (satis.getSatisTarihi().isBefore(LocalDateTime.now().minusHours(24))) {
            throw new IsKuraliException(
                    "24 saatten eski satışlar iptal edilemez.");
        }

        Arac arac = satis.getArac();
        arac.setDurum(AracDurumu.SATISTA);
        satisRepository.delete(satis);
    }

    // ==================================================================
    // Okuma işlemleri (DTO döndürür)
    // ==================================================================

    /** Tüm satışları en yeniden eskiye doğru DTO listesi olarak döndürür. */
    @Transactional(readOnly = true)
    public List<SatisYanitDTO> tumSatislar() {
        return satisRepository.findAllByOrderBySatisTarihiDesc()
                .stream()
                .map(SatisYanitDTO::from)
                .toList();
    }

    /** ID ile tek bir satış DTO'su döndürür. */
    @Transactional(readOnly = true)
    public SatisYanitDTO satisBul(String satisId) {
        return SatisYanitDTO.from(satisEntityBul(satisId));
    }

    /** Belirli bir müşterinin tüm satışları (TC ile). */
    @Transactional(readOnly = true)
    public List<SatisYanitDTO> musteriSatislari(String tcKimlik) {
        return satisRepository.findByMusteri_TcKimlik(tcKimlik)
                .stream()
                .map(SatisYanitDTO::from)
                .toList();
    }

    /**
     * Bir satışın fatura metnini üretir. Entity'ye internal erişim
     * gerektiği için ayrı bir endpoint olarak tutulur (fatura DTO içinde
     * yer almaz; istek üzerine üretilir).
     */
    @Transactional(readOnly = true)
    public String fatura(String satisId) {
        return satisEntityBul(satisId).faturaOlustur();
    }

    // ==================================================================
    // Yardımcı / dahili
    // ==================================================================

    /**
     * Service içi kullanım için entity döndürür. Controller'a sızdırılmaz.
     */
    private Satis satisEntityBul(String satisId) {
        return satisRepository.findById(satisId).orElseThrow(
                () -> new EntityNotFoundException("Satış bulunamadı: " + satisId));
    }

    public double toplamCiro() {
        return satisRepository.toplamCiro();
    }

    public double toplamKar() {
        return satisRepository.toplamKar();
    }

    public long satisSayisi() {
        return satisRepository.count();
    }
}
