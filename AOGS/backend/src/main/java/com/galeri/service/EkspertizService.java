package com.galeri.service;

import com.galeri.model.Arac;
import com.galeri.model.Calisan;
import com.galeri.repository.AracRepository;
import com.galeri.repository.CalisanRepository;
import com.galeri.model.Ekspertiz;
import com.galeri.repository.EkspertizRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EkspertizService {

    private final EkspertizRepository ekspertizRepository;
    private final AracRepository aracRepository;
    private final CalisanRepository calisanRepository;

    public EkspertizService(EkspertizRepository ekspertizRepository, AracRepository aracRepository, CalisanRepository calisanRepository) {
        this.ekspertizRepository = ekspertizRepository;
        this.aracRepository = aracRepository;
        this.calisanRepository = calisanRepository;
    }

    @Transactional
    public Ekspertiz ekspertizEkle(Ekspertiz ekspertiz) {
        if (ekspertiz.getEkspertizId() == null || ekspertiz.getEkspertizId().isBlank()) {
            ekspertiz.setEkspertizId(
                "EKS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        // Araç entity'sini veritabanından tam olarak çekerek Proxy hatalarını (no Session) engelliyoruz
        if (ekspertiz.getArac() != null && ekspertiz.getArac().getAracId() != null) {
            Arac gercekArac = aracRepository.findById(ekspertiz.getArac().getAracId())
                .orElseThrow(() -> new EntityNotFoundException("Araç bulunamadı: " + ekspertiz.getArac().getAracId()));
            ekspertiz.setArac(gercekArac);
        }
        
        // Çalışan (Opsiyonel) proxy hatasını engellemek için
        if (ekspertiz.getCalisan() != null && ekspertiz.getCalisan().getCalisanId() != null) {
            Calisan gercekCalisan = calisanRepository.findById(ekspertiz.getCalisan().getCalisanId())
                .orElseThrow(() -> new EntityNotFoundException("Çalışan bulunamadı: " + ekspertiz.getCalisan().getCalisanId()));
            ekspertiz.setCalisan(gercekCalisan);
        }

        ekspertiz.setEkspertizPuani(ekspertiz.ekspertizPuaniHesapla());
        return ekspertizRepository.save(ekspertiz);
    }

    @Transactional(readOnly = true)
    public List<Ekspertiz> tumEkspertizler() {
        return ekspertizRepository.findAll();
    }

    /** Eski {@code Stack.peek()} muadili — en son yapılan ekspertiz. */
    @Transactional(readOnly = true)
    public Optional<Ekspertiz> sonEkspertiz() {
        return ekspertizRepository.findFirstByOrderByEkspertizTarihiDesc();
    }

    @Transactional(readOnly = true)
    public List<Ekspertiz> aracaGoreEkspertizler(String aracId) {
        return ekspertizRepository.findByArac_AracIdOrderByEkspertizTarihiDesc(aracId);
    }

    @Transactional(readOnly = true)
    public Ekspertiz ekspertizBul(String id) {
        return ekspertizRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("Ekspertiz bulunamadı: " + id));
    }

    @Transactional
    public Ekspertiz ekspertizGuncelle(String id, Ekspertiz guncelData) {
        Ekspertiz mevcut = ekspertizBul(id);
        
        mevcut.setGenelDegerlendirme(guncelData.getGenelDegerlendirme());
        if (guncelData.getParcaDurumlari() != null) {
            mevcut.getParcaDurumlari().clear();
            mevcut.getParcaDurumlari().putAll(guncelData.getParcaDurumlari());
        }
        
        if (guncelData.getArac() != null && guncelData.getArac().getAracId() != null) {
            Arac gercekArac = aracRepository.findById(guncelData.getArac().getAracId())
                .orElseThrow(() -> new EntityNotFoundException("Araç bulunamadı: " + guncelData.getArac().getAracId()));
            mevcut.setArac(gercekArac);
        }

        if (guncelData.getCalisan() != null && guncelData.getCalisan().getCalisanId() != null) {
            Calisan gercekCalisan = calisanRepository.findById(guncelData.getCalisan().getCalisanId())
                .orElseThrow(() -> new EntityNotFoundException("Çalışan bulunamadı: " + guncelData.getCalisan().getCalisanId()));
            mevcut.setCalisan(gercekCalisan);
        } else {
            mevcut.setCalisan(null);
        }
        
        mevcut.setEkspertizPuani(mevcut.ekspertizPuaniHesapla());
        return ekspertizRepository.save(mevcut);
    }

    @Transactional
    public void ekspertizSil(String id) {
        if (!ekspertizRepository.existsById(id)) {
            throw new EntityNotFoundException("Ekspertiz bulunamadı: " + id);
        }
        ekspertizRepository.deleteById(id);
    }
}
