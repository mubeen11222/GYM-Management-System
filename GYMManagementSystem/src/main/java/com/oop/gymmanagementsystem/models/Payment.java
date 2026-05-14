package com.oop.gymmanagementsystem.models;

import java.io.Serializable;

public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String paymentId;
    private String memberId;
    private String memberName;
    private double amount;
    private double paidAmount;
    private String month;
    private String paymentDate;
    private PaymentStatus status;
    private MembershipPlan plan;

    public Payment(String paymentId, String memberId, String memberName, double amount,
                   String month, MembershipPlan plan) {
        this.paymentId = paymentId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.amount = amount;
        this.paidAmount = 0;
        this.month = month;
        this.paymentDate = null;
        this.status = PaymentStatus.UNPAID;
        this.plan = plan;
    }

    public void markPaid(String date) {
        this.paidAmount = this.amount;
        this.paymentDate = date;
        this.status = PaymentStatus.PAID;
    }

    public void markPartial(double paidAmount, String date) {
        this.paidAmount = paidAmount;
        this.paymentDate = date;
        this.status = (paidAmount >= amount) ? PaymentStatus.PAID : PaymentStatus.PARTIAL;
    }

    public double getPendingAmount() {
        return amount - paidAmount;
    }

    // Getters
    public String getPaymentId() { return paymentId; }
    public String getMemberId() { return memberId; }
    public String getMemberName() { return memberName; }
    public double getAmount() { return amount; }
    public double getPaidAmount() { return paidAmount; }
    public String getMonth() { return month; }
    public String getPaymentDate() { return paymentDate; }
    public PaymentStatus getStatus() { return status; }
    public MembershipPlan getPlan() { return plan; }

    // Setters
    public void setStatus(PaymentStatus status) { this.status = status; }
    public void setAmount(double amount) { this.amount = amount; }
}
