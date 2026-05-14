package com.oop.gymmanagementsystem.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class IDGenerator implements Serializable {
    private static final long serialVersionUID = 1L;
    private static IDGenerator instance;
    private final Map<String, Integer> counters;

    private IDGenerator() {
        counters = new HashMap<>();
        counters.put("MEM", 100);
        counters.put("TRN", 100);
        counters.put("PAY", 1000);
        counters.put("USR", 100);
    }

    public static IDGenerator getInstance() {
        if (instance == null) {
            instance = new IDGenerator();
        }
        return instance;
    }

    public static void setInstance(IDGenerator loaded) {
        instance = loaded;
    }

    public String generateMemberId() {
        int count = counters.get("MEM") + 1;
        counters.put("MEM", count);
        return "MEM-" + count;
    }

    public String generateTrainerId() {
        int count = counters.get("TRN") + 1;
        counters.put("TRN", count);
        return "TRN-" + count;
    }

    public String generatePaymentId() {
        int count = counters.get("PAY") + 1;
        counters.put("PAY", count);
        return "PAY-" + count;
    }

    public String generateUserId() {
        int count = counters.get("USR") + 1;
        counters.put("USR", count);
        return "USR-" + count;
    }
}
