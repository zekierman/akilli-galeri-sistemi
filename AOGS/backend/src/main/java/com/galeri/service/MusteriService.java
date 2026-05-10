package com.galeri.service;

import com.galeri.exception.IsKuraliException;
import com.galeri.model.Musteri;
import com.galeri.repository.MusteriRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Müşteri iş mantığı servisi.
 *
 * <p>Tüm müşteri CRUD operasyonlarını, iş kuralı doğrulamalarını ve
 * arama işlemlerini tek noktada yönetir. Controller katmanı bu sınıf
 * üzerinden çalışır; repository'e doğrudan erişmez.
 *
 * <p><b>İş kuralı:</b> Aynı TC kimlik ile iki farklı müşteri kaydı
 * açılamaz ({@link IsKuraliException} fırlatılır).
 */
@Service
public class MusteriService {

    private final MusteriRepository musteriRepository;

    public MusteriService(MusteriRepository musteriRepository) {
        this.musteriRepository = musteriRepository;
    }

    /**
     * Yeni müşteri kaydı oluşturur.
     *
     * <p>ID verilmemişse {@code "MUS-XXXXXXXX"} formatında otomatik üretilir.
     * TC kimlik benzersizliği kontrol edilir; çakışma varsa işlem reddedilir.
     *
     * @param musteri kaydedilecek müşteri nesnesi (id alanı boş bırakılabilir)
     * @return veritabanına kaydedilmiş müşteri
     * @throws IsKuraliException aynı TC kimlik zaten mevcutsa
     */
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

    /**
     * TC kimlik numarasına göre müşteri getirir.
     *
     * <p>Eski {@code MusteriAgaci.ara(tc)} metodunun JPA muadili.
     * Veritabanı indeksi sayesinde O(log n) performans garantisi sağlar.
     *
     * @param tcKimlik 11 haneli TC kimlik numarası
     * @return eşleşen müşteri
     * @throws EntityNotFoundException kayıt bulunamazsa
     */
    public Musteri tcIleAra(String tcKimlik) {
        return musteriRepository.findByTcKimlik(tcKimlik).orElseThrow(
            () -> new EntityNotFoundException("Müşteri bulunamadı: TC=" + tcKimlik));
    }

    /**
     * Sistemdeki tüm müşterileri döndürür.
     *
     * @return müşteri listesi (boş olabilir, null dönmez)
     */
    public List<Musteri> tumMusteriler() {
        return musteriRepository.findAll();
    }

    /**
     * Ad veya soyadda büyük/küçük harf duyarsız arama yapar.
     *
     * @param parca aranacak kelime parçası
     * @return eşleşen müşteri listesi
     */
    public List<Musteri> adaGoreAra(String parca) {
        return musteriRepository
            .findByAdContainingIgnoreCaseOrSoyadContainingIgnoreCase(parca, parca);
    }

    /**
     * Mevcut müşteri bilgilerini günceller.
     *
     * <p>TC kimlik değiştirilemez; diğer tüm alanlar {@code guncelMusteri}
     * nesnesinden alınır.
     *
     * @param id            güncellenecek müşterinin ID'si
     * @param guncelMusteri yeni değerleri taşıyan nesne
     * @return güncellenmiş müşteri
     * @throws EntityNotFoundException kayıt bulunamazsa
     */
    @Transactional
    public Musteri musteriGuncelle(String id, Musteri guncelMusteri) {
        Musteri mevcut = musteriRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("Müşteri bulunamadı: " + id));

        mevcut.setAd(guncelMusteri.getAd());
        mevcut.setSoyad(guncelMusteri.getSoyad());
        // TC kimlik bilerek değiştirilmez; primary niteliğindedir.
        mevcut.setTelefon(guncelMusteri.getTelefon());
        mevcut.setEmail(guncelMusteri.getEmail());
        mevcut.setAdres(guncelMusteri.getAdres());
        mevcut.setNotlar(guncelMusteri.getNotlar());

        return musteriRepository.save(mevcut);
    }

    /**
     * Müşteriyi sistemden siler.
     *
     * @param musteriId silinecek müşterinin ID'si
     * @throws EntityNotFoundException kayıt bulunamazsa
     */
    @Transactional
    public void musteriSil(String musteriId) {
        if (!musteriRepository.existsById(musteriId)) {
            throw new EntityNotFoundException("Müşteri bulunamadı: " + musteriId);
        }
        musteriRepository.deleteById(musteriId);
    }
}
