package com.gestiontache.airportmanagement.gui;

import com.gestiontache.airportmanagement.monitor.Aeroport;
import com.gestiontache.airportmanagement.monitor.Piste;
import com.gestiontache.airportmanagement.monitor.Porte;
import com.gestiontache.airportmanagement.semaphore.AeroportSemaphore;
import com.gestiontache.airportmanagement.semaphore.PisteSemaphore;
import com.gestiontache.airportmanagement.semaphore.PorteSemaphore;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AirportApp extends Application implements LogListener {

    private Aeroport aeroportMonitor;
    private AeroportSemaphore aeroportSemaphore;
    private ScheduledExecutorService scheduler;


    private static final String EMOJI_PISTE_ACQ = " \uD83D\uDEEB";
    private static final String EMOJI_PISTE_REL = " \u2705";
    private static final String EMOJI_WAIT = " \u23F3";
    private static final String EMOJI_GATE = " \uD83D\uDEEB";
    private static final String EMOJI_START = " \uD83D\uDE80";


    private static final String[] AIRCRAFT_NAMES = {
            "Air France", "UPS Airlines", "Fly Emirates", "Qatar Airways",
            "Turkish Airline", "Air Canada", "DHL Aviation", "Tunisair",
            "British Airways", "Russia Aeroflot"
    };
    private int aircraftIndex = 0;

    private TextField numPistesField;
    private TextField numPortesField;
    private Button initButton;
    private TextArea logArea;
    private Label pistesStatusLabel;
    private Label portesStatusLabel;
    private Label arrivéesQueueLabel;
    private Label départsQueueLabel;
    private ToggleGroup syncMechanismGroup;
    private RadioButton monitorRadio;
    private RadioButton semaphoreRadio;


    private int currentNumPistes = 2;
    private int currentNumPortes = 4;

    @Override
    public void start(Stage primaryStage) {

        scheduler = Executors.newSingleThreadScheduledExecutor();


        aeroportMonitor = new Aeroport();
        aeroportSemaphore = new AeroportSemaphore();

        aeroportMonitor.setListener(this);
        aeroportSemaphore.setListener(this);

        initializeSimulation(currentNumPistes, currentNumPortes);

        BorderPane root = new BorderPane();
        root.setTop(createHeader());
        root.setLeft(createControls());
        root.setCenter(createStatusDisplay());


        Scene scene = new Scene(root, 1400, 950);

        String css =
                "-fx-font-family: 'Segoe UI', Arial; " +
                        "-fx-font-size: 11pt; " +
                        "-fx-background-color: #F0F2F5;" +
                        // TRÈS GRAND: Taille du texte log augmentée à 18pt
                        ".log-area { -fx-control-inner-background: #1E1E1E; -fx-text-fill: #b3e5fc; -fx-font-size: 18pt; }" +
                        ".status-label { -fx-font-weight: bold; -fx-font-size: 11pt; -fx-text-fill: #333333; }" +
                        "Button { -fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10 20; }" +
                        "Button:hover { -fx-background-color: #0056b3; }" +
                        "Separator { -fx-padding: 5 0; }";

        scene.getStylesheets().add("data:text/css," + css);

        primaryStage.setTitle("✈️ Projet 1: Gestion Concurrente d'un Aéroport");
        primaryStage.setScene(scene);
        primaryStage.show();


        scheduler.scheduleAtFixedRate(this::updateUI, 0, 500, TimeUnit.MILLISECONDS);

        primaryStage.setOnCloseRequest(event -> stopScheduler());
    }

    private HBox createHeader() {
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(15));
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #007bff, #0056b3); -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 0);");

        Label title = new Label("✈️ AIRPORT CONCURRENCY SIMULATOR");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setTextFill(Color.WHITE);

        VBox titleBox = new VBox(5, title);
        titleBox.setAlignment(Pos.CENTER);

        headerBox.getChildren().add(titleBox);
        return headerBox;
    }

    @Override
    public void onLog(String message) {
        Platform.runLater(() -> {

            if (logArea == null) return;

            String formattedMessage = message;

            if (message.contains("Démarrage du cycle")) {
                formattedMessage = EMOJI_START + " " + message;
            } else if (message.contains("a acquis la Piste")) {
                formattedMessage = EMOJI_PISTE_ACQ + " ACQUISITION PISTE: " + message;
            } else if (message.contains("Piste") && message.contains("libérée")) {
                formattedMessage = EMOJI_PISTE_REL + " LIBÉRATION PISTE: " + message;
            } else if (message.contains("a acquis la Porte")) {
                formattedMessage = EMOJI_GATE + " ACQUISITION PORTE: " + message;
            } else if (message.contains("attend une piste pour décoller (Priorité Arrivées)")) {
                formattedMessage = EMOJI_WAIT + " BLOCAGE PRIORITÉ: " + message;
            } else if (message.contains("attend Piste")) {
                formattedMessage = EMOJI_WAIT + " ATTENTE PISTE: " + message;
            }

            logArea.appendText(formattedMessage + "\n");
            logArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    private VBox createControls() {
        VBox controls = new VBox(15);
        controls.setPadding(new Insets(20));
        controls.setStyle("-fx-background-color: #EAECEF; -fx-border-color: #D3D3D3; -fx-border-width: 0 1 0 0;");
        controls.setPrefWidth(350);


        Label paramsTitle = new Label("🔧 PARAMÈTRES DE SIMULATION");
        paramsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        paramsTitle.setTextFill(Color.web("#004d99"));

        numPistesField = new TextField("2");
        numPistesField.setMaxWidth(60);
        numPortesField = new TextField("4");
        numPortesField.setMaxWidth(60);
        initButton = new Button("🔄 Réinitialiser Ressources");
        initButton.setStyle("-fx-background-color: #4CAF50;");
        initButton.setOnAction(e -> handleInitialization());

        HBox pistesBox = new HBox(10, new Label("Nb Pistes (1-4):"), numPistesField);
        HBox portesBox = new HBox(10, new Label("Nb Portes (1-8):"), numPortesField);
        HBox paramInputBox = new HBox(20, pistesBox, portesBox);

        VBox paramsBox = new VBox(10, paramsTitle, paramInputBox, initButton);
        paramsBox.setPadding(new Insets(15));
        paramsBox.setStyle("-fx-background-color: white; -fx-border-color: #bdbdbd; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 0);");


        Label syncTitle = new Label("⚙ MÉCANISME DE SYNCHRONISATION");
        syncTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        syncTitle.setTextFill(Color.web("#004d99"));

        monitorRadio = new RadioButton("Moniteurs (Synchronized / Wait)");
        semaphoreRadio = new RadioButton("Sémaphores (Acquire / Release)");
        monitorRadio.setToggleGroup(syncMechanismGroup = new ToggleGroup());
        semaphoreRadio.setToggleGroup(syncMechanismGroup);
        monitorRadio.setSelected(true);

        VBox radioBox = new VBox(10, monitorRadio, semaphoreRadio);
        VBox syncBox = new VBox(10, syncTitle, radioBox);
        syncBox.setPadding(new Insets(15));
        syncBox.setStyle("-fx-background-color: white; -fx-border-color: #bdbdbd; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 0);");


        Label addTitle = new Label("➕ AJOUT D'AVION");
        addTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        addTitle.setTextFill(Color.web("#004d99"));

        Button addAvionButton = new Button("✈ Ajouter Avion (Aléatoire)");
        addAvionButton.setStyle("-fx-background-color: #FF5722; -fx-font-size: 16;");
        addAvionButton.setOnAction(e -> addRandomAvion());
        addAvionButton.setMaxWidth(Double.MAX_VALUE);


        Label counterLabel = new Label("Avions ajoutés: 0/" + AIRCRAFT_NAMES.length);
        counterLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        counterLabel.setTextFill(Color.web("#4CAF50"));

        VBox addBox = new VBox(10, addTitle, addAvionButton, counterLabel);
        addBox.setPadding(new Insets(15));
        addBox.setStyle("-fx-background-color: white; -fx-border-color: #bdbdbd; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 0);");


        if (scheduler != null) {
            scheduler.scheduleAtFixedRate(() -> {
                Platform.runLater(() -> {
                    int maxPlanes = AIRCRAFT_NAMES.length;
                    counterLabel.setText("Avions ajoutés: " + aircraftIndex + "/" + maxPlanes);

                    // Changer la couleur selon le nombre d'avions ajoutés
                    if (aircraftIndex >= maxPlanes) {
                        counterLabel.setTextFill(Color.web("#F44336")); // Rouge
                        counterLabel.setText("✓ Tous les avions ajoutés (" + aircraftIndex + "/" + maxPlanes + ")");
                    } else if (aircraftIndex >= maxPlanes * 0.8) {
                        counterLabel.setTextFill(Color.web("#FF9800")); // Orange
                    } else {
                        counterLabel.setTextFill(Color.web("#4CAF50")); // Vert
                    }
                });
            }, 0, 500, TimeUnit.MILLISECONDS);
        }

        controls.getChildren().addAll(paramsBox, syncBox, addBox);
        return controls;
    }

    private VBox createStatusDisplay() {
        VBox display = new VBox(15);
        display.setPadding(new Insets(20));
        display.setPrefWidth(Double.MAX_VALUE);

        // Section Ressources
        Label resourcesTitle = new Label("🛣 STATUT DES RESSOURCES PARTAGÉES");
        resourcesTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        resourcesTitle.setTextFill(Color.web("#1a237e"));

        pistesStatusLabel = new Label("Pistes: En attente d'initialisation...");
        pistesStatusLabel.setWrapText(true);
        pistesStatusLabel.setMinHeight(80);

        portesStatusLabel = new Label("Portes: En attente d'initialisation...");
        portesStatusLabel.setWrapText(true);
        portesStatusLabel.setMinHeight(80);

        VBox resourcesBox = new VBox(10, resourcesTitle, pistesStatusLabel, portesStatusLabel);
        resourcesBox.setPadding(new Insets(15));
        resourcesBox.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 0);");


        Label queueTitle = new Label("📋 FILES D'ATTENTE");
        queueTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        queueTitle.setTextFill(Color.web("#1a237e"));

        VBox arrivalsBox = new VBox(10);
        arrivalsBox.setStyle("-fx-background-color: #E3F2FD; -fx-border-color: #90CAF9; -fx-border-width: 1; -fx-border-radius: 5;");
        arrivalsBox.setPadding(new Insets(10));
        Label arrivalsLabelHeader = new Label("\uD83D\uDEEC ARRIVÉES (Attente Piste)");
        arrivalsLabelHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        arrivéesQueueLabel = new Label("En attente: 0\nAucun avion");
        arrivéesQueueLabel.setFont(Font.font("Consolas", 12));
        arrivalsBox.getChildren().addAll(arrivalsLabelHeader, arrivéesQueueLabel);

        VBox departuresBox = new VBox(10);
        departuresBox.setStyle("-fx-background-color: #FFEBEE; -fx-border-color: #FFCDD2; -fx-border-width: 1; -fx-border-radius: 5;");
        departuresBox.setPadding(new Insets(10));
        Label departuresLabelHeader = new Label("\uD83D\uDEEB DÉPARTS (Attente Piste)");
        departuresLabelHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        départsQueueLabel = new Label("En attente: 0\nAucun avion");
        départsQueueLabel.setFont(Font.font("Consolas", 12));
        departuresBox.getChildren().addAll(departuresLabelHeader, départsQueueLabel);

        HBox queuesBox = new HBox(20, arrivalsBox, departuresBox);
        queuesBox.setPrefWidth(Double.MAX_VALUE);

        VBox queueSection = new VBox(10, queueTitle, queuesBox);


        Label logTitle = new Label("📝 JOURNAL DES ÉVÉNEMENTS");
        logTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        logTitle.setTextFill(Color.web("#1a237e"));

        logArea = new TextArea("🚀 JOURNAL DES ÉVÉNEMENTS - DÉMARRAGE DE LA SIMULATION\n");
        logArea.appendText("=".repeat(80) + "\n\n");
        logArea.setFont(Font.font("Consolas", 18));
        logArea.setStyle("-fx-control-inner-background: #1e1e1e; -fx-text-fill: #b3e5fc; -fx-font-size: 18px; -fx-padding: 10;");
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefHeight(450);

        ScrollPane scrollPane = new ScrollPane(logArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #1e1e1e; -fx-border-color: #424242;");

        VBox logSection = new VBox(10, logTitle, scrollPane);

        display.getChildren().addAll(resourcesBox, queueSection, logSection);
        return display;
    }

    private void handleInitialization() {
        try {
            int numPistes = Integer.parseInt(numPistesField.getText());
            int numPortes = Integer.parseInt(numPortesField.getText());


            if (numPistes <= 0 || numPistes > 4 || numPortes <= 0 || numPortes > 8) {
                throw new IllegalArgumentException("Les nombres de Pistes doivent être entre 1 et 4. Les Portes entre 1 et 8.");
            }

            currentNumPistes = numPistes;
            currentNumPortes = numPortes;

            initializeSimulation(numPistes, numPortes);


            aircraftIndex = 0;

            logArea.appendText("\n\uD83D\uDE80 Simulation réinitialisée avec " + numPistes + " Pistes et " + numPortes + " Portes.\n");
            logArea.appendText("✅ Compteur d'avions réinitialisé (0/" + AIRCRAFT_NAMES.length + ")\n\n");

        } catch (NumberFormatException e) {
            logArea.appendText("\n❌ ERREUR: Veuillez entrer des nombres entiers valides.\n");
        } catch (IllegalArgumentException e) {
            logArea.appendText("\n❌ ERREUR: " + e.getMessage() + "\n");
        }
    }

    private void initializeSimulation(int numPistes, int numPortes) {
        aeroportMonitor.initializeResources(numPistes, numPortes);
        aeroportSemaphore.initializeResources(numPistes, numPortes);


        Platform.runLater(() -> {
            updateUI();
        });
    }

    private void addRandomAvion() {
        boolean isMonitor = monitorRadio.isSelected();


        if (aircraftIndex >= AIRCRAFT_NAMES.length) {
            if (logArea != null) {
                logArea.appendText("\n❌ Tous les avions ont déjà été ajoutés !\n");
                logArea.appendText("   Cliquez sur 'Réinitialiser Ressources' pour recommencer.\n");
            }
            return;
        }

        String baseName = AIRCRAFT_NAMES[aircraftIndex];
        String prefix = isMonitor ? "M-" : "S-";
        String nomAvion = prefix + baseName;
        aircraftIndex++;

        String type = (Math.random() < 0.5) ? "Arrivée" : "Départ";

        if (isMonitor) {
            new com.gestiontache.airportmanagement.monitor.Avion(nomAvion, aeroportMonitor, type, this).start();
            if (logArea != null) {
                logArea.appendText("\n✅ " + nomAvion + " ajouté (Moniteur - " + type + ")\n");
            }
        } else {
            new com.gestiontache.airportmanagement.semaphore.AvionSemaphore(nomAvion, aeroportSemaphore, type, this).start();
            if (logArea != null) {
                logArea.appendText("\n✅ " + nomAvion + " ajouté (Sémaphore - " + type + ")\n");
            }
        }


        int remaining = AIRCRAFT_NAMES.length - aircraftIndex;
        if (logArea != null) {
            if (remaining == 1) {
                logArea.appendText("⚠️  Dernier avion disponible!\n");
            } else if (remaining == 0) {
                logArea.appendText("🎉 Tous les avions ont été ajoutés!\n");
            }
        }
    }

    private void updateUI() {
        Platform.runLater(() -> {
            boolean isMonitor = monitorRadio.isSelected();
            if (isMonitor) {
                updateMonitorDisplay();
            } else {
                updateSemaphoreDisplay();
            }
        });
    }

    private void updateMonitorDisplay() {
        try {

            if (pistesStatusLabel == null || portesStatusLabel == null) return;

            StringBuilder pistesBuilder = new StringBuilder();
            int pistesTrouvees = 0;


            for (int i = 1; i <= currentNumPistes; i++) {
                Piste pisteTrouvee = null;
                for (Piste p : aeroportMonitor.getPistes()) {
                    if (p.getNumero() == i) {
                        pisteTrouvee = p;
                        pistesTrouvees++;
                        break;
                    }
                }

                if (pisteTrouvee != null) {
                    String status = pisteTrouvee.estLibre() ?
                            ": LIBRE \u2705" :
                            ": OCCUPÉE ✈️ " + pisteTrouvee.getAvionCourant().getName();
                    pistesBuilder.append("Piste ").append(i).append(status).append("\n");
                } else {
                    pistesBuilder.append("Piste ").append(i).append(": Non initialisée ⚠️\n");
                }
            }

            String titrePistes = String.format("Pistes (Moniteur) - %d/%d initialisées:\n",
                    pistesTrouvees, currentNumPistes);
            pistesStatusLabel.setText(titrePistes + pistesBuilder.toString());


            StringBuilder portesBuilder = new StringBuilder();
            int portesTrouvees = 0;

            for (int i = 1; i <= currentNumPortes; i++) {
                Porte porteTrouvee = null;
                for (Porte p : aeroportMonitor.getPortes()) {
                    if (p.getNumero() == i) {
                        porteTrouvee = p;
                        portesTrouvees++;
                        break;
                    }
                }

                if (porteTrouvee != null) {
                    String status = porteTrouvee.estLibre() ?
                            ": LIBRE \u2705" :
                            ": OCCUPÉE 🚪 " + porteTrouvee.getAvionCourant().getName();
                    portesBuilder.append("Porte ").append(i).append(status).append("\n");
                } else {
                    portesBuilder.append("Porte ").append(i).append(": Non initialisée ⚠️\n");
                }
            }

            String titrePortes = String.format("Portes (Moniteur) - %d/%d initialisées:\n",
                    portesTrouvees, currentNumPortes);
            portesStatusLabel.setText(titrePortes + portesBuilder.toString());


            updateQueuesDisplay(
                    aeroportMonitor.getFileArrivees(),
                    aeroportMonitor.getFileDeparts()
            );

        } catch (Exception e) {

            if (pistesStatusLabel != null) pistesStatusLabel.setText("Erreur d'affichage");
            if (portesStatusLabel != null) portesStatusLabel.setText("Erreur d'affichage");
        }
    }
    private void updateSemaphoreDisplay() {
        try {
            if (pistesStatusLabel == null || portesStatusLabel == null) return;

            // --- Status Pistes ---
            StringBuilder pistesBuilder = new StringBuilder();
            int pistesTrouvees = 0;

            for (int i = 1; i <= currentNumPistes; i++) {
                PisteSemaphore pisteTrouvee = null;
                for (PisteSemaphore p : aeroportSemaphore.getPistes()) {
                    if (p.getNumero() == i) {
                        pisteTrouvee = p;
                        pistesTrouvees++;
                        break;
                    }
                }
                if (pisteTrouvee != null) {
                    String status = pisteTrouvee.estLibre() ?
                            ": LIBRE \u2705" :
                            ": OCCUPÉE ✈️ " + pisteTrouvee.getAvionCourant().getName();
                    pistesBuilder.append("Piste ").append(i).append(status).append("\n");
                } else {
                    pistesBuilder.append("Piste ").append(i).append(": Non initialisée ⚠️\n");
                }
            }

            pistesStatusLabel.setText(String.format("Pistes (Sémaphore) - %d/%d initialisées:\n%s",
                    pistesTrouvees, currentNumPistes, pistesBuilder));

            // --- Status Portes ---
            StringBuilder portesBuilder = new StringBuilder();
            int portesTrouvees = 0;

            for (int i = 1; i <= currentNumPortes; i++) {
                PorteSemaphore porteTrouvee = null;
                for (PorteSemaphore p : aeroportSemaphore.getPortes()) {
                    if (p.getNumero() == i) {
                        porteTrouvee = p;
                        portesTrouvees++;
                        break;
                    }
                }
                if (porteTrouvee != null) {
                    String status = porteTrouvee.estLibre() ?
                            ": LIBRE \u2705" :
                            ": OCCUPÉE 🚪 " + porteTrouvee.getAvionCourant().getName();
                    portesBuilder.append("Porte ").append(i).append(status).append("\n");
                } else {
                    portesBuilder.append("Porte ").append(i).append(": Non initialisée ⚠️\n");
                }
            }

            portesStatusLabel.setText(String.format("Portes (Sémaphore) - %d/%d initialisées:\n%s",
                    portesTrouvees, currentNumPortes, portesBuilder));

            // --- Queues ---
            String arrivalsText = "En attente: " + aeroportSemaphore.getNbAttentesArrivees();
            arrivalsText += "\nAucun avion (Thread non tracké)";  // simple placeholder
            arrivéesQueueLabel.setText(arrivalsText);

            String departuresText = "En attente: " + aeroportSemaphore.getNbAttentesDeparts();
            departuresText += "\nAucun avion (Thread non tracké)";
            départsQueueLabel.setText(departuresText);

        } catch (Exception e) {
            if (pistesStatusLabel != null) pistesStatusLabel.setText("Erreur d'affichage");
            if (portesStatusLabel != null) portesStatusLabel.setText("Erreur d'affichage");
        }
    }

    private void updateQueuesDisplay(java.util.List<? extends Thread> arrivees, java.util.List<? extends Thread> departs) {
        try {

            if (arrivéesQueueLabel == null || départsQueueLabel == null) return;


            String arrivalsText = "En attente: " + arrivees.size();
            if (!arrivees.isEmpty()) {
                arrivalsText += "\n" + formatQueue(arrivees, 3);
            } else {
                arrivalsText += "\nAucun avion";
            }
            arrivéesQueueLabel.setText(arrivalsText);


            String departuresText = "En attente: " + departs.size();
            if (!departs.isEmpty()) {
                departuresText += "\n" + formatQueue(departs, 3);
            } else {
                departuresText += "\nAucun avion";
            }
            départsQueueLabel.setText(departuresText);

        } catch (Exception e) {
            if (arrivéesQueueLabel != null) arrivéesQueueLabel.setText("Erreur d'affichage");
            if (départsQueueLabel != null) départsQueueLabel.setText("Erreur d'affichage");
        }
    }

    private String formatQueue(java.util.List<? extends Thread> queue, int maxDisplay) {
        if (queue.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < Math.min(queue.size(), maxDisplay); i++) {
            sb.append("• ").append(queue.get(i).getName());
            if (i < Math.min(queue.size(), maxDisplay) - 1) {
                sb.append("\n");
            }
        }

        if (queue.size() > maxDisplay) {
            sb.append("\n... +").append(queue.size() - maxDisplay).append(" autres");
        }

        return sb.toString();
    }

    private void stopScheduler() {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}