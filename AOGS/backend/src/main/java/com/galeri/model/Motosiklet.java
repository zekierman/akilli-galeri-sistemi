package com.galeri.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Motosiklet (PDR §3.4.1.5). 500cc üzerinde %10 premium.
 */
@Entity
@DiscriminatorValue("MOTOSIKLET")
@Table(name = "motosikletler")
public class Motosiklet extends Arac {

    private int motorHacmi;
    private String motosikletTipi;
    private boolean abs;

    @Override
    public double fiyatHesapla() {
        return motorHacmi > 500 ? getSatisFiyati() * 1.10 : getSatisFiyati();
    }

    @Override
    public String aracTipiGetir() { return "Motosiklet"; }

    public int getMotorHacmi() { return motorHacmi; }
    public void setMotorHacmi(int motorHacmi) { this.motorHacmi = motorHacmi; }

    public String getMotosikletTipi() { return motosikletTipi; }
    public void setMotosikletTipi(String t) { this.motosikletTipi = t; }

    public boolean isAbs() { return abs; }
    public void setAbs(boolean abs) { this.abs = abs; }
}
