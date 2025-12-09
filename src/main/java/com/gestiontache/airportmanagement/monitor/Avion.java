package com.gestiontache.airportmanagement.monitor;

import com.gestiontache.airportmanagement.gui.LogListener;


public class Avion extends Thread {
    private final Aeroport aeroport;
    private final String etatInitial;
    private final LogListener listener;


    private int pisteOccupee = -1;
    private int porteOccupee = -1;
    private String etatActuel = "Créé";


    public Avion(String nom, Aeroport aeroport, String etatInitial, LogListener listener) {
        super(nom);
        this.aeroport = aeroport;
        this.etatInitial = etatInitial;
        this.etatActuel = etatInitial;
        this.listener = listener;
    }


    private void log(String message) {
        if (listener != null) {
            listener.onLog(getName() + ": " + message);
        }
    }

    @Override
    public void run() {
        log("Démarrage du cycle (" + etatInitial + ")");
        if (etatInitial.equals("Arrivée")) {
            cycleArriveeStationnementDepart();
        } else if (etatInitial.equals("Départ")) {
            cycleDepart();
        }
    }

    private void cycleArriveeStationnementDepart() {

        try {
            etatActuel = "Attente Piste (Atterrissage)";
            log(etatActuel);

            pisteOccupee = aeroport.acquerirPistePourArrivee(this);
            etatActuel = "Atterrissage sur Piste " + pisteOccupee;


            sleep(2000 + (long) (Math.random() * 2000));

        } catch (InterruptedException e) {
            log("Interrompu lors de l'arrivée.");
            Thread.currentThread().interrupt();
            return;
        } finally {
            if (pisteOccupee != -1) {
                aeroport.libererPiste(pisteOccupee);
                pisteOccupee = -1;
            }
        }


        try {
            etatActuel = "Attente Porte";
            log(etatActuel);

            porteOccupee = aeroport.acquerirPorte(this);
            etatActuel = "À Porte " + porteOccupee + " (Service)";


            sleep(1000 + (long) (Math.random() * 3000));

        } catch (InterruptedException e) {
            log("Interrompu au stationnement.");
            Thread.currentThread().interrupt();
            return;
        } finally {
            if (porteOccupee != -1) {
                aeroport.libererPorte(porteOccupee);
                porteOccupee = -1;
            }
        }


        cycleDepart();
    }

    private void cycleDepart() {

        try {
            etatActuel = "Attente Piste (Décollage)";
            log(etatActuel);

            pisteOccupee = aeroport.acquerirPistePourDepart(this);
            etatActuel = "Décollage sur Piste " + pisteOccupee;


            sleep(2000 + (long) (Math.random() * 2000));

        } catch (InterruptedException e) {
            log("Interrompu lors du départ.");
            Thread.currentThread().interrupt();
        } finally {
            if (pisteOccupee != -1) {
                aeroport.libererPiste(pisteOccupee);
                pisteOccupee = -1;
                etatActuel = "Parti";
                log("a quitté l'aéroport."); // Log final
            }
        }
    }


    public String getEtatActuel() { return etatActuel; }
    public int getPisteOccupee() { return pisteOccupee; }
    public int getPorteOccupee() { return porteOccupee; }
}