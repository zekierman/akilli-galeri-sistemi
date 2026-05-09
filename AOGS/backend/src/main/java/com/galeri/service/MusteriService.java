package com.galeri.service;

import com.galeri.exception.IsKuraliException;
import com.galeri.model.Musteri;
import com.galeri.repository.MusteriRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MusteriService {

    private final MusteriRepository musteriRepository;

    public MusteriService(MusteriRepository musteriRepository) {
        this.musteriRepository = musteriRepository;
    }

    @Transactional
    public Musteri musteriEkle(Musteri musteri) {
        if (musteri.getMusteriId() == null || musteri.getMusteriId().isBlank()) {
            musteri.setMusteriId("MUS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        if (musteriRepository.existsByTcKimlik(musteri.getTcKimlik())) {
            throw new IsKuraliException("Bu TC kimlik zaten kayıtlı: " + musteri.getTcKimlik());
        }
        return musteriRepository.save(musteri);
    }

    /** TC ile arama — eski {@code MusteriAgaci.ara(tc)} muadili. */
    public Musteri tcIleAra(String tcKimlik) {
        return musteriRepository.findByTcKimlik(tcKimlik).orElseThrow(
            () -> new EntityNotFoundException("Müşteri bulunamadı: TC=" + tcKimlik));
    }

    public List<Musteri> tumMusteriler() {
        return musteriRepository.findAll();
    }

    public List<Musteri> adaGoreAra(String parca) {
        return musteriRepository
            .findByAdContainingIgnoreCaseOrSoyadContainingIgnoreCase(parca, parca);
    }

    @Transactional
    public Musteri musteriGuncelle(String id, Musteri guncelMusteri) {
        Musteri mevcut = musteriRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("Müşteri bulunamadı: " + id));
        
        mevcut.setAd(guncelMusteri.getAd());
        mevcut.setSoyad(guncelMusteri.getSoyad());
        // TC kimlik bilerek değiştirilmez, primary niteliğindedir.
        mevcut.setTelefon(guncelMusteri.getTelefon());
        mevcut.setEmail(guncelMusteri.getEmail());
        mevcut.setAdres(guncelMusteri.getAdres());
        mevcut.setNotlar(guncelMusteri.getNotlar());
        
        return musteriRepository.save(mevcut);
    }

    @Transactional
    public void musteriSil(String musteriId) {
        if (!musteriRepository.existsById(musteriId)) {
            throw new EntityNotFoundException("Müşteri bulunamadı: " + musteriId);
        }
        musteriRepository.deleteById(musteriId);
    }
}
