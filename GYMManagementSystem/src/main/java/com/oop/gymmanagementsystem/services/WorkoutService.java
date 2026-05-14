package com.oop.gymmanagementsystem.services;

import com.oop.gymmanagementsystem.models.*;
import com.oop.gymmanagementsystem.utils.DateUtils;

public class WorkoutService {

    public void generateDefaultPlan(Member member) {
        String goal = member.getFitnessGoal() != null ? member.getFitnessGoal() : "General Fitness";
        WorkoutPlan plan;

        switch (goal.toLowerCase()) {
            case "weight loss":
                plan = createWeightLossPlan();
                break;
            case "muscle gain":
                plan = createMuscleGainPlan();
                break;
            case "strength":
                plan = createStrengthPlan();
                break;
            default:
                plan = createGeneralFitnessPlan();
                break;
        }

        member.setWorkoutPlan(plan);
    }

    private WorkoutPlan createWeightLossPlan() {
        WorkoutPlan plan = new WorkoutPlan("Fat Burner Program", "Intermediate", DateUtils.today());
        plan.addExercise(new Exercise("Treadmill Running", 1, 30, 60, "Cardio"));
        plan.addExercise(new Exercise("Burpees", 4, 15, 45, "Full Body"));
        plan.addExercise(new Exercise("Jump Squats", 4, 20, 45, "Legs"));
        plan.addExercise(new Exercise("Mountain Climbers", 4, 20, 30, "Core"));
        plan.addExercise(new Exercise("Kettlebell Swings", 4, 15, 45, "Full Body"));
        plan.addExercise(new Exercise("Plank Hold", 3, 60, 30, "Core"));
        return plan;
    }

    private WorkoutPlan createMuscleGainPlan() {
        WorkoutPlan plan = new WorkoutPlan("Hypertrophy Program", "Advanced", DateUtils.today());
        plan.addExercise(new Exercise("Bench Press", 4, 10, 90, "Chest"));
        plan.addExercise(new Exercise("Barbell Squats", 4, 10, 120, "Legs"));
        plan.addExercise(new Exercise("Deadlift", 4, 8, 120, "Back"));
        plan.addExercise(new Exercise("Overhead Press", 4, 10, 90, "Shoulders"));
        plan.addExercise(new Exercise("Barbell Rows", 4, 10, 90, "Back"));
        plan.addExercise(new Exercise("Bicep Curls", 3, 12, 60, "Arms"));
        plan.addExercise(new Exercise("Tricep Dips", 3, 12, 60, "Arms"));
        return plan;
    }

    private WorkoutPlan createStrengthPlan() {
        WorkoutPlan plan = new WorkoutPlan("Strength Builder", "Advanced", DateUtils.today());
        plan.addExercise(new Exercise("Back Squats", 5, 5, 180, "Legs"));
        plan.addExercise(new Exercise("Bench Press", 5, 5, 180, "Chest"));
        plan.addExercise(new Exercise("Deadlift", 5, 3, 180, "Back"));
        plan.addExercise(new Exercise("Overhead Press", 5, 5, 120, "Shoulders"));
        plan.addExercise(new Exercise("Pull-ups", 4, 8, 90, "Back"));
        return plan;
    }

    private WorkoutPlan createGeneralFitnessPlan() {
        WorkoutPlan plan = new WorkoutPlan("General Fitness", "Beginner", DateUtils.today());
        plan.addExercise(new Exercise("Treadmill Walk", 1, 20, 60, "Cardio"));
        plan.addExercise(new Exercise("Push-ups", 3, 15, 60, "Chest"));
        plan.addExercise(new Exercise("Bodyweight Squats", 3, 20, 60, "Legs"));
        plan.addExercise(new Exercise("Plank", 3, 30, 45, "Core"));
        plan.addExercise(new Exercise("Dumbbell Rows", 3, 12, 60, "Back"));
        plan.addExercise(new Exercise("Lunges", 3, 12, 60, "Legs"));
        return plan;
    }

    public void toggleExerciseCompletion(Member member, int exerciseIndex) {
        if (member.getWorkoutPlan() == null) return;
        var exercises = member.getWorkoutPlan().getExercises();
        if (exerciseIndex >= 0 && exerciseIndex < exercises.size()) {
            Exercise ex = exercises.get(exerciseIndex);
            ex.setCompleted(!ex.isCompleted());
        }
    }
}
