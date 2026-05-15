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
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

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
        root.setStyle("-fx-background-color: #0D1016;");

        ScrollPane scrollPane = new ScrollPane(createDashboardContent());
        UIHelper.applyDarkScrollPane(scrollPane);
        scrollPane.setStyle("-fx-background: #0D1016; -fx-background-color: #0D1016;");
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
        double capacityRate = capacity == 0 ? 0 : assigned / (double) capacity;

        VBox page = new VBox(22);
        page.setPadding(new Insets(30));
        page.setStyle("-fx-background-color: #0D1016;");

        HBox header = new HBox(18);
        header.setAlignment(Pos.CENTER_LEFT);
        VBox headerText = new VBox(7);
        Label over = overline("Operations Dashboard");
        Label title = new Label("Command center");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-family: " + UIHelper.FONT + "; -fx-font-weight: 600;");
        Label subtitle = new Label("Monitor members, trainer capacity, collections, pending fees and salary impact from one place.");
        subtitle.setStyle("-fx-text-fill: " + UIHelper.TEXT_SECONDARY + "; -fx-font-size: 14px; -fx-font-family: " + UIHelper.FONT + "; -fx-font-weight: 600;");
        headerText.getChildren().addAll(over, title, subtitle);
        HBox.setHgrow(headerText, Priority.ALWAYS);

        VBox live = new VBox(5);
        live.setAlignment(Pos.CENTER_RIGHT);
        live.setPadding(new Insets(14, 18, 14, 18));
        live.setStyle(panelStyle("#171C26"));
        Label liveValue = new Label(profit >= 0 ? "Healthy" : "Needs attention");
        liveValue.setStyle("-fx-text-fill: " + (profit >= 0 ? UIHelper.SUCCESS : UIHelper.DANGER)
                + "; -fx-font-size: 19px; -fx-font-family: " + UIHelper.FONT + "; -fx-font-weight: 600;");
        Label liveCaption = new Label("Financial position");
        liveCaption.setStyle("-fx-text-fill: " + UIHelper.TEXT_SECONDARY + "; -fx-font-size: 12px; -fx-font-family: " + UIHelper.FONT + "; -fx-font-weight: 600;");
        live.getChildren().addAll(liveValue, liveCaption);
        header.getChildren().addAll(headerText, live);

        HBox heroRow = new HBox(18);
        VBox commandCard = new VBox(18);
        commandCard.setPadding(new Insets(24));
        commandCard.setMinHeight(230);
        commandCard.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #202635, #171B24);" +
                "-fx-background-radius: 24;" +
                "-fx-border-color: #30384A;" +
                "-fx-border-radius: 24;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.30), 30, 0.22, 0, 12);"
        );
        HBox.setHgrow(commandCard, Priority.ALWAYS);
        Label commandTitle = new Label("Revenue and operations overview");
        commandTitle.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-family: " + UIHelper.FONT + "; -fx-font-weight: 600;");
        Label commandCopy = new Label("Your gym is currently running " + memberCount + " active member profiles across "
                + trainerCount + " trainers, with " + assigned + " assigned coaching slots.");
        commandCopy.setWrapText(true);
        commandCopy.setStyle("-fx-text-fill: " + UIHelper.TEXT_SECONDARY + "; -fx-font-size: 14px; -fx-font-family: " + UIHelper.FONT + "; -fx-font-weight: 600;");
        HBox miniStats = new HBox(12);
        miniStats.getChildren().addAll(miniStat("Revenue", "Rs. " + (int) revenue, UIHelper.SUCCESS),
                miniStat("Expected", "Rs. " + (int) expected, UIHelper.ACCENT),
                miniStat("Pending", "Rs. " + (int) pending, UIHelper.WARNING));
        stretch(miniStats);
        commandCard.getChildren().addAll(commandTitle, commandCopy, miniStats);

        VBox profitCard = new VBox(14);
        profitCard.setPadding(new Insets(24));
        profitCard.setPrefWidth(330);
        profitCard.setStyle(panelStyle("#1A1F2A"));
        Label profitLabel = overline("Net Profit");
        Label profitValue = new Label("Rs. " + (int) profit);
        profitValue.setStyle("-fx-text-fill: " + (profit >= 0 ? UIHelper.SUCCESS : UIHelper.DANGER)
                + "; -fx-font-size: 36px; -fx-font-family: " + UIHelper.FONT + "; -fx-font-weight: 600;");
        Canvas ring = new Canvas(170, 126);
        drawProfitGauge(ring, revenue, salaries);
        Label expense = new Label("Salary Expense: Rs. " + (int) salaries);
        expense.setStyle("-fx-text-fill: " + UIHelper.TEXT_SECONDARY + "; -fx-font-size: 13px; -fx-font-family: " + UIHelper.FONT + "; -fx-font-weight: 600;");
        profitCard.getChildren().addAll(profitLabel, profitValue, ring, expense);
        heroRow.getChildren().addAll(commandCard, profitCard);

        GridPane metrics = new GridPane();
        metrics.setHgap(16);
        metrics.setVgap(16);
        metrics.add(metricCard("Members", String.valueOf(memberCount), "Advanced " + advancedCount + " / Basic " + basicCount, UIHelper.SUCCESS), 0, 0);
        metrics.add(metricCard("Trainers", String.valueOf(trainerCount), assigned + "/" + capacity + " slots used", UIHelper.ACCENT), 1, 0);
        metrics.add(metricCard("Collections", "Rs. " + (int) revenue, paidCount + " paid payments", UIHelper.SUCCESS), 2, 0);
        metrics.add(metricCard("Outstanding", "Rs. " + (int) pending, unpaidCount + " pending payments", UIHelper.WARNING), 3, 0);
        for (int i = 0; i < 4; i++) {
            metrics.getColumnConstraints().add(new javafx.scene.layout.ColumnConstraints(220, 260, Double.MAX_VALUE, Priority.ALWAYS, null, true));
        }

        HBox lower = new HBox(18);
        VBox memberMix = insightPanel("Membership Mix");
        memberMix.getChildren().addAll(
                barRow("Advanced", advancedCount, Math.max(memberCount, 1), UIHelper.PRIMARY),
                barRow("Basic", basicCount, Math.max(memberCount, 1), UIHelper.ACCENT)
        );

        VBox trainerCapacity = insightPanel("Trainer Capacity");
        trainerCapacity.getChildren().addAll(
                capacityRow("Assigned Slots", assigned + "/" + capacity, capacityRate),
                trainerLineItems(trainers, ts)
        );

        VBox cashFlow = insightPanel("Cashflow Health");
        double collectionRate = expected == 0 ? 0 : revenue / expected;
        cashFlow.getChildren().addAll(
                capacityRow("Collection Rate", String.format("%.0f%%", collectionRate * 100), collectionRate),
                UIHelper.createMetricRow("Paid Records", String.valueOf(paidCount)),
                UIHelper.createMetricRow("Pending Records", String.valueOf(unpaidCount)),
                UIHelper.createMetricRow("Expected Revenue", "Rs. " + (int) expected)
        );

        lower.getChildren().addAll(memberMix, trainerCapacity, cashFlow);
        stretch(lower);

        page.getChildren().addAll(header, heroRow, metrics, lower);
        return page;
    }

    private VBox metricCard(String title, String value, String caption, String color) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(18));
        card.setMinHeight(120);
        card.setStyle(panelStyle("#171C26"));
        Label t = overline(title);
        Label v = new Label(value);
        v.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 28px; -fx-font-family: " + UIHelper.FONT + "; -fx-font-weight: 600;");
        Label c = new Label(caption);
        c.setStyle("-fx-text-fill: " + UIHelper.TEXT_SECONDARY + "; -fx-font-size: 12px; -fx-font-family: " + UIHelper.FONT + "; -fx-font-weight: 600;");
        card.getChildren().addAll(t, v, c);
        return card;
    }

    private VBox miniStat(String title, String value, String color) {
        VBox box = new VBox(4);
        box.setPadding(new Insets(14));
        box.setStyle("-fx-background-color: rgba(255,255,255,0.045); -fx-background-radius: 16; -fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 16;");
        Label label = overline(title);
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 20px; -fx-font-family: " + UIHelper.FONT + "; -fx-font-weight: 600;");
        box.getChildren().addAll(label, val);
        return box;
    }

    private VBox insightPanel(String title) {
        VBox panel = new VBox(12);
        panel.setPadding(new Insets(18));
        panel.setMinHeight(260);
        panel.setStyle(panelStyle("#171C26"));
        panel.getChildren().add(overline(title));
        return panel;
    }

    private VBox trainerLineItems(List<Trainer> trainers, TrainerService service) {
        VBox list = new VBox(8);
        for (Trainer trainer : trainers) {
            list.getChildren().add(UIHelper.createMetricRow(
                    trainer.getName(),
                    trainer.getAssignedMemberIds().size() + "/" + Trainer.MAX_MEMBERS + " | Rs. " + (int) service.calculateSalary(trainer.getTrainerId())
            ));
        }
        return list;
    }

    private VBox barRow(String label, long value, int total, String color) {
        VBox box = new VBox(7);
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        Label name = smallText(label);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label val = smallText(String.valueOf(value));
        row.getChildren().addAll(name, spacer, val);
        ProgressBar bar = new ProgressBar(value / (double) total);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setStyle("-fx-accent: " + color + ";");
        box.getChildren().addAll(row, bar);
        return box;
    }

    private VBox capacityRow(String label, String value, double rate) {
        VBox box = new VBox(7);
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        Label name = smallText(label);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label val = smallText(value);
        row.getChildren().addAll(name, spacer, val);
        ProgressBar bar = new ProgressBar(Math.max(0, Math.min(rate, 1)));
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setStyle("-fx-accent: " + (rate >= 0.8 ? UIHelper.WARNING : UIHelper.SUCCESS) + ";");
        box.getChildren().addAll(row, bar);
        return box;
    }

    private Label overline(String text) {
        Label label = new Label(text.toUpperCase());
        label.setStyle("-fx-text-fill: " + UIHelper.TEXT_SECONDARY + "; -fx-font-size: 11px; -fx-font-family: " + UIHelper.FONT + "; -fx-font-weight: 600;");
        return label;
    }

    private Label smallText(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + UIHelper.TEXT_SECONDARY + "; -fx-font-size: 13px; -fx-font-family: " + UIHelper.FONT + "; -fx-font-weight: 600;");
        return label;
    }

    private String panelStyle(String color) {
        return "-fx-background-color: " + color + ";"
                + "-fx-background-radius: 20;"
                + "-fx-border-color: #273041;"
                + "-fx-border-radius: 20;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.26), 28, 0.20, 0, 10);";
    }

    private void stretch(HBox row) {
        for (var node : row.getChildren()) {
            HBox.setHgrow(node, Priority.ALWAYS);
            if (node instanceof VBox) {
                ((VBox) node).setMaxWidth(Double.MAX_VALUE);
            }
        }
    }

    private void drawProfitGauge(Canvas canvas, double revenue, double salaries) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        double total = Math.max(revenue + salaries, 1);
        double revenueAngle = (revenue / total) * 270;

        gc.setStroke(Color.web("#293244"));
        gc.setLineWidth(18);
        gc.strokeArc(22, 8, w - 44, h + 34, 135, 270, javafx.scene.shape.ArcType.OPEN);

        gc.setStroke(Color.web(UIHelper.SUCCESS));
        gc.strokeArc(22, 8, w - 44, h + 34, 135, revenueAngle, javafx.scene.shape.ArcType.OPEN);

        gc.setFill(Color.web(UIHelper.TEXT_PRIMARY));
        gc.fillText("Revenue vs salary", 38, 88);
        gc.setFill(Color.web(UIHelper.TEXT_SECONDARY));
        gc.fillText("Rs. " + (int) revenue + " / Rs. " + (int) salaries, 38, 108);
    }

    public BorderPane getRoot() {
        return root;
    }
}
