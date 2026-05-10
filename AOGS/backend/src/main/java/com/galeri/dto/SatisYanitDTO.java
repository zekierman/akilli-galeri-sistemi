package com.galeri.dto;

import com.galeri.model.Satis;
import java.time.LocalDateTime;

/**
 * Satış kaydı yanıt DTO'su.
 * <p>
 * <b>Sorumlu:</b> Emre Kuzal — Satış Modülü.
 * <p>
 * Proje kuralı gereği API katmanı asla doğrudan {@link Satis} entity'sini
 * dönmez (bkz. proje talimatları, "DTO ile API arasında veri taşınır").
 * Bu DTO, frontend'in bir satış kaydını listelemek/göstermek için ihtiyaç
 * duyduğu tüm alanları düz (flat) bir yapıda taşır.
 * <p>
 * <b>Tasarım gerekçesi:</b>
 * <ul>
 *   <li>Lazy-loaded {@code @ManyToOne} ilişkilerinin (arac, musteri, calisan)
 *       Jackson tarafından serileştirilmeye çalışılırken
 *       {@code LazyInitializationException} fırlatması engellenir.</li>
 *   <li>Müşterinin TC kimliği ve çalışanın maaş bilgisi gibi gereksiz/hassas
 *       alanlar API yanıtına sızdırılmaz.</li>
 *   <li>{@code karMarji} gibi türev alanlar tek seferde hesaplanıp gönderilir;
 *       frontend bu hesabı tekrar yapmak zorunda kalmaz.</li>
 *   <li>Araç bilgileri hem ham alanlar olarak ({@code aracMarka},
 *       {@code aracModel}, {@code aracYil}, {@code aracPlaka}) hem de
 *       tek satırlık özet ({@code aracBilgi}) olarak verilir; frontend
 *       gösterim ihtiyacına göre seçer (tablo: multi-line; listeleme: özet).</li>
 * </ul>
 *
 * @param satisId          Satış kaydının UUID'si.
 * @param aracId           Satılan aracın ID'si.
 * @param aracMarka        Aracın markası (örn. "Toyota").
 * @param aracModel        Aracın modeli (örn. "Corolla").
 * @param aracYil          Aracın model yılı (örn. 2020).
 * @param aracPlaka        Aracın plaka numarası (örn. "34ABC123").
 * @param aracBilgi        Tek satırlık özet (örn. "Toyota Corolla 2020 - 34ABC123").
 * @param aracTipi         "Otomobil", "SUV", "Ticari", "Motosiklet".
 * @param musteriAdSoyad   Müşterinin ad soyadı.
 * @param musteriTc        Müşterinin TC kimlik numarası.
 * @param calisanId        Satışı yapan çalışanın ID'si — null olabilir.
 * @param calisanAdSoyad   Çalışanın ad soyadı — null olabilir.
 * @param satisFiyati      Anlaşılan satış fiyatı (TL).
 * @param alisFiyati       Galerinin aracı aldığı maliyet (TL).
 * @param kar              {@code satisFiyati - alisFiyati} (TL).
 * @param karMarji         Yüzde cinsinden kâr marjı (0-100).
 * @param satisTarihi      İşlemin gerçekleştiği zaman damgası.
 * @param odemeSekli       "Nakit", "Kredi Kartı", "Finansman" vb.
 * @param faturaKesildi    Fatura düzenlendi mi?
 */
public record SatisYanitDTO(
    String        satisId,
    String        aracId,
    String        aracMarka,
    String        aracModel,
    int           aracYil,
    String        aracPlaka,
    String        aracBilgi,
    String        aracTipi,
    String        musteriAdSoyad,
    String        musteriTc,
    String        calisanId,
    String        calisanAdSoyad,
    double        satisFiyati,
    double        alisFiyati,
    double        kar,
    double        karMarji,
    LocalDateTime satisTarihi,
    String        odemeSekli,
    boolean       faturaKesildi
) {

    /**
     * Bir {@link Satis} entity'sinden DTO üretir.
     * <p>
     * <b>Önemli:</b> Bu metot lazy ilişkilere ({@code arac}, {@code musteri},
     * {@code calisan}) dokunduğu için <i>aktif bir Hibernate session içinde</i>,
     * yani {@code @Transactional} bir Service metodu içinde çağrılmalıdır.
     *
     * @param satis kaynak entity (null değil)
     * @return entity'nin sığ ve serileştirilmeye hazır kopyası
     */
    public static SatisYanitDTO from(Satis satis) {
        var arac     = satis.getArac();
        var musteri  = satis.getMusteri();
        var calisan  = satis.getCalisan();

        String aracMarka = arac != null ? arac.getMarka()   : "—";
        String aracModel = arac != null ? arac.getModel()   : "—";
        int    aracYil   = arac != null ? arac.getYil()     : 0;
        String aracPlaka = arac != null ? arac.getPlakaNo() : "—";

        String aracBilgi = arac == null ? "—" : String.format(
            "%s %s %d - %s", aracMarka, aracModel, aracYil, aracPlaka);

        return new SatisYanitDTO(
            satis.getSatisId(),
            arac     != null ? arac.getAracId()      : null,
            aracMarka,
            aracModel,
            aracYil,
            aracPlaka,
            aracBilgi,
            arac     != null ? arac.aracTipiGetir()  : "—",
            musteri  != null ? musteri.getAdSoyad()  : "—",
            musteri  != null ? musteri.getTcKimlik() : "—",
            calisan  != null ? calisan.getCalisanId(): null,
            calisan  != null ? calisan.getAd() + " " + calisan.getSoyad() : null,
            satis.getSatisFiyati(),
            satis.getAlisFiyati(),
            satis.getKar(),
            satis.karMarji(),
            satis.getSatisTarihi(),
            satis.getOdemeSekli(),
            satis.isFaturaKesildi()
        );
    }
}
