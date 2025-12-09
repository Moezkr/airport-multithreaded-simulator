package com.gestiontache.airportmanagement.monitor;


public class Porte {
    private final int numero;
    private boolean estOccupee;
    private Avion avionCourant;

    public Porte(int numero) {
        this.numero = numero;
        this.estOccupee = false;
        this.avionCourant = null;
    }


    public int getNumero() { return numero; }
    public boolean estLibre() { return !estOccupee; }
    public Avion getAvionCourant() { return avionCourant; }


    public void occuper(Avion avion) {
        this.estOccupee = true;
        this.avionCourant = avion;
    }


    public void liberer() {
        this.estOccupee = false;
        this.avionCourant = null;
    }

    @Override
    public String toString() {
        return "Porte " + numero + (estOccupee ? " (Occupée par " + avionCourant.getName() + ")" : " (Libre)");
    }
}