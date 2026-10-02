package com.networkmonitor.service;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.networkmonitor.model.PortResult;

/**
 * Checks a small, fixed set of common ports using TCP connect attempts.
 *
 * Each port is checked concurrently because every connection attempt
 * is an independent I/O-bound operation.
 */
@Service
public class PortCheckService {

    private static final int TIMEOUT_MS = 1500;

    private static final Map<Integer, String> COMMON_PORTS = new LinkedHashMap<>();

    static {
        COMMON_PORTS.put(22, "SSH");
        COMMON_PORTS.put(53, "DNS");
        COMMON_PORTS.put(80, "HTTP");
        COMMON_PORTS.put(443, "HTTPS");
    }

    private final Executor portExecutor;

    public PortCheckService(
            @Qualifier("portExecutor")
            Executor portExecutor) {

        this.portExecutor = portExecutor;
    }

    public List<PortResult> checkCommonPorts(String target) {

        List<CompletableFuture<PortResult>> futures = new ArrayList<>();

        for (Map.Entry<Integer, String> entry : COMMON_PORTS.entrySet()) {

            CompletableFuture<PortResult> future =
                    CompletableFuture.supplyAsync(
                            () -> checkPort(
                                    target,
                                    entry.getKey(),
                                    entry.getValue()
                            ),
                            portExecutor
                    );

            futures.add(future);
        }

        /*
         * join() waits for each already-started task.
         * The actual socket operations are running concurrently.
         */
        List<PortResult> results = new ArrayList<>();

        for (CompletableFuture<PortResult> future : futures) {
            results.add(future.join());
        }

        return results;
    }

    private PortResult checkPort(
            String target,
            int port,
            String service
    ) {
        long start = System.nanoTime();

        try (Socket socket = new Socket()) {

            socket.connect(
                    new InetSocketAddress(target, port),
                    TIMEOUT_MS
            );

            long elapsedMs =
                    (System.nanoTime() - start) / 1_000_000;

            return new PortResult(
                    port,
                    service,
                    true,
                    elapsedMs,
                    "OPEN"
            );

        } catch (SocketTimeoutException e) {

            long elapsedMs =
                    (System.nanoTime() - start) / 1_000_000;

            return new PortResult(
                    port,
                    service,
                    false,
                    elapsedMs,
                    "TIMEOUT"
            );

        } catch (ConnectException e) {

            long elapsedMs =
                    (System.nanoTime() - start) / 1_000_000;

            return new PortResult(
                    port,
                    service,
                    false,
                    elapsedMs,
                    "REFUSED"
            );

        } catch (IOException e) {

            long elapsedMs =
                    (System.nanoTime() - start) / 1_000_000;

            return new PortResult(
                    port,
                    service,
                    false,
                    elapsedMs,
                    "ERROR"
            );
        }
    }
}