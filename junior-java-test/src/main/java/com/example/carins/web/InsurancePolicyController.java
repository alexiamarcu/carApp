package com.example.carins.web;

import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.service.CarService;
import com.example.carins.service.InsurancePolicyService;
import com.example.carins.web.dto.InsurancePolicyCreateDto;
import com.example.carins.web.dto.InsurancePolicyDto;
import com.example.carins.web.dto.InsurancePolicyUpdateDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
@RestController
@RequestMapping("/api")
public class InsurancePolicyController {
    private final InsurancePolicyService insurancePolicyService;
    private final CarService carService;

    public InsurancePolicyController(InsurancePolicyService insurancePolicyService, CarService carService) {
        this.insurancePolicyService = insurancePolicyService;
        this.carService = carService;
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

    @PostMapping("/policies")
    public ResponseEntity<InsurancePolicyDto> createPolicy(@RequestBody InsurancePolicyCreateDto body) {
        Car car = carService.findById(body.carId());
        if (carService.isInsuranceValid(car.getId(), body.startDate()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "There is another active policy for this car at the start Time provided.");

        InsurancePolicy saved = insurancePolicyService.create(car, body.provider(), body.startDate(), body.endDate());

        return ResponseEntity.status(201).body(
                new InsurancePolicyDto(saved.getId(), car.getId(), saved.getProvider(),
                        saved.getStartDate().toString(), saved.getEndDate().toString(), saved.isExpiryNotified())
        );
    }

    @PutMapping("/policies/{id}")
    public InsurancePolicyDto updatePolicy(@PathVariable Long id, @RequestBody InsurancePolicyUpdateDto body) {
        InsurancePolicy updated = insurancePolicyService.update(id, body.provider(), body.startDate(), body.endDate());
        return new InsurancePolicyDto(updated.getId(), updated.getCar().getId(), updated.getProvider(),
                updated.getStartDate().toString(), updated.getEndDate().toString(), updated.isExpiryNotified());
    }
}
