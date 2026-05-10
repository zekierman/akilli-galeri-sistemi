package com.galeri.repository;

import com.galeri.model.Arac;
import com.galeri.model.enums.AracDurumu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Araç Repository (Veri Erişim Katmanı).
 * <p>
 * <b>Mimari değişiklik:</b> PDR v1.0'da {@code AracListesi} özel
 * LinkedList yapısı bu işi yapıyordu (O(1) ekleme, O(n) arama).
 * Spring Data JPA'nın {@code JpaRepository} arayüzü; CRUD operasyonlarını
 * otomatik üretir, plaka ve marka indeksleri sayesinde aramayı O(log n)'e
 * indirir ve veriyi <b>kalıcı</b> olarak PostgreSQL'de tutar.
 * <p>
 * <b>Strategy Pattern muadili:</b> Eski {@code sirala(Comparator)} ve
 * {@code filtrele(Predicate)} metotlarının yerini, dinamik sorgu için
 * {@code @Query} JPQL ifadeleri ve metot isimlendirme tabanlı sorgu
 * türetmesi alır.
 */
@Repository
public interface AracRepository extends JpaRepository<Arac, String> {

    /** Plaka ile arama - O(log n), unique B-Tree indeks üzerinden. */
    Optional<Arac> findByPlakaNo(String plakaNo);

    /** Marka adına göre filtreleme — eski {@code markayaGoreFiltrele()}. */
    List<Arac> findByMarkaIgnoreCase(String marka);

    /** Duruma göre filtreleme. */
    List<Arac> findByDurum(AracDurumu durum);

    /** Fiyat aralığında filtreleme — eski {@code fiyatAraliginaGoreFiltrele()}. */
    List<Arac> findBySatisFiyatiBetween(double min, double max);

    /**
     * Marka, model veya plaka içinde arama (SQL LIKE).
     * UI'daki ana arama kutusu için kullanılır.
     */
    @Query("""
        SELECT a FROM Arac a
        WHERE LOWER(a.marka) LIKE LOWER(CONCAT('%', :kriter, '%'))
           OR LOWER(a.model) LIKE LOWER(CONCAT('%', :kriter, '%'))
           OR LOWER(a.plakaNo) LIKE LOWER(CONCAT('%', :kriter, '%'))
        """)
    List<Arac> aramaYap(@Param("kriter") String kriter);

    /** Toplam stok değeri (SATISTA olanlar). */
    @Query("SELECT COALESCE(SUM(a.satisFiyati), 0) FROM Arac a WHERE a.durum = 'SATISTA'")
    double toplamStokDegeri();

    /** Belirli durumda kaç araç var? (Dashboard kartları için.) */
    long countByDurum(AracDurumu durum);
}
