package com.gestiontache.airportmanagement.semaphore;


public class PorteSemaphore {
    private final int numero;
    private boolean estOccupee;
    private AvionSemaphore avionCourant;

    public PorteSemaphore(int numero) {
        this.numero = numero;
        this.estOccupee = false;
        this.avionCourant = null;
    }

    public int getNumero() { return numero; }
    public boolean estLibre() { return !estOccupee; }
    public AvionSemaphore getAvionCourant() { return avionCourant; }

    public void occuper(AvionSemaphore avion) {
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