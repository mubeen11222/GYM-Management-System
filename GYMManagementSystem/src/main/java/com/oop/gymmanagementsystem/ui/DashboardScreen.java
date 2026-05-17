package com.oop.gymmanagementsystem.ui;

import com.oop.gymmanagementsystem.models.Member;
import com.oop.gymmanagementsystem.models.MembershipPlan;
import com.oop.gymmanagementsystem.models.PaymentStatus;
import com.oop.gymmanagementsystem.models.Trainer;
import com.oop.gymmanagementsystem.services.AuthService;
import com.oop.gymmanagementsystem.services.PaymentService;
import com.oop.gymmanagementsystem.services.TrainerService;
import com.oop.gymmanagementsystem.storage.DataStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardScreen {
    private BorderPane root;

    public DashboardScreen() {
        buildUI();
    }

    private AuthService authService;
    private java.util.function.Consumer<String> navigate;
    private Runnable onLogout;

    public DashboardScreen(AuthService authService, java.util.function.Consumer<String> navigate, Runnable onLogout) {
        this.authService = authService;
        this.navigate = navigate;
        this.onLogout = onLogout;
        buildUI();
    }

    private void buildUI() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #0A0A0A;");

        ScrollPane scrollPane = new ScrollPane(createDashboardContent());
        UIHelper.applyDarkScrollPane(scrollPane);
        scrollPane.setStyle("-fx-background: #0A0A0A; -fx-background-color: #0A0A0A;");
        root.setCenter(scrollPane);
    }

    private VBox createDashboardContent() {
        DataStore ds = DataStore.getInstance();
        PaymentService ps = new PaymentService();
        TrainerService ts = new TrainerService();

        List<Member> members = ds.getAllMembers();
        List<Trainer> trainers = ds.getAllTrainers();
        int memberCount = members.size();
        int trainerCount = trainers.size();
        long advancedCount = members.stream().filter(m -> m.getPlan() == MembershipPlan.ADVANCED).count();
        long basicCount = memberCount - advancedCount;
        long paidCount = ps.getAllPayments().stream().filter(p -> p.getStatus() == PaymentStatus.PAID).count();
        long unpaidCount = ps.getAllPayments().stream().filter(p -> p.getStatus() != PaymentStatus.PAID).count();
        double revenue = ps.getTotalRevenue();
        double expected = ps.getTotalExpectedRevenue();
        double pending = ps.getTotalPendingAmount();
        double salaries = ts.getTotalSalaryExpense();
        double profit = revenue - salaries;
        int assigned = trainers.stream().mapToInt(t -> t.getAssignedMemberIds().size()).sum();
        int capacity = trainerCount * Trainer.MAX_MEMBERS;
        double collectionRate = expected == 0 ? 0 : revenue / expected;
        double salaryCoverage = salaries == 0 ? 0 : revenue / salaries;

        VBox page = new VBox(24);
        page.setPadding(new Insets(32));
        page.setStyle("-fx-background-color: #0A0A0A;");

        // Header Row
        HBox header = new HBox(18);
        header.setAlignment(Pos.CENTER_LEFT);
        VBox headerText = new VBox(4);
        Label over = overline("OPERATIONS DASHBOARD");
        Label title = new Label("Command center");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 34px; -fx-font-family: " + UIHelper.FONT + "; -fx-font-weight: 900;");
        Label subtitle = new Label(String.format("%d members · %d trainers · %s", 
                memberCount, trainerCount, LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"))));
        subtitle.setStyle("-fx-text-fill: " + UIHelper.TEXT_SECONDARY + "; -fx-font-size: 13px; -fx-font-weight: 600;");
        headerText.getChildren().addAll(over, title, subtitle);
        HBox.setHgrow(headerText, Priority.ALWAYS);

        if (profit < 0) {
            header.getChildren().addAll(headerText, attentionBadge("Salary exceeds revenue"));
        } else {
            header.getChildren().addAll(headerText);
        }

        // Top Stats Row (Revenue, Pending, Net Profit)
        HBox topRow = new HBox(18);
        topRow.getChildren().addAll(
                summaryStat("REVENUE COLLECTED", "Rs. " + formatAmount(revenue), paidCount + " payments received", UIHelper.SUCCESS),
                summaryStat("STILL PENDING", "Rs. " + formatAmount(pending), unpaidCount + " members overdue", UIHelper.WARNING),
                profitStat("NET PROFIT", profit, salaries, salaryCoverage)
        );
        stretch(topRow);

        // Middle Stats Row (Members, Trainers, Collections, Outstanding)
        HBox midRow = new HBox(18);
        midRow.getChildren().addAll(
                metricCard("MEMBERS", String.valueOf(memberCount), advancedCount + " advanced · " + basicCount + " basic"),
                metricCard("TRAINERS", String.valueOf(trainerCount), assigned + " of " + capacity + " slots filled"),
                metricCard("COLLECTIONS", "Rs. " + formatAmount(revenue), String.format("%.0f%% collection rate", collectionRate * 100)),
                metricCard("OUTSTANDING", "Rs. " + formatAmount(pending), unpaidCount + " pending payments")
        );
        stretch(midRow);

        // Bottom Detailed Row
        HBox bottomRow = new HBox(18);
        VBox memberMix = detailedPanel("MEMBERSHIP MIX");
        memberMix.getChildren().addAll(
                detailBar("Advanced", advancedCount, memberCount, UIHelper.ACCENT),
                detailBar("Basic", basicCount, memberCount, "#A855F7"),
                new Label(memberCount + " of 20 capacity used") {{
                    setStyle("-fx-text-fill: #555555; -fx-font-size: 11px; -fx-padding: 10 0 0 0;");
                }}
        );

        VBox trainerCap = detailedPanel("TRAINER CAPACITY");
        for (Trainer t : trainers.subList(0, Math.min(trainers.size(), 3))) {
            trainerCap.getChildren().add(detailBar(t.getName(), t.getAssignedMemberIds().size(), Trainer.MAX_MEMBERS, "#2DD4BF"));
        }

        VBox cashflow = detailedPanel("CASHFLOW SUMMARY");
        cashflow.getChildren().addAll(
                rowStat("Collection rate", String.format("%.0f%%", collectionRate * 100), UIHelper.SUCCESS),
                rowStat("Paid records", String.valueOf(paidCount), "white"),
                rowStat("Pending records", String.valueOf(unpaidCount), UIHelper.WARNING),
                rowStat("Salary coverage", String.format("%.0f%%", salaryCoverage * 100), UIHelper.PRIMARY)
        );

        bottomRow.getChildren().addAll(memberMix, trainerCap, cashflow);
        stretch(bottomRow);

        page.getChildren().addAll(header, topRow, midRow, bottomRow);
        return page;
    }

    private VBox summaryStat(String title, String value, String sub, String color) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(24));
        card.setStyle(panelStyle("#141414"));
        Label t = overline(title);
        Label v = new Label(value);
        v.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 30px; -fx-font-weight: 900;");
        Label s = new Label(sub);
        s.setStyle("-fx-text-fill: #555555; -fx-font-size: 12px; -fx-font-weight: 600;");
        card.getChildren().addAll(t, v, s);
        return card;
    }

    private VBox profitStat(String title, double profit, double salaries, double coverage) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(24));
        card.setStyle(panelStyle("#141414"));
        Label t = overline(title);
        Label v = new Label((profit >= 0 ? "Rs. " : "-Rs. ") + formatAmount(Math.abs(profit)));
        v.setStyle("-fx-text-fill: " + (profit >= 0 ? UIHelper.SUCCESS : UIHelper.PRIMARY) + "; -fx-font-size: 30px; -fx-font-weight: 900;");
        Label s = new Label("Salary: Rs. " + formatAmount(salaries));
        s.setStyle("-fx-text-fill: #555555; -fx-font-size: 12px; -fx-font-weight: 600;");
        
        Region spacer = new Region() {{ setPrefHeight(10); }};
        
        HBox covRow = new HBox();
        Label covL = new Label("Revenue covers");
        covL.setStyle("-fx-text-fill: #777777; -fx-font-size: 11px;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label covV = new Label(String.format("%.0f%% of salary", coverage * 100));
        covV.setStyle("-fx-text-fill: " + UIHelper.PRIMARY + "; -fx-font-size: 11px;");
        covRow.getChildren().addAll(covL, sp, covV);
        
        ProgressBar pb = new ProgressBar(Math.min(coverage, 1.0));
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setPrefHeight(6);
        pb.setStyle("-fx-accent: " + UIHelper.PRIMARY + ";");
        
        card.getChildren().addAll(t, v, s, spacer, covRow, pb);
        return card;
    }

    private VBox metricCard(String title, String value, String sub) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setStyle(panelStyle("#111111"));
        Label t = overline(title);
        Label v = new Label(value);
        v.setStyle("-fx-text-fill: " + UIHelper.ACCENT + "; -fx-font-size: 24px; -fx-font-weight: 900;");
        Label s = new Label(sub);
        s.setStyle("-fx-text-fill: #555555; -fx-font-size: 11px; -fx-font-weight: 600;");
        card.getChildren().addAll(t, v, s);
        return card;
    }

    private VBox detailedPanel(String title) {
        VBox panel = new VBox(14);
        panel.setPadding(new Insets(24));
        panel.setStyle(panelStyle("#111111"));
        panel.getChildren().add(overline(title));
        return panel;
    }

    private VBox detailBar(String label, long value, int total, String color) {
        VBox box = new VBox(6);
        HBox row = new HBox();
        Label l = new Label(label);
        l.setStyle("-fx-text-fill: #999999; -fx-font-size: 12px;");
        Region s = new Region(); HBox.setHgrow(s, Priority.ALWAYS);
        Label v = new Label(String.valueOf(value));
        v.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: 900;");
        row.getChildren().addAll(l, s, v);
        
        ProgressBar pb = new ProgressBar(total == 0 ? 0 : value / (double) total);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setPrefHeight(4);
        pb.setStyle("-fx-accent: " + color + ";");
        
        box.getChildren().addAll(row, pb);
        return box;
    }

    private HBox rowStat(String label, String value, String color) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label l = new Label(label);
        l.setStyle("-fx-text-fill: #777777; -fx-font-size: 13px;");
        Region s = new Region(); HBox.setHgrow(s, Priority.ALWAYS);
        Label v = new Label(value);
        v.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 14px; -fx-font-weight: 900;");
        row.getChildren().addAll(l, s, v);
        return row;
    }

    private HBox attentionBadge(String text) {
        HBox badge = new HBox(8);
        badge.setAlignment(Pos.CENTER_LEFT);
        badge.setPadding(new Insets(10, 16, 10, 16));
        badge.setStyle("-fx-background-color: #241B15; -fx-background-radius: 14; -fx-border-color: #442D20; -fx-border-radius: 14;");
        
        Circle dot = new Circle(4, Color.web(UIHelper.PRIMARY));
        VBox textV = new VBox(0);
        Label top = new Label("Needs attention");
        top.setStyle("-fx-text-fill: " + UIHelper.PRIMARY + "; -fx-font-size: 12px; -fx-font-weight: 900;");
        Label bot = new Label(text);
        bot.setStyle("-fx-text-fill: #816852; -fx-font-size: 10px;");
        textV.getChildren().addAll(top, bot);
        
        badge.getChildren().addAll(dot, textV);
        return badge;
    }

    private Label overline(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #444444; -fx-font-size: 10px; -fx-font-weight: 900; -fx-letter-spacing: 0.5;");
        return l;
    }

    private String formatAmount(double amount) {
        if (amount >= 1000) return String.format("%.0fk", amount / 1000.0);
        return String.valueOf((int) amount);
    }

    private String panelStyle(String bg) {
        return "-fx-background-color: " + bg + "; -fx-background-radius: 20;";
    }

    private void stretch(HBox row) {
        for (var node : row.getChildren()) {
            HBox.setHgrow(node, Priority.ALWAYS);
            if (node instanceof VBox) ((VBox) node).setMaxWidth(Double.MAX_VALUE);
        }
    }

    public BorderPane getRoot() {
        return root;
    }
}
