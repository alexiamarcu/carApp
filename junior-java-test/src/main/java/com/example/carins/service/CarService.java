package com.example.carins.service;

import com.example.carins.model.Car;
import com.example.carins.model.InsuranceClaim;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsuranceClaimRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.ResponseEntity.notFound;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final InsurancePolicyRepository policyRepository;
    private final InsuranceClaimRepository claimRepository;

    public CarService(CarRepository carRepository, InsurancePolicyRepository policyRepository, InsuranceClaimRepository claimRepository) {
        this.carRepository = carRepository;
        this.policyRepository = policyRepository;
        this.claimRepository = claimRepository;
    }

    public List<Car> listCars() {
        return carRepository.findAll();
    }

    public boolean isInsuranceValid(Long carId, LocalDate date) {
        if (carId == null || date == null) return false;
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
        return policyRepository.existsActiveOnDate(carId, date);
    }

    public InsuranceClaim registerClaim(Long carId, LocalDate claimDate, String description, Integer amount) {
        Car car = carRepository.findById(carId).orElseThrow();
        var claim = new InsuranceClaim(car, claimDate, description, amount);
        return claimRepository.save(claim);
    }
    public List<InsuranceClaim> getClaimsForCar(Long carId) {
        carRepository.findById(carId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
        return claimRepository.findByCarIdOrderByClaimDateAsc(carId);
    }
}
