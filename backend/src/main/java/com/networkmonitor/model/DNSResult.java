package com.networkmonitor.model;

import java.util.ArrayList;
import java.util.List;

public class DNSResult {
    private boolean resolved;
    private String hostname;
    private List<String> addresses = new ArrayList<>();
    private String errorMessage;

    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }

    public String getHostname() { return hostname; }
    public void setHostname(String hostname) { this.hostname = hostname; }

    public List<String> getAddresses() { return addresses; }
    public void setAddresses(List<String> addresses) {
        this.addresses = addresses != null ? addresses : new ArrayList<>();
    }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
