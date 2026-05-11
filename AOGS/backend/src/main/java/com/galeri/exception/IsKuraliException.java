package com.galeri.exception;

/**
 * İş kuralı ihlali — örneğin "TC zaten kayıtlı", "Plaka mevcut",
 * "Araç SATILDI durumunda silinemez".
 * REST katmanında HTTP 400 Bad Request olarak yanıtlanır.
 */
public class IsKuraliException extends RuntimeException {
    public IsKuraliException(String mesaj) { super(mesaj); }
}
