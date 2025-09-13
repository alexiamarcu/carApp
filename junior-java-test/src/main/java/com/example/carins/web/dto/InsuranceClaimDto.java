package com.example.carins.web.dto;
import java.math.BigDecimal;

public record InsuranceClaimDto(
        Long id,
        Long carId,
        String claimDate,
        String description,
        Integer amount
) {}
