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
import java.util.function.Consumer;

public class MemberDetailPanel {
    private final Member member;
    private final Consumer<String> onNavigate;
    private BorderPane root;

    public MemberDetailPanel(Member member, Consumer<String> onNavigate) {
        this.member = member;
        this.onNavigate = onNavigate;
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

        Button backBtn = UIHelper.createPrimaryButton("← Members");
        backBtn.setOnAction(e -> onNavigate.accept("members"));

        Label title = new Label("Member: " + member.getName());
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        topBar.getChildren().addAll(backBtn, title);
        root.setTop(topBar);

        // ── Left Panel - Quick Info ──
        VBox leftPanel = new VBox(16);
        leftPanel.setPadding(new Insets(24));
        leftPanel.setPrefWidth(260);
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setStyle("-fx-background-color: " + UIHelper.BG_SIDEBAR + ";");

        // Avatar
        Label avatar = new Label(member.getName().substring(0, 1).toUpperCase());
        avatar.setMinSize(80, 80);
        avatar.setMaxSize(80, 80);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle(
            "-fx-background-color: " + UIHelper.PRIMARY + ";" +
            "-fx-background-radius: 40;" +
            "-fx-text-fill: white; -fx-font-size: 32px; -fx-font-weight: bold;"
        );

        Label nameLabel = UIHelper.createValueLabel(member.getName());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label idLabel = UIHelper.createLabel(member.getMemberId());
        Label planLabel = new Label(member.getPlan().getDisplayName() + " Plan");
        String planColor = member.getPlan() == MembershipPlan.ADVANCED ? "#E91E63" : "#2196F3";
        planLabel.setStyle(
            "-fx-text-fill: " + planColor + "; -fx-font-weight: bold; -fx-font-size: 13px;" +
            "-fx-padding: 4 12; -fx-background-color: " + planColor + "22;" +
            "-fx-background-radius: 12;"
        );

        VBox quickStats = new VBox(6);
        quickStats.setPadding(new Insets(12));
        quickStats.setStyle("-fx-background-color: " + UIHelper.BG_CARD + "; -fx-background-radius: 10;");
        quickStats.getChildren().addAll(
            createQuickRow("Age", member.getAge() + " years"),
            createQuickRow("Weight", member.getCurrentWeight() + " kg"),
            createQuickRow("Height", member.getHeight() + " cm"),
            createQuickRow("Goal", member.getFitnessGoal()),
            createQuickRow("Joined", member.getJoinDate())
        );

        Trainer trainer = member.hasTrainer() ? DataStore.getInstance().getTrainer(member.getTrainerId()) : null;
        Label trainerLabel = UIHelper.createLabel("Trainer: " + (trainer != null ? trainer.getName() : "None"));

        leftPanel.getChildren().addAll(avatar, nameLabel, idLabel, planLabel,
            new Region() {{ setPrefHeight(8); }}, quickStats, trainerLabel);
        root.setLeft(leftPanel);

        // ── Right Panel - Tabs ──
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: " + UIHelper.BG_DARK + ";");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabPane.getTabs().addAll(
            createInfoTab(),
            createWorkoutTab(),
            createNutritionTab(),
            createProgressTab()
        );
        root.setCenter(tabPane);
    }

    private Tab createInfoTab() {
        Tab tab = new Tab("INFO");
        VBox content = new VBox(12);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: " + UIHelper.BG_DARK + ";");

        content.getChildren().add(sectionTitle("Personal Details"));
        GridPane grid = new GridPane();
        grid.setHgap(24); grid.setVgap(8);
        grid.setPadding(new Insets(12));
        grid.setStyle("-fx-background-color: " + UIHelper.BG_CARD + "; -fx-background-radius: 10;");

        int r = 0;
        grid.add(UIHelper.createLabel("Name:"), 0, r); grid.add(UIHelper.createValueLabel(member.getName()), 1, r++);
        grid.add(UIHelper.createLabel("Gender:"), 0, r); grid.add(UIHelper.createValueLabel(member.getGender()), 1, r++);
        grid.add(UIHelper.createLabel("Phone:"), 0, r); grid.add(UIHelper.createValueLabel(member.getPhone()), 1, r++);
        grid.add(UIHelper.createLabel("Email:"), 0, r); grid.add(UIHelper.createValueLabel(member.getEmail()), 1, r++);
        grid.add(UIHelper.createLabel("Address:"), 0, r); grid.add(UIHelper.createValueLabel(member.getAddress()), 1, r++);

        content.getChildren().add(grid);

        // Payment status
        content.getChildren().add(sectionTitle("Payment Status"));
        PaymentService ps = new PaymentService();
        List<Payment> payments = ps.getMemberPayments(member.getMemberId());
        VBox payBox = new VBox(6);
        payBox.setPadding(new Insets(12));
        payBox.setStyle("-fx-background-color: " + UIHelper.BG_CARD + "; -fx-background-radius: 10;");

        if (payments.isEmpty()) {
            payBox.getChildren().add(UIHelper.createLabel("No payment records."));
        } else {
            for (Payment p : payments) {
                String color = p.getStatus() == PaymentStatus.PAID ? UIHelper.SUCCESS : UIHelper.WARNING;
                Label pl = new Label(p.getMonth() + "  |  Rs. " + (int)p.getAmount() + "  |  " + p.getStatus());
                pl.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 13px;");
                payBox.getChildren().add(pl);
            }
        }
        content.getChildren().add(payBox);

        ScrollPane sp = new ScrollPane(content);
        UIHelper.applyDarkScrollPane(sp);
        tab.setContent(sp);
        return tab;
    }

    private Tab createWorkoutTab() {
        Tab tab = new Tab("WORKOUT");
        VBox content = new VBox(12);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: " + UIHelper.BG_DARK + ";");

        WorkoutPlan plan = member.getWorkoutPlan();
        if (plan == null) {
            content.getChildren().add(UIHelper.createLabel("No workout plan assigned."));
        } else {
            content.getChildren().add(sectionTitle(plan.getPlanName() + " (" + plan.getDifficulty() + ")"));

            Label progress = new Label("Completion: " + String.format("%.0f%%", plan.getCompletionPercentage()));
            progress.setStyle("-fx-text-fill: " + UIHelper.SUCCESS + "; -fx-font-size: 14px; -fx-font-weight: bold;");
            content.getChildren().add(progress);

            // Progress bar
            ProgressBar pb = new ProgressBar(plan.getCompletionPercentage() / 100.0);
            pb.setPrefWidth(400);
            pb.setStyle("-fx-accent: " + UIHelper.PRIMARY + ";");
            content.getChildren().add(pb);

            for (int i = 0; i < plan.getExercises().size(); i++) {
                Exercise ex = plan.getExercises().get(i);
                HBox exCard = new HBox(12);
                exCard.setPadding(new Insets(12));
                exCard.setAlignment(Pos.CENTER_LEFT);
                exCard.setStyle("-fx-background-color: " + UIHelper.BG_CARD + "; -fx-background-radius: 8;");

                CheckBox cb = new CheckBox();
                cb.setSelected(ex.isCompleted());
                int idx = i;
                cb.setOnAction(e -> {
                    ex.setCompleted(cb.isSelected());
                    DataStore.getInstance().saveAll();
                });

                VBox exInfo = new VBox(2);
                Label exName = UIHelper.createValueLabel(ex.getName());
                if (ex.isCompleted()) exName.setStyle("-fx-text-fill: " + UIHelper.SUCCESS + "; -fx-font-size: 14px;");
                Label exDetail = UIHelper.createLabel(
                    ex.getSets() + " sets × " + ex.getReps() + " reps  |  Rest: " + ex.getRestSeconds() + "s  |  " + ex.getMuscleGroup()
                );
                exInfo.getChildren().addAll(exName, exDetail);
                HBox.setHgrow(exInfo, Priority.ALWAYS);

                exCard.getChildren().addAll(cb, exInfo);
                content.getChildren().add(exCard);
            }
        }

        ScrollPane sp = new ScrollPane(content);
        UIHelper.applyDarkScrollPane(sp);
        tab.setContent(sp);
        return tab;
    }

    private Tab createNutritionTab() {
        Tab tab = new Tab("NUTRITION");
        VBox content = new VBox(12);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: " + UIHelper.BG_DARK + ";");

        NutritionPlan plan = member.getNutritionPlan();
        if (plan == null) {
            content.getChildren().add(UIHelper.createLabel("No nutrition plan assigned."));
        } else {
            content.getChildren().add(sectionTitle("AI-Generated Diet Plan (" + plan.getGoal() + ")"));

            // Macros cards
            HBox macros = new HBox(12);
            macros.getChildren().addAll(
                UIHelper.createInfoCard("Calories", plan.getTargetCalories() + " kcal", UIHelper.WARNING),
                UIHelper.createInfoCard("Protein", (int)plan.getProteinGrams() + "g", UIHelper.DANGER),
                UIHelper.createInfoCard("Carbs", (int)plan.getCarbsGrams() + "g", "#2196F3"),
                UIHelper.createInfoCard("Fat", (int)plan.getFatGrams() + "g", UIHelper.SUCCESS)
            );
            for (var node : macros.getChildren()) {
                HBox.setHgrow(node, Priority.ALWAYS);
                if (node instanceof VBox) ((VBox) node).setMaxWidth(Double.MAX_VALUE);
            }
            content.getChildren().add(macros);

            content.getChildren().add(sectionTitle("Meal Plan"));
            VBox mealsBox = new VBox(6);
            mealsBox.setPadding(new Insets(12));
            mealsBox.setStyle("-fx-background-color: " + UIHelper.BG_CARD + "; -fx-background-radius: 10;");
            for (String meal : plan.getMeals()) {
                Label ml = new Label("🍽  " + meal);
                ml.setStyle("-fx-text-fill: " + UIHelper.TEXT_SECONDARY + "; -fx-font-size: 13px;");
                ml.setWrapText(true);
                mealsBox.getChildren().add(ml);
            }
            content.getChildren().add(mealsBox);
        }

        ScrollPane sp = new ScrollPane(content);
        UIHelper.applyDarkScrollPane(sp);
        tab.setContent(sp);
        return tab;
    }

    private Tab createProgressTab() {
        Tab tab = new Tab("PROGRESS");
        VBox content = new VBox(12);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: " + UIHelper.BG_DARK + ";");

        List<ProgressRecord> history = member.getProgressHistory();
        content.getChildren().add(sectionTitle("Weight Progress"));

        if (history.isEmpty()) {
            content.getChildren().add(UIHelper.createLabel("No progress records yet."));
        } else {
            // Draw chart
            Canvas canvas = new Canvas(500, 200);
            drawProgressChart(canvas, history);
            content.getChildren().add(canvas);

            // History table
            content.getChildren().add(sectionTitle("History"));
            for (ProgressRecord rec : history) {
                HBox row = new HBox(16);
                row.setPadding(new Insets(8, 12, 8, 12));
                row.setStyle("-fx-background-color: " + UIHelper.BG_CARD + "; -fx-background-radius: 6;");
                row.getChildren().addAll(
                    UIHelper.createLabel(rec.getDate()),
                    UIHelper.createValueLabel(rec.getWeight() + " kg"),
                    UIHelper.createLabel("BF: " + rec.getBodyFatPercentage() + "%"),
                    UIHelper.createLabel("Workouts: " + rec.getWorkoutsCompleted()),
                    UIHelper.createLabel(rec.getNotes())
                );
                content.getChildren().add(row);
            }
        }

        // Add progress button
        Button addBtn = UIHelper.createPrimaryButton("+ Add Progress Record");
        addBtn.setOnAction(e -> showAddProgressDialog());
        content.getChildren().add(addBtn);

        ScrollPane sp = new ScrollPane(content);
        UIHelper.applyDarkScrollPane(sp);
        tab.setContent(sp);
        return tab;
    }

    private void drawProgressChart(Canvas canvas, List<ProgressRecord> records) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth(), h = canvas.getHeight();

        gc.setFill(Color.web(UIHelper.BG_CARD));
        gc.fillRoundRect(0, 0, w, h, 12, 12);

        if (records.size() < 2) return;

        double minW = records.stream().mapToDouble(ProgressRecord::getWeight).min().orElse(0) - 5;
        double maxW = records.stream().mapToDouble(ProgressRecord::getWeight).max().orElse(100) + 5;

        double padL = 50, padR = 20, padT = 20, padB = 30;
        double chartW = w - padL - padR;
        double chartH = h - padT - padB;

        // Grid lines
        gc.setStroke(Color.web("#444444"));
        gc.setLineWidth(0.5);
        for (int i = 0; i <= 4; i++) {
            double y = padT + (chartH * i / 4);
            gc.strokeLine(padL, y, w - padR, y);
            double val = maxW - (maxW - minW) * i / 4;
            gc.setFill(Color.web(UIHelper.TEXT_SECONDARY));
            gc.fillText(String.format("%.0f", val), 5, y + 4);
        }

        // Draw line
        gc.setStroke(Color.web(UIHelper.PRIMARY));
        gc.setLineWidth(2.5);
        for (int i = 0; i < records.size() - 1; i++) {
            double x1 = padL + (chartW * i / (records.size() - 1));
            double y1 = padT + chartH * (1 - (records.get(i).getWeight() - minW) / (maxW - minW));
            double x2 = padL + (chartW * (i + 1) / (records.size() - 1));
            double y2 = padT + chartH * (1 - (records.get(i + 1).getWeight() - minW) / (maxW - minW));
            gc.strokeLine(x1, y1, x2, y2);
        }

        // Draw points
        gc.setFill(Color.web(UIHelper.PRIMARY));
        for (int i = 0; i < records.size(); i++) {
            double x = padL + (chartW * i / (records.size() - 1));
            double y = padT + chartH * (1 - (records.get(i).getWeight() - minW) / (maxW - minW));
            gc.fillOval(x - 5, y - 5, 10, 10);
        }
    }

    private void showAddProgressDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add Progress Record");
        DialogPane dp = dialog.getDialogPane();
        dp.setStyle("-fx-background-color: " + UIHelper.BG_CARD + ";");
        dp.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10);
        grid.setPadding(new Insets(16));

        TextField weightF = UIHelper.createTextField("Weight (kg)");
        TextField bfF = UIHelper.createTextField("Body Fat %");
        TextField workoutsF = UIHelper.createTextField("Workouts Completed");
        TextField notesF = UIHelper.createTextField("Notes");

        grid.add(UIHelper.createLabel("Weight:"), 0, 0); grid.add(weightF, 1, 0);
        grid.add(UIHelper.createLabel("Body Fat %:"), 0, 1); grid.add(bfF, 1, 1);
        grid.add(UIHelper.createLabel("Workouts:"), 0, 2); grid.add(workoutsF, 1, 2);
        grid.add(UIHelper.createLabel("Notes:"), 0, 3); grid.add(notesF, 1, 3);
        dp.setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    ProgressService ps = new ProgressService();
                    ps.addProgressRecord(member.getMemberId(),
                        Double.parseDouble(weightF.getText()),
                        Double.parseDouble(bfF.getText()),
                        Integer.parseInt(workoutsF.getText()),
                        notesF.getText());
                } catch (Exception ex) {
                    UIHelper.showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    private HBox createQuickRow(String label, String value) {
        HBox row = new HBox();
        Label lbl = UIHelper.createLabel(label + ": ");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label val = UIHelper.createValueLabel(value);
        row.getChildren().addAll(lbl, spacer, val);
        return row;
    }

    private Label sectionTitle(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        l.setPadding(new Insets(8, 0, 4, 0));
        return l;
    }

    public BorderPane getRoot() { return root; }
}
