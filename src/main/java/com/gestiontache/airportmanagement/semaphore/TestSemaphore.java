package com.gestiontache.airportmanagement.semaphore;

import com.gestiontache.airportmanagement.gui.LogListener;


public class TestSemaphore {

    public static void main(String[] args) {
        System.out.println("-------------------------------------------------------");
        System.out.println("--- Démarrage de la simulation Sémaphore d'Aéroport ---");
        System.out.println("--- Vérifiez le comportement de la PRIORITÉ des ARRIVÉES ---");
        System.out.println("-------------------------------------------------------");


        LogListener consoleListener = new LogListener() {
            @Override
            public void onLog(String message) {
                System.out.println("LOG (TEST S): " + message);
            }
        };


        AeroportSemaphore aeroport = new AeroportSemaphore();
        aeroport.setListener(consoleListener);




        new AvionSemaphore("Air France", aeroport, "Départ", consoleListener).start();
        new AvionSemaphore("UPS Airlines", aeroport, "Départ", consoleListener).start();


        new AvionSemaphore("Emirates", aeroport, "Arrivée", consoleListener).start();
        new AvionSemaphore("Qatar Airways", aeroport, "Arrivée", consoleListener).start();
        new AvionSemaphore("Turkish Aieline", aeroport, "Arrivée", consoleListener).start();


        new AvionSemaphore("Air Canada", aeroport, "Départ", consoleListener).start();
        new AvionSemaphore("DHL Aviation", aeroport, "Départ", consoleListener).start();


        new AvionSemaphore("Tunisair", aeroport, "Arrivée", consoleListener).start();
        new AvionSemaphore("British Airways", aeroport, "Arrivée", consoleListener).start();

        new AvionSemaphore("Russia Aeroflot", aeroport, "Arrivée", consoleListener).start();


    }
}