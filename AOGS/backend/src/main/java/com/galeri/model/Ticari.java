package com.galeri.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Hafif ticari araç (PDR §3.4.1.4). Frigorifik için %12 premium.
 */
@Entity
@DiscriminatorValue("TICARI")
@Table(name = "ticariler")
public class Ticari extends Arac {

    private double tasimaKapasitesi;
    private String ticariTip;
    private boolean frigorifik;

    @Override
    public double fiyatHesapla() {
        return frigorifik ? getSatisFiyati() * 1.12 : getSatisFiyati();
    }

    @Override
    public String aracTipiGetir() { return "Ticari"; }

    public double getTasimaKapasitesi() { return tasimaKapasitesi; }
    public void setTasimaKapasitesi(double k) { this.tasimaKapasitesi = k; }

    public String getTicariTip() { return ticariTip; }
    public void setTicariTip(String ticariTip) { this.ticariTip = ticariTip; }

    public boolean isFrigorifik() { return frigorifik; }
    public void setFrigorifik(boolean frigorifik) { this.frigorifik = frigorifik; }
}
