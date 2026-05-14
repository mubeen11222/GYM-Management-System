package com.oop.gymmanagementsystem.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class UIHelper {

    // ── Colors ──
    public static final String BG_DARK = "#1A1A1A";
    public static final String BG_CARD = "#2D2D2D";
    public static final String BG_SIDEBAR = "#222222";
    public static final String PRIMARY = "#B30000";
    public static final String PRIMARY_HOVER = "#CC0000";
    public static final String TEXT_PRIMARY = "#FFFFFF";
    public static final String TEXT_SECONDARY = "#CCCCCC";
    public static final String SUCCESS = "#00C853";
    public static final String WARNING = "#FFA000";
    public static final String DANGER = "#FF1744";

    public static Button createPrimaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: " + PRIMARY + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 24;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: " + PRIMARY_HOVER + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 24;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, " + PRIMARY + ", 15, 0.5, 0, 0);"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: " + PRIMARY + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 24;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        return btn;
    }

    public static Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + TEXT_SECONDARY + ";" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 12 16;" +
            "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: " + PRIMARY + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 12 16;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + TEXT_SECONDARY + ";" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 12 16;" +
            "-fx-cursor: hand;"
        ));
        return btn;
    }

    public static TextField createTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(
            "-fx-background-color: #333333;" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #888888;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 10;" +
            "-fx-background-radius: 6;" +
            "-fx-border-color: #444444;" +
            "-fx-border-radius: 6;"
        );
        return tf;
    }

    public static PasswordField createPasswordField(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setStyle(
            "-fx-background-color: #333333;" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #888888;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 10;" +
            "-fx-background-radius: 6;" +
            "-fx-border-color: #444444;" +
            "-fx-border-radius: 6;"
        );
        return pf;
    }

    public static Label createTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");
        return label;
    }

    public static Label createSubtitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-size: 16px;");
        return label;
    }

    public static Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-size: 14px;");
        return label;
    }

    public static Label createValueLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        return label;
    }

    public static VBox createCard(String title, String subtitle, String emoji) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(30));
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(220, 180);
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 16;" +
            "-fx-cursor: hand;"
        );

        Label emojiLabel = new Label(emoji);
        emojiLabel.setStyle("-fx-font-size: 36px;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-size: 12px;");

        card.getChildren().addAll(emojiLabel, titleLabel, subtitleLabel);

        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: " + PRIMARY + ";" +
            "-fx-background-radius: 16;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, " + PRIMARY + ", 20, 0.4, 0, 0);"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 16;" +
            "-fx-cursor: hand;"
        ));

        return card;
    }

    public static VBox createInfoCard(String label, String value, String color) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(16));
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 10;"
        );

        Label valLabel = new Label(value);
        valLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 24px; -fx-font-weight: bold;");

        Label nameLabel = new Label(label);
        nameLabel.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-size: 12px;");

        card.getChildren().addAll(valLabel, nameLabel);
        return card;
    }

    public static void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        DialogPane dp = alert.getDialogPane();
        dp.setStyle("-fx-background-color: " + BG_CARD + ";");
        dp.lookup(".content.label").setStyle("-fx-text-fill: white;");
        alert.showAndWait();
    }

    public static void applyDarkScrollPane(ScrollPane sp) {
        sp.setStyle("-fx-background: " + BG_DARK + "; -fx-background-color: " + BG_DARK + ";");
        sp.setFitToWidth(true);
    }
}
