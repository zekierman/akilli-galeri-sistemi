package com.galeri.dto;

/**
 * Stok değer raporu — kalan envanterin parasal değeri.
 * <p>
 * <b>Endpoint:</b> {@code GET /api/raporlar/stok-deger}
 * <p>
 * <b>Sorumlu:</b> Emre Kuzal — Raporlama Modülü.
 * <p>
 * Şu an satışta olan araçların toplam liste değeri ve adedi.
 * Galeri sahibinin "Param ne kadar bağlanmış durumda?" sorusunu
 * yanıtlar.
 *
 * @param toplamDeger Stoktaki araçların liste fiyatı toplamı (TL).
 * @param satistaAdet {@code AracDurumu.SATISTA} olan araç sayısı.
 */
public record StokDegerDTO(
    double toplamDeger,
    long   satistaAdet
) {}
