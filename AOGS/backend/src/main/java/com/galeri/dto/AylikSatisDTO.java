package com.galeri.dto;

/**
 * Aylık satış grafiği veri noktası.
 * <p>
 * <b>Endpoint:</b> {@code GET /api/raporlar/aylik-satis?yil=YYYY}
 * <p>
 * <b>Sorumlu:</b> Emre Kuzal — Raporlama Modülü.
 * <p>
 * Bir yılın 12 ayı için Recharts {@code LineChart} / {@code BarChart}
 * bileşenlerinin doğrudan tükettiği şekilde dönülür. Sorgu
 * {@code GROUP BY MONTH(satis_tarihi)} ile veritabanı tarafında çalışır.
 *
 * @param ay    Ay numarası (1=Ocak ... 12=Aralık).
 * @param adet  O ay yapılan satış adedi.
 * @param tutar O ayın toplam cirosu (TL).
 */
public record AylikSatisDTO(
    int    ay,
    long   adet,
    double tutar
) {}
