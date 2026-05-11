package com.galeri.service;

import com.galeri.exception.IsKuraliException;
import com.galeri.model.Calisan;
import com.galeri.repository.CalisanRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Çalışan Modülü İş Mantığı (Eray Gök / Zeki Erman).
 */
@Service
public class CalisanService {

    private final CalisanRepository calisanRepository;

    public CalisanService(CalisanRepository calisanRepository) {
        this.calisanRepository = calisanRepository;
    }

    public List<Calisan> tumCalisanlar() {
        return calisanRepository.findAll();
    }

    public Calisan calisanBul(String id) {
        return calisanRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("Çalışan bulunamadı: " + id));
    }

    @Transactional
    public Calisan calisanEkle(Calisan calisan) {
        if (calisan.getCalisanId() == null || calisan.getCalisanId().isBlank()) {
            calisan.setCalisanId("CLS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        if (calisan.getIseBaslamaTarihi() == null) {
            calisan.setIseBaslamaTarihi(LocalDate.now());
        }
        if (calisanRepository.findByTcKimlik(calisan.getTcKimlik()).isPresent()) {
            throw new IsKuraliException("Bu TC kimlik numarası ile kayıtlı bir çalışan zaten var.");
        }
        return calisanRepository.save(calisan);
    }

    @Transactional
    public Calisan calisanGuncelle(String id, Calisan guncelData) {
        Calisan mevcut = calisanBul(id);
        
        mevcut.setAd(guncelData.getAd());
        mevcut.setSoyad(guncelData.getSoyad());
        // tc kimlik değiştirilmez
        mevcut.setPozisyon(guncelData.getPozisyon());
        mevcut.setMaas(guncelData.getMaas());
        if (guncelData.getIseBaslamaTarihi() != null) {
            mevcut.setIseBaslamaTarihi(guncelData.getIseBaslamaTarihi());
        }
        
        return calisanRepository.save(mevcut);
    }

    @Transactional
    public void calisanSil(String id) {
        if (!calisanRepository.existsById(id)) {
            throw new EntityNotFoundException("Çalışan bulunamadı: " + id);
        }
        calisanRepository.deleteById(id);
    }
}
