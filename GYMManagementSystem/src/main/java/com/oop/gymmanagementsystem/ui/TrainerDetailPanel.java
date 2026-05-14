package com.oop.gymmanagementsystem.ui;

import com.oop.gymmanagementsystem.exceptions.TrainerLimitExceededException;
import com.oop.gymmanagementsystem.models.*;
import com.oop.gymmanagementsystem.services.TrainerService;
import com.oop.gymmanagementsystem.storage.DataStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TrainerDetailPanel {
    private final Trainer trainer;
    private final TrainerService trainerService;
    private final Consumer<String> onNavigate;
    private BorderPane root;
    private VBox memberListBox;

    public TrainerDetailPanel(Trainer trainer, TrainerService trainerService, Consumer<String> onNavigate) {
        this.trainer = trainer;
        this.trainerService = trainerService;
        this.onNavigate = onNavigate;
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
        Button backBtn = UIHelper.createPrimaryButton("← Trainers");
        backBtn.setOnAction(e -> onNavigate.accept("trainers"));
        Label title = new Label("Trainer: " + trainer.getName());
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        topBar.getChildren().addAll(backBtn, title);
        root.setTop(topBar);

        // Left panel - Trainer info
        VBox leftPanel = new VBox(16);
        leftPanel.setPadding(new Insets(24));
        leftPanel.setPrefWidth(260);
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setStyle("-fx-background-color: " + UIHelper.BG_SIDEBAR + ";");

        Label avatar = new Label(trainer.getName().substring(0, 1).toUpperCase());
        avatar.setMinSize(80, 80); avatar.setMaxSize(80, 80);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle(
            "-fx-background-color: #1565C0; -fx-background-radius: 40;" +
            "-fx-text-fill: white; -fx-font-size: 32px; -fx-font-weight: bold;"
        );

        Label nameLabel = new Label(trainer.getName());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label idLabel = UIHelper.createLabel(trainer.getTrainerId());

        VBox infoBox = new VBox(6);
        infoBox.setPadding(new Insets(12));
        infoBox.setStyle("-fx-background-color: " + UIHelper.BG_CARD + "; -fx-background-radius: 10;");
        infoBox.getChildren().addAll(
            createRow("Specialization", trainer.getSpecialization()),
            createRow("Experience", trainer.getExperienceYears() + " years"),
            createRow("Phone", trainer.getPhone()),
            createRow("Email", trainer.getEmail()),
            createRow("Base Salary", "Rs. " + (int) trainer.getBaseSalary()),
            createRow("Total Salary", "Rs. " + (int) trainerService.calculateSalary(trainer.getTrainerId())),
            createRow("Members", trainer.getAssignedMemberIds().size() + "/" + Trainer.MAX_MEMBERS)
        );

        leftPanel.getChildren().addAll(avatar, nameLabel, idLabel, infoBox);
        root.setLeft(leftPanel);

        // Right panel - Assigned members
        VBox rightPanel = new VBox(12);
        rightPanel.setPadding(new Insets(20));

        Label secTitle = new Label("ASSIGNED MEMBERS (" +
                trainer.getAssignedMemberIds().size() + "/" + Trainer.MAX_MEMBERS + ")");
        secTitle.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        HBox actions = new HBox(12);
        Button addMemberBtn = UIHelper.createPrimaryButton("+ Assign Member");
        addMemberBtn.setDisable(!trainer.canAcceptMember());
        addMemberBtn.setOnAction(e -> showAssignMemberDialog());

        actions.getChildren().add(addMemberBtn);

        memberListBox = new VBox(8);
        refreshMemberList();

        rightPanel.getChildren().addAll(secTitle, actions, memberListBox);
        ScrollPane sp = new ScrollPane(rightPanel);
        UIHelper.applyDarkScrollPane(sp);
        root.setCenter(sp);
    }

    private void refreshMemberList() {
        memberListBox.getChildren().clear();
        DataStore ds = DataStore.getInstance();

        if (trainer.getAssignedMemberIds().isEmpty()) {
            memberListBox.getChildren().add(UIHelper.createLabel("No members assigned."));
            return;
        }

        for (String memberId : trainer.getAssignedMemberIds()) {
            Member member = ds.getMember(memberId);
            if (member == null) continue;

            HBox card = new HBox(12);
            card.setPadding(new Insets(12));
            card.setAlignment(Pos.CENTER_LEFT);
            card.setStyle("-fx-background-color: " + UIHelper.BG_CARD + "; -fx-background-radius: 8;");

            Label mAvatar = new Label(member.getName().substring(0, 1));
            mAvatar.setMinSize(36, 36); mAvatar.setMaxSize(36, 36);
            mAvatar.setAlignment(Pos.CENTER);
            mAvatar.setStyle(
                "-fx-background-color: " + UIHelper.PRIMARY + "; -fx-background-radius: 18;" +
                "-fx-text-fill: white; -fx-font-weight: bold;"
            );

            VBox mInfo = new VBox(2);
            mInfo.getChildren().addAll(
                UIHelper.createValueLabel(member.getName()),
                UIHelper.createLabel(member.getPlan().getDisplayName() + " | " + member.getFitnessGoal())
            );
            HBox.setHgrow(mInfo, Priority.ALWAYS);

            Button removeBtn = new Button("Remove");
            removeBtn.setStyle(
                "-fx-background-color: " + UIHelper.DANGER + ";" +
                "-fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 6; -fx-cursor: hand;"
            );
            removeBtn.setOnAction(e -> {
                trainerService.removeMemberFromTrainer(trainer.getTrainerId(), memberId);
                refreshMemberList();
            });

            card.getChildren().addAll(mAvatar, mInfo, removeBtn);
            memberListBox.getChildren().add(card);
        }
    }

    private void showAssignMemberDialog() {
        DataStore ds = DataStore.getInstance();
        // Get unassigned members
        List<Member> unassigned = ds.getAllMembers().stream()
                .filter(m -> !m.hasTrainer())
                .collect(Collectors.toList());

        if (unassigned.isEmpty()) {
            UIHelper.showAlert("Info", "No unassigned members available.", Alert.AlertType.INFORMATION);
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>();
        dialog.setTitle("Assign Member");
        dialog.setHeaderText("Select a member to assign to " + trainer.getName());

        for (Member m : unassigned) {
            dialog.getItems().add(m.getMemberId() + " - " + m.getName());
        }
        if (!dialog.getItems().isEmpty()) {
            dialog.setSelectedItem(dialog.getItems().get(0));
        }

        dialog.showAndWait().ifPresent(selection -> {
            String memberId = selection.split(" - ")[0];
            try {
                trainerService.assignMemberToTrainer(trainer.getTrainerId(), memberId);
                refreshMemberList();
            } catch (TrainerLimitExceededException ex) {
                UIHelper.showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private HBox createRow(String label, String value) {
        HBox row = new HBox();
        Label lbl = UIHelper.createLabel(label + ": ");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label val = UIHelper.createValueLabel(value);
        row.getChildren().addAll(lbl, spacer, val);
        return row;
    }

    public BorderPane getRoot() { return root; }
}
