package com.networkmonitor.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.networkmonitor.model.DiagnosticHistory;
import com.networkmonitor.model.NetworkDiagnosticResult;
import com.networkmonitor.repository.DiagnosticHistoryRepository;

@Service
public class DiagnosticHistoryService {

    private final DiagnosticHistoryRepository repository;

    public DiagnosticHistoryService(
            DiagnosticHistoryRepository repository) {
        this.repository = repository;
    }

    public DiagnosticHistory saveResult(NetworkDiagnosticResult result) {

        boolean dnsOnline =
                result.getDns() != null &&
                result.getDns().isResolved();

        boolean reachable =
                result.getPing() != null &&
                result.getPing().getPacketsReceived() > 0;

        int packetLoss = 100;

        if (result.getPing() != null) {
            int sent = result.getPing().getPacketsSent();
            int received = result.getPing().getPacketsReceived();

            if (sent > 0) {
                packetLoss =
                        (int) (((double) (sent - received) / sent) * 100);
            }
        }

        DiagnosticHistory history = new DiagnosticHistory(
                result.getTarget(),
                result.getTotalDurationMs(),
                dnsOnline,
                reachable,
                packetLoss,
                LocalDateTime.now()
        );

        return repository.save(history);
    }

    public List<DiagnosticHistory> getHistory() {
        return repository.findAll();
    }

    public void clearHistory() {
        repository.clear();
    }
}