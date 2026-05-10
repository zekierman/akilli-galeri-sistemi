package com.galeri.dto;

/**
 * Çalışan satış performansı raporu satırı.
 * <p>
 * <b>Endpoint:</b> {@code GET /api/raporlar/calisan-performans}
 * <p>
 * <b>Sorumlu:</b> Emre Kuzal — Raporlama Modülü.
 * <p>
 * Her satır bir çalışanın toplam satış adedi, cirosu ve kârıdır.
 * Sonuçlar ciro alanına göre azalan sırada gelir.
 *
 * @param calisanId Çalışanın ID'si.
 * @param ad        Çalışanın adı.
 * @param soyad     Çalışanın soyadı.
 * @param adet      Çalışanın gerçekleştirdiği satış adedi.
 * @param ciro      Çalışanın toplam cirosu (SUM satisFiyati) — TL.
 * @param kar       Çalışanın getirdiği toplam kâr (SUM kar) — TL.
 */
public record CalisanPerformansDTO(
    String calisanId,
    String ad,
    String soyad,
    long   adet,
    double ciro,
    double kar
) {

    /** UI'da kolay gösterim için ad ve soyadı birleştirir. */
    public String adSoyad() {
        return ad + " " + soyad;
    }
}
