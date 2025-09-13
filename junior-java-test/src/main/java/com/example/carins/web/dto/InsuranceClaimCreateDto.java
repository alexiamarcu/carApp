package com.example.carins.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InsuranceClaimCreateDto(
        @NotNull(message = "claimDate is required") LocalDate claimDate,
        @NotNull(message = "description is required") String description,
        @NotNull(message = "amount is required") @Positive(message = "amount must be > 0") Integer amount
) {}
