package com.galeri.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Satış işlemini temsil eden JPA Entity (PDR §3.4.1.9).
 * <p>
 * <b>Sorumlu:</b> Emre Kuzal (Satış Modülü).
 * <p>
 * <b>Mimari değişiklik:</b>
 * <ul>
 *       Bu mimaride satışlar PostgreSQL tablosunda kalıcı
 *       saklanır; FIFO sırası {@code satis_tarihi} üzerinde
 *       {@code ORDER BY} indeksi ile elde edilir.</li>
 *   <li>{@link ManyToOne} ilişkileri ile {@link Arac}, {@link Musteri} ve
 *       {@link Calisan}'a foreign key referansı kurulur — referans
 *       bütünlüğü veritabanı seviyesinde garanti altına alınır.</li>
 *   <li>{@code kar} alanı için ek indeks; raporlama sorgularını hızlandırır.</li>
 * </ul>
 */
@Entity
@Table(
    name = "satislar",
    indexes = {
        @Index(name = "idx_satis_tarih", columnList = "satis_tarihi"),
        @Index(name = "idx_satis_arac", columnList = "arac_id"),
        @Index(name = "idx_satis_musteri", columnList = "musteri_id"),
        @Index(name = "idx_satis_calisan", columnList = "calisan_id")
    }
)
public class Satis {

    @Id
    @Column(name = "satis_id", length = 36)
    private String satisId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "arac_id", nullable = false)
    private Arac arac;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "musteri_id", nullable = false)
    private Musteri musteri;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calisan_id")
    private Calisan calisan;

    @Positive
    @Column(name = "satis_fiyati", nullable = false)
    private double satisFiyati;

    @PositiveOrZero
    @Column(name = "alis_fiyati", nullable = false)
    private double alisFiyati;

    /** Otomatik hesaplanır: satisFiyati - alisFiyati. */
    @Column(nullable = false)
    private double kar;

    @Column(name = "satis_tarihi", nullable = false)
    private LocalDateTime satisTarihi = LocalDateTime.now();

    /** "Nakit", "Kredi Kartı", "Finansman", "Havale" vb. */
    @NotBlank
    @Column(name = "odeme_sekli", nullable = false, length = 30)
    private String odemeSekli;

    @Column(name = "fatura_kesildi")
    private boolean faturaKesildi;

    // ---------------------------------------------------------------
    // İş mantığı metotları
    // ---------------------------------------------------------------

    /**
     * Kâr değerini hesaplar ve {@code kar} alanına yazar.
     * @return hesaplanan kâr (TL)
     */
    public double karHesapla() {
        this.kar = this.satisFiyati - this.alisFiyati;
        return this.kar;
    }

    /**
     * Kâr marjını yüzde cinsinden döndürür (0-100 arası).
     */
    @Transient
    public double karMarji() {
        return satisFiyati > 0 ? (kar / satisFiyati) * 100 : 0;
    }

    /**
     * Satış için özetli fatura metni üretir. Frontend bunu indirilebilir
     * dokümana dönüştürebilir.
     */
    public String faturaOlustur() {
        return String.format(
            "==== AOGS FATURASI ====%n" +
            "Satış No: %s%nTarih: %s%n" +
            "Müşteri: %s (TC: %s)%n" +
            "Araç: %s %s (%s)%n" +
            "Tutar: %.2f TL  |  Ödeme: %s%n" +
            "=======================",
            satisId, satisTarihi,
            musteri != null ? musteri.getAdSoyad() : "-",
            musteri != null ? musteri.getTcKimlik() : "-",
            arac != null ? arac.getMarka() : "-",
            arac != null ? arac.getModel() : "-",
            arac != null ? arac.getPlakaNo() : "-",
            satisFiyati, odemeSekli
        );
    }

    @PrePersist
    @PreUpdate
    private void karGuncelle() {
        karHesapla();
    }

    // ---------------------------------------------------------------
    // Getter / Setter'lar
    // ---------------------------------------------------------------

    public String getSatisId() { return satisId; }
    public void setSatisId(String id) { this.satisId = id; }

    public Arac getArac() { return arac; }
    public void setArac(Arac arac) { this.arac = arac; }

    public Musteri getMusteri() { return musteri; }
    public void setMusteri(Musteri m) { this.musteri = m; }

    public Calisan getCalisan() { return calisan; }
    public void setCalisan(Calisan c) { this.calisan = c; }

    public double getSatisFiyati() { return satisFiyati; }
    public void setSatisFiyati(double f) { this.satisFiyati = f; }

    public double getAlisFiyati() { return alisFiyati; }
    public void setAlisFiyati(double f) { this.alisFiyati = f; }

    public double getKar() { return kar; }
    public void setKar(double kar) { this.kar = kar; }

    public LocalDateTime getSatisTarihi() { return satisTarihi; }
    public void setSatisTarihi(LocalDateTime t) { this.satisTarihi = t; }

    public String getOdemeSekli() { return odemeSekli; }
    public void setOdemeSekli(String o) { this.odemeSekli = o; }

    public boolean isFaturaKesildi() { return faturaKesildi; }
    public void setFaturaKesildi(boolean f) { this.faturaKesildi = f; }
}
