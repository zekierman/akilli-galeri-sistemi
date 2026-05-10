package com.galeri.dto;

/**
 * Ödeme şekli bazında satış dağılımı.
 * <p>
 * <b>Endpoint:</b> {@code GET /api/raporlar/odeme-sekli}
 * <p>
 * <b>Sorumlu:</b> Emre Kuzal — Raporlama Modülü.
 * <p>
 * Pasta grafiği (Recharts {@code PieChart}) için kullanılır; her ödeme
 * yönteminin (Nakit / Kredi Kartı / Finansman / Havale) toplam ciro
 * içindeki payını gösterir.
 *
 * @param odemeSekli "Nakit", "Kredi Kartı", "Finansman", "Havale" vb.
 * @param adet       O ödeme şekliyle yapılan satış adedi.
 * @param tutar      O ödeme şekli ile elde edilen toplam ciro (TL).
 */
public record OdemeSekliDTO(
    String odemeSekli,
    long   adet,
    double tutar
) {}
