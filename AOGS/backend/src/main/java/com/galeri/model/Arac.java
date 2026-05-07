package com.galeri.model;

import com.galeri.model.enums.AracDurumu;
import com.galeri.model.enums.VitesTipi;
import com.galeri.model.enums.YakitTipi;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Tüm araç tiplerinin ortak özniteliklerini ve davranışlarını tanımlayan
 * soyut JPA Entity'si (PDR §3.4.1.1).
 * <p>
 * <b>JPA Inheritance stratejisi:</b> {@link InheritanceType#JOINED} —
 * her alt sınıf (Otomobil, SUV, Ticari, Motosiklet) kendi tablosuna
 * sahiptir; ortak alanlar {@code aracs} tablosunda, alt sınıfa özgü
 * alanlar (sunroof, dortCeker, motorHacmi vb.) ayrı tablolarda
 * normalize edilir. Bu yaklaşım PDR'ın OOP hiyerarşisini birebir
 * korurken veritabanı bütünlüğünü de sağlar.
 * <p>
 * <b>Performans:</b> {@code @Index} anotasyonları ile plaka ve marka
 * üzerinde B-Tree indeksi oluşturulur. PDR v1.0'daki manuel BST
 * arama (O(log n) ortalama) yerine, veritabanı motorunun B-Tree
 * indeksleme algoritması (yine O(log n) ama dengeli ve disk-tabanlı)
 * kullanılır.
 * <p>
 * <b>Polimorfik fiyat hesaplama:</b> {@code fiyatHesapla()} metodu
 * soyut bırakılmıştır; alt sınıflar kendi iş kurallarını override eder
 * (sunroof %5, 4x4 %8, frigorifik %12, 500cc+ %10 — PDR §3.4.1.2-5).
 */
@Entity
@Table(
    name = "araclar",
    indexes = {
        @Index(name = "idx_arac_plaka", columnList = "plaka_no", unique = true),
        @Index(name = "idx_arac_marka", columnList = "marka"),
        @Index(name = "idx_arac_durum", columnList = "durum")
    }
)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "arac_tipi", discriminatorType = DiscriminatorType.STRING, length = 20)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Otomobil.class, name = "OTOMOBIL"),
    @JsonSubTypes.Type(value = SUV.class, name = "SUV"),
    @JsonSubTypes.Type(value = Ticari.class, name = "TICARI"),
    @JsonSubTypes.Type(value = Motosiklet.class, name = "MOTOSIKLET")
})
public abstract class Arac {

    @Id
    @Column(name = "arac_id", length = 36)
    private String aracId;

    @NotBlank @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String marka;

    @NotBlank @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String model;

    @Min(1900) @Max(2100)
    @Column(nullable = false)
    private int yil;

    @PositiveOrZero
    @Column(nullable = false)
    private double km;

    @Enumerated(EnumType.STRING)
    @Column(name = "yakit_tipi", nullable = false, length = 20)
    private YakitTipi yakitTipi;

    @Enumerated(EnumType.STRING)
    @Column(name = "vites_tipi", nullable = false, length = 20)
    private VitesTipi vitesTipi;

    @Column(length = 30)
    private String renk;

    @NotBlank @Size(max = 20)
    @Column(name = "plaka_no", nullable = false, unique = true, length = 20)
    private String plakaNo;

    @Column(name = "sasi_no", length = 30)
    private String sasiNo;

    @PositiveOrZero
    @Column(name = "alis_fiyati", nullable = false)
    private double alisFiyati;

    @Positive
    @Column(name = "satis_fiyati", nullable = false)
    private double satisFiyati;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AracDurumu durum = AracDurumu.SATISTA;

    @Column(name = "eklenme_tarihi", nullable = false)
    private LocalDateTime eklenmeTarihi = LocalDateTime.now();

    // ---------------------------------------------------------------
    // Soyut metotlar (polimorfik davranış)
    // ---------------------------------------------------------------

    /**
     * Polimorfik fiyat hesaplama. Her alt sınıf kendi premium kuralını
     * uygular (PDR §3.4.1.2-5).
     *
     * @return araç tipine özgü hesaplanmış nihai satış fiyatı (TL)
     */
    public abstract double fiyatHesapla();

    /**
     * Aracın tip etiketini döndürür ("Otomobil", "SUV" vb.).
     * UI'da listelerde görüntülemek için kullanılır.
     */
    public abstract String aracTipiGetir();

    // ---------------------------------------------------------------
    // Yardımcı metotlar
    // ---------------------------------------------------------------

    /** Aracın özet bilgisini formatlı string olarak döndürür. */
    public String getAracBilgisi() {
        return String.format("[%s] %s %s (%d) - %s - %.0f TL",
                aracTipiGetir(), marka, model, yil, plakaNo, satisFiyati);
    }

    // ---------------------------------------------------------------
    // Getter / Setter'lar (kapsülleme)
    // ---------------------------------------------------------------

    public String getAracId() { return aracId; }
    public void setAracId(String aracId) { this.aracId = aracId; }

    public String getMarka() { return marka; }
    public void setMarka(String marka) { this.marka = marka; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getYil() { return yil; }
    public void setYil(int yil) { this.yil = yil; }

    public double getKm() { return km; }
    public void setKm(double km) { this.km = km; }

    public YakitTipi getYakitTipi() { return yakitTipi; }
    public void setYakitTipi(YakitTipi yakitTipi) { this.yakitTipi = yakitTipi; }

    public VitesTipi getVitesTipi() { return vitesTipi; }
    public void setVitesTipi(VitesTipi vitesTipi) { this.vitesTipi = vitesTipi; }

    public String getRenk() { return renk; }
    public void setRenk(String renk) { this.renk = renk; }

    public String getPlakaNo() { return plakaNo; }
    public void setPlakaNo(String plakaNo) { this.plakaNo = plakaNo; }

    public String getSasiNo() { return sasiNo; }
    public void setSasiNo(String sasiNo) { this.sasiNo = sasiNo; }

    public double getAlisFiyati() { return alisFiyati; }
    public void setAlisFiyati(double alisFiyati) { this.alisFiyati = alisFiyati; }

    public double getSatisFiyati() { return satisFiyati; }
    public void setSatisFiyati(double satisFiyati) { this.satisFiyati = satisFiyati; }

    public AracDurumu getDurum() { return durum; }
    public void setDurum(AracDurumu durum) { this.durum = durum; }

    public LocalDateTime getEklenmeTarihi() { return eklenmeTarihi; }
    public void setEklenmeTarihi(LocalDateTime eklenmeTarihi) { this.eklenmeTarihi = eklenmeTarihi; }
}
