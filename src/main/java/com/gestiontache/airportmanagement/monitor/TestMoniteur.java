package com.gestiontache.airportmanagement.monitor;

import com.gestiontache.airportmanagement.gui.LogListener;


public class TestMoniteur {

    public static void main(String[] args) {
        System.out.println("-------------------------------------------------------");
        System.out.println("--- Démarrage de la simulation Moniteur d'Aéroport ---");
        System.out.println("--- Vérifiez la PRIORITÉ des ARRIVÉES sur les DÉPARTS ---");
        System.out.println("-------------------------------------------------------");


        LogListener consoleListener = new LogListener() {
            @Override
            public void onLog(String message) {
                System.out.println("LOG (TEST): " + message);
            }
        };


        Aeroport aeroport = new Aeroport();
        aeroport.setListener(consoleListener);


        new Avion("Air France", aeroport, "Départ", consoleListener).start();
        new Avion("UPS Airlines", aeroport, "Départ", consoleListener).start();


        new Avion("Fly Emirates", aeroport, "Arrivée", consoleListener).start();
        new Avion("Qatar Airways", aeroport, "Arrivée", consoleListener).start();
        new Avion("Turkish Airline", aeroport, "Arrivée", consoleListener).start();


        new Avion("Air Canada", aeroport, "Départ", consoleListener).start();
        new Avion("DHL Aviation", aeroport, "Départ", consoleListener).start();


        new Avion("Tunisair", aeroport, "Arrivée", consoleListener).start();
        new Avion("British Airways", aeroport, "Arrivée", consoleListener).start();

        new Avion("Russia Aeroflot", aeroport, "Arrivée", consoleListener).start();


    }
}