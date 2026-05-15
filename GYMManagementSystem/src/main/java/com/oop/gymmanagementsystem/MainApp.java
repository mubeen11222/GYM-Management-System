package com.oop.gymmanagementsystem;

import com.oop.gymmanagementsystem.models.*;
import com.oop.gymmanagementsystem.services.*;
import com.oop.gymmanagementsystem.storage.DataStore;
import com.oop.gymmanagementsystem.ui.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class MainApp extends Application {

    private StackPane rootContainer;
    private BorderPane appShell;
    private Scene scene;

    // Services
    private AuthService authService;
    private PaymentService paymentService;
    private TrainerService trainerService;
    private WorkoutService workoutService;
    private NutritionService nutritionService;
    private ProgressService progressService;
    private MemberService memberService;

    @Override
    public void start(Stage primaryStage) {
        // Initialize data store and load saved data
        DataStore dataStore = DataStore.getInstance();
        dataStore.loadAll();

        // Initialize services
        authService = new AuthService();
        paymentService = new PaymentService();
        trainerService = new TrainerService();
        workoutService = new WorkoutService();
        nutritionService = new NutritionService();
        progressService = new ProgressService();
        memberService = new MemberService(paymentService, workoutService, nutritionService);

        // Initialize default users and sample data
        authService.initializeDefaultUsers();
        SampleDataService sampleService = new SampleDataService();
        sampleService.initializeSampleData();

        // Root container
        rootContainer = new StackPane();
        rootContainer.setStyle("-fx-background-color: #0A0A0A;");

        scene = new Scene(rootContainer, 1100, 700);
        var stylesheet = getClass().getResource("/styles.css");
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }

        // Apply dark theme to dialogs
        scene.getRoot().setStyle("-fx-background-color: " + UIHelper.BG_DARK + ";");

        // Start with login screen
        showLogin();

        primaryStage.setTitle("MMS GYM Management System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1040);
        primaryStage.setMinHeight(680);
        primaryStage.show();
    }

    private void showLogin() {
        appShell = null;
        LoginScreen loginScreen = new LoginScreen(authService, this::showDashboard);
        setContent(loginScreen.getRoot());
    }

    private void showDashboard() {
        ensureShell("dashboard");
        DashboardScreen dashboard = new DashboardScreen();
        setShellContent(dashboard.getRoot(), "dashboard");
    }

    private void navigate(String target) {
        switch (target) {
            case "dashboard":
                showDashboard();
                break;
            case "members":
                showMembers();
                break;
            case "trainers":
                showTrainers();
                break;
            case "payments":
                showPayments();
                break;
        }
    }

    private void showMembers() {
        ensureShell("members");
        MembersPanel panel = new MembersPanel(memberService, trainerService,
                this::navigate, this::showMemberDetail);
        setShellContent(panel.getRoot(), "members");
    }

    private void showMemberDetail(Member member) {
        // Refresh member data from store
        Member fresh = DataStore.getInstance().getMember(member.getMemberId());
        if (fresh == null) fresh = member;

        MemberDetailPanel detail = new MemberDetailPanel(fresh, this::navigate);
        ensureShell("members");
        setShellContent(detail.getRoot(), "members");
    }

    private void showTrainers() {
        ensureShell("trainers");
        TrainersPanel panel = new TrainersPanel(trainerService,
                this::navigate, this::showTrainerDetail);
        setShellContent(panel.getRoot(), "trainers");
    }

    private void showTrainerDetail(Trainer trainer) {
        Trainer fresh = DataStore.getInstance().getTrainer(trainer.getTrainerId());
        if (fresh == null) fresh = trainer;

        TrainerDetailPanel detail = new TrainerDetailPanel(fresh, trainerService, this::navigate);
        ensureShell("trainers");
        setShellContent(detail.getRoot(), "trainers");
    }

    private void showPayments() {
        ensureShell("payments");
        PaymentsPanel panel = new PaymentsPanel(paymentService, trainerService, this::navigate);
        setShellContent(panel.getRoot(), "payments");
    }

    private void setContent(javafx.scene.layout.Pane content) {
        rootContainer.getChildren().clear();
        rootContainer.getChildren().add(content);
    }

    private void ensureShell(String activeSection) {
        if (appShell == null) {
            appShell = new BorderPane();
            appShell.setStyle("-fx-background-color: #0A0A0A;");
            rootContainer.getChildren().clear();
            rootContainer.getChildren().add(appShell);
        }
        appShell.setLeft(createPersistentSidePanel(activeSection));
    }

    private void setShellContent(javafx.scene.layout.Pane content, String activeSection) {
        ensureShell(activeSection);
        appShell.setCenter(content);
    }

    private VBox createPersistentSidePanel(String activeSection) {
        VBox sidebar = new VBox(18);
        sidebar.setPrefWidth(320);
        sidebar.setMinWidth(320);
        sidebar.setMaxWidth(320);
        sidebar.setPadding(new Insets(26, 20, 22, 20));
        sidebar.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #0E0E0E, #080808);" +
                "-fx-border-color: transparent #1A0A0D transparent transparent;"
        );

        HBox brand = new HBox(10);
        brand.setAlignment(Pos.CENTER_LEFT);

        ImageView logoView = new ImageView();
        try {
            Image logoImg = new Image(getClass().getResourceAsStream("/mms_gym_logo.png"));
            logoView.setImage(logoImg);
            logoView.setFitWidth(130);
            logoView.setFitHeight(130);
            logoView.setPreserveRatio(true);
            logoView.setSmooth(true);
            Circle clip = new Circle(65, 65, 65);
            logoView.setClip(clip);
        } catch (Exception ex) {
            System.err.println("Could not load logo: " + ex.getMessage());
        }

        VBox brandText = new VBox(0);
        Label name = new Label("MMS GYM");
        name.setMinWidth(Region.USE_PREF_SIZE);
        name.setStyle("-fx-text-fill: #E63946; -fx-font-size: 26px; -fx-font-family: 'Anton', 'Impact', sans-serif; -fx-font-weight: 900; -fx-letter-spacing: -1;");
        Label sub = new Label("Management Suite");
        sub.setStyle("-fx-text-fill: #E63946; -fx-font-size: 11px; -fx-font-family: " + UIHelper.FONT + "; -fx-font-weight: 600;");
        brandText.getChildren().addAll(name, sub);
        brand.getChildren().addAll(logoView, brandText);

        VBox nav = new VBox(8);
        nav.getChildren().addAll(
                sideOverline("Navigation"),
                navButton("Dashboard", "D", "dashboard".equals(activeSection), () -> navigate("dashboard")),
                navButton("Members", "M", "members".equals(activeSection), () -> navigate("members")),
                navButton("Trainers", "T", "trainers".equals(activeSection), () -> navigate("trainers")),
                navButton("Payments", "P", "payments".equals(activeSection), () -> navigate("payments"))
        );

        DataStore ds = DataStore.getInstance();
        VBox snapshot = new VBox(10);
        snapshot.setPadding(new Insets(16));
        snapshot.setStyle(shellPanelStyle("#111111"));
        snapshot.getChildren().addAll(
                sideOverline("System Snapshot"),
                UIHelper.createMetricRow("Members", String.valueOf(ds.getAllMembers().size())),
                UIHelper.createMetricRow("Trainers", String.valueOf(ds.getAllTrainers().size())),
                UIHelper.createMetricRow("Pending", "Rs. " + (int) paymentService.getTotalPendingAmount())
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        User user = authService.getCurrentUser();
        VBox account = new VBox(10);
        account.setPadding(new Insets(16));
        account.setStyle(shellPanelStyle("#111111"));
        Label accountLabel = sideOverline("Signed In");
        Label userName = new Label(user != null ? user.getFullName() : "User");
        userName.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-family: " + UIHelper.FONT + "; -fx-font-weight: 600;");
        Label role = new Label(user != null ? user.getRole().getDisplayName() : "Account");
        role.setStyle("-fx-text-fill: " + UIHelper.TEXT_SECONDARY + "; -fx-font-size: 12px; -fx-font-family: " + UIHelper.FONT + "; -fx-font-weight: 600;");
        Button logout = UIHelper.createSecondaryButton("Logout");
        logout.setMaxWidth(Double.MAX_VALUE);
        logout.setOnAction(e -> {
            authService.logout();
            showLogin();
        });
        account.getChildren().addAll(accountLabel, userName, role, logout);

        sidebar.getChildren().addAll(brand, nav, snapshot, spacer, account);
        return sidebar;
    }

    private Button navButton(String text, String icon, boolean active, Runnable action) {
        Button button = new Button(icon + "   " + text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMinHeight(46);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setStyle(navStyle(active, false));
        button.setOnAction(e -> action.run());
        button.setOnMouseEntered(e -> button.setStyle(navStyle(active, true)));
        button.setOnMouseExited(e -> button.setStyle(navStyle(active, false)));
        return button;
    }

    private String navStyle(boolean active, boolean hover) {
        String bg = active ? "linear-gradient(to right, #E63946, #8B1A25)" : (hover ? "#1A1A1A" : "transparent");
        String border = active ? "rgba(230,57,70,0.3)" : (hover ? "#2A1015" : "transparent");
        return "-fx-background-color: " + bg + ";"
                + "-fx-background-radius: 14;"
                + "-fx-border-color: " + border + ";"
                + "-fx-border-radius: 14;"
                + "-fx-text-fill: " + (active ? "white" : "#9A9A9A") + ";"
                + "-fx-font-family: " + UIHelper.FONT + ";"
                + "-fx-font-size: 14px;"
                + "-fx-font-weight: 600;"
                + "-fx-padding: 0 16;"
                + "-fx-cursor: hand;";
    }

    private Label sideOverline(String text) {
        Label label = new Label(text.toUpperCase());
        label.setStyle("-fx-text-fill: " + UIHelper.TEXT_SECONDARY + "; -fx-font-size: 11px; -fx-font-family: " + UIHelper.FONT + "; -fx-font-weight: 600;");
        return label;
    }

    private String shellPanelStyle(String color) {
        return "-fx-background-color: " + color + ";"
                + "-fx-background-radius: 20;"
                + "-fx-border-color: #2A1015;"
                + "-fx-border-radius: 20;"
                + "-fx-effect: dropshadow(gaussian, rgba(230,57,70,0.08), 28, 0.20, 0, 10);";
    }

    public static void main(String[] args) {
        launch(args);
    }
}
