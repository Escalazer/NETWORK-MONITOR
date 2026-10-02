package com.networkmonitor.model;

public class PingResult {
    private int packetsSent;
    private int packetsReceived;
    private double packetLossPercent;
    private double averageLatencyMs;
    private boolean reachable;
    private String method;
    private String errorMessage;

    public int getPacketsSent() { return packetsSent; }
    public void setPacketsSent(int packetsSent) { this.packetsSent = packetsSent; }

    public int getPacketsReceived() { return packetsReceived; }
    public void setPacketsReceived(int packetsReceived) { this.packetsReceived = packetsReceived; }

    public double getPacketLossPercent() { return packetLossPercent; }
    public void setPacketLossPercent(double packetLossPercent) { this.packetLossPercent = packetLossPercent; }

    public double getAverageLatencyMs() { return averageLatencyMs; }
    public void setAverageLatencyMs(double averageLatencyMs) { this.averageLatencyMs = averageLatencyMs; }

    public boolean isReachable() { return reachable; }
    public void setReachable(boolean reachable) { this.reachable = reachable; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
