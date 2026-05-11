package com.galeri.repository;

import com.galeri.model.MuayeneBilgisi;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MuayeneRepository extends JpaRepository<MuayeneBilgisi, String> {

    @EntityGraph(attributePaths = {"arac"})
    List<MuayeneBilgisi> findBySonrakiMuayeneTarihiBefore(LocalDate tarih);

    @EntityGraph(attributePaths = {"arac"})
    List<MuayeneBilgisi> findBySonrakiMuayeneTarihiBetween(LocalDate baslangic, LocalDate bitis);

    @EntityGraph(attributePaths = {"arac"})
    List<MuayeneBilgisi> findByArac_AracId(String aracId);
}
