package com.oop.gymmanagementsystem.storage;

import com.oop.gymmanagementsystem.models.*;
import com.oop.gymmanagementsystem.utils.IDGenerator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataStore implements Serializable {
    private static final long serialVersionUID = 1L;
    private static DataStore instance;
    private static final FileManager fileManager = new FileManager();

    private ArrayList<Member> members;
    private ArrayList<Trainer> trainers;
    private ArrayList<Payment> payments;
    private ArrayList<User> users;

    private DataStore() {
        members = new ArrayList<>();
        trainers = new ArrayList<>();
        payments = new ArrayList<>();
        users = new ArrayList<>();
    }

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    // ── Member Operations ──
    public void addMember(Member member) { members.add(member); }
    public void removeMember(String memberId) {
        members.removeIf(m -> m.getMemberId().equals(memberId));
    }
    public Member getMember(String memberId) {
        return members.stream().filter(m -> m.getMemberId().equals(memberId)).findFirst().orElse(null);
    }
    public List<Member> getAllMembers() { return new ArrayList<>(members); }

    // ── Trainer Operations ──
    public void addTrainer(Trainer trainer) { trainers.add(trainer); }
    public void removeTrainer(String trainerId) {
        trainers.removeIf(t -> t.getTrainerId().equals(trainerId));
    }
    public Trainer getTrainer(String trainerId) {
        return trainers.stream().filter(t -> t.getTrainerId().equals(trainerId)).findFirst().orElse(null);
    }
    public List<Trainer> getAllTrainers() { return new ArrayList<>(trainers); }
    public List<Trainer> getAvailableTrainers() {
        return trainers.stream().filter(Trainer::canAcceptMember).collect(Collectors.toList());
    }

    // ── Payment Operations ──
    public void addPayment(Payment payment) { payments.add(payment); }
    public List<Payment> getAllPayments() { return new ArrayList<>(payments); }
    public List<Payment> getPaymentsByMember(String memberId) {
        return payments.stream().filter(p -> p.getMemberId().equals(memberId)).collect(Collectors.toList());
    }
    public List<Payment> getPaymentsByMonth(String month) {
        return payments.stream().filter(p -> p.getMonth().equals(month)).collect(Collectors.toList());
    }
    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return payments.stream().filter(p -> p.getStatus() == status).collect(Collectors.toList());
    }

    // ── User Operations ──
    public void addUser(User user) { users.add(user); }
    public List<User> getAllUsers() { return new ArrayList<>(users); }
    public User authenticateUser(String username, String password) {
        return users.stream()
                .filter(u -> u.authenticate(username, password))
                .findFirst().orElse(null);
    }

    // ── Persistence ──
    public void saveAll() {
        fileManager.saveObject(members, "members.dat");
        fileManager.saveObject(trainers, "trainers.dat");
        fileManager.saveObject(payments, "payments.dat");
        fileManager.saveObject(users, "users.dat");
        fileManager.saveObject(IDGenerator.getInstance(), "idgen.dat");
    }

    @SuppressWarnings("unchecked")
    public void loadAll() {
        Object obj;

        obj = fileManager.loadObject("members.dat");
        if (obj != null) members = (ArrayList<Member>) obj;

        obj = fileManager.loadObject("trainers.dat");
        if (obj != null) trainers = (ArrayList<Trainer>) obj;

        obj = fileManager.loadObject("payments.dat");
        if (obj != null) payments = (ArrayList<Payment>) obj;

        obj = fileManager.loadObject("users.dat");
        if (obj != null) users = (ArrayList<User>) obj;

        obj = fileManager.loadObject("idgen.dat");
        if (obj != null) IDGenerator.setInstance((IDGenerator) obj);
    }
}
