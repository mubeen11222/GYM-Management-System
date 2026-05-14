package com.oop.gymmanagementsystem.services;

import com.oop.gymmanagementsystem.models.Role;
import com.oop.gymmanagementsystem.models.User;
import com.oop.gymmanagementsystem.storage.DataStore;
import com.oop.gymmanagementsystem.utils.IDGenerator;

public class AuthService {
    private final DataStore dataStore;
    private User currentUser;

    public AuthService() {
        this.dataStore = DataStore.getInstance();
        this.currentUser = null;
    }

    public User login(String username, String password) {
        User user = dataStore.authenticateUser(username, password);
        if (user != null) {
            currentUser = user;
        }
        return user;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void initializeDefaultUsers() {
        if (dataStore.getAllUsers().isEmpty()) {
            IDGenerator idGen = IDGenerator.getInstance();
            dataStore.addUser(new User(idGen.generateUserId(), "admin", "admin123", Role.ADMIN, "System Administrator"));
            dataStore.addUser(new User(idGen.generateUserId(), "staff", "staff123", Role.STAFF, "Front Desk Staff"));
            dataStore.addUser(new User(idGen.generateUserId(), "trainer", "trainer123", Role.TRAINER, "Head Trainer"));
            dataStore.saveAll();
        }
    }
}
