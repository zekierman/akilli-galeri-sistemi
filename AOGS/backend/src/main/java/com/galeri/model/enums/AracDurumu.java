package com.galeri.model.enums;

/**
 * Araç yaşam döngüsündeki durumlar (PDR §3.4.1.13).
 * Veritabanında {@code VARCHAR} olarak saklanır (bkz.
 * {@link jakarta.persistence.EnumType#STRING}).
 */
public enum AracDurumu {
    /** Galeride satışa sunulmuş, müşterilere gösteriliyor. */
    SATISTA,
    /** Müşteriye satılmış, kayıttan düşülmüştür. */
    SATILDI,
    /** Müşteri tarafından rezerve edilmiş. */
    REZERVE,
    /** Serviste / tamir aşamasında. */
    SERVISTE
}
