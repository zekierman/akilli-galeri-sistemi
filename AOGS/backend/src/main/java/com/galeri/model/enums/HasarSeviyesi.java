package com.galeri.model.enums;

/**
 * Ekspertiz parça hasar kategorileri (PDR §3.4.1.16).
 * {@code agirlik()} metodu, ekspertiz puan hesaplamasında kullanılır.
 */
public enum HasarSeviyesi {
    HASARSIZ(1.0),
    BOYALI(0.85),
    DEGISEN(0.6),
    AGIR_HASARLI(0.3);

    private final double agirlik;

    HasarSeviyesi(double agirlik) {
        this.agirlik = agirlik;
    }

    /** Ekspertiz puanı için kullanılan ağırlık katsayısı (0.0–1.0). */
    public double getAgirlik() {
        return agirlik;
    }
}
