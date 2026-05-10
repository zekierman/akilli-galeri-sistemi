package com.galeri.repository;

import com.galeri.model.Satis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Satış Repository — Emre Kuzal'ın modülünün veri erişim katmanı.
 * <b>Mimari değişiklik:</b> PDR v1.0'da {@code SatisKuyrugu} (FIFO Queue)
 * bekleyen satışları tutuyor, ana liste de {@code AracListesi} muadili
 * yapıdaydı. Yeni mimaride PostgreSQL tek tablodan FIFO sırası
 * {@code ORDER BY satis_tarihi ASC} ile, raporlama agregasyonları da
 * {@code SUM/AVG/COUNT GROUP BY} ile elde edilir.
 * <p>
 * <b>Performans kazancı:</b> Eski raporlamada her hesap için tüm
 * AracListesi geziliyordu (O(n) per metric). Yeni mimaride veritabanı
 * indekslenmiş sütunlar üzerinde tek geçişte agrega üretir.
 */
@Repository
public interface SatisRepository extends JpaRepository<Satis, String> {

    // ---------- Listeleme ve filtreleme ----------

    /** FIFO sırasıyla tüm satışlar (eski {@code SatisKuyrugu.tumunuListele()}). */
    List<Satis> findAllByOrderBySatisTarihiAsc();

    /** En son satışlar (Dashboard ve son işlemler için). */
    List<Satis> findAllByOrderBySatisTarihiDesc();

    /** Müşterinin tüm satışları (eski {@code musteriyeGoreAra()}). */
    List<Satis> findByMusteri_TcKimlik(String tcKimlik);

    /** Tarih aralığındaki satışlar. */
    List<Satis> findBySatisTarihiBetween(LocalDateTime baslangic, LocalDateTime bitis);

    // ---------- Raporlama agregasyonları ----------

    /** Toplam ciro (tüm zamanlar). */
    @Query("SELECT COALESCE(SUM(s.satisFiyati), 0) FROM Satis s")
    double toplamCiro();

    /** Toplam kâr. */
    @Query("SELECT COALESCE(SUM(s.kar), 0) FROM Satis s")
    double toplamKar();

    /** Tarih aralığındaki gelir-gider. */
    @Query("""
        SELECT COALESCE(SUM(s.satisFiyati), 0) AS gelir,
               COALESCE(SUM(s.alisFiyati), 0)  AS gider,
               COALESCE(SUM(s.kar), 0)         AS kar,
               COUNT(s)                         AS adet
          FROM Satis s
         WHERE s.satisTarihi BETWEEN :baslangic AND :bitis
        """)
    Object[] gelirGiderOzeti(@Param("baslangic") LocalDateTime baslangic,
                             @Param("bitis")     LocalDateTime bitis);

    /** Aylık satış grafiği için: belirli yılda her ay için satış adedi+tutarı. */
    @Query("""
        SELECT MONTH(s.satisTarihi) AS ay,
               COUNT(s)              AS adet,
               COALESCE(SUM(s.satisFiyati), 0) AS tutar
          FROM Satis s
         WHERE YEAR(s.satisTarihi) = :yil
         GROUP BY MONTH(s.satisTarihi)
         ORDER BY ay
        """)
    List<Object[]> aylikSatislar(@Param("yil") int yil);

    /** En çok satılan markalar. */
    @Query("""
        SELECT s.arac.marka AS marka, COUNT(s) AS adet
          FROM Satis s
         GROUP BY s.arac.marka
         ORDER BY adet DESC
        """)
    List<Object[]> markaBazliSatis();

    /** Ödeme şekli analizi. */
    @Query("""
        SELECT s.odemeSekli AS sekil, COUNT(s) AS adet,
               COALESCE(SUM(s.satisFiyati), 0) AS tutar
          FROM Satis s
         GROUP BY s.odemeSekli
         ORDER BY tutar DESC
        """)
    List<Object[]> odemeSekliAnalizi();

    /** Çalışan başına satış performansı. */
    @Query("""
        SELECT s.calisan.calisanId AS calisanId,
               s.calisan.ad AS ad,
               s.calisan.soyad AS soyad,
               COUNT(s) AS adet,
               COALESCE(SUM(s.satisFiyati), 0) AS ciro,
               COALESCE(SUM(s.kar), 0) AS kar
          FROM Satis s
         WHERE s.calisan IS NOT NULL
         GROUP BY s.calisan.calisanId, s.calisan.ad, s.calisan.soyad
         ORDER BY ciro DESC
        """)
    List<Object[]> calisanPerformansi();
}
