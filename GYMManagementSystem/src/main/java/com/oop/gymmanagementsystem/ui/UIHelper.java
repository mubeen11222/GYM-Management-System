package com.oop.gymmanagementsystem.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class UIHelper {
    public static final String BG_DARK = "#0A0A0A";
    public static final String BG_CARD = "#141414";
    public static final String BG_SIDEBAR = "#0E0E0E";
    public static final String BG_INPUT = "#1A1A1A";
    public static final String PRIMARY = "#E63946";
    public static final String PRIMARY_HOVER = "#FF4D5B";
    public static final String ACCENT = "#CC2936";
    public static final String TEXT_PRIMARY = "#F8FAFC";
    public static final String TEXT_SECONDARY = "#9A9A9A";
    public static final String BORDER = "#2A1015";
    public static final String SUCCESS = "#2DD4BF";
    public static final String WARNING = "#F59E0B";
    public static final String DANGER = "#FB7185";

    public static final String FONT = "'MiddleWeather SemiBold', 'MiddleWeather', 'Segoe UI Semibold', 'Segoe UI', Arial";

    public static Button createPrimaryButton(String text) {
        Button btn = new Button(text);
        btn.setMinHeight(40);
        btn.setStyle(primaryButtonStyle(PRIMARY, true));
        btn.setOnMouseEntered(e -> btn.setStyle(primaryButtonStyle(PRIMARY_HOVER, true)
                + "-fx-effect: dropshadow(gaussian, rgba(230,57,70,0.35), 18, 0.35, 0, 6);"));
        btn.setOnMouseExited(e -> btn.setStyle(primaryButtonStyle(PRIMARY, true)));
        return btn;
    }

    public static Button createSecondaryButton(String text) {
        Button btn = new Button(text);
        btn.setMinHeight(38);
        btn.setStyle(
                "-fx-background-color: " + BG_INPUT + ";" +
                "-fx-text-fill: " + TEXT_PRIMARY + ";" +
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: 600;" +
                "-fx-padding: 9 18;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 10;" +
                "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: #2B313D;" +
                "-fx-text-fill: " + TEXT_PRIMARY + ";" +
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: 600;" +
                "-fx-padding: 9 18;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #465063;" +
                "-fx-border-radius: 10;" +
                "-fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: " + BG_INPUT + ";" +
                "-fx-text-fill: " + TEXT_PRIMARY + ";" +
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: 600;" +
                "-fx-padding: 9 18;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 10;" +
                "-fx-cursor: hand;"
        ));
        return btn;
    }

    public static Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setMinHeight(40);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(sidebarButtonStyle("transparent", TEXT_SECONDARY, "transparent"));
        btn.setOnMouseEntered(e -> btn.setStyle(sidebarButtonStyle("#242A35", TEXT_PRIMARY, "#343C4C")));
        btn.setOnMouseExited(e -> btn.setStyle(sidebarButtonStyle("transparent", TEXT_SECONDARY, "transparent")));
        return btn;
    }

    public static TextField createTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setMinHeight(40);
        tf.setStyle(inputStyle());
        return tf;
    }

    public static PasswordField createPasswordField(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setMinHeight(40);
        pf.setStyle(inputStyle());
        return pf;
    }

    public static Label createTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-family: " + FONT
                + "; -fx-font-size: 30px; -fx-font-weight: 600;");
        return label;
    }

    public static Label createSubtitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-family: " + FONT
                + "; -fx-font-size: 14px;");
        return label;
    }

    public static Label createLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-family: " + FONT
                + "; -fx-font-size: 13px;");
        return label;
    }

    public static Label createValueLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-family: " + FONT
                + "; -fx-font-size: 14px; -fx-font-weight: 600;");
        return label;
    }

    public static Label createSectionLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-family: " + FONT
                + "; -fx-font-size: 18px; -fx-font-weight: 600;");
        return label;
    }

    public static VBox createCard(String title, String subtitle, String icon) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(24));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefSize(240, 170);
        card.setStyle(cardStyle());

        Label iconLabel = new Label(icon);
        iconLabel.setAlignment(Pos.CENTER);
        iconLabel.setMinSize(48, 48);
        iconLabel.setStyle(
                "-fx-background-color: rgba(230,57,70,0.14);" +
                "-fx-background-radius: 14;" +
                "-fx-text-fill: " + PRIMARY + ";" +
                "-fx-font-size: 24px;" +
                "-fx-font-weight: 600;"
        );

        Label titleLabel = createValueLabel(title);
        titleLabel.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-family: " + FONT
                + "; -fx-font-size: 18px; -fx-font-weight: 600;");

        Label subtitleLabel = createLabel(subtitle);
        subtitleLabel.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-family: " + FONT
                + "; -fx-font-size: 12px;");

        card.getChildren().addAll(iconLabel, titleLabel, subtitleLabel);
        card.setOnMouseEntered(e -> card.setStyle(cardStyle("#232A36", "#3C4658")));
        card.setOnMouseExited(e -> card.setStyle(cardStyle()));
        return card;
    }

    public static VBox createInfoCard(String label, String value, String color) {
        VBox card = new VBox(7);
        card.setPadding(new Insets(18));
        card.setMinHeight(104);
        card.setStyle(cardStyle());

        Label valLabel = new Label(value);
        valLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-family: " + FONT
                + "; -fx-font-size: 26px; -fx-font-weight: 600;");

        Label nameLabel = new Label(label.toUpperCase());
        nameLabel.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-family: " + FONT
                + "; -fx-font-size: 11px; -fx-font-weight: 600;");

        card.getChildren().addAll(valLabel, nameLabel);
        return card;
    }

    public static HBox createTopBar(String title, Button backButton) {
        HBox topBar = new HBox(14);
        topBar.setPadding(new Insets(18, 24, 18, 24));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: " + BG_SIDEBAR + "; -fx-border-color: transparent transparent "
                + BORDER + " transparent;");
        Label titleLabel = createSectionLabel(title);
        if (backButton != null) {
            topBar.getChildren().add(backButton);
        }
        topBar.getChildren().add(titleLabel);
        return topBar;
    }

    public static VBox createSidebar(String title, Node... nodes) {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(18));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: " + BG_SIDEBAR + "; -fx-border-color: transparent "
                + BORDER + " transparent transparent;");
        Label sideTitle = new Label(title.toUpperCase());
        sideTitle.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-family: " + FONT
                + "; -fx-font-size: 11px; -fx-font-weight: 600;");
        sidebar.getChildren().add(sideTitle);
        sidebar.getChildren().addAll(nodes);
        return sidebar;
    }

    public static HBox createMetricRow(String label, String value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        Label lbl = createLabel(label);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label val = createValueLabel(value);
        row.getChildren().addAll(lbl, spacer, val);
        return row;
    }

    public static String cardStyle() {
        return cardStyle(BG_CARD, BORDER);
    }

    public static String cardStyle(String background, String border) {
        return "-fx-background-color: " + background + ";"
                + "-fx-background-radius: 14;"
                + "-fx-border-color: " + border + ";"
                + "-fx-border-radius: 14;"
                + "-fx-effect: dropshadow(gaussian, rgba(230,57,70,0.12), 22, 0.18, 0, 8);"
                + "-fx-cursor: hand;";
    }

    public static void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        DialogPane dp = alert.getDialogPane();
        dp.setStyle("-fx-background-color: " + BG_CARD + "; -fx-border-color: " + BORDER + ";");
        Node content = dp.lookup(".content.label");
        if (content != null) {
            content.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-family: " + FONT + ";");
        }
        alert.showAndWait();
    }

    public static void applyDarkScrollPane(ScrollPane sp) {
        sp.setStyle("-fx-background: " + BG_DARK + "; -fx-background-color: " + BG_DARK + ";");
        sp.setFitToWidth(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    public static DropShadow softShadow() {
        DropShadow shadow = new DropShadow();
        shadow.setRadius(26);
        shadow.setOffsetY(8);
        shadow.setColor(Color.rgb(0, 0, 0, 0.28));
        return shadow;
    }

    private static String primaryButtonStyle(String color, boolean withBorder) {
        return "-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: 600;" +
                "-fx-padding: 10 22;" +
                "-fx-background-radius: 10;" +
                (withBorder ? "-fx-border-color: rgba(255,255,255,0.10);-fx-border-radius: 10;" : "") +
                "-fx-cursor: hand;";
    }

    private static String sidebarButtonStyle(String background, String text, String border) {
        return "-fx-background-color: " + background + ";" +
                "-fx-text-fill: " + text + ";" +
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: 600;" +
                "-fx-padding: 10 14;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: " + border + ";" +
                "-fx-border-radius: 10;" +
                "-fx-cursor: hand;";
    }

    private static String inputStyle() {
        return "-fx-background-color: " + BG_INPUT + ";" +
                "-fx-text-fill: " + TEXT_PRIMARY + ";" +
                "-fx-prompt-text-fill: #737D8C;" +
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 10 12;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 10;" +
                "-fx-highlight-fill: " + PRIMARY + ";";
    }
}
