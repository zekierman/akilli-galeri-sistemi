package com.galeri.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Yeni satış oluşturma isteği için DTO (Data Transfer Object).
 * <p>
 * <b>Endpoint:</b> {@code POST /api/satislar}
 * <p>
 * <b>Sorumlu:</b> Emre Kuzal — Satış Modülü.
 * <p>
 * Frontend'den nested {@code Satis} entity'sini almak yerine yalnızca
 * referans ID'leri ve işlem bilgilerini taşıyan sade bir sözleşme tanımlar.
 * Böylece istemcinin {@link com.galeri.model.Satis} JPA modelinin iç
 * detaylarını bilmesi gerekmez ve API yüzeyi daraltılır.
 * <p>
 * <b>Doğrulama:</b> Controller'da {@code @Valid} ile birlikte kullanıldığında
 * Bean Validation alan kurallarını otomatik denetler; ihlal durumunda
 * {@link com.galeri.exception.GlobalExceptionHandler} 400 Bad Request döndürür.
 *
 * @param aracId       Satılacak aracın UUID'si — zorunlu.
 * @param musteriTc    Müşterinin 11 haneli TC kimlik numarası — zorunlu.
 * @param calisanId    Satışı gerçekleştiren çalışanın ID'si — opsiyonel
 *                     (galeri sahibi/yönetici doğrudan satarsa boş kalabilir).
 * @param satisFiyati  Müzakere sonrası nihai satış fiyatı (TL) — pozitif olmalı.
 *                     Aracın liste fiyatından farklı olabilir (indirim/pazarlık).
 * @param odemeSekli   "Nakit", "Kredi Kartı", "Finansman", "Havale" — zorunlu.
 */
public record SatisIstegi(

    @NotBlank(message = "Araç ID boş olamaz")
    @Size(max = 36, message = "Araç ID en fazla 36 karakter olabilir")
    String aracId,

    @NotBlank(message = "TC kimlik numarası boş olamaz")
    @Size(min = 11, max = 11, message = "TC kimlik 11 haneli olmalıdır")
    String musteriTc,

    @Size(max = 36, message = "Çalışan ID en fazla 36 karakter olabilir")
    String calisanId,

    @Positive(message = "Satış fiyatı pozitif olmalıdır")
    double satisFiyati,

    @NotBlank(message = "Ödeme şekli boş olamaz")
    @Size(max = 30, message = "Ödeme şekli en fazla 30 karakter olabilir")
    String odemeSekli

) {}
