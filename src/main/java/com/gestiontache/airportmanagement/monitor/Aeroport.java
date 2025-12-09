package com.gestiontache.airportmanagement.monitor;

import com.gestiontache.airportmanagement.gui.LogListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Aeroport {

    private List<Piste> pistes;
    private List<Porte> portes;

    private final LinkedList<Avion> fileArrivees;
    private final LinkedList<Avion> fileDeparts;
    private final LinkedList<Avion> filePortes;

    private LogListener listener;

    public Aeroport() {
        pistes = new ArrayList<>();
        portes = new ArrayList<>();
        fileArrivees = new LinkedList<>();
        fileDeparts = new LinkedList<>();
        filePortes = new LinkedList<>();
    }


    public synchronized void initializeResources(int numPistes, int numPortes) {
        pistes.clear();
        portes.clear();
        fileArrivees.clear();
        fileDeparts.clear();

        for (int i = 1; i <= numPistes; i++) pistes.add(new Piste(i));
        for (int i = 1; i <= numPortes; i++) portes.add(new Porte(i));

        log("Ressources réinitialisées: " + numPistes + " Pistes, " + numPortes + " Portes.");
    }

    public void setListener(LogListener listener) {
        this.listener = listener;
    }

    private void log(String msg) {
        if (listener != null) listener.onLog("Aéroport M: " + msg);
    }

    private Piste trouverPisteLibre() {
        for (Piste p : pistes) if (p.estLibre()) return p;
        return null;
    }

    private Porte trouverPorteLibre() {
        for (Porte p : portes) if (p.estLibre()) return p;
        return null;
    }


    public synchronized int acquerirPistePourArrivee(Avion avion) throws InterruptedException {
        fileArrivees.add(avion);

        while (fileArrivees.getFirst() != avion || trouverPisteLibre() == null) {
            log(avion.getName() + " attend en FIFO pour une piste (Arrivée)");
            wait();
        }


        Piste pisteLibre = trouverPisteLibre();
        fileArrivees.removeFirst();

        pisteLibre.occuper(avion);
        log(avion.getName() + " a acquis la Piste " + pisteLibre.getNumero() + " (Atterrissage)");

        return pisteLibre.getNumero();
    }


    public synchronized int acquerirPistePourDepart(Avion avion) throws InterruptedException {
        fileDeparts.add(avion);

        while (
                fileDeparts.getFirst() != avion ||
                        !fileArrivees.isEmpty() ||
                        trouverPisteLibre() == null
        ) {
            log(avion.getName() + " attend en FIFO pour une piste (Départ, priorité aux arrivées)");
            wait();
        }

        Piste pisteLibre = trouverPisteLibre();
        fileDeparts.removeFirst();

        pisteLibre.occuper(avion);
        log(avion.getName() + " a acquis la Piste " + pisteLibre.getNumero() + " (Décollage)");

        return pisteLibre.getNumero();
    }


    public synchronized void libererPiste(int numeroPiste) {
        for (Piste piste : pistes) {
            if (piste.getNumero() == numeroPiste) {
                piste.liberer();
                break;
            }
        }

        log("Piste " + numeroPiste + " libérée.");
        notifyAll();
    }



    public synchronized int acquerirPorte(Avion avion) throws InterruptedException {
        filePortes.add(avion);

        while (filePortes.getFirst() != avion || trouverPorteLibre() == null) {
            log(avion.getName() + " attend en FIFO pour une porte.");
            wait();
        }

        Porte porteLibre = trouverPorteLibre();
        filePortes.removeFirst();
        porteLibre.occuper(avion);

        log(avion.getName() + " a acquis la Porte " + porteLibre.getNumero());
        return porteLibre.getNumero();
    }


    public synchronized void libererPorte(int numeroPorte) {
        for (Porte porte : portes) {
            if (porte.getNumero() == numeroPorte) {
                porte.liberer();
                break;
            }
        }
        log("Porte " + numeroPorte + " libérée.");
        notifyAll();
    }

    public List<Piste> getPistes() { return pistes; }
    public List<Porte> getPortes() { return portes; }
    public List<Avion> getFileArrivees() { return fileArrivees; }
    public List<Avion> getFileDeparts() { return fileDeparts; }
}
