package com.oop.gymmanagementsystem.services;

import com.oop.gymmanagementsystem.exceptions.InvalidWeightException;
import com.oop.gymmanagementsystem.models.Member;
import com.oop.gymmanagementsystem.models.ProgressRecord;
import com.oop.gymmanagementsystem.storage.DataStore;
import com.oop.gymmanagementsystem.utils.DateUtils;

import java.util.List;

public class ProgressService {
    private final DataStore dataStore;

    public ProgressService() {
        this.dataStore = DataStore.getInstance();
    }

    public void addProgressRecord(String memberId, double weight, double bodyFat,
                                  int workoutsCompleted, String notes)
            throws InvalidWeightException {
        if (weight < 20 || weight > 300) {
            throw new InvalidWeightException(weight);
        }
        Member member = dataStore.getMember(memberId);
        if (member == null) return;

        ProgressRecord record = new ProgressRecord(
                DateUtils.today(), weight, bodyFat, workoutsCompleted, notes);
        member.addProgressRecord(record);
        member.setCurrentWeight(weight);
        dataStore.saveAll();
    }

    public List<ProgressRecord> getProgressHistory(String memberId) {
        Member member = dataStore.getMember(memberId);
        if (member == null) return List.of();
        return member.getProgressHistory();
    }

    public double getWeightChange(String memberId) {
        Member member = dataStore.getMember(memberId);
        if (member == null) return 0;
        List<ProgressRecord> history = member.getProgressHistory();
        if (history.size() < 2) return 0;
        return history.get(history.size() - 1).getWeight() - history.get(0).getWeight();
    }
}
