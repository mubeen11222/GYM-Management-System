package com.oop.gymmanagementsystem.ui;

import com.oop.gymmanagementsystem.models.Role;
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
        root.setStyle("-fx-background-color: " + UIHelper.BG_DARK + ";");

        // Login card container
        VBox loginCard = new VBox(20);
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setPadding(new Insets(40));
        loginCard.setMaxWidth(400);
        loginCard.setStyle(
            "-fx-background-color: " + UIHelper.BG_CARD + ";" +
            "-fx-background-radius: 20;"
        );

        // Logo / Title
        Label logo = new Label("\uD83C\uDFCB");
        logo.setStyle("-fx-font-size: 48px;");

        Label title = UIHelper.createTitle("AI GYM");
        Label subtitle = UIHelper.createSubtitle("Management System");

        // Form fields
        TextField usernameField = UIHelper.createTextField("Username");
        usernameField.setMaxWidth(300);

        PasswordField passwordField = UIHelper.createPasswordField("Password");
        passwordField.setMaxWidth(300);

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Admin", "Staff", "Trainer");
        roleBox.setValue("Admin");
        roleBox.setMaxWidth(300);
        roleBox.setStyle(
            "-fx-background-color: #333333;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 6;"
        );

        Label roleLabel = UIHelper.createLabel("Select Role");

        // Error label
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #FF1744; -fx-font-size: 13px;");
        errorLabel.setVisible(false);

        // Login button
        Button loginBtn = UIHelper.createPrimaryButton("LOGIN");
        loginBtn.setMaxWidth(300);
        loginBtn.setPrefHeight(45);

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
                onLoginSuccess.run();
            } else {
                errorLabel.setText("Invalid credentials. Try again.");
                errorLabel.setVisible(true);
                passwordField.clear();
            }
        });

        // Enter key support
        passwordField.setOnAction(e -> loginBtn.fire());

        // Default credentials hint
        Label hintLabel = new Label("Default: admin / admin123");
        hintLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 11px;");

        loginCard.getChildren().addAll(
            logo, title, subtitle,
            new Region() {{ setPrefHeight(10); }},
            usernameField, passwordField,
            roleLabel, roleBox,
            errorLabel, loginBtn, hintLabel
        );

        root.getChildren().add(loginCard);
    }

    public VBox getRoot() { return root; }
}
