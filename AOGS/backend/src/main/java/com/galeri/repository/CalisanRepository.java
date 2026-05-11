package com.galeri.repository;

import com.galeri.model.Calisan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CalisanRepository extends JpaRepository<Calisan, String> {
    Optional<Calisan> findByTcKimlik(String tcKimlik);
}
