package com.oop.gymmanagementsystem.exceptions;

public class InvalidAgeException extends Exception {
    public InvalidAgeException(String message) {
        super(message);
    }

    public InvalidAgeException(int age) {
        super("Invalid age: " + age + ". Age must be between 12 and 80.");
    }
}
