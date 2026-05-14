package com.oop.gymmanagementsystem.services;

import com.oop.gymmanagementsystem.exceptions.InvalidAgeException;
import com.oop.gymmanagementsystem.exceptions.TrainerLimitExceededException;
import com.oop.gymmanagementsystem.models.*;
import com.oop.gymmanagementsystem.storage.DataStore;
import com.oop.gymmanagementsystem.utils.DateUtils;
import com.oop.gymmanagementsystem.utils.IDGenerator;

import java.util.List;

public class MemberService {
    private final DataStore dataStore;
    private final PaymentService paymentService;
    private final WorkoutService workoutService;
    private final NutritionService nutritionService;

    public MemberService(PaymentService paymentService, WorkoutService workoutService,
                         NutritionService nutritionService) {
        this.dataStore = DataStore.getInstance();
        this.paymentService = paymentService;
        this.workoutService = workoutService;
        this.nutritionService = nutritionService;
    }

    public Member addMember(String name, int age, String gender, String phone,
                            String email, String address, MembershipPlan plan,
                            double weight, double height, String fitnessGoal,
                            String trainerId)
            throws InvalidAgeException, TrainerLimitExceededException {

        // Validate age
        if (age < 12 || age > 80) {
            throw new InvalidAgeException(age);
        }

        // If ADVANCED plan, trainer is mandatory
        if (plan == MembershipPlan.ADVANCED && trainerId == null) {
            throw new IllegalArgumentException("Advanced plan requires a trainer assignment.");
        }

        // Validate trainer capacity
        if (trainerId != null) {
            Trainer trainer = dataStore.getTrainer(trainerId);
            if (trainer == null) {
                throw new IllegalArgumentException("Trainer not found: " + trainerId);
            }
            if (!trainer.canAcceptMember()) {
                throw new TrainerLimitExceededException(trainerId);
            }
        }

        // Create member
        String memberId = IDGenerator.getInstance().generateMemberId();
        Member member = new Member(memberId, name, age, gender, phone, email,
                address, plan, DateUtils.today(), weight, height, fitnessGoal);

        // Assign trainer if provided
        if (trainerId != null) {
            member.setTrainerId(trainerId);
            Trainer trainer = dataStore.getTrainer(trainerId);
            trainer.addMember(memberId);
        }

        // Generate workout and nutrition plans
        workoutService.generateDefaultPlan(member);
        nutritionService.generatePlan(member);

        // Add to data store
        dataStore.addMember(member);

        // Create payment record for current month
        paymentService.createMonthlyPayment(member);

        // Save everything
        dataStore.saveAll();
        return member;
    }

    public void updateMemberPlan(String memberId, MembershipPlan newPlan, String trainerId)
            throws TrainerLimitExceededException {
        Member member = dataStore.getMember(memberId);
        if (member == null) return;

        MembershipPlan oldPlan = member.getPlan();
        member.setPlan(newPlan);

        // If upgrading to ADVANCED, need trainer
        if (newPlan == MembershipPlan.ADVANCED && trainerId != null) {
            assignTrainer(memberId, trainerId);
        }

        // If downgrading to BASIC, remove trainer
        if (newPlan == MembershipPlan.BASIC && member.hasTrainer()) {
            removeTrainer(memberId);
        }

        dataStore.saveAll();
    }

    public void assignTrainer(String memberId, String trainerId) throws TrainerLimitExceededException {
        Member member = dataStore.getMember(memberId);
        Trainer trainer = dataStore.getTrainer(trainerId);

        if (member == null || trainer == null) return;

        if (!trainer.canAcceptMember()) {
            throw new TrainerLimitExceededException(trainerId);
        }

        // Remove from old trainer if any
        if (member.hasTrainer()) {
            Trainer oldTrainer = dataStore.getTrainer(member.getTrainerId());
            if (oldTrainer != null) {
                oldTrainer.removeMember(memberId);
            }
        }

        member.setTrainerId(trainerId);
        trainer.addMember(memberId);
        dataStore.saveAll();
    }

    public void removeTrainer(String memberId) {
        Member member = dataStore.getMember(memberId);
        if (member == null || !member.hasTrainer()) return;

        Trainer trainer = dataStore.getTrainer(member.getTrainerId());
        if (trainer != null) {
            trainer.removeMember(memberId);
        }
        member.setTrainerId(null);
        dataStore.saveAll();
    }

    public List<Member> getAllMembers() {
        return dataStore.getAllMembers();
    }

    public Member getMember(String memberId) {
        return dataStore.getMember(memberId);
    }

    public void removeMember(String memberId) {
        Member member = dataStore.getMember(memberId);
        if (member == null) return;

        // Remove from trainer
        if (member.hasTrainer()) {
            Trainer trainer = dataStore.getTrainer(member.getTrainerId());
            if (trainer != null) {
                trainer.removeMember(memberId);
            }
        }

        dataStore.removeMember(memberId);
        dataStore.saveAll();
    }
}
