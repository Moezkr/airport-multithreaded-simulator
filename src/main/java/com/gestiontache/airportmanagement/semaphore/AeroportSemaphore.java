package com.gestiontache.airportmanagement.semaphore;

import com.gestiontache.airportmanagement.gui.LogListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class AeroportSemaphore {

    private Semaphore semaphorePistes;
    private Semaphore semaphorePortes;

    private Semaphore fileAtterrissage;
    private Semaphore fileDecollage;
    private Semaphore mutex;

    private int nbAttentesArrivees = 0;
    private int nbAttentesDeparts = 0;

    private List<PisteSemaphore> pistes;
    private List<PorteSemaphore> portes;

    private LogListener listener;

    public AeroportSemaphore() {
        pistes = new ArrayList<>();
        portes = new ArrayList<>();
    }

    public void setListener(LogListener listener) {
        this.listener = listener;
    }

    private void log(String msg) {
        if (listener != null) listener.onLog("Aéroport S: " + msg);
    }

    public void initializeResources(int nbPistes, int nbPortes) {
        pistes.clear();
        portes.clear();

        semaphorePistes = new Semaphore(nbPistes, true);
        semaphorePortes = new Semaphore(nbPortes, true);

        fileAtterrissage = new Semaphore(0, true);
        fileDecollage = new Semaphore(0, true);
        mutex = new Semaphore(1, true);

        for (int i = 1; i <= nbPistes; i++) pistes.add(new PisteSemaphore(i));
        for (int i = 1; i <= nbPortes; i++) portes.add(new PorteSemaphore(i));

        log("Ressources initialisées : " + nbPistes + " pistes, " + nbPortes + " portes");
    }

    public int acquerirPistePourArrivee(AvionSemaphore avion) throws InterruptedException {
        log(avion.getName() + " demande piste (Arrivée)");

        mutex.acquire();
        if (semaphorePistes.tryAcquire()) {
            mutex.release();
        } else {
            nbAttentesArrivees++;
            log(avion.getName() + " en attente (File Atterrissage)");
            mutex.release();
            fileAtterrissage.acquire();
        }

        PisteSemaphore pisteLibre = trouverPisteLibre();
        pisteLibre.occuper(avion);

        log(avion.getName() + " a acquis Piste " + pisteLibre.getNumero() + " (Atterrissage)");
        return pisteLibre.getNumero();
    }

    public int acquerirPistePourDepart(AvionSemaphore avion) throws InterruptedException {
        log(avion.getName() + " demande piste (Décollage)");

        mutex.acquire();
        if (semaphorePistes.tryAcquire() && nbAttentesArrivees == 0) {
            mutex.release();
        } else {
            nbAttentesDeparts++;
            log(avion.getName() + " en attente (File Décollage)");
            mutex.release();
            fileDecollage.acquire();
        }

        PisteSemaphore pisteLibre = trouverPisteLibre();
        pisteLibre.occuper(avion);

        log(avion.getName() + " a acquis Piste " + pisteLibre.getNumero() + " (Décollage)");
        return pisteLibre.getNumero();
    }

    public void libererPiste(int numeroPiste) {
        PisteSemaphore piste = null;
        for (PisteSemaphore p : pistes) {
            if (p.getNumero() == numeroPiste) {
                piste = p;
                break;
            }
        }
        if (piste != null) piste.liberer();

        log("Piste " + numeroPiste + " libérée");

        try {
            mutex.acquire();

            if (nbAttentesArrivees > 0) {
                nbAttentesArrivees--;
                fileAtterrissage.release();
            } else if (nbAttentesDeparts > 0) {
                nbAttentesDeparts--;
                fileDecollage.release();
            } else {
                semaphorePistes.release();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            mutex.release();
        }
    }

    public int acquerirPorte(AvionSemaphore avion) throws InterruptedException {
        log(avion.getName() + " demande une porte");
        semaphorePortes.acquire();

        PorteSemaphore porteLibre = trouverPorteLibre();
        porteLibre.occuper(avion);

        log(avion.getName() + " a acquis Porte " + porteLibre.getNumero());
        return porteLibre.getNumero();
    }

    public void libererPorte(int numeroPorte) {
        PorteSemaphore porte = null;
        for (PorteSemaphore p : portes) {
            if (p.getNumero() == numeroPorte) {
                porte = p;
                break;
            }
        }
        if (porte != null) porte.liberer();

        log("Porte " + numeroPorte + " libérée");
        semaphorePortes.release();
    }

    private PisteSemaphore trouverPisteLibre() {
        for (PisteSemaphore p : pistes) {
            if (p.estLibre()) return p;
        }
        return null;
    }

    private PorteSemaphore trouverPorteLibre() {
        for (PorteSemaphore p : portes) {
            if (p.estLibre()) return p;
        }
        return null;
    }

    public int getNbAttentesArrivees() { return nbAttentesArrivees; }
    public int getNbAttentesDeparts() { return nbAttentesDeparts; }

    public List<PisteSemaphore> getPistes() { return pistes; }
    public List<PorteSemaphore> getPortes() { return portes; }
}
