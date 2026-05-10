package com.galeri.dto;

/**
 * Marka bazlı satış adedi.
 * <p>
 * <b>Endpoint:</b> {@code GET /api/raporlar/marka-satis}
 * <p>
 * <b>Sorumlu:</b> Emre Kuzal — Raporlama Modülü.
 * <p>
 * En çok satılan markaları azalan sırada listelemek için kullanılır;
 * Recharts {@code BarChart} ile yatay bar grafiği halinde gösterilir.
 *
 * @param marka Aracın markası (örn. "Toyota", "BMW").
 * @param adet  O markaya ait toplam satış adedi.
 */
public record MarkaSatisDTO(
    String marka,
    long   adet
) {}
