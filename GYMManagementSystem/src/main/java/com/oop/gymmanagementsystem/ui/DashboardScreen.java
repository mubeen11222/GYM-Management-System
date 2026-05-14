package com.oop.gymmanagementsystem.ui;

import com.oop.gymmanagementsystem.models.User;
import com.oop.gymmanagementsystem.services.*;
import com.oop.gymmanagementsystem.storage.DataStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.function.Consumer;

public class DashboardScreen {
    private final AuthService authService;
    private final Consumer<String> onNavigate;
    private final Runnable onLogout;
    private VBox root;

    public DashboardScreen(AuthService authService, Consumer<String> onNavigate, Runnable onLogout) {
        this.authService = authService;
        this.onNavigate = onNavigate;
        this.onLogout = onLogout;
        buildUI();
    }

    private void buildUI() {
        root = new VBox(0);
        root.setStyle("-fx-background-color: " + UIHelper.BG_DARK + ";");

        // Top bar
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(16, 24, 16, 24));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: " + UIHelper.BG_SIDEBAR + ";");

        Label titleLabel = new Label("\uD83C\uDFCB  AI GYM Dashboard");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        User user = authService.getCurrentUser();
        Label userLabel = new Label("Welcome, " + (user != null ? user.getFullName() : "User"));
        userLabel.setStyle("-fx-text-fill: " + UIHelper.TEXT_SECONDARY + "; -fx-font-size: 14px;");

        Button logoutBtn = UIHelper.createPrimaryButton("Logout");
        logoutBtn.setOnAction(e -> onLogout.run());

        topBar.getChildren().addAll(titleLabel, spacer, userLabel, new Region() {{ setPrefWidth(16); }}, logoutBtn);

        // Stats row
        DataStore ds = DataStore.getInstance();
        PaymentService ps = new PaymentService();
        TrainerService ts = new TrainerService();

        int memberCount = ds.getAllMembers().size();
        int trainerCount = ds.getAllTrainers().size();
        double revenue = ps.getTotalRevenue();
        double pending = ps.getTotalPendingAmount();

        HBox statsRow = new HBox(16);
        statsRow.setPadding(new Insets(24));
        statsRow.setAlignment(Pos.CENTER);
        statsRow.getChildren().addAll(
            UIHelper.createInfoCard("Total Members", String.valueOf(memberCount), UIHelper.SUCCESS),
            UIHelper.createInfoCard("Total Trainers", String.valueOf(trainerCount), "#2196F3"),
            UIHelper.createInfoCard("Revenue", "Rs. " + (int)revenue, UIHelper.SUCCESS),
            UIHelper.createInfoCard("Pending", "Rs. " + (int)pending, UIHelper.WARNING)
        );
        for (var node : statsRow.getChildren()) {
            HBox.setHgrow(node, Priority.ALWAYS);
            if (node instanceof VBox) ((VBox) node).setMaxWidth(Double.MAX_VALUE);
        }

        // Module cards
        Label modulesTitle = new Label("MODULES");
        modulesTitle.setStyle("-fx-text-fill: " + UIHelper.TEXT_SECONDARY + "; -fx-font-size: 14px; -fx-font-weight: bold;");
        modulesTitle.setPadding(new Insets(0, 24, 0, 24));

        HBox cardsRow = new HBox(24);
        cardsRow.setPadding(new Insets(16, 24, 24, 24));
        cardsRow.setAlignment(Pos.CENTER);

        VBox membersCard = UIHelper.createCard("MEMBERS", memberCount + " registered", "\uD83D\uDC65");
        membersCard.setOnMouseClicked(e -> onNavigate.accept("members"));

        VBox trainersCard = UIHelper.createCard("TRAINERS", trainerCount + " active", "\uD83E\uDD3C");
        trainersCard.setOnMouseClicked(e -> onNavigate.accept("trainers"));

        VBox paymentsCard = UIHelper.createCard("PAYMENTS", "Rs. " + (int)revenue + " total", "\uD83D\uDCB0");
        paymentsCard.setOnMouseClicked(e -> onNavigate.accept("payments"));

        cardsRow.getChildren().addAll(membersCard, trainersCard, paymentsCard);
        for (var node : cardsRow.getChildren()) {
            HBox.setHgrow(node, Priority.ALWAYS);
            if (node instanceof VBox) ((VBox) node).setMaxWidth(Double.MAX_VALUE);
        }

        // Recent activity
        VBox activityBox = new VBox(8);
        activityBox.setPadding(new Insets(0, 24, 24, 24));
        Label actLabel = new Label("QUICK OVERVIEW");
        actLabel.setStyle("-fx-text-fill: " + UIHelper.TEXT_SECONDARY + "; -fx-font-size: 14px; -fx-font-weight: bold;");

        VBox actCard = new VBox(8);
        actCard.setPadding(new Insets(16));
        actCard.setStyle("-fx-background-color: " + UIHelper.BG_CARD + "; -fx-background-radius: 10;");

        long advancedCount = ds.getAllMembers().stream()
                .filter(m -> m.getPlan() == com.oop.gymmanagementsystem.models.MembershipPlan.ADVANCED).count();
        long basicCount = memberCount - advancedCount;
        double salaryExpense = ts.getTotalSalaryExpense();

        actCard.getChildren().addAll(
            createInfoRow("Advanced Members", String.valueOf(advancedCount)),
            createInfoRow("Basic Members", String.valueOf(basicCount)),
            createInfoRow("Trainer Salary Expense", "Rs. " + (int) salaryExpense),
            createInfoRow("Net Profit", "Rs. " + (int)(revenue - salaryExpense))
        );

        activityBox.getChildren().addAll(actLabel, actCard);

        root.getChildren().addAll(topBar, statsRow, modulesTitle, cardsRow, activityBox);
    }

    private HBox createInfoRow(String label, String value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label lbl = UIHelper.createLabel("  •  " + label);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label val = UIHelper.createValueLabel(value);
        row.getChildren().addAll(lbl, spacer, val);
        return row;
    }

    public VBox getRoot() { return root; }
}
