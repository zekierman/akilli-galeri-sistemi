package com.galeri.service;

import com.galeri.exception.IsKuraliException;
import com.galeri.model.Arac;
import com.galeri.model.enums.AracDurumu;
import com.galeri.repository.AracRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Araç iş mantığı servisi.
 * <p>
 * <b>Mimari değişiklik:</b> PDR v1.0'da {@code AracYonetimi} Singleton
 * pattern ile yazılmıştı ({@code getInstance()}). Spring Boot'ta
 * {@code @Service} sınıfları varsayılan olarak Singleton scope'tadır;
 * dolayısıyla "tek veri kaynağı" garantisi devam eder, ek olarak
 * <i>constructor injection</i> ile test edilebilirlik kazanılır.
 */
@Service
public class AracService {

    private final AracRepository aracRepository;

    public AracService(AracRepository aracRepository) {
        this.aracRepository = aracRepository;
    }

    @Transactional
    public Arac aracEkle(Arac arac) {
        if (arac.getAracId() == null || arac.getAracId().isBlank()) {
            arac.setAracId("ARC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        // Plaka benzersizliği kontrolü
        aracRepository.findByPlakaNo(arac.getPlakaNo()).ifPresent(a -> {
            throw new IsKuraliException("Bu plaka zaten kayıtlı: " + arac.getPlakaNo());
        });
        return aracRepository.save(arac);
    }

    public Arac aracBul(String aracId) {
        return aracRepository.findById(aracId).orElseThrow(
            () -> new EntityNotFoundException("Araç bulunamadı: " + aracId));
    }

    @Transactional
    public Arac aracGuncelle(String aracId, Arac yeni) {
        Arac mevcut = aracBul(aracId);
        mevcut.setMarka(yeni.getMarka());
        mevcut.setModel(yeni.getModel());
        mevcut.setYil(yeni.getYil());
        mevcut.setKm(yeni.getKm());
        mevcut.setSatisFiyati(yeni.getSatisFiyati());
        mevcut.setDurum(yeni.getDurum());
        return aracRepository.save(mevcut);
    }

    @Transactional
    public void aracSil(String aracId) {
        Arac arac = aracBul(aracId);
        if (arac.getDurum() == AracDurumu.SATILDI) {
            throw new IsKuraliException("Satılmış araç silinemez (kayıt bütünlüğü).");
        }
        aracRepository.deleteById(aracId);
    }

    @Transactional(readOnly = true)
    public List<Arac> tumAraclar() {
        return aracRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Arac> arama(String kriter) {
        return (kriter == null || kriter.isBlank())
                ? aracRepository.findAll()
                : aracRepository.aramaYap(kriter);
    }

    @Transactional(readOnly = true)
    public List<Arac> durumaGore(AracDurumu durum) {
        return aracRepository.findByDurum(durum);
    }

    public double toplamStokDegeri() {
        return aracRepository.toplamStokDegeri();
    }

    public long aracSayisi(AracDurumu durum) {
        return aracRepository.countByDurum(durum);
    }
}
