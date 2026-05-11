package com.galeri.service;

import com.galeri.model.MuayeneBilgisi;
import com.galeri.model.SigortaBilgisi;
import com.galeri.repository.MuayeneRepository;
import com.galeri.repository.SigortaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Sigorta ve muayene tarihlerini takip eden servis.
 * <p>
 * <b>Mimari değişiklik:</b> PDR v1.0'da {@code BildirimSistemi} bellekteki
 * tüm sigorta ve muayene listelerini her sorgulamada baştan sona tarıyordu
 * (O(n)). Yeni mimaride {@code bitis_tarihi} sütunu üzerinde B-Tree
 * indeksi sayesinde {@code WHERE bitis_tarihi BETWEEN ? AND ?} sorgusu
 * O(log n + k) sürede çalışır (k: dönen kayıt sayısı).
 */
@Service
public class BildirimService {

    private final SigortaRepository sigortaRepository;
    private final MuayeneRepository muayeneRepository;

    public BildirimService(SigortaRepository sigortaRepository,
                           MuayeneRepository muayeneRepository) {
        this.sigortaRepository = sigortaRepository;
        this.muayeneRepository = muayeneRepository;
    }

    @Transactional
    public SigortaBilgisi sigortaEkle(SigortaBilgisi sigorta) {
        return sigortaRepository.save(sigorta);
    }

    @Transactional
    public SigortaBilgisi sigortaGuncelle(String policeNo, SigortaBilgisi guncel) {
        SigortaBilgisi mevcut = sigortaRepository.findById(policeNo)
            .orElseThrow(() -> new RuntimeException("Sigorta bulunamadı"));
        mevcut.setSigortaSirketi(guncel.getSigortaSirketi());
        mevcut.setBaslangicTarihi(guncel.getBaslangicTarihi());
        mevcut.setBitisTarihi(guncel.getBitisTarihi());
        mevcut.setPrimTutari(guncel.getPrimTutari());
        mevcut.setAktif(guncel.isAktif());
        if (guncel.getArac() != null) mevcut.setArac(guncel.getArac());
        return sigortaRepository.save(mevcut);
    }

    public void sigortaSil(String policeNo) {
        sigortaRepository.deleteById(policeNo);
    }

    @Transactional
    public MuayeneBilgisi muayeneEkle(MuayeneBilgisi muayene) {
        return muayeneRepository.save(muayene);
    }

    @Transactional
    public MuayeneBilgisi muayeneGuncelle(String muayeneId, MuayeneBilgisi guncel) {
        MuayeneBilgisi mevcut = muayeneRepository.findById(muayeneId)
            .orElseThrow(() -> new RuntimeException("Muayene bulunamadı"));
        mevcut.setMuayeneTarihi(guncel.getMuayeneTarihi());
        mevcut.setSonrakiMuayeneTarihi(guncel.getSonrakiMuayeneTarihi());
        mevcut.setGectiMi(guncel.isGectiMi());
        mevcut.setMuayeneIstasyonu(guncel.getMuayeneIstasyonu());
        mevcut.setNotlar(guncel.getNotlar());
        if (guncel.getArac() != null) mevcut.setArac(guncel.getArac());
        return muayeneRepository.save(mevcut);
    }

    public void muayeneSil(String muayeneId) {
        muayeneRepository.deleteById(muayeneId);
    }

    /** Önümüzdeki N gün içinde süresi dolacak sigortalar. */
    public List<SigortaBilgisi> yaklasanSigortalar(int gun) {
        LocalDate bugun = LocalDate.now();
        return sigortaRepository.findByBitisTarihiBetween(bugun, bugun.plusDays(gun));
    }

    /** Önümüzdeki N gün içinde süresi dolacak muayeneler. */
    public List<MuayeneBilgisi> yaklasanMuayeneler(int gun) {
        LocalDate bugun = LocalDate.now();
        return muayeneRepository.findBySonrakiMuayeneTarihiBetween(bugun, bugun.plusDays(gun));
    }

    public List<SigortaBilgisi> suresiDolmusSigortalar() {
        return sigortaRepository.findByBitisTarihiBefore(LocalDate.now());
    }

    public List<MuayeneBilgisi> suresiDolmusMuayeneler() {
        return muayeneRepository.findBySonrakiMuayeneTarihiBefore(LocalDate.now());
    }

    /** Dashboard için tek seferlik özet. */
    @Transactional(readOnly = true)
    public Map<String, Object> dashboardOzeti() {
        Map<String, Object> ozet = new LinkedHashMap<>();
        ozet.put("yaklasanSigortalar", yaklasanSigortalar(30));
        ozet.put("yaklasanMuayeneler", yaklasanMuayeneler(30));
        ozet.put("suresiDolmusSigortalar", suresiDolmusSigortalar());
        ozet.put("suresiDolmusMuayeneler", suresiDolmusMuayeneler());
        return ozet;
    }
}
