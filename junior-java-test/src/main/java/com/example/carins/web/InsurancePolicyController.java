package com.example.carins.web;

import com.example.carins.service.InsurancePolicyService;
import com.example.carins.web.dto.InsurancePolicyDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api")
public class InsurancePolicyController {
    private final InsurancePolicyService insurancePolicyService;

    public InsurancePolicyController(InsurancePolicyService insurancePolicyService) {
        this.insurancePolicyService = insurancePolicyService;
    }

    @GetMapping("/policies")
    public List<InsurancePolicyDto> getPolicies() {
        return insurancePolicyService.listPolicies().stream()
                .map(p -> new InsurancePolicyDto(
                        p.getId(),
                        p.getCar().getId(),
                        p.getProvider(),
                        p.getStartDate().toString(),
                        p.getEndDate().toString(),
                        p.isExpiryNotified()
                ))
                .toList();
    }
}
