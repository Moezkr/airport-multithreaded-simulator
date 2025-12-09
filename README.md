<h1 align="center"> ✈️ Gestion Concurrente d'un Aéroport ✈️ </h1>

<p align="center">
<b>Moniteurs vs Sémaphores – Simulation multithreading avec Java, JavaFX</b><br>



---

## 🚀 Introduction

Ce projet implémente **une simulation complète d’un aéroport multithreadé**, mettant en évidence les problèmes classiques de :

- concurrence entre threads  
- gestion de ressources limitées  
- synchronisation avec **Moniteurs** et **Sémaphores**  
- files d’attente FIFO  
- priorité absolue des arrivées sur les départs  

Chaque avion est un **thread** indépendant qui exécute le cycle :

> **Arrivée → Piste → Porte → Départ → Piste**

Le but principal du projet est de comparer **deux modèles de synchronisation :**

### ✔️ **Moniteurs (synchronized, wait, notifyAll)**  
— Attente passive, vraie FIFO.

### ✔️ **Sémaphores (java.util.concurrent.Semaphore)**  
— Gestion par permis, priorité simulée via sémaphore + compteur.

L’interface JavaFX permet une **visualisation en temps réel** de l’état des ressources (pistes, portes), des files d’attente et du journal des événements.

## 🏛️ 1. Architecture du Projet

L’application repose sur un **moniteur central** (ou équivalent) qui gère toutes les ressources partagées.

```sh
📦 AirportManagement/
│
├── src/
│   └── main/
│       └── java/
│           └── com.gestiontache.airportmanagement/
│
│               ├── gui/                       # Interface graphique (JavaFX)
│               │   ├── AirportApp.java        # Application principale
│               │   └── LogListener.java       # Interface callback pour logs
│               │
│               ├── monitor/                   # Version Moniteur (synchronized, wait, notifyAll)
│               │   ├── Aeroport.java          # Moniteur central
│               │   ├── Avion.java             # Thread Avion (version Moniteur)
│               │   ├── Piste.java             # Ressource Piste (Moniteur)
│               │   ├── Porte.java             # Ressource Porte (Moniteur)
│               │   └── TestMoniteur.java      # Tests unitaires / démonstratifs
│               │
│               ├── semaphore/                 # Version Sémaphore (Semaphore.acquire / release)
│               │   ├── AeroportSemaphore.java # Gestion de l'aéroport avec Sémaphores
│               │   ├── AvionSemaphore.java    # Thread Avion (version Sémaphore)
│               │   ├── PisteSemaphore.java    # Ressource Piste (Sémaphore)
│               │   ├── PorteSemaphore.java    # Ressource Porte (Sémaphore)
│               │   └── TestSemaphore.java     # Tests unitaires / démonstratifs
│
│               └── Launcher.java              # Classe loader JavaFX (pour exécution propre)
│
│       └── resources/                         # Fichiers de ressources JavaFX (si nécessaires)
│
├── pom.xml                                    # Configuration Maven + dépendances JavaFX
├── mvnw / mvnw.cmd                            # Maven wrapper
└── .gitignore
```
---

## 🔎 2. Ressources & Contraintes Implémentées

| Élément | Description | Synchronisation |
|--------|-------------|------------------|
| **Pistes** | Nombre paramétrable (par défaut : 2) | Verrou (Moniteur) ou Permis (Sémaphore) |
| **Portes** | Nombre paramétrable (par défaut : 4) | Verrou ou Permis |
| **Avions (Threads)** | Cycle complet Arrivée/Depart | Attente, acquisition, libération |
| **File d’Arrivées (FIFO)** | Toujours prioritaire | Gérée dans le moniteur |
| **File de Départs (FIFO)** | Traçable uniquement en Moniteur | Pas traçable en Sémaphore |

### ⭐ **Contrainte clé : Priorité des Arrivées**
Dans `acquerirPistePourDepart()` :




➡️ Les départs **ne passent jamais** tant qu’un avion d’arrivée est en attente.

---

## 🛠️ 3. Exécution de la Simulation (JavaFX)

### 1. **Configurer JavaFX**
Assurez-vous d’avoir ajouté les modules JavaFX :

- dans `pom.xml` (Maven)
- ou dans `module-info.java`

### 2. **Lancer l’application**
Exécutez l’une de ces classes :

- `AirportApp.java`
- `Launcher.java`

---

## 🖥️ 4. Fonctionnalités de l’Interface

### ✔️ **Paramètres dynamiques**
- Modifier nombre de **pistes**
- Modifier nombre de **portes**
- Réinitialisation instantanée

### ✔️ **Choix du Mécanisme**
`Moniteurs` ⬅️→➡️ `Sémaphores`

Les deux versions tournent avec **exactement le même flux d’avions**, pour un vrai comparatif.

### ✔️ **Journal d’Événements (avec emojis)**
Affiche les actions critiques :

- ✔️ Libération de piste/porte  
- 🛫 Acquisition de piste  
- 🧳 Acquisition porte  
- ⏳ Attente / Priorité  

### ✔️ **Statuts en Temps Réel**
- Pistes : LIBRE / OCCUPÉE  
- Portes : LIBRE / OCCUPÉE  

### ✔️ **Files d’Attente**
- Arrivées → visible (FIFO)
- Départs → visible (FIFO) 


---

## ⭐ 5. Demo 


<img width="1395" height="977" alt="Screenshot 2025-12-09 015247" src="https://github.com/user-attachments/assets/40d3b3a8-d928-4a4d-818a-6aed88b7b944" />








