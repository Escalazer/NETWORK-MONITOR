package com.networkmonitor.model;

public class PortResult {
    private int port;
    private String service;
    private boolean open;
    private long responseTimeMs;
    private String status;

    public PortResult() {}

    public PortResult(int port, String service, boolean open, long responseTimeMs, String status) {
        this.port = port;
        this.service = service;
        this.open = open;
        this.responseTimeMs = responseTimeMs;
        this.status = status;
    }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }

    public boolean isOpen() { return open; }
    public void setOpen(boolean open) { this.open = open; }

    public long getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(long responseTimeMs) { this.responseTimeMs = responseTimeMs; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
