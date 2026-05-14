package com.oop.gymmanagementsystem.exceptions;

public class TrainerLimitExceededException extends Exception {
    private final String trainerId;

    public TrainerLimitExceededException(String trainerId) {
        super("Trainer " + trainerId + " has reached maximum member limit (5).");
        this.trainerId = trainerId;
    }

    public TrainerLimitExceededException(String trainerId, String message) {
        super(message);
        this.trainerId = trainerId;
    }

    public String getTrainerId() { return trainerId; }
}
