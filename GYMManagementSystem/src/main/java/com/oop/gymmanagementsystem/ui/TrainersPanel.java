package com.oop.gymmanagementsystem.ui;

import com.oop.gymmanagementsystem.models.Trainer;
import com.oop.gymmanagementsystem.services.TrainerService;
import com.oop.gymmanagementsystem.utils.DateUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

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

        Button backBtn = UIHelper.createSecondaryButton("Back to Dashboard");
        backBtn.setOnAction(e -> onNavigate.accept("dashboard"));
        root.setTop(UIHelper.createTopBar("Trainers Module", backBtn));

        Button addBtn = UIHelper.createSidebarButton("+ Add Trainer");
        addBtn.setOnAction(e -> showAddTrainerDialog());
        Button refreshBtn = UIHelper.createSidebarButton("Refresh List");
        refreshBtn.setOnAction(e -> refreshList());

        VBox statsBox = new VBox(8);
        statsBox.setPadding(new Insets(14));
        statsBox.setStyle(UIHelper.cardStyle());
        statsBox.getChildren().addAll(
                createStatLabel("Total: " + trainerService.getAllTrainers().size()),
                createStatLabel("Salary: Rs. " + (int) trainerService.getTotalSalaryExpense())
        );

        root.setLeft(UIHelper.createSidebar("Actions", addBtn, refreshBtn,
                new Region() {{ setPrefHeight(16); }}, statsBox));

        trainerListBox = new VBox(10);
        trainerListBox.setPadding(new Insets(18));
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
        card.setStyle(cardStyle(false));

        Label avatar = new Label(trainer.getName().substring(0, 1).toUpperCase());
        avatar.setMinSize(48, 48);
        avatar.setMaxSize(48, 48);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle(
                "-fx-background-color: " + UIHelper.ACCENT + "; -fx-background-radius: 24;" +
                "-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: 900;"
        );

        VBox info = new VBox(4);
        info.getChildren().addAll(
                UIHelper.createValueLabel(trainer.getName()),
                UIHelper.createLabel(trainer.getSpecialization() + " | " + trainer.getTrainerId()
                        + " | " + trainer.getExperienceYears() + " yrs exp")
        );
        HBox.setHgrow(info, Priority.ALWAYS);

        int used = trainer.getAssignedMemberIds().size();
        String capColor = used >= Trainer.MAX_MEMBERS ? UIHelper.DANGER : (used >= 3 ? UIHelper.WARNING : UIHelper.SUCCESS);
        Label capLabel = createPill(used + "/" + Trainer.MAX_MEMBERS + " members", capColor);
        Label salaryLabel = UIHelper.createValueLabel("Rs. " + (int) trainerService.calculateSalary(trainer.getTrainerId()));

        card.getChildren().addAll(avatar, info, capLabel, salaryLabel);
        card.setOnMouseClicked(e -> onTrainerSelected.accept(trainer));
        card.setOnMouseEntered(e -> card.setStyle(cardStyle(true)));
        card.setOnMouseExited(e -> card.setStyle(cardStyle(false)));
        return card;
    }

    private void showAddTrainerDialog() {
        Dialog<Trainer> dialog = new Dialog<>();
        dialog.setTitle("Add New Trainer");
        DialogPane dp = dialog.getDialogPane();
        dp.setStyle("-fx-background-color: " + UIHelper.BG_CARD + "; -fx-border-color: " + UIHelper.BORDER + ";");
        dp.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));

        TextField nameF = UIHelper.createTextField("Full Name");
        TextField ageF = UIHelper.createTextField("Age");
        ComboBox<String> genderF = new ComboBox<>();
        genderF.getItems().addAll("Male", "Female");
        genderF.setValue("Male");
        genderF.setMaxWidth(Double.MAX_VALUE);
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
        grid.add(fLabel("Experience:"), 0, r); grid.add(expF, 1, r);
        dp.setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    if (isBlank(nameF, ageF, phoneF, emailF, addressF, specF, salaryF, expF)) {
                        UIHelper.showAlert("Missing Information", "Please complete all trainer fields.", Alert.AlertType.WARNING);
                        return null;
                    }
                    return trainerService.addTrainer(
                            nameF.getText().trim(), Integer.parseInt(ageF.getText().trim()),
                            genderF.getValue(), phoneF.getText().trim(), emailF.getText().trim(),
                            addressF.getText().trim(), specF.getText().trim(),
                            Double.parseDouble(salaryF.getText().trim()),
                            DateUtils.today(),
                            Integer.parseInt(expF.getText().trim()));
                } catch (NumberFormatException ex) {
                    UIHelper.showAlert("Invalid Number", "Please enter valid numeric values for age, salary and experience.", Alert.AlertType.ERROR);
                } catch (Exception ex) {
                    UIHelper.showAlert("Error", "Invalid input: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            }
            return null;
        });
        dialog.showAndWait().ifPresent(t -> refreshList());
    }

    private Label fLabel(String text) {
        Label label = UIHelper.createLabel(text);
        label.setStyle("-fx-text-fill: " + UIHelper.TEXT_SECONDARY + "; -fx-font-size: 13px; -fx-font-weight: 700;");
        return label;
    }

    private Label createStatLabel(String text) {
        Label label = UIHelper.createLabel(text);
        label.setStyle("-fx-text-fill: " + UIHelper.TEXT_SECONDARY + "; -fx-font-size: 12px; -fx-font-weight: 700;");
        return label;
    }

    private Label createPill(String text, String color) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px; -fx-font-weight: 900;"
                + "-fx-padding: 5 12; -fx-background-color: " + color + "22; -fx-background-radius: 999;");
        return label;
    }

    private String cardStyle(boolean hover) {
        return "-fx-background-color: " + (hover ? "#232A36" : UIHelper.BG_CARD) + ";" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: " + (hover ? "#3C4658" : UIHelper.BORDER) + ";" +
                "-fx-border-radius: 14;" +
                "-fx-cursor: hand;";
    }

    private boolean isBlank(TextField... fields) {
        for (TextField field : fields) {
            if (field.getText() == null || field.getText().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public BorderPane getRoot() {
        return root;
    }
}
