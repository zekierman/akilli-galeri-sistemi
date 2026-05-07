package com.galeri.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Sport Utility Vehicle (PDR §3.4.1.3). 4x4 için %8, off-road paketi için
 * ek %5 premium uygular. Premium'lar çarpılır (kümülatif).
 */
@Entity
@DiscriminatorValue("SUV")
@Table(name = "suvler")
public class SUV extends Arac {

    private boolean dortCeker;
    private double bagajHacmi;
    private boolean offRoad;

    @Override
    public double fiyatHesapla() {
        double fiyat = getSatisFiyati();
        if (dortCeker) fiyat *= 1.08;
        if (offRoad)   fiyat *= 1.05;
        return fiyat;
    }

    @Override
    public String aracTipiGetir() { return "SUV"; }

    public boolean isDortCeker() { return dortCeker; }
    public void setDortCeker(boolean dortCeker) { this.dortCeker = dortCeker; }

    public double getBagajHacmi() { return bagajHacmi; }
    public void setBagajHacmi(double bagajHacmi) { this.bagajHacmi = bagajHacmi; }

    public boolean isOffRoad() { return offRoad; }
    public void setOffRoad(boolean offRoad) { this.offRoad = offRoad; }
}
