package com.networkmonitor.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.networkmonitor.model.HTTPResult;

/**
 * Checks HTTP and HTTPS availability.
 *
 * HTTP and HTTPS are independent operations, so both are
 * executed concurrently.
 */
@Service
public class HTTPService {

    private static final Duration TIMEOUT =
            Duration.ofSeconds(4);

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private final Executor httpExecutor;

    public HTTPService(
            @Qualifier("httpExecutor")
            Executor httpExecutor) {

        this.httpExecutor = httpExecutor;
    }

    public List<HTTPResult> checkHttpAndHttps(String target) {

        CompletableFuture<HTTPResult> httpFuture =
                CompletableFuture.supplyAsync(
                        () -> checkScheme(target, "http"),
                        httpExecutor
                );

        CompletableFuture<HTTPResult> httpsFuture =
                CompletableFuture.supplyAsync(
                        () -> checkScheme(target, "https"),
                        httpExecutor
                );

        List<HTTPResult> results = new ArrayList<>();

        results.add(httpFuture.join());
        results.add(httpsFuture.join());

        return results;
    }

    private HTTPResult checkScheme(
            String target,
            String scheme
    ) {
        HTTPResult result = new HTTPResult();
        result.setScheme(scheme);

        HttpRequest request;

        try {

            request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            scheme + "://" + target
                    ))
                    .timeout(TIMEOUT)
                    .GET()
                    .build();

        } catch (IllegalArgumentException e) {

            result.setReachable(false);
            result.setErrorMessage(
                    "Invalid URL: " + e.getMessage()
            );

            return result;
        }

        long start = System.nanoTime();

        try {

            HttpResponse<Void> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.discarding()
                    );

            long elapsedMs =
                    (System.nanoTime() - start) / 1_000_000;

            result.setReachable(true);
            result.setStatusCode(response.statusCode());
            result.setResponseTimeMs(elapsedMs);

        } catch (java.net.http.HttpTimeoutException e) {

            result.setReachable(false);
            result.setErrorMessage(
                    "Request timed out after "
                            + TIMEOUT.toSeconds()
                            + "s"
            );

        } catch (javax.net.ssl.SSLHandshakeException e) {

            result.setReachable(false);
            result.setErrorMessage(
                    "SSL/TLS handshake failed: "
                            + e.getMessage()
            );

        } catch (java.net.ConnectException e) {

            result.setReachable(false);
            result.setErrorMessage(
                    "Connection refused: "
                            + e.getMessage()
            );

        } catch (Exception e) {

            result.setReachable(false);
            result.setErrorMessage(
                    e.getClass().getSimpleName()
                            + ": "
                            + e.getMessage()
            );
        }

        return result;
    }
}