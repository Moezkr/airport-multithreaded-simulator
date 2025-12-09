
<h1 align="center"> 📝 Cahier des Charges 📝 </h1>

## I. Objectif Général du Projet

L'objectif principal du projet est de simuler un système concurrent complexe (un aéroport) pour analyser et comparer les mécanismes de synchronisation en Java. Il s'agit de résoudre les problèmes d'accès concurrent et d'attente conditionnelle.

## II. Acteurs et Ressources

| Composant      | Rôle                                             | Contrainte                                         |
|----------------|-------------------------------------------------|--------------------------------------------------|
| **Avion (Thread)** | Acteur actif. Cycle de vie : Atterrissage → Stationnement → Décollage. | Doit attendre si les ressources nécessaires sont indisponibles. |
| **Pistes**        | Ressource limitée (par défaut 2).            | Une seule utilisation à la fois.                 |
| **Portes**        | Ressource limitée (par défaut 4).            | Une seule utilisation à la fois.                 |

## III. Critères de Performance et Contraintes

- **Exclusion Mutuelle** : Garantie absolue qu'un seul avion utilise une ressource à la fois.  
- **Priorité des Arrivées** : Les avions en arrivée (atterrissage) doivent être prioritaires sur les avions en départ (décollage).  
- **Gestion de l'Attente** : Implémentation correcte de la mise en sommeil des threads.  
- **Interface Utilisateur (GUI)** : Fournir une visualisation en temps réel de l'état des ressources et des files d'attente.
