package com.example.carins.web.dto;


public record InsurancePolicyDto(
        Long id,
        Long carId,
        String provider,
        String startDate,
        String endDate
) {}
