package com.networkmonitor.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import com.networkmonitor.exception.NetworkDiagnosticException;
import com.networkmonitor.model.DNSResult;
import com.networkmonitor.model.HTTPResult;
import com.networkmonitor.model.NetworkDiagnosticResult;
import com.networkmonitor.model.NetworkInterfaceInfo;
import com.networkmonitor.model.PingResult;
import com.networkmonitor.model.PortResult;

/**
 * Orchestrates the individual network checks and assembles them
 * into one NetworkDiagnosticResult.
 *
 * The checks are independent I/O-bound operations, so they are
 * executed concurrently to reduce the total diagnostic time.
 */
@Service
public class NetworkDiagnosticService {

    private final DNSService dnsService;
    private final PingService pingService;
    private final PortCheckService portCheckService;
    private final HTTPService httpService;
    private final NetworkInterfaceService networkInterfaceService;
    private final DiagnosticHistoryService diagnosticHistoryService;

    private final ExecutorService diagnosticExecutor =
            Executors.newFixedThreadPool(8);

    public NetworkDiagnosticService(
            DNSService dnsService,
            PingService pingService,
            PortCheckService portCheckService,
            HTTPService httpService,
            NetworkInterfaceService networkInterfaceService,
            DiagnosticHistoryService diagnosticHistoryService) {

        this.dnsService = dnsService;
        this.pingService = pingService;
        this.portCheckService = portCheckService;
        this.httpService = httpService;
        this.networkInterfaceService = networkInterfaceService;
        this.diagnosticHistoryService = diagnosticHistoryService;
    }

    public NetworkDiagnosticResult runDiagnostics(String target) {

        if (target == null || target.isBlank()) {
            throw new NetworkDiagnosticException(
                    "Target hostname or IP address must not be empty."
            );
        }

        String cleanTarget = target.trim();

        long start = System.nanoTime();

        CompletableFuture<DNSResult> dnsFuture =
                CompletableFuture.supplyAsync(
                        () -> dnsService.resolve(cleanTarget),
                        diagnosticExecutor
                );

        CompletableFuture<PingResult> pingFuture =
                CompletableFuture.supplyAsync(
                        () -> pingService.check(cleanTarget),
                        diagnosticExecutor
                );

        CompletableFuture<List<PortResult>> portsFuture =
                CompletableFuture.supplyAsync(
                        () -> portCheckService.checkCommonPorts(cleanTarget),
                        diagnosticExecutor
                );

        CompletableFuture<List<HTTPResult>> httpFuture =
                CompletableFuture.supplyAsync(
                        () -> httpService.checkHttpAndHttps(cleanTarget),
                        diagnosticExecutor
                );

        CompletableFuture<List<NetworkInterfaceInfo>> networkFuture =
                CompletableFuture.supplyAsync(
                        networkInterfaceService::getLocalInterfaces,
                        diagnosticExecutor
                );

        CompletableFuture.allOf(
                dnsFuture,
                pingFuture,
                portsFuture,
                httpFuture,
                networkFuture
        ).join();

        NetworkDiagnosticResult result =
                new NetworkDiagnosticResult();

        result.setTarget(cleanTarget);

        result.setDns(dnsFuture.join());
        result.setPing(pingFuture.join());
        result.setPorts(portsFuture.join());
        result.setHttp(httpFuture.join());
        result.setLocalNetwork(networkFuture.join());

        long elapsedMs =
                (System.nanoTime() - start) / 1_000_000;

        result.setTotalDurationMs(elapsedMs);

        diagnosticHistoryService.saveResult(result);

        return result;
    }
}