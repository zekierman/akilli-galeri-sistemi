package com.galeri.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Trafik muayene kaydı (PDR §3.4.1.12).
 */
@Entity
@Table(name = "muayeneler",
       indexes = {
           @Index(name = "idx_muayene_arac", columnList = "arac_id"),
           @Index(name = "idx_muayene_sonraki", columnList = "sonraki_muayene_tarihi")
       })
public class MuayeneBilgisi {

    @Id
    @Column(name = "muayene_id", length = 36)
    private String muayeneId = UUID.randomUUID().toString();

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "arac_id", nullable = false)
    private Arac arac;

    @Column(name = "muayene_tarihi", nullable = false)
    private LocalDate muayeneTarihi;

    @Column(name = "sonraki_muayene_tarihi", nullable = false)
    private LocalDate sonrakiMuayeneTarihi;

    @Column(name = "gecti_mi", nullable = false)
    private boolean gectiMi;

    @Column(name = "muayene_istasyonu", length = 80)
    private String muayeneIstasyonu;

    @Column(length = 500)
    private String notlar;

    @Transient
    public boolean suresiDolduMu() {
        return LocalDate.now().isAfter(sonrakiMuayeneTarihi);
    }

    @Transient
    public long getKalanGun() {
        return ChronoUnit.DAYS.between(LocalDate.now(), sonrakiMuayeneTarihi);
    }

    public String getMuayeneId() { return muayeneId; }
    public void setMuayeneId(String id) { this.muayeneId = id; }

    public Arac getArac() { return arac; }
    public void setArac(Arac arac) { this.arac = arac; }

    public LocalDate getMuayeneTarihi() { return muayeneTarihi; }
    public void setMuayeneTarihi(LocalDate t) { this.muayeneTarihi = t; }

    public LocalDate getSonrakiMuayeneTarihi() { return sonrakiMuayeneTarihi; }
    public void setSonrakiMuayeneTarihi(LocalDate t) { this.sonrakiMuayeneTarihi = t; }

    public boolean isGectiMi() { return gectiMi; }
    public void setGectiMi(boolean g) { this.gectiMi = g; }

    public String getMuayeneIstasyonu() { return muayeneIstasyonu; }
    public void setMuayeneIstasyonu(String i) { this.muayeneIstasyonu = i; }

    public String getNotlar() { return notlar; }
    public void setNotlar(String n) { this.notlar = n; }
}
