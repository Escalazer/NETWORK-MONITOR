package com.networkmonitor.model;

public class NetworkInterfaceInfo {
    private String name;
    private String displayName;
    private String ipAddress;
    private String macAddress;
    private boolean up;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getMacAddress() { return macAddress; }
    public void setMacAddress(String macAddress) { this.macAddress = macAddress; }

    public boolean isUp() { return up; }
    public void setUp(boolean up) { this.up = up; }
}
