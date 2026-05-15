package com.oop.gymmanagementsystem.ui;

import com.oop.gymmanagementsystem.models.MembershipPlan;
import com.oop.gymmanagementsystem.models.Payment;
import com.oop.gymmanagementsystem.models.PaymentStatus;
import com.oop.gymmanagementsystem.models.Trainer;
import com.oop.gymmanagementsystem.services.PaymentService;
import com.oop.gymmanagementsystem.services.TrainerService;
import com.oop.gymmanagementsystem.storage.DataStore;
import com.oop.gymmanagementsystem.utils.DateUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PaymentsPanel {
    private final PaymentService paymentService;
    private final TrainerService trainerService;
    private final Consumer<String> onNavigate;
    private BorderPane root;
    private ScrollPane contentScroll;

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

        Button backBtn = UIHelper.createSecondaryButton("Back to Dashboard");
        backBtn.setOnAction(e -> onNavigate.accept("dashboard"));
        root.setTop(UIHelper.createTopBar("Payments Module", backBtn));

        Button reportsBtn = UIHelper.createSidebarButton("Reports");
        Button feesBtn = UIHelper.createSidebarButton("Member Fees");
        Button salariesBtn = UIHelper.createSidebarButton("Trainer Salaries");
        reportsBtn.setOnAction(e -> contentScroll.setContent(buildReportsView()));
        feesBtn.setOnAction(e -> contentScroll.setContent(buildFeesView()));
        salariesBtn.setOnAction(e -> contentScroll.setContent(buildSalariesView()));

        root.setLeft(UIHelper.createSidebar("Sections", reportsBtn, feesBtn, salariesBtn));

        contentScroll = new ScrollPane();
        UIHelper.applyDarkScrollPane(contentScroll);
        root.setCenter(contentScroll);
        contentScroll.setContent(buildReportsView());
    }

    private VBox buildReportsView() {
        VBox content = pageContent();
        content.getChildren().add(sectionTitle("Financial Reports"));

        double totalRevenue = paymentService.getTotalRevenue();
        double totalExpected = paymentService.getTotalExpectedRevenue();
        double totalPending = paymentService.getTotalPendingAmount();
        double trainerExpense = trainerService.getTotalSalaryExpense();
        double profit = totalRevenue - trainerExpense;

        HBox statsRow = new HBox(12);
        statsRow.getChildren().addAll(
                UIHelper.createInfoCard("Total Revenue", "Rs. " + (int) totalRevenue, UIHelper.SUCCESS),
                UIHelper.createInfoCard("Expected", "Rs. " + (int) totalExpected, UIHelper.ACCENT),
                UIHelper.createInfoCard("Pending", "Rs. " + (int) totalPending, UIHelper.WARNING),
                UIHelper.createInfoCard("Expenses", "Rs. " + (int) trainerExpense, UIHelper.DANGER)
        );
        stretch(statsRow);
        content.getChildren().add(statsRow);

        VBox profitCard = new VBox(6);
        profitCard.setPadding(new Insets(22));
        profitCard.setAlignment(Pos.CENTER);
        profitCard.setStyle(UIHelper.cardStyle());
        Label profitVal = new Label("Rs. " + (int) profit);
        profitVal.setStyle("-fx-text-fill: " + (profit >= 0 ? UIHelper.SUCCESS : UIHelper.DANGER)
                + "; -fx-font-size: 38px; -fx-font-weight: 900;");
        Label profitLabel = UIHelper.createLabel("NET PROFIT");
        profitLabel.setStyle("-fx-text-fill: " + UIHelper.TEXT_SECONDARY + "; -fx-font-size: 12px; -fx-font-weight: 900;");
        profitCard.getChildren().addAll(profitVal, profitLabel);
        content.getChildren().add(profitCard);

        content.getChildren().add(sectionTitle("Revenue by Month"));
        Canvas chart = new Canvas(720, 240);
        drawBarChart(chart, paymentService.getRevenueByMonth());
        content.getChildren().add(chart);
        return content;
    }

    private VBox buildFeesView() {
        VBox content = pageContent();
        content.getChildren().add(sectionTitle("Member Fees"));

        HBox filters = new HBox(10);
        Button allBtn = UIHelper.createPrimaryButton("All");
        Button paidBtn = UIHelper.createSecondaryButton("Paid");
        Button unpaidBtn = UIHelper.createSecondaryButton("Unpaid");
        VBox listBox = new VBox(10);

        Runnable showAll = () -> fillPayments(listBox, paymentService.getAllPayments());
        Runnable showPaid = () -> fillPayments(listBox, paymentService.getPaidPayments());
        Runnable showUnpaid = () -> fillPayments(listBox, paymentService.getUnpaidPayments());
        allBtn.setOnAction(e -> showAll.run());
        paidBtn.setOnAction(e -> showPaid.run());
        unpaidBtn.setOnAction(e -> showUnpaid.run());
        filters.getChildren().addAll(allBtn, paidBtn, unpaidBtn);

        content.getChildren().addAll(filters, listBox);
        showAll.run();

        Label summary = UIHelper.createValueLabel("Total Pending: Rs. " + (int) paymentService.getTotalPendingAmount());
        summary.setStyle("-fx-text-fill: " + UIHelper.WARNING + "; -fx-font-size: 14px; -fx-font-weight: 900;");
        content.getChildren().add(summary);
        return content;
    }

    private void fillPayments(VBox listBox, List<Payment> payments) {
        listBox.getChildren().clear();
        if (payments.isEmpty()) {
            listBox.getChildren().add(UIHelper.createLabel("No payments found."));
            return;
        }

        for (Payment p : payments) {
            HBox card = new HBox(12);
            card.setPadding(new Insets(14));
            card.setAlignment(Pos.CENTER_LEFT);
            card.setStyle(cardStyle());

            VBox info = new VBox(4);
            info.getChildren().addAll(
                    UIHelper.createValueLabel(p.getMemberName() + " (" + p.getMemberId() + ")"),
                    UIHelper.createLabel(p.getPlan().getDisplayName() + " Plan | " + p.getMonth())
            );
            HBox.setHgrow(info, Priority.ALWAYS);

            Label amountLabel = UIHelper.createValueLabel("Rs. " + (int) p.getAmount());
            String statusColor = p.getStatus() == PaymentStatus.PAID ? UIHelper.SUCCESS : UIHelper.WARNING;
            Label statusLabel = pill(p.getStatus().getDisplayName(), statusColor);

            card.getChildren().addAll(info, amountLabel, statusLabel);
            if (p.getStatus() != PaymentStatus.PAID) {
                Button payBtn = UIHelper.createSecondaryButton("Mark Paid");
                payBtn.setOnAction(e -> {
                    paymentService.markPaymentPaid(p.getPaymentId());
                    fillPayments(listBox, paymentService.getAllPayments());
                });
                card.getChildren().add(payBtn);
            }
            listBox.getChildren().add(card);
        }
    }

    private VBox buildSalariesView() {
        VBox content = pageContent();
        content.getChildren().add(sectionTitle("Trainer Salaries"));

        Label note = UIHelper.createLabel("Salary = Base + Rs. 2000 per basic member + Rs. 5000 per advanced member");
        content.getChildren().add(note);

        double totalSalary = 0;
        DataStore ds = DataStore.getInstance();

        for (Trainer trainer : trainerService.getAllTrainers()) {
            double salary = trainerService.calculateSalary(trainer.getTrainerId());
            totalSalary += salary;
            long advanced = trainer.getAssignedMemberIds().stream()
                    .map(ds::getMember)
                    .filter(m -> m != null && m.getPlan() == MembershipPlan.ADVANCED)
                    .count();
            long basic = trainer.getAssignedMemberIds().size() - advanced;

            HBox card = new HBox(16);
            card.setPadding(new Insets(16));
            card.setAlignment(Pos.CENTER_LEFT);
            card.setStyle(cardStyle());

            Label avatar = new Label(trainer.getName().substring(0, 1).toUpperCase());
            avatar.setMinSize(42, 42);
            avatar.setMaxSize(42, 42);
            avatar.setAlignment(Pos.CENTER);
            avatar.setStyle("-fx-background-color: " + UIHelper.ACCENT + "; -fx-background-radius: 21;"
                    + "-fx-text-fill: white; -fx-font-weight: 900;");

            VBox info = new VBox(4);
            info.getChildren().addAll(
                    UIHelper.createValueLabel(trainer.getName()),
                    UIHelper.createLabel("Members: " + trainer.getAssignedMemberIds().size()
                            + "/" + Trainer.MAX_MEMBERS + " | " + trainer.getSpecialization())
            );
            HBox.setHgrow(info, Priority.ALWAYS);

            VBox salaryBox = new VBox(3);
            salaryBox.setAlignment(Pos.CENTER_RIGHT);
            salaryBox.getChildren().addAll(
                    UIHelper.createLabel("Base: Rs. " + (int) trainer.getBaseSalary()),
                    UIHelper.createLabel("Bonus: " + basic + "x2K + " + advanced + "x5K"),
                    value("Rs. " + (int) salary, UIHelper.SUCCESS)
            );
            card.getChildren().addAll(avatar, info, salaryBox);
            content.getChildren().add(card);
        }

        VBox totalCard = new VBox(5);
        totalCard.setPadding(new Insets(18));
        totalCard.setAlignment(Pos.CENTER);
        totalCard.setStyle(UIHelper.cardStyle());
        totalCard.getChildren().addAll(
                value("Rs. " + (int) totalSalary, UIHelper.DANGER),
                UIHelper.createLabel("TOTAL SALARY EXPENSE")
        );
        content.getChildren().add(totalCard);
        return content;
    }

    private void drawBarChart(Canvas canvas, Map<String, Double> data) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        gc.setFill(Color.web(UIHelper.BG_CARD));
        gc.fillRoundRect(0, 0, w, h, 16, 16);

        if (data.isEmpty()) {
            gc.setFill(Color.web(UIHelper.TEXT_SECONDARY));
            gc.fillText("No revenue data available", w / 2 - 65, h / 2);
            return;
        }

        double maxVal = data.values().stream().mapToDouble(v -> v).max().orElse(1);
        double padL = 64;
        double padR = 24;
        double padT = 28;
        double padB = 42;
        double chartW = w - padL - padR;
        double chartH = h - padT - padB;

        int n = data.size();
        double barW = Math.min(58, (chartW / n) * 0.58);
        double gap = (chartW - barW * n) / (n + 1);

        int i = 0;
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double barH = (entry.getValue() / maxVal) * chartH;
            double x = padL + gap + i * (barW + gap);
            double y = padT + chartH - barH;

            gc.setFill(Color.web(UIHelper.PRIMARY));
            gc.fillRoundRect(x, y, barW, barH, 8, 8);
            gc.setFill(Color.web(UIHelper.TEXT_SECONDARY));
            gc.fillText(DateUtils.getMonthName(entry.getKey()), x, h - 14);
            gc.fillText("Rs. " + (int) (double) entry.getValue(), x, y - 7);
            i++;
        }
    }

    private VBox pageContent() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(22));
        return content;
    }

    private void stretch(HBox row) {
        for (var n : row.getChildren()) {
            HBox.setHgrow(n, Priority.ALWAYS);
            if (n instanceof VBox) {
                ((VBox) n).setMaxWidth(Double.MAX_VALUE);
            }
        }
    }

    private Label sectionTitle(String text) {
        return UIHelper.createSectionLabel(text);
    }

    private Label value(String text, String color) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 18px; -fx-font-weight: 900;");
        return label;
    }

    private Label pill(String text, String color) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px; -fx-font-weight: 900;"
                + "-fx-padding: 5 12; -fx-background-color: " + color + "22; -fx-background-radius: 999;");
        return label;
    }

    private String cardStyle() {
        return "-fx-background-color: " + UIHelper.BG_CARD + "; -fx-background-radius: 14;"
                + "-fx-border-color: " + UIHelper.BORDER + "; -fx-border-radius: 14;";
    }

    public BorderPane getRoot() {
        return root;
    }
}
