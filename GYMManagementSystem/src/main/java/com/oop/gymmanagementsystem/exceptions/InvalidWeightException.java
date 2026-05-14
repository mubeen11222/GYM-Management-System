package com.oop.gymmanagementsystem.exceptions;

public class InvalidWeightException extends Exception {
    public InvalidWeightException(String message) {
        super(message);
    }

    public InvalidWeightException(double weight) {
        super("Invalid weight: " + weight + " kg. Weight must be between 20 and 300 kg.");
    }
}
