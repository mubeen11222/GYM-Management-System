package com.oop.gymmanagementsystem.services;

import com.oop.gymmanagementsystem.models.*;
import com.oop.gymmanagementsystem.storage.DataStore;
import com.oop.gymmanagementsystem.utils.DateUtils;
import com.oop.gymmanagementsystem.utils.IDGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PaymentService {
    private final DataStore dataStore;

    public PaymentService() {
        this.dataStore = DataStore.getInstance();
    }

    public Payment createMonthlyPayment(Member member) {
        String paymentId = IDGenerator.getInstance().generatePaymentId();
        double amount = member.getPlan().getMonthlyFee();
        String currentMonth = DateUtils.currentMonth();

        Payment payment = new Payment(paymentId, member.getMemberId(),
                member.getName(), amount, currentMonth, member.getPlan());
        dataStore.addPayment(payment);
        dataStore.saveAll();
        return payment;
    }

    public void markPaymentPaid(String paymentId) {
        List<Payment> payments = dataStore.getAllPayments();
        for (Payment p : payments) {
            if (p.getPaymentId().equals(paymentId)) {
                p.markPaid(DateUtils.today());
                break;
            }
        }
        dataStore.saveAll();
    }

    public void markPaymentPartial(String paymentId, double amount) {
        List<Payment> payments = dataStore.getAllPayments();
        for (Payment p : payments) {
            if (p.getPaymentId().equals(paymentId)) {
                p.markPartial(amount, DateUtils.today());
                break;
            }
        }
        dataStore.saveAll();
    }

    public List<Payment> getPaidPayments() {
        return dataStore.getPaymentsByStatus(PaymentStatus.PAID);
    }

    public List<Payment> getUnpaidPayments() {
        return dataStore.getPaymentsByStatus(PaymentStatus.UNPAID);
    }

    public List<Payment> getMemberPayments(String memberId) {
        return dataStore.getPaymentsByMember(memberId);
    }

    public double getTotalPendingAmount() {
        return dataStore.getAllPayments().stream()
                .filter(p -> p.getStatus() != PaymentStatus.PAID)
                .mapToDouble(Payment::getPendingAmount)
                .sum();
    }

    public double getMonthlyRevenue(String month) {
        return dataStore.getPaymentsByMonth(month).stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .mapToDouble(Payment::getPaidAmount)
                .sum();
    }

    public Map<String, Double> getRevenueByMonth() {
        Map<String, Double> revenueMap = new HashMap<>();
        for (Payment p : dataStore.getAllPayments()) {
            if (p.getStatus() == PaymentStatus.PAID) {
                revenueMap.merge(p.getMonth(), p.getPaidAmount(), Double::sum);
            }
        }
        return revenueMap;
    }

    public double getTotalRevenue() {
        return dataStore.getAllPayments().stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .mapToDouble(Payment::getPaidAmount)
                .sum();
    }

    public double getTotalExpectedRevenue() {
        return dataStore.getAllPayments().stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public PaymentStatus getMemberPaymentStatus(String memberId) {
        List<Payment> payments = dataStore.getPaymentsByMember(memberId);
        if (payments.isEmpty()) return PaymentStatus.UNPAID;
        Payment latest = payments.get(payments.size() - 1);
        return latest.getStatus();
    }

    public List<Payment> getAllPayments() {
        return dataStore.getAllPayments();
    }
}
