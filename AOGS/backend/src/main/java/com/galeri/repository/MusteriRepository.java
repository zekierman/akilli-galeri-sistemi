package com.galeri.repository;

import com.galeri.model.Musteri;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Müşteri Repository.
 * <p>
 * <b>Mimari değişiklik:</b> PDR v1.0'da {@code MusteriAgaci} (Binary
 * Search Tree) bu işi yapıyordu — TC üzerinde O(log n) arama. Yeni
 * mimaride {@code tc_kimlik} sütununda <b>UNIQUE B-Tree indeksi</b> aynı
 * O(log n) garantisini, ek olarak kalıcılık ve eşzamanlı erişim ile
 * sunar. PDR'ın bahsettiği "ağaç dengesizliği" sorunu (sıralı eklemede
 * dejenerasyon) PostgreSQL B-Tree'sinde otomatik dengeleme ile çözülür.
 */
@Repository
public interface MusteriRepository extends JpaRepository<Musteri, String> {

    /** TC ile müşteri arama (eski {@code MusteriAgaci.ara(tc)} muadili). */
    Optional<Musteri> findByTcKimlik(String tcKimlik);

    /** Ad veya soyadda arama (case-insensitive). */
    List<Musteri> findByAdContainingIgnoreCaseOrSoyadContainingIgnoreCase(String ad, String soyad);

    /** TC zaten var mı? — Müşteri eklemeden önce duplicate kontrolü için. */
    boolean existsByTcKimlik(String tcKimlik);
}
