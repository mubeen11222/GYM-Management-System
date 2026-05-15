package com.oop.gymmanagementsystem;

import com.oop.gymmanagementsystem.models.*;
import com.oop.gymmanagementsystem.services.*;
import com.oop.gymmanagementsystem.storage.DataStore;
import com.oop.gymmanagementsystem.ui.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    private StackPane rootContainer;
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
        rootContainer.setStyle("-fx-background-color: " + UIHelper.BG_DARK + ";");

        scene = new Scene(rootContainer, 1100, 700);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Apply dark theme to dialogs
        scene.getRoot().setStyle("-fx-background-color: " + UIHelper.BG_DARK + ";");

        // Start with login screen
        showLogin();

        primaryStage.setTitle("AI GYM Management System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    private void showLogin() {
        LoginScreen loginScreen = new LoginScreen(authService, this::showDashboard);
        setContent(loginScreen.getRoot());
    }

    private void showDashboard() {
        DashboardScreen dashboard = new DashboardScreen(authService, this::navigate, () -> {
            authService.logout();
            showLogin();
        });
        setContent(dashboard.getRoot());
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
        MembersPanel panel = new MembersPanel(memberService, trainerService,
                this::navigate, this::showMemberDetail);
        setContent(panel.getRoot());
    }

    private void showMemberDetail(Member member) {
        // Refresh member data from store
        Member fresh = DataStore.getInstance().getMember(member.getMemberId());
        if (fresh == null) fresh = member;

        MemberDetailPanel detail = new MemberDetailPanel(fresh, this::navigate);
        setContent(detail.getRoot());
    }

    private void showTrainers() {
        TrainersPanel panel = new TrainersPanel(trainerService,
                this::navigate, this::showTrainerDetail);
        setContent(panel.getRoot());
    }

    private void showTrainerDetail(Trainer trainer) {
        Trainer fresh = DataStore.getInstance().getTrainer(trainer.getTrainerId());
        if (fresh == null) fresh = trainer;

        TrainerDetailPanel detail = new TrainerDetailPanel(fresh, trainerService, this::navigate);
        setContent(detail.getRoot());
    }

    private void showPayments() {
        PaymentsPanel panel = new PaymentsPanel(paymentService, trainerService, this::navigate);
        setContent(panel.getRoot());
    }

    private void setContent(javafx.scene.layout.Pane content) {
        rootContainer.getChildren().clear();
        rootContainer.getChildren().add(content);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
