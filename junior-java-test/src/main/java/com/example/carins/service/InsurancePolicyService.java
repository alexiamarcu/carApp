package com.example.carins.service;

import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.InsurancePolicyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
@Service
public class InsurancePolicyService {
    private final InsurancePolicyRepository policyRepository;

    public InsurancePolicyService(InsurancePolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    public List<InsurancePolicy> listPolicies() {
        return this.policyRepository.findAll();
    }

    public InsurancePolicy create(Car car, String provider, LocalDate start, LocalDate end) {
        if (end == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date cannot be null");
        if (start.isAfter(end))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be after end date");

        var p = new InsurancePolicy(car, provider, start, end);

        return policyRepository.save(p);
    }
    public InsurancePolicy update(Long id, String provider, LocalDate start, LocalDate end) {
        var p = policyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy not found"));
        if (end == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date cannot be null");
        if (start.isAfter(end))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date cannot be after end date");

        p.setProvider(provider);
        p.setStartDate(start);
        p.setEndDate(end);
        return policyRepository.save(p);
    }

}
