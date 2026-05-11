package com.galeri.repository;

import com.galeri.model.SigortaBilgisi;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SigortaRepository extends JpaRepository<SigortaBilgisi, String> {

    /** Süresi dolmuş sigortalar. */
    @EntityGraph(attributePaths = {"arac"})
    List<SigortaBilgisi> findByBitisTarihiBefore(LocalDate tarih);

    /** Yaklaşan sigortalar — bitis_tarihi indeksi sayesinde hızlı. */
    @EntityGraph(attributePaths = {"arac"})
    List<SigortaBilgisi> findByBitisTarihiBetween(LocalDate baslangic, LocalDate bitis);

    @EntityGraph(attributePaths = {"arac"})
    List<SigortaBilgisi> findByArac_AracId(String aracId);
}
