package com.networkmonitor.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.springframework.stereotype.Service;

import com.networkmonitor.model.PingResult;

/**
 * Network reachability and latency check.
 *
 * The service first attempts Java's reachability mechanism.
 * If that does not succeed, it falls back to a TCP connection
 * on common service ports.
 *
 * We intentionally perform one reachability probe rather than
 * repeatedly waiting for the full timeout. This keeps the
 * diagnostic responsive when ICMP is unavailable.
 */
@Service
public class PingService {

    private static final int TIMEOUT_MS = 1500;

    private static final int[] FALLBACK_PORTS = {
            443,
            80
    };

    public PingResult check(String target) {

        PingResult result = new PingResult();

        /*
         * This check represents one reachability probe.
         * It is not claiming to be four ICMP packets.
         */
        result.setPacketsSent(1);

        InetAddress address;

        try {
            address = InetAddress.getByName(target);

        } catch (IOException e) {

            result.setReachable(false);
            result.setPacketsReceived(0);
            result.setPacketLossPercent(100.0);
            result.setMethod("NONE");
            result.setErrorMessage(
                    "Host could not be resolved: "
                            + e.getMessage()
            );

            return result;
        }

        /*
         * First attempt Java reachability.
         */
        long start = System.nanoTime();

        boolean reachable = tryIcmpReachable(address);

        if (reachable) {

            double latency =
                    (System.nanoTime() - start) / 1_000_000.0;

            result.setReachable(true);
            result.setPacketsReceived(1);
            result.setPacketLossPercent(0.0);
            result.setAverageLatencyMs(round2(latency));
            result.setMethod(
                    "ICMP (InetAddress.isReachable)"
            );

            return result;
        }

        /*
         * ICMP/reachability attempt failed.
         *
         * Instead of waiting another 4 × 1500 ms, perform a
         * TCP connection fallback.
         */
        start = System.nanoTime();

        boolean tcpReachable = tryTcpFallback(address);

        if (tcpReachable) {

            double latency =
                    (System.nanoTime() - start) / 1_000_000.0;

            result.setReachable(true);
            result.setPacketsReceived(1);
            result.setPacketLossPercent(0.0);
            result.setAverageLatencyMs(round2(latency));
            result.setMethod("TCP connect fallback");

        } else {

            result.setReachable(false);
            result.setPacketsReceived(0);
            result.setPacketLossPercent(100.0);
            result.setMethod("NONE");
            result.setErrorMessage(
                    "Host did not respond to the reachability "
                            + "or TCP fallback probe within "
                            + TIMEOUT_MS + "ms."
            );
        }

        return result;
    }

    private boolean tryIcmpReachable(InetAddress address) {

        try {
            return address.isReachable(TIMEOUT_MS);

        } catch (IOException e) {
            return false;
        }
    }

    private boolean tryTcpFallback(InetAddress address) {

        for (int port : FALLBACK_PORTS) {

            try (Socket socket = new Socket()) {

                socket.connect(
                        new InetSocketAddress(
                                address,
                                port
                        ),
                        TIMEOUT_MS
                );

                return true;

            } catch (IOException ignored) {
                // Try the next fallback port.
            }
        }

        return false;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}