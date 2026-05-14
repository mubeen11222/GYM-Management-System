package com.oop.gymmanagementsystem.services;

import com.oop.gymmanagementsystem.models.*;
import com.oop.gymmanagementsystem.storage.DataStore;
import com.oop.gymmanagementsystem.utils.IDGenerator;

public class SampleDataService {
    private final DataStore dataStore;

    public SampleDataService() {
        this.dataStore = DataStore.getInstance();
    }

    public void initializeSampleData() {
        if (!dataStore.getAllTrainers().isEmpty()) return;

        IDGenerator idGen = IDGenerator.getInstance();

        // Create Trainers
        Trainer t1 = new Trainer(idGen.generateTrainerId(), "Ahmed Khan", 32, "Male",
                "0301-1234567", "ahmed@gym.com", "Lahore", "Weight Training", 15000, "2024-01-15", 8);
        Trainer t2 = new Trainer(idGen.generateTrainerId(), "Sara Ali", 28, "Female",
                "0302-2345678", "sara@gym.com", "Lahore", "Cardio & HIIT", 15000, "2024-03-01", 5);
        Trainer t3 = new Trainer(idGen.generateTrainerId(), "Bilal Hussain", 35, "Male",
                "0303-3456789", "bilal@gym.com", "Islamabad", "CrossFit", 18000, "2023-06-10", 10);

        dataStore.addTrainer(t1);
        dataStore.addTrainer(t2);
        dataStore.addTrainer(t3);

        // Create Members with services
        PaymentService paymentService = new PaymentService();
        WorkoutService workoutService = new WorkoutService();
        NutritionService nutritionService = new NutritionService();

        // Member 1 - Advanced with trainer
        Member m1 = new Member(idGen.generateMemberId(), "Usman Tariq", 25, "Male",
                "0311-1111111", "usman@mail.com", "Lahore",
                MembershipPlan.ADVANCED, "2025-01-10", 85, 178, "Muscle Gain");
        m1.setTrainerId(t1.getTrainerId());
        t1.addMember(m1.getMemberId());
        workoutService.generateDefaultPlan(m1);
        nutritionService.generatePlan(m1);
        m1.addProgressRecord(new ProgressRecord("2025-01-10", 85, 22, 0, "Starting"));
        m1.addProgressRecord(new ProgressRecord("2025-02-10", 83, 20, 12, "Good progress"));
        m1.addProgressRecord(new ProgressRecord("2025-03-10", 81, 18, 24, "Leaning out"));
        dataStore.addMember(m1);
        Payment p1 = paymentService.createMonthlyPayment(m1);
        p1.markPaid("2025-01-15");

        // Member 2 - Basic
        Member m2 = new Member(idGen.generateMemberId(), "Ayesha Nawaz", 22, "Female",
                "0312-2222222", "ayesha@mail.com", "Karachi",
                MembershipPlan.BASIC, "2025-02-01", 65, 165, "Weight Loss");
        workoutService.generateDefaultPlan(m2);
        nutritionService.generatePlan(m2);
        m2.addProgressRecord(new ProgressRecord("2025-02-01", 65, 28, 0, "Starting"));
        m2.addProgressRecord(new ProgressRecord("2025-03-01", 63, 26, 10, "Lost 2kg"));
        dataStore.addMember(m2);
        paymentService.createMonthlyPayment(m2);

        // Member 3 - Advanced with trainer
        Member m3 = new Member(idGen.generateMemberId(), "Hassan Raza", 30, "Male",
                "0313-3333333", "hassan@mail.com", "Lahore",
                MembershipPlan.ADVANCED, "2025-01-20", 92, 182, "Strength");
        m3.setTrainerId(t2.getTrainerId());
        t2.addMember(m3.getMemberId());
        workoutService.generateDefaultPlan(m3);
        nutritionService.generatePlan(m3);
        m3.addProgressRecord(new ProgressRecord("2025-01-20", 92, 25, 0, "Starting"));
        dataStore.addMember(m3);
        Payment p3 = paymentService.createMonthlyPayment(m3);
        p3.markPaid("2025-02-01");

        // Member 4 - Basic
        Member m4 = new Member(idGen.generateMemberId(), "Fatima Shah", 27, "Female",
                "0314-4444444", "fatima@mail.com", "Islamabad",
                MembershipPlan.BASIC, "2025-03-05", 58, 160, "General Fitness");
        workoutService.generateDefaultPlan(m4);
        nutritionService.generatePlan(m4);
        dataStore.addMember(m4);
        paymentService.createMonthlyPayment(m4);

        // Member 5 - Advanced with trainer
        Member m5 = new Member(idGen.generateMemberId(), "Ali Zafar", 28, "Male",
                "0315-5555555", "ali@mail.com", "Lahore",
                MembershipPlan.ADVANCED, "2025-02-15", 78, 175, "Muscle Gain");
        m5.setTrainerId(t1.getTrainerId());
        t1.addMember(m5.getMemberId());
        workoutService.generateDefaultPlan(m5);
        nutritionService.generatePlan(m5);
        dataStore.addMember(m5);
        Payment p5 = paymentService.createMonthlyPayment(m5);
        p5.markPaid("2025-02-20");

        dataStore.saveAll();
    }
}
