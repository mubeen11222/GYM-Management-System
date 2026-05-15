package com.oop.gymmanagementsystem.ui;

import com.oop.gymmanagementsystem.models.Exercise;
import com.oop.gymmanagementsystem.models.Member;
import com.oop.gymmanagementsystem.models.MembershipPlan;
import com.oop.gymmanagementsystem.models.NutritionPlan;
import com.oop.gymmanagementsystem.models.Payment;
import com.oop.gymmanagementsystem.models.PaymentStatus;
import com.oop.gymmanagementsystem.models.ProgressRecord;
import com.oop.gymmanagementsystem.models.Trainer;
import com.oop.gymmanagementsystem.models.WorkoutPlan;
import com.oop.gymmanagementsystem.services.PaymentService;
import com.oop.gymmanagementsystem.services.ProgressService;
import com.oop.gymmanagementsystem.storage.DataStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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

        Button backBtn = UIHelper.createSecondaryButton("Back to Members");
        backBtn.setOnAction(e -> onNavigate.accept("members"));
        root.setTop(UIHelper.createTopBar("Member: " + member.getName(), backBtn));

        root.setLeft(buildProfilePanel());

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().addAll(createInfoTab(), createWorkoutTab(), createNutritionTab(), createProgressTab());
        root.setCenter(tabPane);
    }

    private VBox buildProfilePanel() {
        VBox leftPanel = new VBox(16);
        leftPanel.setPadding(new Insets(24));
        leftPanel.setPrefWidth(290);
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setStyle("-fx-background-color: " + UIHelper.BG_SIDEBAR
                + "; -fx-border-color: transparent " + UIHelper.BORDER + " transparent transparent;");

        Label avatar = new Label(member.getName().substring(0, 1).toUpperCase());
        avatar.setMinSize(84, 84);
        avatar.setMaxSize(84, 84);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle("-fx-background-color: " + UIHelper.PRIMARY + "; -fx-background-radius: 42;"
                + "-fx-text-fill: white; -fx-font-size: 32px; -fx-font-weight: 900;");

        Label nameLabel = UIHelper.createSectionLabel(member.getName());
        Label idLabel = UIHelper.createLabel(member.getMemberId());
        String planColor = member.getPlan() == MembershipPlan.ADVANCED ? UIHelper.PRIMARY : UIHelper.ACCENT;
        Label planLabel = pill(member.getPlan().getDisplayName() + " Plan", planColor);

        VBox quickStats = new VBox(8);
        quickStats.setPadding(new Insets(14));
        quickStats.setStyle(UIHelper.cardStyle());
        quickStats.getChildren().addAll(
                UIHelper.createMetricRow("Age", member.getAge() + " years"),
                UIHelper.createMetricRow("Weight", member.getCurrentWeight() + " kg"),
                UIHelper.createMetricRow("Height", member.getHeight() + " cm"),
                UIHelper.createMetricRow("Goal", member.getFitnessGoal()),
                UIHelper.createMetricRow("Joined", member.getJoinDate())
        );

        Trainer trainer = member.hasTrainer() ? DataStore.getInstance().getTrainer(member.getTrainerId()) : null;
        Label trainerLabel = UIHelper.createLabel("Trainer: " + (trainer != null ? trainer.getName() : "None"));

        leftPanel.getChildren().addAll(avatar, nameLabel, idLabel, planLabel,
                new Region() {{ setPrefHeight(8); }}, quickStats, trainerLabel);
        return leftPanel;
    }

    private Tab createInfoTab() {
        Tab tab = new Tab("INFO");
        VBox content = pageContent();
        content.getChildren().add(sectionTitle("Personal Details"));

        GridPane grid = new GridPane();
        grid.setHgap(24);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));
        grid.setStyle(UIHelper.cardStyle());
        int r = 0;
        grid.add(UIHelper.createLabel("Name:"), 0, r); grid.add(UIHelper.createValueLabel(member.getName()), 1, r++);
        grid.add(UIHelper.createLabel("Gender:"), 0, r); grid.add(UIHelper.createValueLabel(member.getGender()), 1, r++);
        grid.add(UIHelper.createLabel("Phone:"), 0, r); grid.add(UIHelper.createValueLabel(member.getPhone()), 1, r++);
        grid.add(UIHelper.createLabel("Email:"), 0, r); grid.add(UIHelper.createValueLabel(member.getEmail()), 1, r++);
        grid.add(UIHelper.createLabel("Address:"), 0, r); grid.add(UIHelper.createValueLabel(member.getAddress()), 1, r);
        content.getChildren().add(grid);

        content.getChildren().add(sectionTitle("Payment Status"));
        VBox payBox = new VBox(8);
        payBox.setPadding(new Insets(14));
        payBox.setStyle(UIHelper.cardStyle());
        List<Payment> payments = new PaymentService().getMemberPayments(member.getMemberId());
        if (payments.isEmpty()) {
            payBox.getChildren().add(UIHelper.createLabel("No payment records."));
        } else {
            for (Payment p : payments) {
                String color = p.getStatus() == PaymentStatus.PAID ? UIHelper.SUCCESS : UIHelper.WARNING;
                payBox.getChildren().add(pill(p.getMonth() + " | Rs. " + (int) p.getAmount() + " | " + p.getStatus(), color));
            }
        }
        content.getChildren().add(payBox);

        tab.setContent(scroller(content));
        return tab;
    }

    private Tab createWorkoutTab() {
        Tab tab = new Tab("WORKOUT");
        VBox content = pageContent();
        WorkoutPlan plan = member.getWorkoutPlan();
        if (plan == null) {
            content.getChildren().add(UIHelper.createLabel("No workout plan assigned."));
        } else {
            content.getChildren().add(sectionTitle(plan.getPlanName() + " (" + plan.getDifficulty() + ")"));
            Label progress = UIHelper.createValueLabel("Completion: " + String.format("%.0f%%", plan.getCompletionPercentage()));
            progress.setStyle("-fx-text-fill: " + UIHelper.SUCCESS + "; -fx-font-size: 14px; -fx-font-weight: 900;");
            ProgressBar pb = new ProgressBar(plan.getCompletionPercentage() / 100.0);
            pb.setPrefWidth(520);
            content.getChildren().addAll(progress, pb);

            for (Exercise ex : plan.getExercises()) {
                HBox exCard = new HBox(12);
                exCard.setPadding(new Insets(14));
                exCard.setAlignment(Pos.CENTER_LEFT);
                exCard.setStyle("-fx-background-color: " + UIHelper.BG_CARD + "; -fx-background-radius: 14;"
                        + "-fx-border-color: " + UIHelper.BORDER + "; -fx-border-radius: 14;");

                CheckBox cb = new CheckBox();
                cb.setSelected(ex.isCompleted());
                cb.setOnAction(e -> {
                    ex.setCompleted(cb.isSelected());
                    DataStore.getInstance().saveAll();
                });

                VBox exInfo = new VBox(3);
                exInfo.getChildren().addAll(
                        UIHelper.createValueLabel(ex.getName()),
                        UIHelper.createLabel(ex.getSets() + " sets x " + ex.getReps()
                                + " reps | Rest: " + ex.getRestSeconds() + "s | " + ex.getMuscleGroup())
                );
                HBox.setHgrow(exInfo, Priority.ALWAYS);
                exCard.getChildren().addAll(cb, exInfo);
                content.getChildren().add(exCard);
            }
        }
        tab.setContent(scroller(content));
        return tab;
    }

    private Tab createNutritionTab() {
        Tab tab = new Tab("NUTRITION");
        VBox content = pageContent();
        NutritionPlan plan = member.getNutritionPlan();
        if (plan == null) {
            content.getChildren().add(UIHelper.createLabel("No nutrition plan assigned."));
        } else {
            content.getChildren().add(sectionTitle("AI-Generated Diet Plan (" + plan.getGoal() + ")"));
            HBox macros = new HBox(12);
            macros.getChildren().addAll(
                    UIHelper.createInfoCard("Calories", plan.getTargetCalories() + " kcal", UIHelper.WARNING),
                    UIHelper.createInfoCard("Protein", (int) plan.getProteinGrams() + "g", UIHelper.DANGER),
                    UIHelper.createInfoCard("Carbs", (int) plan.getCarbsGrams() + "g", UIHelper.ACCENT),
                    UIHelper.createInfoCard("Fat", (int) plan.getFatGrams() + "g", UIHelper.SUCCESS)
            );
            for (var node : macros.getChildren()) {
                HBox.setHgrow(node, Priority.ALWAYS);
                if (node instanceof VBox) {
                    ((VBox) node).setMaxWidth(Double.MAX_VALUE);
                }
            }
            content.getChildren().add(macros);

            content.getChildren().add(sectionTitle("Meal Plan"));
            VBox mealsBox = new VBox(8);
            mealsBox.setPadding(new Insets(14));
            mealsBox.setStyle(UIHelper.cardStyle());
            for (String meal : plan.getMeals()) {
                mealsBox.getChildren().add(UIHelper.createLabel(meal));
            }
            content.getChildren().add(mealsBox);
        }
        tab.setContent(scroller(content));
        return tab;
    }

    private Tab createProgressTab() {
        Tab tab = new Tab("PROGRESS");
        VBox content = pageContent();
        List<ProgressRecord> history = member.getProgressHistory();
        content.getChildren().add(sectionTitle("Weight Progress"));

        if (history.isEmpty()) {
            content.getChildren().add(UIHelper.createLabel("No progress records yet."));
        } else {
            Canvas canvas = new Canvas(620, 230);
            drawProgressChart(canvas, history);
            content.getChildren().add(canvas);
            content.getChildren().add(sectionTitle("History"));
            for (ProgressRecord rec : history) {
                HBox row = new HBox(16);
                row.setPadding(new Insets(10, 14, 10, 14));
                row.setStyle("-fx-background-color: " + UIHelper.BG_CARD + "; -fx-background-radius: 12;"
                        + "-fx-border-color: " + UIHelper.BORDER + "; -fx-border-radius: 12;");
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

        Button addBtn = UIHelper.createPrimaryButton("+ Add Progress Record");
        addBtn.setOnAction(e -> showAddProgressDialog());
        content.getChildren().add(addBtn);
        tab.setContent(scroller(content));
        return tab;
    }

    private void drawProgressChart(Canvas canvas, List<ProgressRecord> records) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        gc.setFill(Color.web(UIHelper.BG_CARD));
        gc.fillRoundRect(0, 0, w, h, 16, 16);
        if (records.size() < 2) {
            return;
        }

        double minW = records.stream().mapToDouble(ProgressRecord::getWeight).min().orElse(0) - 5;
        double maxW = records.stream().mapToDouble(ProgressRecord::getWeight).max().orElse(100) + 5;
        double padL = 54;
        double padR = 24;
        double padT = 24;
        double padB = 34;
        double chartW = w - padL - padR;
        double chartH = h - padT - padB;

        gc.setStroke(Color.web(UIHelper.BORDER));
        gc.setLineWidth(0.5);
        for (int i = 0; i <= 4; i++) {
            double y = padT + (chartH * i / 4);
            gc.strokeLine(padL, y, w - padR, y);
            double val = maxW - (maxW - minW) * i / 4;
            gc.setFill(Color.web(UIHelper.TEXT_SECONDARY));
            gc.fillText(String.format("%.0f", val), 12, y + 4);
        }

        gc.setStroke(Color.web(UIHelper.PRIMARY));
        gc.setLineWidth(3);
        for (int i = 0; i < records.size() - 1; i++) {
            double x1 = padL + (chartW * i / (records.size() - 1));
            double y1 = padT + chartH * (1 - (records.get(i).getWeight() - minW) / (maxW - minW));
            double x2 = padL + (chartW * (i + 1) / (records.size() - 1));
            double y2 = padT + chartH * (1 - (records.get(i + 1).getWeight() - minW) / (maxW - minW));
            gc.strokeLine(x1, y1, x2, y2);
        }

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
        dp.setStyle("-fx-background-color: " + UIHelper.BG_CARD + "; -fx-border-color: " + UIHelper.BORDER + ";");
        dp.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
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
                    if (isBlank(weightF, bfF, workoutsF)) {
                        UIHelper.showAlert("Missing Information", "Please enter weight, body fat and workout count.", Alert.AlertType.WARNING);
                        return null;
                    }
                    new ProgressService().addProgressRecord(member.getMemberId(),
                            Double.parseDouble(weightF.getText().trim()),
                            Double.parseDouble(bfF.getText().trim()),
                            Integer.parseInt(workoutsF.getText().trim()),
                            notesF.getText().trim());
                } catch (Exception ex) {
                    UIHelper.showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    private VBox pageContent() {
        VBox content = new VBox(14);
        content.setPadding(new Insets(22));
        content.setStyle("-fx-background-color: " + UIHelper.BG_DARK + ";");
        return content;
    }

    private ScrollPane scroller(VBox content) {
        ScrollPane sp = new ScrollPane(content);
        UIHelper.applyDarkScrollPane(sp);
        return sp;
    }

    private Label sectionTitle(String text) {
        return UIHelper.createSectionLabel(text);
    }

    private Label pill(String text, String color) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px; -fx-font-weight: 900;"
                + "-fx-padding: 5 12; -fx-background-color: " + color + "22; -fx-background-radius: 999;");
        return label;
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
