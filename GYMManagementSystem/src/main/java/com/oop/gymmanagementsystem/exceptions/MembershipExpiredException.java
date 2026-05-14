package com.oop.gymmanagementsystem.exceptions;

public class MembershipExpiredException extends Exception {
    private final String memberId;

    public MembershipExpiredException(String memberId) {
        super("Membership expired for member ID: " + memberId);
        this.memberId = memberId;
    }

    public MembershipExpiredException(String memberId, String message) {
        super(message);
        this.memberId = memberId;
    }

    public String getMemberId() { return memberId; }
}
