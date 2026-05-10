package com.galeri.repository;

import com.galeri.model.Ekspertiz;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Ekspertiz Repository.
 * <p>
 * <b>Mimari değişiklik:</b> PDR v1.0'da {@code EkspertizYigini} (Stack/LIFO)
 * son ekspertize O(1) erişim sağlıyordu. Yeni mimaride
 * {@code findFirstByOrderByEkspertizTarihiDesc()} aynı işi indeks
 * üzerinden yapar (O(log n)) — pratikte aynı, ölçeklenirken çok daha güvenli.
 */
@Repository
public interface EkspertizRepository extends JpaRepository<Ekspertiz, String> {

    /** Son yapılan ekspertiz (eski {@code Stack.peek()}). */
    @EntityGraph(attributePaths = {"arac", "calisan", "parcaDurumlari"})
    Optional<Ekspertiz> findFirstByOrderByEkspertizTarihiDesc();

    /** Bir araç için tüm ekspertizler (en yeniden eskiye). */
    @EntityGraph(attributePaths = {"arac", "calisan", "parcaDurumlari"})
    List<Ekspertiz> findByArac_AracIdOrderByEkspertizTarihiDesc(String aracId);

    @Override
    @EntityGraph(attributePaths = {"arac", "calisan", "parcaDurumlari"})
    List<Ekspertiz> findAll();

    @EntityGraph(attributePaths = {"arac", "calisan", "parcaDurumlari"})
    Optional<Ekspertiz> findById(String id);
}
