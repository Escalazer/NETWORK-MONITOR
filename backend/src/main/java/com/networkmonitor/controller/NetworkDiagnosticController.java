package com.networkmonitor.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.networkmonitor.model.NetworkDiagnosticResult;
import com.networkmonitor.service.NetworkDiagnosticService;

@RestController
@RequestMapping("/api/network")
@CrossOrigin(origins = "*")
public class NetworkDiagnosticController {

    private final NetworkDiagnosticService diagnosticService;

    public NetworkDiagnosticController(NetworkDiagnosticService diagnosticService) {
        this.diagnosticService = diagnosticService;
    }

    @GetMapping("/diagnose")
    public NetworkDiagnosticResult diagnose(
            @RequestParam String target) {
        return diagnosticService.runDiagnostics(target);
    }
}
