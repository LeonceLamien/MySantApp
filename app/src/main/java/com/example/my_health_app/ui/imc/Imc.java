package com.example.my_health_app.ui.imc;

import java.time.LocalDateTime;

public class Imc {
    public String phone_id;
    private int bmi_value;
    private double taille;
    private double poids;
    public LocalDateTime date;

    public Imc(String phone_id, int bmi_value, double taille, double poids, LocalDateTime date) {
        this.phone_id = phone_id;
        this.bmi_value = bmi_value;
        this.taille = taille;
        this.poids = poids;
        this.date = date;
    }

    public String getPhone_id() {
        return phone_id;
    }

    public int getBmi_value() {
        return bmi_value;
    }

    public double getTaille() {
        return taille;
    }

    public double getPoids() {
        return poids;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
