package com.oop.gymmanagementsystem.services;

import com.oop.gymmanagementsystem.exceptions.TrainerLimitExceededException;
import com.oop.gymmanagementsystem.models.*;
import com.oop.gymmanagementsystem.storage.DataStore;
import com.oop.gymmanagementsystem.utils.IDGenerator;

import java.util.List;

public class TrainerService {
    private final DataStore dataStore;

    public TrainerService() {
        this.dataStore = DataStore.getInstance();
    }

    public Trainer addTrainer(String name, int age, String gender, String phone,
                              String email, String address, String specialization,
                              double baseSalary, String hireDate, int experienceYears) {
        String trainerId = IDGenerator.getInstance().generateTrainerId();
        Trainer trainer = new Trainer(trainerId, name, age, gender, phone, email,
                address, specialization, baseSalary, hireDate, experienceYears);
        dataStore.addTrainer(trainer);
        dataStore.saveAll();
        return trainer;
    }

    public void assignMemberToTrainer(String trainerId, String memberId)
            throws TrainerLimitExceededException {
        Trainer trainer = dataStore.getTrainer(trainerId);
        Member member = dataStore.getMember(memberId);

        if (trainer == null || member == null) {
            throw new IllegalArgumentException("Invalid trainer or member ID.");
        }

        if (!trainer.canAcceptMember()) {
            throw new TrainerLimitExceededException(trainerId);
        }

        // Remove from old trainer
        if (member.hasTrainer()) {
            Trainer oldTrainer = dataStore.getTrainer(member.getTrainerId());
            if (oldTrainer != null) {
                oldTrainer.removeMember(memberId);
            }
        }

        trainer.addMember(memberId);
        member.setTrainerId(trainerId);
        dataStore.saveAll();
    }

    public void removeMemberFromTrainer(String trainerId, String memberId) {
        Trainer trainer = dataStore.getTrainer(trainerId);
        Member member = dataStore.getMember(memberId);

        if (trainer != null) {
            trainer.removeMember(memberId);
        }
        if (member != null) {
            member.setTrainerId(null);
            // If member has ADVANCED plan, they need to be downgraded or warned
        }
        dataStore.saveAll();
    }

    public double calculateSalary(String trainerId) {
        Trainer trainer = dataStore.getTrainer(trainerId);
        if (trainer == null) return 0;

        double salary = trainer.getBaseSalary();

        for (String memberId : trainer.getAssignedMemberIds()) {
            Member member = dataStore.getMember(memberId);
            if (member != null) {
                if (member.getPlan() == MembershipPlan.ADVANCED) {
                    salary += 5000; // Higher bonus for advanced members
                } else {
                    salary += 2000; // Basic member bonus
                }
            }
        }
        return salary;
    }

    public List<Trainer> getAllTrainers() {
        return dataStore.getAllTrainers();
    }

    public List<Trainer> getAvailableTrainers() {
        return dataStore.getAvailableTrainers();
    }

    public Trainer getTrainer(String trainerId) {
        return dataStore.getTrainer(trainerId);
    }

    public void removeTrainer(String trainerId) {
        Trainer trainer = dataStore.getTrainer(trainerId);
        if (trainer == null) return;

        // Unassign all members
        for (String memberId : trainer.getAssignedMemberIds()) {
            Member member = dataStore.getMember(memberId);
            if (member != null) {
                member.setTrainerId(null);
            }
        }

        dataStore.removeTrainer(trainerId);
        dataStore.saveAll();
    }

    public double getTotalSalaryExpense() {
        double total = 0;
        for (Trainer trainer : dataStore.getAllTrainers()) {
            total += calculateSalary(trainer.getTrainerId());
        }
        return total;
    }
}
