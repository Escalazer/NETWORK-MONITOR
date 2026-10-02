package com.networkmonitor.model;

import java.util.ArrayList;
import java.util.List;

public class NetworkDiagnosticResult {
    private String target;
    private DNSResult dns;
    private PingResult ping;
    private List<PortResult> ports = new ArrayList<>();
    private List<HTTPResult> http = new ArrayList<>();
    private List<NetworkInterfaceInfo> localNetwork = new ArrayList<>();
    private long totalDurationMs;

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public DNSResult getDns() { return dns; }
    public void setDns(DNSResult dns) { this.dns = dns; }

    public PingResult getPing() { return ping; }
    public void setPing(PingResult ping) { this.ping = ping; }

    public List<PortResult> getPorts() { return ports; }
    public void setPorts(List<PortResult> ports) {
        this.ports = ports != null ? ports : new ArrayList<>();
    }

    public List<HTTPResult> getHttp() { return http; }
    public void setHttp(List<HTTPResult> http) {
        this.http = http != null ? http : new ArrayList<>();
    }

    public List<NetworkInterfaceInfo> getLocalNetwork() { return localNetwork; }
    public void setLocalNetwork(List<NetworkInterfaceInfo> localNetwork) {
        this.localNetwork = localNetwork != null ? localNetwork : new ArrayList<>();
    }

    public long getTotalDurationMs() { return totalDurationMs; }
    public void setTotalDurationMs(long totalDurationMs) { this.totalDurationMs = totalDurationMs; }
}
