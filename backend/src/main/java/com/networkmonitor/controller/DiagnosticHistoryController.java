package com.networkmonitor.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.networkmonitor.model.DiagnosticHistory;
import com.networkmonitor.service.DiagnosticHistoryService;

@RestController
@RequestMapping("/api/history")
@CrossOrigin(origins = "http://localhost:5173")
public class DiagnosticHistoryController {

    private final DiagnosticHistoryService historyService;

    public DiagnosticHistoryController(DiagnosticHistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping
    public List<DiagnosticHistory> getHistory() {
        return historyService.getHistory();
    }
}