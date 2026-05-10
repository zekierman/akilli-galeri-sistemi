package com.galeri.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Aracın sigorta poliçesi (PDR §3.4.1.11).
 * BildirimService bu Entity üzerinde {@code WHERE bitis_tarihi BETWEEN ?} 
 * sorgusu ile yaklaşan sigortaları bulur (eski "tüm listeyi tara"
 * yaklaşımının yerini alır).
 */
@Entity
@Table(name = "sigortalar",
       indexes = {
           @Index(name = "idx_sigorta_arac", columnList = "arac_id"),
           @Index(name = "idx_sigorta_bitis", columnList = "bitis_tarihi")
       })
public class SigortaBilgisi {

    @Id
    @Column(name = "police_no", length = 30)
    private String policeNo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "arac_id", nullable = false)
    private Arac arac;

    @Column(name = "sigorta_sirketi", length = 80)
    private String sigortaSirketi;

    @Column(name = "baslangic_tarihi", nullable = false)
    private LocalDate baslangicTarihi;

    @Column(name = "bitis_tarihi", nullable = false)
    private LocalDate bitisTarihi;

    @Column(name = "prim_tutari")
    private double primTutari;

    @Column(nullable = false)
    private boolean aktif = true;

    @Transient
    public boolean suresiDolduMu() {
        return LocalDate.now().isAfter(bitisTarihi);
    }

    @Transient
    public long getKalanGun() {
        return ChronoUnit.DAYS.between(LocalDate.now(), bitisTarihi);
    }

    public String getPoliceNo() { return policeNo; }
    public void setPoliceNo(String p) { this.policeNo = p; }

    public Arac getArac() { return arac; }
    public void setArac(Arac arac) { this.arac = arac; }

    public String getSigortaSirketi() { return sigortaSirketi; }
    public void setSigortaSirketi(String s) { this.sigortaSirketi = s; }

    public LocalDate getBaslangicTarihi() { return baslangicTarihi; }
    public void setBaslangicTarihi(LocalDate b) { this.baslangicTarihi = b; }

    public LocalDate getBitisTarihi() { return bitisTarihi; }
    public void setBitisTarihi(LocalDate b) { this.bitisTarihi = b; }

    public double getPrimTutari() { return primTutari; }
    public void setPrimTutari(double p) { this.primTutari = p; }

    public boolean isAktif() { return aktif; }
    public void setAktif(boolean a) { this.aktif = a; }
}
