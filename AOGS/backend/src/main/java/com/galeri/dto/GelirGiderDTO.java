package com.galeri.dto;

import java.time.LocalDateTime;

/**
 * Belirli bir tarih aralığındaki gelir-gider raporu.
 * <p>
 * <b>Endpoint:</b> {@code GET /api/raporlar/gelir-gider?baslangic=...&bitis=...}
 * <p>
 * <b>Sorumlu:</b> Emre Kuzal — Raporlama Modülü.
 * <p>
 * Veriler {@code SatisRepository.gelirGiderOzeti(...)} JPQL sorgusu ile
 * veritabanı tarafında {@code SUM} agregasyonu kullanılarak hesaplanır;
 * uygulama belleğinde gezinme yapılmaz.
 *
 * @param baslangic Aralığın başlangıç zaman damgası (dahil).
 * @param bitis     Aralığın bitiş zaman damgası (dahil).
 * @param gelir     Aralıkta toplam ciro (SUM satisFiyati) — TL.
 * @param gider     Aralıkta toplam alış maliyeti (SUM alisFiyati) — TL.
 * @param kar       {@code gelir - gider} (SUM kar) — TL.
 * @param adet      Aralıktaki satış adedi.
 */
public record GelirGiderDTO(
    LocalDateTime baslangic,
    LocalDateTime bitis,
    double        gelir,
    double        gider,
    double        kar,
    long          adet
) {}
