package com.oop.gymmanagementsystem.ui;

import com.oop.gymmanagementsystem.models.User;
import com.oop.gymmanagementsystem.services.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class LoginScreen {
    private final AuthService authService;
    private final Runnable onLoginSuccess;
    private VBox root;

    public LoginScreen(AuthService authService, Runnable onLoginSuccess) {
        this.authService = authService;
        this.onLoginSuccess = onLoginSuccess;
        buildUI();
    }

    private void buildUI() {
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(32));
        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #111318, #161A23 52%, #20171C);"
        );

        HBox shell = new HBox(0);
        shell.setMaxWidth(920);
        shell.setMinHeight(520);
        shell.setEffect(UIHelper.softShadow());
        shell.setStyle(
            "-fx-background-color: " + UIHelper.BG_CARD + ";" +
            "-fx-background-radius: 24;" +
            "-fx-border-color: " + UIHelper.BORDER + ";" +
            "-fx-border-radius: 24;"
        );

        VBox brandPanel = new VBox(18);
        brandPanel.setPadding(new Insets(42));
        brandPanel.setPrefWidth(450);
        brandPanel.setAlignment(Pos.CENTER_LEFT);
        brandPanel.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #E63946, #7A1F2B);" +
            "-fx-background-radius: 24 0 0 24;"
        );

        Label badge = new Label("AI");
        badge.setAlignment(Pos.CENTER);
        badge.setMinSize(58, 58);
        badge.setStyle(
            "-fx-background-color: rgba(255,255,255,0.16);" +
            "-fx-background-radius: 18;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 24px;" +
            "-fx-font-weight: 900;"
        );

        Label heroTitle = new Label("Gym operations,\nmanaged beautifully.");
        heroTitle.setStyle("-fx-text-fill: white; -fx-font-size: 34px; -fx-font-weight: 900;");

        Label heroCopy = new Label("Members, trainers, payments, salaries, workout plans and nutrition records in one polished OOP system.");
        heroCopy.setWrapText(true);
        heroCopy.setStyle("-fx-text-fill: rgba(255,255,255,0.78); -fx-font-size: 14px; -fx-line-spacing: 4;");

        HBox featureRow = new HBox(10);
        featureRow.getChildren().addAll(createFeatureChip("File saved"), createFeatureChip("JavaFX GUI"), createFeatureChip("OOP ready"));
        brandPanel.getChildren().addAll(badge, heroTitle, heroCopy, featureRow);

        VBox loginCard = new VBox(20);
        loginCard.setAlignment(Pos.CENTER_LEFT);
        loginCard.setPadding(new Insets(42));
        HBox.setHgrow(loginCard, Priority.ALWAYS);

        Label title = UIHelper.createTitle("AI GYM");
        Label subtitle = UIHelper.createSubtitle("Sign in to your management dashboard");

        TextField usernameField = UIHelper.createTextField("Username");
        usernameField.setMaxWidth(Double.MAX_VALUE);

        PasswordField passwordField = UIHelper.createPasswordField("Password");
        passwordField.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Admin", "Staff", "Trainer");
        roleBox.setValue("Admin");
        roleBox.setMaxWidth(Double.MAX_VALUE);

        Label roleLabel = UIHelper.createLabel("Select Role");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: " + UIHelper.DANGER + "; -fx-font-size: 13px; -fx-font-weight: 700;");
        errorLabel.setVisible(false);

        Button loginBtn = UIHelper.createPrimaryButton("Sign In");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setPrefHeight(44);

        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please fill in all fields.");
                errorLabel.setVisible(true);
                return;
            }

            User user = authService.login(username, password);
            if (user != null) {
                if (!user.getRole().getDisplayName().equals(roleBox.getValue())) {
                    authService.logout();
                    errorLabel.setText("Selected role does not match this account.");
                    errorLabel.setVisible(true);
                    return;
                }
                onLoginSuccess.run();
            } else {
                errorLabel.setText("Invalid credentials. Try again.");
                errorLabel.setVisible(true);
                passwordField.clear();
            }
        });

        passwordField.setOnAction(e -> loginBtn.fire());

        Label hintLabel = new Label("Default: admin / admin123");
        hintLabel.setStyle("-fx-text-fill: #737D8C; -fx-font-size: 12px;");

        loginCard.getChildren().addAll(
            title, subtitle,
            new Region() {{ setPrefHeight(8); }},
            usernameField, passwordField,
            roleLabel, roleBox,
            errorLabel, loginBtn, hintLabel
        );

        shell.getChildren().addAll(brandPanel, loginCard);
        root.getChildren().add(shell);
    }

    private Label createFeatureChip(String text) {
        Label chip = new Label(text);
        chip.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: 800;" +
            "-fx-padding: 7 10;" +
            "-fx-background-color: rgba(255,255,255,0.14);" +
            "-fx-background-radius: 999;"
        );
        return chip;
    }

    public VBox getRoot() { return root; }
}
