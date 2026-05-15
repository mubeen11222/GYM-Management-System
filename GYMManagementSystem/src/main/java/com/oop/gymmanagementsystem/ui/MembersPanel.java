package com.oop.gymmanagementsystem.ui;

import com.oop.gymmanagementsystem.exceptions.InvalidAgeException;
import com.oop.gymmanagementsystem.exceptions.TrainerLimitExceededException;
import com.oop.gymmanagementsystem.models.Member;
import com.oop.gymmanagementsystem.models.MembershipPlan;
import com.oop.gymmanagementsystem.models.PaymentStatus;
import com.oop.gymmanagementsystem.models.Trainer;
import com.oop.gymmanagementsystem.services.MemberService;
import com.oop.gymmanagementsystem.services.PaymentService;
import com.oop.gymmanagementsystem.services.TrainerService;
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

        Button backBtn = UIHelper.createSecondaryButton("Back to Dashboard");
        backBtn.setOnAction(e -> onNavigate.accept("dashboard"));
        root.setTop(UIHelper.createTopBar("Members Module", backBtn));

        Button addBtn = UIHelper.createSidebarButton("+ Add Member");
        addBtn.setOnAction(e -> showAddMemberDialog());
        Button refreshBtn = UIHelper.createSidebarButton("Refresh List");
        refreshBtn.setOnAction(e -> refreshList());

        int total = memberService.getAllMembers().size();
        long advanced = memberService.getAllMembers().stream()
                .filter(m -> m.getPlan() == MembershipPlan.ADVANCED).count();

        VBox statsBox = new VBox(8);
        statsBox.setPadding(new Insets(14));
        statsBox.setStyle(UIHelper.cardStyle());
        statsBox.getChildren().addAll(
                createStatLabel("Total: " + total),
                createStatLabel("Advanced: " + advanced),
                createStatLabel("Basic: " + (total - advanced))
        );

        root.setLeft(UIHelper.createSidebar("Actions", addBtn, refreshBtn,
                new Region() {{ setPrefHeight(16); }}, statsBox));

        memberListBox = new VBox(10);
        memberListBox.setPadding(new Insets(18));

        ScrollPane scrollPane = new ScrollPane(memberListBox);
        UIHelper.applyDarkScrollPane(scrollPane);
        root.setCenter(scrollPane);
        refreshList();
    }

    private void refreshList() {
        memberListBox.getChildren().clear();
        List<Member> members = memberService.getAllMembers();

        if (members.isEmpty()) {
            memberListBox.getChildren().add(UIHelper.createLabel("No members found. Add a member to get started."));
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
        card.setStyle(cardStyle(false));

        Label avatar = new Label(member.getName().substring(0, 1).toUpperCase());
        avatar.setMinSize(48, 48);
        avatar.setMaxSize(48, 48);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle(
                "-fx-background-color: " + UIHelper.PRIMARY + ";" +
                "-fx-background-radius: 24;" +
                "-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: 900;"
        );

        VBox info = new VBox(4);
        Label nameLabel = UIHelper.createValueLabel(member.getName());
        Label detailLabel = UIHelper.createLabel(
                member.getPlan().getDisplayName() + " Plan | " +
                member.getMemberId() + " | " + member.getFitnessGoal()
        );
        info.getChildren().addAll(nameLabel, detailLabel);
        HBox.setHgrow(info, Priority.ALWAYS);

        PaymentService ps = new PaymentService();
        PaymentStatus status = ps.getMemberPaymentStatus(member.getMemberId());
        String statusColor = status == PaymentStatus.PAID ? UIHelper.SUCCESS : UIHelper.WARNING;
        Label statusLabel = createPill(status.getDisplayName(), statusColor);

        card.getChildren().addAll(avatar, info, statusLabel);
        card.setOnMouseClicked(e -> onMemberSelected.accept(member));
        card.setOnMouseEntered(e -> card.setStyle(cardStyle(true)));
        card.setOnMouseExited(e -> card.setStyle(cardStyle(false)));
        return card;
    }

    private void showAddMemberDialog() {
        Dialog<Member> dialog = new Dialog<>();
        dialog.setTitle("Add New Member");

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
        TextField weightF = UIHelper.createTextField("Weight (kg)");
        TextField heightF = UIHelper.createTextField("Height (cm)");

        ComboBox<String> goalF = new ComboBox<>();
        goalF.getItems().addAll("Weight Loss", "Muscle Gain", "Strength", "General Fitness");
        goalF.setValue("General Fitness");
        goalF.setMaxWidth(Double.MAX_VALUE);

        ComboBox<MembershipPlan> planF = new ComboBox<>();
        planF.getItems().addAll(MembershipPlan.values());
        planF.setValue(MembershipPlan.BASIC);
        planF.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> trainerF = new ComboBox<>();
        trainerF.setDisable(true);
        trainerF.setMaxWidth(Double.MAX_VALUE);

        planF.setOnAction(e -> {
            trainerF.getItems().clear();
            trainerF.setValue(null);
            boolean advanced = planF.getValue() == MembershipPlan.ADVANCED;
            trainerF.setDisable(!advanced);
            if (advanced) {
                for (Trainer t : trainerService.getAvailableTrainers()) {
                    trainerF.getItems().add(t.getTrainerId() + " - " + t.getName());
                }
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
        grid.add(createFormLabel("Trainer:"), 0, row); grid.add(trainerF, 1, row);
        dp.setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    if (isBlank(nameF, ageF, phoneF, emailF, addressF, weightF, heightF)) {
                        UIHelper.showAlert("Missing Information", "Please complete all required member fields.", Alert.AlertType.WARNING);
                        return null;
                    }
                    if (planF.getValue() == MembershipPlan.ADVANCED && trainerF.getValue() == null) {
                        UIHelper.showAlert("Trainer Required", "Advanced members must be assigned to an available trainer.", Alert.AlertType.WARNING);
                        return null;
                    }
                    String trainerId = trainerF.getValue() == null ? null : trainerF.getValue().split(" - ")[0];
                    return memberService.addMember(
                            nameF.getText().trim(), Integer.parseInt(ageF.getText().trim()),
                            genderF.getValue(), phoneF.getText().trim(), emailF.getText().trim(),
                            addressF.getText().trim(), planF.getValue(),
                            Double.parseDouble(weightF.getText().trim()),
                            Double.parseDouble(heightF.getText().trim()),
                            goalF.getValue(), trainerId
                    );
                } catch (InvalidAgeException | TrainerLimitExceededException ex) {
                    UIHelper.showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
                } catch (NumberFormatException ex) {
                    UIHelper.showAlert("Invalid Number", "Please enter valid numeric values for age, weight and height.", Alert.AlertType.ERROR);
                } catch (IllegalArgumentException ex) {
                    UIHelper.showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(m -> refreshList());
    }

    private Label createFormLabel(String text) {
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
        label.setStyle(
                "-fx-text-fill: " + color + "; -fx-font-size: 12px; -fx-font-weight: 900;" +
                "-fx-padding: 5 12; -fx-background-color: " + color + "22;" +
                "-fx-background-radius: 999;"
        );
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
