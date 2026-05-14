package com.oop.gymmanagementsystem.ui;

import com.oop.gymmanagementsystem.exceptions.InvalidAgeException;
import com.oop.gymmanagementsystem.exceptions.TrainerLimitExceededException;
import com.oop.gymmanagementsystem.models.*;
import com.oop.gymmanagementsystem.services.*;
import com.oop.gymmanagementsystem.storage.DataStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.function.Consumer;

public class MembersPanel {
    private final MemberService memberService;
    private final TrainerService trainerService;
    private final Consumer<String> onNavigate;
    private final Consumer<Member> onMemberSelected;
    private BorderPane root;
    private VBox memberListBox;

    public MembersPanel(MemberService memberService, TrainerService trainerService,
                        Consumer<String> onNavigate, Consumer<Member> onMemberSelected) {
        this.memberService = memberService;
        this.trainerService = trainerService;
        this.onNavigate = onNavigate;
        this.onMemberSelected = onMemberSelected;
        buildUI();
    }

    private void buildUI() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIHelper.BG_DARK + ";");

        // ── Top Bar ──
        HBox topBar = new HBox(12);
        topBar.setPadding(new Insets(16, 24, 16, 24));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: " + UIHelper.BG_SIDEBAR + ";");

        Button backBtn = UIHelper.createPrimaryButton("← Dashboard");
        backBtn.setOnAction(e -> onNavigate.accept("dashboard"));

        Label title = new Label("\uD83D\uDC65  Members Module");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        topBar.getChildren().addAll(backBtn, title);
        root.setTop(topBar);

        // ── Sidebar ──
        VBox sidebar = new VBox(8);
        sidebar.setPadding(new Insets(16));
        sidebar.setPrefWidth(200);
        sidebar.setStyle("-fx-background-color: " + UIHelper.BG_SIDEBAR + ";");

        Label sideTitle = new Label("ACTIONS");
        sideTitle.setStyle("-fx-text-fill: " + UIHelper.TEXT_SECONDARY + "; -fx-font-size: 12px; -fx-font-weight: bold;");

        Button addBtn = UIHelper.createSidebarButton("➕ Add Member");
        addBtn.setOnAction(e -> showAddMemberDialog());

        Button refreshBtn = UIHelper.createSidebarButton("🔄 Refresh");
        refreshBtn.setOnAction(e -> refreshList());

        // Stats in sidebar
        int total = memberService.getAllMembers().size();
        long advanced = memberService.getAllMembers().stream()
                .filter(m -> m.getPlan() == MembershipPlan.ADVANCED).count();

        VBox statsBox = new VBox(4);
        statsBox.setPadding(new Insets(12));
        statsBox.setStyle("-fx-background-color: " + UIHelper.BG_CARD + "; -fx-background-radius: 8;");
        statsBox.getChildren().addAll(
            createStatLabel("Total: " + total),
            createStatLabel("Advanced: " + advanced),
            createStatLabel("Basic: " + (total - advanced))
        );

        sidebar.getChildren().addAll(sideTitle, addBtn, refreshBtn,
                new Region() {{ setPrefHeight(16); }}, statsBox);
        root.setLeft(sidebar);

        // ── Main Content ──
        memberListBox = new VBox(8);
        memberListBox.setPadding(new Insets(16));

        ScrollPane scrollPane = new ScrollPane(memberListBox);
        UIHelper.applyDarkScrollPane(scrollPane);
        root.setCenter(scrollPane);

        refreshList();
    }

    private void refreshList() {
        memberListBox.getChildren().clear();
        List<Member> members = memberService.getAllMembers();

        if (members.isEmpty()) {
            Label empty = UIHelper.createLabel("No members found. Click 'Add Member' to get started.");
            memberListBox.getChildren().add(empty);
            return;
        }

        for (Member member : members) {
            memberListBox.getChildren().add(createMemberCard(member));
        }
    }

    private HBox createMemberCard(Member member) {
        HBox card = new HBox(16);
        card.setPadding(new Insets(16));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
            "-fx-background-color: " + UIHelper.BG_CARD + ";" +
            "-fx-background-radius: 10; -fx-cursor: hand;"
        );

        // Avatar circle
        Label avatar = new Label(member.getName().substring(0, 1).toUpperCase());
        avatar.setMinSize(48, 48);
        avatar.setMaxSize(48, 48);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle(
            "-fx-background-color: " + UIHelper.PRIMARY + ";" +
            "-fx-background-radius: 24;" +
            "-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;"
        );

        // Info
        VBox info = new VBox(2);
        Label nameLabel = UIHelper.createValueLabel(member.getName());
        Label detailLabel = UIHelper.createLabel(
            member.getPlan().getDisplayName() + " Plan  |  " +
            member.getMemberId() + "  |  " + member.getFitnessGoal()
        );
        info.getChildren().addAll(nameLabel, detailLabel);
        HBox.setHgrow(info, Priority.ALWAYS);

        // Status
        PaymentService ps = new PaymentService();
        PaymentStatus status = ps.getMemberPaymentStatus(member.getMemberId());
        Label statusLabel = new Label(status.getDisplayName());
        String statusColor = status == PaymentStatus.PAID ? UIHelper.SUCCESS : UIHelper.WARNING;
        statusLabel.setStyle(
            "-fx-text-fill: " + statusColor + "; -fx-font-size: 12px; -fx-font-weight: bold;" +
            "-fx-padding: 4 12; -fx-background-color: " + statusColor + "22;" +
            "-fx-background-radius: 12;"
        );

        card.getChildren().addAll(avatar, info, statusLabel);

        card.setOnMouseClicked(e -> onMemberSelected.accept(member));
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: #3A3A3A; -fx-background-radius: 10; -fx-cursor: hand;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: " + UIHelper.BG_CARD + "; -fx-background-radius: 10; -fx-cursor: hand;"
        ));

        return card;
    }

    private void showAddMemberDialog() {
        Dialog<Member> dialog = new Dialog<>();
        dialog.setTitle("Add New Member");

        DialogPane dp = dialog.getDialogPane();
        dp.setStyle("-fx-background-color: " + UIHelper.BG_CARD + ";");
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
        genderF.setStyle("-fx-background-color: #333; -fx-background-radius: 6;");
        TextField phoneF = UIHelper.createTextField("Phone");
        TextField emailF = UIHelper.createTextField("Email");
        TextField addressF = UIHelper.createTextField("Address");
        TextField weightF = UIHelper.createTextField("Weight (kg)");
        TextField heightF = UIHelper.createTextField("Height (cm)");

        ComboBox<String> goalF = new ComboBox<>();
        goalF.getItems().addAll("Weight Loss", "Muscle Gain", "Strength", "General Fitness");
        goalF.setValue("General Fitness");
        goalF.setStyle("-fx-background-color: #333; -fx-background-radius: 6;");

        ComboBox<MembershipPlan> planF = new ComboBox<>();
        planF.getItems().addAll(MembershipPlan.values());
        planF.setValue(MembershipPlan.BASIC);
        planF.setStyle("-fx-background-color: #333; -fx-background-radius: 6;");

        ComboBox<String> trainerF = new ComboBox<>();
        trainerF.setDisable(true);
        trainerF.setStyle("-fx-background-color: #333; -fx-background-radius: 6;");

        // Load available trainers
        planF.setOnAction(e -> {
            if (planF.getValue() == MembershipPlan.ADVANCED) {
                trainerF.setDisable(false);
                trainerF.getItems().clear();
                for (Trainer t : trainerService.getAvailableTrainers()) {
                    trainerF.getItems().add(t.getTrainerId() + " - " + t.getName());
                }
            } else {
                trainerF.setDisable(true);
                trainerF.setValue(null);
            }
        });

        int row = 0;
        grid.add(createFormLabel("Name:"), 0, row); grid.add(nameF, 1, row++);
        grid.add(createFormLabel("Age:"), 0, row); grid.add(ageF, 1, row++);
        grid.add(createFormLabel("Gender:"), 0, row); grid.add(genderF, 1, row++);
        grid.add(createFormLabel("Phone:"), 0, row); grid.add(phoneF, 1, row++);
        grid.add(createFormLabel("Email:"), 0, row); grid.add(emailF, 1, row++);
        grid.add(createFormLabel("Address:"), 0, row); grid.add(addressF, 1, row++);
        grid.add(createFormLabel("Weight (kg):"), 0, row); grid.add(weightF, 1, row++);
        grid.add(createFormLabel("Height (cm):"), 0, row); grid.add(heightF, 1, row++);
        grid.add(createFormLabel("Goal:"), 0, row); grid.add(goalF, 1, row++);
        grid.add(createFormLabel("Plan:"), 0, row); grid.add(planF, 1, row++);
        grid.add(createFormLabel("Trainer:"), 0, row); grid.add(trainerF, 1, row++);

        dp.setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    String trainerId = null;
                    if (trainerF.getValue() != null) {
                        trainerId = trainerF.getValue().split(" - ")[0];
                    }
                    return memberService.addMember(
                        nameF.getText(), Integer.parseInt(ageF.getText()),
                        genderF.getValue(), phoneF.getText(), emailF.getText(),
                        addressF.getText(), planF.getValue(),
                        Double.parseDouble(weightF.getText()),
                        Double.parseDouble(heightF.getText()),
                        goalF.getValue(), trainerId
                    );
                } catch (InvalidAgeException | TrainerLimitExceededException ex) {
                    UIHelper.showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
                } catch (NumberFormatException ex) {
                    UIHelper.showAlert("Error", "Please enter valid numbers.", Alert.AlertType.ERROR);
                } catch (IllegalArgumentException ex) {
                    UIHelper.showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(m -> refreshList());
    }

    private Label createFormLabel(String text) {
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
