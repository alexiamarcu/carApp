package com.example.carins.web.dto;

import java.time.LocalDate;

public record InsurancePolicyUpdateDto(
        String provider,
        LocalDate startDate,
        LocalDate endDate
) {}
