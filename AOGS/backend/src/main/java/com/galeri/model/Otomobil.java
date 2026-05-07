package com.galeri.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Binek otomobil segmentini temsil eden somut alt sınıf (PDR §3.4.1.2).
 * Sunroof varsa fiyata %5 premium uygulanır.
 */
@Entity
@DiscriminatorValue("OTOMOBIL")
@Table(name = "otomobiller")
public class Otomobil extends Arac {

    private String kasaTipi;
    private int kapiSayisi;
    private boolean sunroof;

    @Override
    public double fiyatHesapla() {
        return sunroof ? getSatisFiyati() * 1.05 : getSatisFiyati();
    }

    @Override
    public String aracTipiGetir() {
        return "Otomobil";
    }

    public String getKasaTipi() { return kasaTipi; }
    public void setKasaTipi(String kasaTipi) { this.kasaTipi = kasaTipi; }

    public int getKapiSayisi() { return kapiSayisi; }
    public void setKapiSayisi(int kapiSayisi) { this.kapiSayisi = kapiSayisi; }

    public boolean isSunroof() { return sunroof; }
    public void setSunroof(boolean sunroof) { this.sunroof = sunroof; }
}
