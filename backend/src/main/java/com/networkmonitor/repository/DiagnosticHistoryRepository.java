package com.networkmonitor.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Repository;

import com.networkmonitor.model.DiagnosticHistory;

@Repository
public class DiagnosticHistoryRepository {

    private final List<DiagnosticHistory> history =
            new CopyOnWriteArrayList<>();

    public DiagnosticHistory save(DiagnosticHistory record) {
        record.setId((long) (history.size() + 1));
        history.add(record);
        return record;
    }

    public List<DiagnosticHistory> findAll() {
        return new ArrayList<>(history);
    }

    public void clear() {
        history.clear();
    }
}