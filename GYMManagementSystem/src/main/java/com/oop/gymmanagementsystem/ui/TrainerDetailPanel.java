package com.oop.gymmanagementsystem.ui;

import com.oop.gymmanagementsystem.exceptions.TrainerLimitExceededException;
import com.oop.gymmanagementsystem.models.Member;
import com.oop.gymmanagementsystem.models.Trainer;
import com.oop.gymmanagementsystem.services.TrainerService;
import com.oop.gymmanagementsystem.storage.DataStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TrainerDetailPanel {
    private final Trainer trainer;
    private final TrainerService trainerService;
    private final Consumer<String> onNavigate;
    private BorderPane root;
    private VBox memberListBox;
    private Label sectionTitle;

    public TrainerDetailPanel(Trainer trainer, TrainerService trainerService, Consumer<String> onNavigate) {
        this.trainer = trainer;
        this.trainerService = trainerService;
        this.onNavigate = onNavigate;
        buildUI();
    }

    private void buildUI() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIHelper.BG_DARK + ";");

        Button backBtn = UIHelper.createSecondaryButton("Back to Trainers");
        backBtn.setOnAction(e -> onNavigate.accept("trainers"));
        root.setTop(UIHelper.createTopBar("Trainer: " + trainer.getName(), backBtn));

        VBox leftPanel = new VBox(16);
        leftPanel.setPadding(new Insets(24));
        leftPanel.setPrefWidth(280);
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setStyle("-fx-background-color: " + UIHelper.BG_SIDEBAR
                + "; -fx-border-color: transparent " + UIHelper.BORDER + " transparent transparent;");

        Label avatar = new Label(trainer.getName().substring(0, 1).toUpperCase());
        avatar.setMinSize(82, 82);
        avatar.setMaxSize(82, 82);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle("-fx-background-color: " + UIHelper.ACCENT + "; -fx-background-radius: 41;"
                + "-fx-text-fill: white; -fx-font-size: 32px; -fx-font-weight: 900;");

        Label nameLabel = UIHelper.createSectionLabel(trainer.getName());
        Label idLabel = UIHelper.createLabel(trainer.getTrainerId());

        VBox infoBox = new VBox(8);
        infoBox.setPadding(new Insets(14));
        infoBox.setStyle(UIHelper.cardStyle());
        infoBox.getChildren().addAll(
                UIHelper.createMetricRow("Specialization", trainer.getSpecialization()),
                UIHelper.createMetricRow("Experience", trainer.getExperienceYears() + " years"),
                UIHelper.createMetricRow("Phone", trainer.getPhone()),
                UIHelper.createMetricRow("Email", trainer.getEmail()),
                UIHelper.createMetricRow("Base Salary", "Rs. " + (int) trainer.getBaseSalary()),
                UIHelper.createMetricRow("Total Salary", "Rs. " + (int) trainerService.calculateSalary(trainer.getTrainerId())),
                UIHelper.createMetricRow("Members", trainer.getAssignedMemberIds().size() + "/" + Trainer.MAX_MEMBERS)
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button removeTrainerBtn = new Button("Remove Trainer");
        removeTrainerBtn.setMaxWidth(Double.MAX_VALUE);
        removeTrainerBtn.setMinHeight(44);
        removeTrainerBtn.setStyle(
            "-fx-background-color: " + UIHelper.PRIMARY + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 900;" +
            "-fx-font-family: " + UIHelper.FONT + ";" +
            "-fx-background-radius: 12;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 10 20;"
        );
        removeTrainerBtn.setOnMouseEntered(e -> removeTrainerBtn.setStyle(
            "-fx-background-color: " + UIHelper.PRIMARY_HOVER + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 900;" +
            "-fx-font-family: " + UIHelper.FONT + ";" +
            "-fx-background-radius: 12;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 10 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(255,106,0,0.4), 14, 0.3, 0, 4);"
        ));
        removeTrainerBtn.setOnMouseExited(e -> removeTrainerBtn.setStyle(
            "-fx-background-color: " + UIHelper.PRIMARY + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 900;" +
            "-fx-font-family: " + UIHelper.FONT + ";" +
            "-fx-background-radius: 12;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 10 20;"
        ));
        removeTrainerBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Remove Trainer");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to remove " + trainer.getName() + "?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    DataStore.getInstance().removeTrainer(trainer.getTrainerId());
                    DataStore.getInstance().saveAll();
                    onNavigate.accept("trainers");
                }
            });
        });

        leftPanel.getChildren().addAll(avatar, nameLabel, idLabel, infoBox, spacer, removeTrainerBtn);
        root.setLeft(leftPanel);

        VBox rightPanel = new VBox(14);
        rightPanel.setPadding(new Insets(22));
        sectionTitle = UIHelper.createSectionLabel(assignedTitle());

        Button addMemberBtn = UIHelper.createPrimaryButton("+ Assign Member");
        addMemberBtn.setDisable(!trainer.canAcceptMember());
        addMemberBtn.setOnAction(e -> showAssignMemberDialog());

        memberListBox = new VBox(10);
        refreshMemberList();

        rightPanel.getChildren().addAll(sectionTitle, addMemberBtn, memberListBox);
        ScrollPane sp = new ScrollPane(rightPanel);
        UIHelper.applyDarkScrollPane(sp);
        root.setCenter(sp);
    }

    private void refreshMemberList() {
        memberListBox.getChildren().clear();
        sectionTitle.setText(assignedTitle());
        DataStore ds = DataStore.getInstance();

        if (trainer.getAssignedMemberIds().isEmpty()) {
            memberListBox.getChildren().add(UIHelper.createLabel("No members assigned."));
            return;
        }

        for (String memberId : trainer.getAssignedMemberIds()) {
            Member member = ds.getMember(memberId);
            if (member == null) {
                continue;
            }
            memberListBox.getChildren().add(createMemberRow(memberId, member));
        }
    }

    private HBox createMemberRow(String memberId, Member member) {
        HBox card = new HBox(12);
        card.setPadding(new Insets(14));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: " + UIHelper.BG_CARD + "; -fx-background-radius: 14;"
                + "-fx-border-color: " + UIHelper.BORDER + "; -fx-border-radius: 14;");

        Label avatar = new Label(member.getName().substring(0, 1).toUpperCase());
        avatar.setMinSize(38, 38);
        avatar.setMaxSize(38, 38);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle("-fx-background-color: " + UIHelper.PRIMARY + "; -fx-background-radius: 19;"
                + "-fx-text-fill: white; -fx-font-weight: 900;");

        VBox info = new VBox(3);
        info.getChildren().addAll(
                UIHelper.createValueLabel(member.getName()),
                UIHelper.createLabel(member.getPlan().getDisplayName() + " | " + member.getFitnessGoal())
        );
        HBox.setHgrow(info, Priority.ALWAYS);

        Button removeBtn = UIHelper.createSecondaryButton("Remove");
        removeBtn.setOnAction(e -> {
            trainerService.removeMemberFromTrainer(trainer.getTrainerId(), memberId);
            refreshMemberList();
        });

        card.getChildren().addAll(avatar, info, removeBtn);
        return card;
    }

    private void showAssignMemberDialog() {
        DataStore ds = DataStore.getInstance();
        List<Member> unassigned = ds.getAllMembers().stream()
                .filter(m -> !m.hasTrainer())
                .collect(Collectors.toList());

        if (unassigned.isEmpty()) {
            UIHelper.showAlert("No Members", "No unassigned members are available.", Alert.AlertType.INFORMATION);
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>();
        dialog.setTitle("Assign Member");
        dialog.setHeaderText("Select a member to assign to " + trainer.getName());
        for (Member m : unassigned) {
            dialog.getItems().add(m.getMemberId() + " - " + m.getName());
        }
        dialog.setSelectedItem(dialog.getItems().get(0));

        dialog.showAndWait().ifPresent(selection -> {
            String memberId = selection.split(" - ")[0];
            try {
                trainerService.assignMemberToTrainer(trainer.getTrainerId(), memberId);
                refreshMemberList();
            } catch (TrainerLimitExceededException ex) {
                UIHelper.showAlert("Trainer Full", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private String assignedTitle() {
        return "Assigned Members (" + trainer.getAssignedMemberIds().size() + "/" + Trainer.MAX_MEMBERS + ")";
    }

    public BorderPane getRoot() {
        return root;
    }
}
