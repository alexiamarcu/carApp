package com.example.carins.web;

import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.service.CarService;
import com.example.carins.web.dto.CarDto;
import com.example.carins.web.dto.InsuranceClaimCreateDto;
import com.example.carins.web.dto.InsuranceClaimDto;
import com.example.carins.web.dto.InsurancePolicyDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.springframework.http.ResponseEntity.badRequest;

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
        if (date == null || !date.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid date format. Expected YYYY-MM-DD."
            );
        }

        LocalDate d;
        try {
            d = LocalDate.parse(date);
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid date value: " + date + ". Provide a real calendar date in YYYY-MM-DD.");
        }

        LocalDate MIN_SUPPORTED_DATE = LocalDate.of(2024, 1, 1);
        LocalDate MAX_SUPPORTED_DATE = LocalDate.of(2026, 1, 1);

        if (d.isBefore(MIN_SUPPORTED_DATE) || d.isAfter(MAX_SUPPORTED_DATE)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Date out of supported range (" + MIN_SUPPORTED_DATE + " to " + MAX_SUPPORTED_DATE + ").");
        }
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
