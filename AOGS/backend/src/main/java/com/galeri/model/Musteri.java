package com.galeri.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Müşteri Entity (PDR §3.4.1.6 + §3.4.1.7).
 * <p>
 * <b>Mimari değişiklik:</b> PDR v1.0'da {@code Kisi} ayrı bir soyut sınıftı
 * ve {@code Musteri} ile {@code Calisan} ondan kalıtım alıyordu. Web
 * mimarisinde bu hiyerarşinin tek tablo halinde tutulması sorgu
 * performansını artırır; bu nedenle Kisi'nin alanları {@code Musteri}
 * ve {@code Calisan} entity'lerinin her birine doğrudan kopyalanmıştır
 * (Kisi soyutu artık sadece konsept düzeyindedir).
 * <p>
 * <b>Performans devrimi:</b> PDR v1.0'da müşteriler özel
 * {@code MusteriAgaci} (BST) içinde tutuluyordu ve TC ile arama
 * O(log n) sürede yapılıyordu. Yeni mimaride bunun yerine
 * {@code tc_kimlik} sütunu üzerinde <b>UNIQUE indeks</b> oluşturulmuştur;
 * PostgreSQL B-Tree indeks ile aynı O(log n) garantisini, ek olarak
 * <i>kalıcılık</i> ve <i>çok kullanıcılı erişim</i> ile sağlar.
 */
@Entity
@Table(
    name = "musteriler",
    indexes = {
        @Index(name = "idx_musteri_tc", columnList = "tc_kimlik", unique = true),
        @Index(name = "idx_musteri_ad_soyad", columnList = "ad,soyad")
    }
)
public class Musteri {

    @Id
    @Column(name = "musteri_id", length = 36)
    private String musteriId;

    @NotBlank @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String ad;

    @NotBlank @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String soyad;

    /** TC Kimlik - 11 haneli, benzersiz. Eski BST anahtarının yerini alır. */
    @NotBlank
    @Pattern(regexp = "\\d{11}", message = "TC kimlik 11 haneli rakam olmalıdır")
    @Column(name = "tc_kimlik", nullable = false, unique = true, length = 11)
    private String tcKimlik;

    @Pattern(regexp = "[0-9+\\-\\s()]{7,20}", message = "Geçersiz telefon")
    @Column(length = 20)
    private String telefon;

    @Email
    @Column(length = 100)
    private String email;

    @Column(length = 255)
    private String adres;

    @Column(name = "kayit_tarihi", nullable = false)
    private LocalDateTime kayitTarihi = LocalDateTime.now();

    @Column(length = 500)
    private String notlar;

    /** Ad + soyad birleşimini döndürür. */
    @Transient
    public String getAdSoyad() {
        return ad + " " + soyad;
    }

    // Getter / Setter'lar
    public String getMusteriId() { return musteriId; }
    public void setMusteriId(String id) { this.musteriId = id; }

    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }

    public String getSoyad() { return soyad; }
    public void setSoyad(String soyad) { this.soyad = soyad; }

    public String getTcKimlik() { return tcKimlik; }
    public void setTcKimlik(String tc) { this.tcKimlik = tc; }

    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAdres() { return adres; }
    public void setAdres(String adres) { this.adres = adres; }

    public LocalDateTime getKayitTarihi() { return kayitTarihi; }
    public void setKayitTarihi(LocalDateTime t) { this.kayitTarihi = t; }

    public String getNotlar() { return notlar; }
    public void setNotlar(String notlar) { this.notlar = notlar; }
}
