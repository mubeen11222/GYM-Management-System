package com.oop.gymmanagementsystem.ui;

import com.oop.gymmanagementsystem.models.*;
import com.oop.gymmanagementsystem.services.TrainerService;
import com.oop.gymmanagementsystem.storage.DataStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.function.Consumer;

public class TrainersPanel {
    private final TrainerService trainerService;
    private final Consumer<String> onNavigate;
    private final Consumer<Trainer> onTrainerSelected;
    private BorderPane root;
    private VBox trainerListBox;

    public TrainersPanel(TrainerService trainerService, Consumer<String> onNavigate,
                         Consumer<Trainer> onTrainerSelected) {
        this.trainerService = trainerService;
        this.onNavigate = onNavigate;
        this.onTrainerSelected = onTrainerSelected;
        buildUI();
    }

    private void buildUI() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIHelper.BG_DARK + ";");

        // Top bar
        HBox topBar = new HBox(12);
        topBar.setPadding(new Insets(16, 24, 16, 24));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: " + UIHelper.BG_SIDEBAR + ";");
        Button backBtn = UIHelper.createPrimaryButton("← Dashboard");
        backBtn.setOnAction(e -> onNavigate.accept("dashboard"));
        Label title = new Label("\uD83E\uDD3C  Trainers Module");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        topBar.getChildren().addAll(backBtn, title);
        root.setTop(topBar);

        // Sidebar
        VBox sidebar = new VBox(8);
        sidebar.setPadding(new Insets(16));
        sidebar.setPrefWidth(200);
        sidebar.setStyle("-fx-background-color: " + UIHelper.BG_SIDEBAR + ";");

        Label sideTitle = new Label("ACTIONS");
        sideTitle.setStyle("-fx-text-fill: " + UIHelper.TEXT_SECONDARY + "; -fx-font-size: 12px; -fx-font-weight: bold;");

        Button addBtn = UIHelper.createSidebarButton("➕ Add Trainer");
        addBtn.setOnAction(e -> showAddTrainerDialog());
        Button refreshBtn = UIHelper.createSidebarButton("🔄 Refresh");
        refreshBtn.setOnAction(e -> refreshList());

        double totalSalary = trainerService.getTotalSalaryExpense();
        VBox statsBox = new VBox(4);
        statsBox.setPadding(new Insets(12));
        statsBox.setStyle("-fx-background-color: " + UIHelper.BG_CARD + "; -fx-background-radius: 8;");
        statsBox.getChildren().addAll(
            createStatLabel("Total: " + trainerService.getAllTrainers().size()),
            createStatLabel("Salary: Rs. " + (int) totalSalary)
        );

        sidebar.getChildren().addAll(sideTitle, addBtn, refreshBtn,
                new Region() {{ setPrefHeight(16); }}, statsBox);
        root.setLeft(sidebar);

        // Main content
        trainerListBox = new VBox(8);
        trainerListBox.setPadding(new Insets(16));
        ScrollPane sp = new ScrollPane(trainerListBox);
        UIHelper.applyDarkScrollPane(sp);
        root.setCenter(sp);

        refreshList();
    }

    private void refreshList() {
        trainerListBox.getChildren().clear();
        List<Trainer> trainers = trainerService.getAllTrainers();

        if (trainers.isEmpty()) {
            trainerListBox.getChildren().add(UIHelper.createLabel("No trainers found."));
            return;
        }

        for (Trainer trainer : trainers) {
            trainerListBox.getChildren().add(createTrainerCard(trainer));
        }
    }

    private HBox createTrainerCard(Trainer trainer) {
        HBox card = new HBox(16);
        card.setPadding(new Insets(16));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: " + UIHelper.BG_CARD + "; -fx-background-radius: 10; -fx-cursor: hand;");

        Label avatar = new Label(trainer.getName().substring(0, 1).toUpperCase());
        avatar.setMinSize(48, 48); avatar.setMaxSize(48, 48);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle(
            "-fx-background-color: #1565C0; -fx-background-radius: 24;" +
            "-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;"
        );

        VBox info = new VBox(2);
        info.getChildren().addAll(
            UIHelper.createValueLabel(trainer.getName()),
            UIHelper.createLabel(trainer.getSpecialization() + "  |  " + trainer.getTrainerId() +
                "  |  " + trainer.getExperienceYears() + " yrs exp")
        );
        HBox.setHgrow(info, Priority.ALWAYS);

        // Capacity indicator
        int used = trainer.getAssignedMemberIds().size();
        int max = Trainer.MAX_MEMBERS;
        String capColor = used >= max ? UIHelper.DANGER : (used >= 3 ? UIHelper.WARNING : UIHelper.SUCCESS);
        Label capLabel = new Label(used + "/" + max + " members");
        capLabel.setStyle(
            "-fx-text-fill: " + capColor + "; -fx-font-size: 12px; -fx-font-weight: bold;" +
            "-fx-padding: 4 12; -fx-background-color: " + capColor + "22; -fx-background-radius: 12;"
        );

        Label salaryLabel = UIHelper.createLabel("Rs. " + (int) trainerService.calculateSalary(trainer.getTrainerId()));

        card.getChildren().addAll(avatar, info, capLabel, salaryLabel);

        card.setOnMouseClicked(e -> onTrainerSelected.accept(trainer));
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: #3A3A3A; -fx-background-radius: 10; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: " + UIHelper.BG_CARD + "; -fx-background-radius: 10; -fx-cursor: hand;"));

        return card;
    }

    private void showAddTrainerDialog() {
        Dialog<Trainer> dialog = new Dialog<>();
        dialog.setTitle("Add New Trainer");
        DialogPane dp = dialog.getDialogPane();
        dp.setStyle("-fx-background-color: " + UIHelper.BG_CARD + ";");
        dp.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10); grid.setPadding(new Insets(16));

        TextField nameF = UIHelper.createTextField("Full Name");
        TextField ageF = UIHelper.createTextField("Age");
        ComboBox<String> genderF = new ComboBox<>();
        genderF.getItems().addAll("Male", "Female"); genderF.setValue("Male");
        genderF.setStyle("-fx-background-color: #333; -fx-background-radius: 6;");
        TextField phoneF = UIHelper.createTextField("Phone");
        TextField emailF = UIHelper.createTextField("Email");
        TextField addressF = UIHelper.createTextField("Address");
        TextField specF = UIHelper.createTextField("Specialization");
        TextField salaryF = UIHelper.createTextField("Base Salary");
        TextField expF = UIHelper.createTextField("Experience (years)");

        int r = 0;
        grid.add(fLabel("Name:"), 0, r); grid.add(nameF, 1, r++);
        grid.add(fLabel("Age:"), 0, r); grid.add(ageF, 1, r++);
        grid.add(fLabel("Gender:"), 0, r); grid.add(genderF, 1, r++);
        grid.add(fLabel("Phone:"), 0, r); grid.add(phoneF, 1, r++);
        grid.add(fLabel("Email:"), 0, r); grid.add(emailF, 1, r++);
        grid.add(fLabel("Address:"), 0, r); grid.add(addressF, 1, r++);
        grid.add(fLabel("Specialization:"), 0, r); grid.add(specF, 1, r++);
        grid.add(fLabel("Base Salary:"), 0, r); grid.add(salaryF, 1, r++);
        grid.add(fLabel("Experience:"), 0, r); grid.add(expF, 1, r++);
        dp.setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    return trainerService.addTrainer(
                        nameF.getText(), Integer.parseInt(ageF.getText()),
                        genderF.getValue(), phoneF.getText(), emailF.getText(),
                        addressF.getText(), specF.getText(),
                        Double.parseDouble(salaryF.getText()),
                        com.oop.gymmanagementsystem.utils.DateUtils.today(),
                        Integer.parseInt(expF.getText()));
                } catch (Exception ex) {
                    UIHelper.showAlert("Error", "Invalid input: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            }
            return null;
        });
        dialog.showAndWait().ifPresent(t -> refreshList());
    }

    private Label fLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + UIHelper.TEXT_SECONDARY + "; -fx-font-size: 13px;");
        return l;
    }

    private Label createStatLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + UIHelper.TEXT_SECONDARY + "; -fx-font-size: 12px;");
        return l;
    }

    public BorderPane getRoot() { return root; }
}
