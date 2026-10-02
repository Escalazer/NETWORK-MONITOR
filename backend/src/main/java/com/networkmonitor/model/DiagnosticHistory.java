package com.networkmonitor.model;

import java.time.LocalDateTime;

public class DiagnosticHistory {

    private Long id;
    private String target;
    private long durationMs;
    private boolean dnsOnline;
    private boolean reachable;
    private int packetLoss;
    private LocalDateTime timestamp;

    public DiagnosticHistory() {
    }

    public DiagnosticHistory(
            String target,
            long durationMs,
            boolean dnsOnline,
            boolean reachable,
            int packetLoss,
            LocalDateTime timestamp) {

        this.target = target;
        this.durationMs = durationMs;
        this.dnsOnline = dnsOnline;
        this.reachable = reachable;
        this.packetLoss = packetLoss;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    public boolean isDnsOnline() {
        return dnsOnline;
    }

    public void setDnsOnline(boolean dnsOnline) {
        this.dnsOnline = dnsOnline;
    }

    public boolean isReachable() {
        return reachable;
    }

    public void setReachable(boolean reachable) {
        this.reachable = reachable;
    }

    public int getPacketLoss() {
        return packetLoss;
    }

    public void setPacketLoss(int packetLoss) {
        this.packetLoss = packetLoss;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}