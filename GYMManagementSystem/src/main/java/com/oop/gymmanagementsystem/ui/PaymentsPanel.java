package com.oop.gymmanagementsystem.ui;

import com.oop.gymmanagementsystem.models.*;
import com.oop.gymmanagementsystem.services.*;
import com.oop.gymmanagementsystem.storage.DataStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PaymentsPanel {
    private final PaymentService paymentService;
    private final TrainerService trainerService;
    private final Consumer<String> onNavigate;
    private BorderPane root;

    public PaymentsPanel(PaymentService paymentService, TrainerService trainerService,
                         Consumer<String> onNavigate) {
        this.paymentService = paymentService;
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
        Button backBtn = UIHelper.createPrimaryButton("← Dashboard");
        backBtn.setOnAction(e -> onNavigate.accept("dashboard"));
        Label title = new Label("\uD83D\uDCB0  Payments Module");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        topBar.getChildren().addAll(backBtn, title);
        root.setTop(topBar);

        // Sidebar with sub-sections
        VBox sidebar = new VBox(8);
        sidebar.setPadding(new Insets(16));
        sidebar.setPrefWidth(200);
        sidebar.setStyle("-fx-background-color: " + UIHelper.BG_SIDEBAR + ";");

        Label sideTitle = new Label("SECTIONS");
        sideTitle.setStyle("-fx-text-fill: " + UIHelper.TEXT_SECONDARY + "; -fx-font-size: 12px; -fx-font-weight: bold;");

        Button reportsBtn = UIHelper.createSidebarButton("📊 Reports");
        Button feesBtn = UIHelper.createSidebarButton("💳 Member Fees");
        Button salariesBtn = UIHelper.createSidebarButton("💰 Trainer Salaries");

        sidebar.getChildren().addAll(sideTitle, reportsBtn, feesBtn, salariesBtn);
        root.setLeft(sidebar);

        // Default: show reports
        ScrollPane contentScroll = new ScrollPane();
        UIHelper.applyDarkScrollPane(contentScroll);
        root.setCenter(contentScroll);

        reportsBtn.setOnAction(e -> contentScroll.setContent(buildReportsView()));
        feesBtn.setOnAction(e -> contentScroll.setContent(buildFeesView()));
        salariesBtn.setOnAction(e -> contentScroll.setContent(buildSalariesView()));

        // Show reports by default
        contentScroll.setContent(buildReportsView());
    }

    private VBox buildReportsView() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(20));

        Label sec = sectionTitle("Financial Reports");
        content.getChildren().add(sec);

        double totalRevenue = paymentService.getTotalRevenue();
        double totalExpected = paymentService.getTotalExpectedRevenue();
        double totalPending = paymentService.getTotalPendingAmount();
        double trainerExpense = trainerService.getTotalSalaryExpense();
        double profit = totalRevenue - trainerExpense;

        // Stats cards
        HBox statsRow = new HBox(12);
        statsRow.getChildren().addAll(
            UIHelper.createInfoCard("Total Revenue", "Rs. " + (int) totalRevenue, UIHelper.SUCCESS),
            UIHelper.createInfoCard("Expected", "Rs. " + (int) totalExpected, "#2196F3"),
            UIHelper.createInfoCard("Pending", "Rs. " + (int) totalPending, UIHelper.WARNING),
            UIHelper.createInfoCard("Expenses", "Rs. " + (int) trainerExpense, UIHelper.DANGER)
        );
        for (var n : statsRow.getChildren()) {
            HBox.setHgrow(n, Priority.ALWAYS);
            if (n instanceof VBox) ((VBox) n).setMaxWidth(Double.MAX_VALUE);
        }
        content.getChildren().add(statsRow);

        // Profit card
        VBox profitCard = new VBox(4);
        profitCard.setPadding(new Insets(20));
        profitCard.setAlignment(Pos.CENTER);
        profitCard.setStyle("-fx-background-color: " + UIHelper.BG_CARD + "; -fx-background-radius: 12;");
        String profitColor = profit >= 0 ? UIHelper.SUCCESS : UIHelper.DANGER;
        Label profitVal = new Label("Rs. " + (int) profit);
        profitVal.setStyle("-fx-text-fill: " + profitColor + "; -fx-font-size: 36px; -fx-font-weight: bold;");
        Label profitLabel = new Label("NET PROFIT");
        profitLabel.setStyle("-fx-text-fill: " + UIHelper.TEXT_SECONDARY + "; -fx-font-size: 14px;");
        profitCard.getChildren().addAll(profitVal, profitLabel);
        content.getChildren().add(profitCard);

        // Revenue chart
        content.getChildren().add(sectionTitle("Revenue by Month"));
        Map<String, Double> revenueMap = paymentService.getRevenueByMonth();
        Canvas chart = new Canvas(500, 220);
        drawBarChart(chart, revenueMap);
        content.getChildren().add(chart);

        return content;
    }

    private VBox buildFeesView() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(20));

        content.getChildren().add(sectionTitle("Member Fees"));

        // Filter buttons
        HBox filters = new HBox(12);
        Button allBtn = UIHelper.createPrimaryButton("All");
        Button paidBtn = UIHelper.createSidebarButton("Paid");
        Button unpaidBtn = UIHelper.createSidebarButton("Unpaid");

        VBox listBox = new VBox(8);

        Runnable showAll = () -> { listBox.getChildren().clear(); fillPayments(listBox, paymentService.getAllPayments()); };
        Runnable showPaid = () -> { listBox.getChildren().clear(); fillPayments(listBox, paymentService.getPaidPayments()); };
        Runnable showUnpaid = () -> { listBox.getChildren().clear(); fillPayments(listBox, paymentService.getUnpaidPayments()); };

        allBtn.setOnAction(e -> showAll.run());
        paidBtn.setOnAction(e -> showPaid.run());
        unpaidBtn.setOnAction(e -> showUnpaid.run());

        filters.getChildren().addAll(allBtn, paidBtn, unpaidBtn);
        content.getChildren().addAll(filters, listBox);

        // Show all by default
        showAll.run();

        // Summary
        Label summary = UIHelper.createLabel("Total Pending: Rs. " + (int) paymentService.getTotalPendingAmount());
        summary.setStyle("-fx-text-fill: " + UIHelper.WARNING + "; -fx-font-size: 14px; -fx-font-weight: bold;");
        content.getChildren().add(summary);

        return content;
    }

    private void fillPayments(VBox listBox, List<Payment> payments) {
        if (payments.isEmpty()) {
            listBox.getChildren().add(UIHelper.createLabel("No payments found."));
            return;
        }

        for (Payment p : payments) {
            HBox card = new HBox(12);
            card.setPadding(new Insets(12));
            card.setAlignment(Pos.CENTER_LEFT);
            card.setStyle("-fx-background-color: " + UIHelper.BG_CARD + "; -fx-background-radius: 8;");

            VBox info = new VBox(2);
            info.getChildren().addAll(
                UIHelper.createValueLabel(p.getMemberName() + " (" + p.getMemberId() + ")"),
                UIHelper.createLabel(p.getPlan().getDisplayName() + " Plan  |  " + p.getMonth())
            );
            HBox.setHgrow(info, Priority.ALWAYS);

            Label amountLabel = UIHelper.createValueLabel("Rs. " + (int) p.getAmount());

            String statusColor = p.getStatus() == PaymentStatus.PAID ? UIHelper.SUCCESS : UIHelper.WARNING;
            Label statusLabel = new Label(p.getStatus().getDisplayName());
            statusLabel.setStyle(
                "-fx-text-fill: " + statusColor + "; -fx-font-size: 11px; -fx-font-weight: bold;" +
                "-fx-padding: 4 10; -fx-background-color: " + statusColor + "22; -fx-background-radius: 10;"
            );

            if (p.getStatus() != PaymentStatus.PAID) {
                Button payBtn = new Button("Mark Paid");
                payBtn.setStyle(
                    "-fx-background-color: " + UIHelper.SUCCESS + ";" +
                    "-fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 6; -fx-cursor: hand;"
                );
                payBtn.setOnAction(e -> {
                    paymentService.markPaymentPaid(p.getPaymentId());
                    statusLabel.setText("Paid");
                    statusLabel.setStyle(
                        "-fx-text-fill: " + UIHelper.SUCCESS + "; -fx-font-size: 11px; -fx-font-weight: bold;" +
                        "-fx-padding: 4 10; -fx-background-color: " + UIHelper.SUCCESS + "22; -fx-background-radius: 10;"
                    );
                    card.getChildren().remove(payBtn);
                });
                card.getChildren().addAll(info, amountLabel, statusLabel, payBtn);
            } else {
                card.getChildren().addAll(info, amountLabel, statusLabel);
            }

            listBox.getChildren().add(card);
        }
    }

    private VBox buildSalariesView() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(20));

        content.getChildren().add(sectionTitle("Trainer Salaries"));

        Label note = UIHelper.createLabel("Salary = Base + Rs.2000/Basic Member + Rs.5000/Advanced Member");
        note.setStyle("-fx-text-fill: " + UIHelper.TEXT_SECONDARY + "; -fx-font-size: 12px; -fx-font-style: italic;");
        content.getChildren().add(note);

        double totalSalary = 0;

        for (Trainer trainer : trainerService.getAllTrainers()) {
            double salary = trainerService.calculateSalary(trainer.getTrainerId());
            totalSalary += salary;

            HBox card = new HBox(16);
            card.setPadding(new Insets(16));
            card.setAlignment(Pos.CENTER_LEFT);
            card.setStyle("-fx-background-color: " + UIHelper.BG_CARD + "; -fx-background-radius: 10;");

            Label avatar = new Label(trainer.getName().substring(0, 1));
            avatar.setMinSize(40, 40); avatar.setMaxSize(40, 40);
            avatar.setAlignment(Pos.CENTER);
            avatar.setStyle(
                "-fx-background-color: #1565C0; -fx-background-radius: 20;" +
                "-fx-text-fill: white; -fx-font-weight: bold;"
            );

            VBox info = new VBox(2);
            info.getChildren().addAll(
                UIHelper.createValueLabel(trainer.getName()),
                UIHelper.createLabel("Members: " + trainer.getAssignedMemberIds().size() +
                    "/" + Trainer.MAX_MEMBERS + "  |  " + trainer.getSpecialization())
            );
            HBox.setHgrow(info, Priority.ALWAYS);

            VBox salaryBox = new VBox(2);
            salaryBox.setAlignment(Pos.CENTER_RIGHT);
            Label baseLbl = UIHelper.createLabel("Base: Rs. " + (int) trainer.getBaseSalary());

            // Count member types
            DataStore ds = DataStore.getInstance();
            long adv = trainer.getAssignedMemberIds().stream()
                .map(ds::getMember).filter(m -> m != null && m.getPlan() == MembershipPlan.ADVANCED).count();
            long basic = trainer.getAssignedMemberIds().size() - adv;
            Label bonusLbl = UIHelper.createLabel("Bonus: " + basic + "×2K + " + adv + "×5K");
            Label totalLbl = new Label("Rs. " + (int) salary);
            totalLbl.setStyle("-fx-text-fill: " + UIHelper.SUCCESS + "; -fx-font-size: 16px; -fx-font-weight: bold;");

            salaryBox.getChildren().addAll(baseLbl, bonusLbl, totalLbl);

            card.getChildren().addAll(avatar, info, salaryBox);
            content.getChildren().add(card);
        }

        // Total
        VBox totalCard = new VBox(4);
        totalCard.setPadding(new Insets(16));
        totalCard.setAlignment(Pos.CENTER);
        totalCard.setStyle("-fx-background-color: " + UIHelper.BG_CARD + "; -fx-background-radius: 10;");
        Label totalVal = new Label("Rs. " + (int) totalSalary);
        totalVal.setStyle("-fx-text-fill: " + UIHelper.DANGER + "; -fx-font-size: 28px; -fx-font-weight: bold;");
        Label totalLbl = new Label("TOTAL SALARY EXPENSE");
        totalLbl.setStyle("-fx-text-fill: " + UIHelper.TEXT_SECONDARY + "; -fx-font-size: 12px;");
        totalCard.getChildren().addAll(totalVal, totalLbl);
        content.getChildren().add(totalCard);

        return content;
    }

    private void drawBarChart(Canvas canvas, Map<String, Double> data) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth(), h = canvas.getHeight();

        gc.setFill(Color.web(UIHelper.BG_CARD));
        gc.fillRoundRect(0, 0, w, h, 12, 12);

        if (data.isEmpty()) {
            gc.setFill(Color.web(UIHelper.TEXT_SECONDARY));
            gc.fillText("No revenue data available", w / 2 - 60, h / 2);
            return;
        }

        double maxVal = data.values().stream().mapToDouble(v -> v).max().orElse(1);
        double padL = 60, padR = 20, padT = 20, padB = 40;
        double chartW = w - padL - padR;
        double chartH = h - padT - padB;

        int n = data.size();
        double barW = Math.min(50, (chartW / n) * 0.6);
        double gap = (chartW - barW * n) / (n + 1);

        int i = 0;
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double barH = (entry.getValue() / maxVal) * chartH;
            double x = padL + gap + i * (barW + gap);
            double y = padT + chartH - barH;

            gc.setFill(Color.web(UIHelper.PRIMARY));
            gc.fillRoundRect(x, y, barW, barH, 4, 4);

            gc.setFill(Color.web(UIHelper.TEXT_SECONDARY));
            gc.fillText(com.oop.gymmanagementsystem.utils.DateUtils.getMonthName(entry.getKey()),
                    x, h - 10);
            gc.fillText("Rs." + (int)(double)entry.getValue(), x, y - 5);
            i++;
        }
    }

    private Label sectionTitle(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        return l;
    }

    public BorderPane getRoot() { return root; }
}
