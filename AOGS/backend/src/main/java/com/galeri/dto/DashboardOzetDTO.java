package com.galeri.dto;

/**
 * Dashboard üst kartları için özet metrikler.
 * <p>
 * <b>Endpoint:</b> {@code GET /api/raporlar/ozet}
 * <p>
 * <b>Sorumlu:</b> Emre Kuzal — Raporlama Modülü.
 * <p>
 * Tek istekte istatistik kartlarının tamamı (Toplam Ciro, Toplam Kâr,
 * Satış Adedi, Stok Değeri, Satıştaki Araç) doldurulur. Her metrik
 * ayrı ayrı sorgulansaydı 5 ayrı HTTP turu gerekirdi; bu DTO ile
 * Dashboard sayfası tek istekte yüklenir.
 * <p>
 * Tüm para birimi alanları TL cinsindendir.
 *
 * @param toplamCiro   Tüm zamanların toplam satış cirosu (SUM satisFiyati).
 * @param toplamKar    Tüm zamanların toplam kârı (SUM kar).
 * @param satisAdet    Toplam satış kaydı sayısı.
 * @param stokDeger    Mevcut stoktaki araçların toplam liste değeri.
 * @param satistaArac  Şu anda {@code SATISTA} durumundaki araç sayısı.
 */
public record DashboardOzetDTO(
    double toplamCiro,
    double toplamKar,
    long   satisAdet,
    double stokDeger,
    long   satistaArac
) {}
