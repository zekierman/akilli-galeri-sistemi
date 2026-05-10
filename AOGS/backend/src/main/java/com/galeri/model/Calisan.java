package com.galeri.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Galeri çalışanı (PDR §3.4.1.8). Performans değerlendirmeleri ve
 * komisyon hesaplamalarında kullanılır.
 */
@Entity
@Table(name = "calisanlar",
       indexes = @Index(name = "idx_calisan_tc", columnList = "tc_kimlik", unique = true))
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Calisan {

    @Id
    @Column(name = "calisan_id", length = 36)
    private String calisanId;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String ad;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String soyad;

    @Column(name = "tc_kimlik", unique = true, length = 11)
    private String tcKimlik;

    @Column(length = 50)
    private String pozisyon;

    @PositiveOrZero
    private double maas;

    @Column(name = "ise_baslama_tarihi")
    private LocalDate iseBaslamaTarihi;

    @Column(name = "toplam_satis")
    private int toplamSatis;

    /**
     * Çalışana satış başına komisyon hesaplar.
     * Varsayılan: brüt tutarın %2'si.
     */
    @Transient
    public double komisyonHesapla(double tutar) {
        return tutar * 0.02;
    }

    public String getCalisanId() { return calisanId; }
    public void setCalisanId(String id) { this.calisanId = id; }

    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }

    public String getSoyad() { return soyad; }
    public void setSoyad(String soyad) { this.soyad = soyad; }

    public String getTcKimlik() { return tcKimlik; }
    public void setTcKimlik(String tc) { this.tcKimlik = tc; }

    public String getPozisyon() { return pozisyon; }
    public void setPozisyon(String pozisyon) { this.pozisyon = pozisyon; }

    public double getMaas() { return maas; }
    public void setMaas(double maas) { this.maas = maas; }

    public LocalDate getIseBaslamaTarihi() { return iseBaslamaTarihi; }
    public void setIseBaslamaTarihi(LocalDate t) { this.iseBaslamaTarihi = t; }

    public int getToplamSatis() { return toplamSatis; }
    public void setToplamSatis(int t) { this.toplamSatis = t; }
}
