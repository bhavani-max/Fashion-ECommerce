package com.example.Fashion.controller;

import com.example.Fashion.dto.AnalyticsDTO;
import com.example.Fashion.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin-only analytics endpoint.
 * Returns aggregated sales data for the admin dashboard.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /** Returns full sales analytics — total revenue, top products, category breakdown */
    @GetMapping("/analytics")
    public ResponseEntity<AnalyticsDTO> getAnalytics() {
        return ResponseEntity.ok(analyticsService.getAnalytics());
    }
}