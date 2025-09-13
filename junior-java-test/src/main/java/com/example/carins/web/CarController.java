package com.example.carins.web;

import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.service.CarService;
import com.example.carins.web.dto.CarDto;
import com.example.carins.web.dto.InsuranceClaimCreateDto;
import com.example.carins.web.dto.InsuranceClaimDto;
import com.example.carins.web.dto.InsurancePolicyDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CarController {

    private final CarService service;

    public CarController(CarService service) {
        this.service = service;
    }

    @GetMapping("/cars")
    public List<CarDto> getCars() {
        return service.listCars().stream().map(this::toDto).toList();
    }

    @GetMapping("/cars/{carId}/insurance-valid")
    public ResponseEntity<?> isInsuranceValid(@PathVariable Long carId, @RequestParam String date) {
        // TODO: validate date format and handle errors consistently
        LocalDate d = LocalDate.parse(date);
        boolean valid = service.isInsuranceValid(carId, d);
        return ResponseEntity.ok(new InsuranceValidityResponse(carId, d.toString(), valid));
    }
    @PostMapping("/cars/{carId}/claims")
    public ResponseEntity<InsuranceClaimDto> registerClaim(
            @PathVariable Long carId,
            @RequestBody InsuranceClaimCreateDto body) {

        var claim = service.registerClaim(carId, body.claimDate(), body.description(), body.amount());

        var dto = new InsuranceClaimDto(
                claim.getId(),
                carId,
                claim.getClaimDate().toString(),
                claim.getDescription(),
                claim.getAmount()
        );

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(claim.getId())
                .toUri();

        return ResponseEntity.created(location).body(dto);
    }

    @GetMapping("/cars/{carId}/history")
    public List<InsuranceClaimDto> getCarHistory(@PathVariable Long carId) {
        return service.getClaimsForCar(carId).stream()
                .map(c -> new InsuranceClaimDto(
                        c.getId(),
                        carId,
                        c.getClaimDate().toString(),
                        c.getDescription(),
                        c.getAmount()
                ))
                .toList();
    }


    private CarDto toDto(Car c) {
        var o = c.getOwner();
        return new CarDto(c.getId(), c.getVin(), c.getMake(), c.getModel(), c.getYearOfManufacture(),
                o != null ? o.getId() : null,
                o != null ? o.getName() : null,
                o != null ? o.getEmail() : null);
    }

    public record InsuranceValidityResponse(Long carId, String date, boolean valid) {}
}
