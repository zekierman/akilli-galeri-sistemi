package com.galeri.model;

import com.galeri.model.enums.HasarSeviyesi;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Bir araç için yapılan ekspertiz raporu (PDR §3.4.1.10).
 * <p>
 * <b>Mimari değişiklik:</b> PDR v1.0'da ekspertizler {@code EkspertizYigini}
 * (LIFO Stack) içinde tutuluyordu. Yeni mimaride PostgreSQL'de saklanır;
 * son ekspertize erişim {@code ORDER BY ekspertiz_tarihi DESC LIMIT 1}
 * sorgusu ile sağlanır.
 * <p>
 * <b>Parça durumları</b> ayrı bir tabloda ({@code ekspertiz_parcalari})
 * Map olarak tutulur — JPA'nın {@code @ElementCollection} özelliği
 * kullanılarak.
 */
@Entity
@Table(name = "ekspertizler",
       indexes = @Index(name = "idx_ekspertiz_arac", columnList = "arac_id"))
public class Ekspertiz {

    @Id
    @Column(name = "ekspertiz_id", length = 36)
    private String ekspertizId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "arac_id", nullable = false)
    private Arac arac;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "calisan_id")
    private Calisan calisan;

    @Column(name = "ekspertiz_tarihi", nullable = false)
    private LocalDateTime ekspertizTarihi = LocalDateTime.now();

    /** Parça adı → Hasar seviyesi (Motor, Şasi, Kaput, Sağ Çamurluk vb.). */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "ekspertiz_parcalari",
        joinColumns = @JoinColumn(name = "ekspertiz_id")
    )
    @MapKeyColumn(name = "parca_adi", length = 50)
    @Column(name = "hasar_seviyesi", length = 20)
    @Enumerated(EnumType.STRING)
    private Map<String, HasarSeviyesi> parcaDurumlari = new HashMap<>();

    @Column(name = "ekspertiz_puani")
    private double ekspertizPuani;

    @Column(name = "genel_degerlendirme", length = 1000)
    private String genelDegerlendirme;

    @Column
    private boolean onaylandi;

    /**
     * Bir parça için hasar seviyesi kaydeder ve ekspertiz puanını günceller.
     */
    public void parcaDurumuEkle(String parca, HasarSeviyesi seviye) {
        this.parcaDurumlari.put(parca, seviye);
        this.ekspertizPuani = ekspertizPuaniHesapla();
    }

    /**
     * Tüm parça durumlarının ağırlıklı ortalamasını alır (0–100).
     * HASARSIZ=1.0, BOYALI=0.85, DEGISEN=0.6, AGIR_HASARLI=0.3.
     */
    public double ekspertizPuaniHesapla() {
        if (parcaDurumlari.isEmpty()) return 0;
        double toplam = 0;
        for (HasarSeviyesi seviye : parcaDurumlari.values()) {
            toplam += seviye.getAgirlik();
        }
        return (toplam / parcaDurumlari.size()) * 100;
    }

    public String getEkspertizId() { return ekspertizId; }
    public void setEkspertizId(String id) { this.ekspertizId = id; }

    public Arac getArac() { return arac; }
    public void setArac(Arac arac) { this.arac = arac; }

    public Calisan getCalisan() { return calisan; }
    public void setCalisan(Calisan c) { this.calisan = c; }

    public LocalDateTime getEkspertizTarihi() { return ekspertizTarihi; }
    public void setEkspertizTarihi(LocalDateTime t) { this.ekspertizTarihi = t; }

    public Map<String, HasarSeviyesi> getParcaDurumlari() { return parcaDurumlari; }
    public void setParcaDurumlari(Map<String, HasarSeviyesi> p) { this.parcaDurumlari = p; }

    public double getEkspertizPuani() { return ekspertizPuani; }
    public void setEkspertizPuani(double p) { this.ekspertizPuani = p; }

    public String getGenelDegerlendirme() { return genelDegerlendirme; }
    public void setGenelDegerlendirme(String g) { this.genelDegerlendirme = g; }

    public boolean isOnaylandi() { return onaylandi; }
    public void setOnaylandi(boolean o) { this.onaylandi = o; }
}
