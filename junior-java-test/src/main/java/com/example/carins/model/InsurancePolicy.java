package com.example.carins.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "insurancepolicy")
public class InsurancePolicy {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Car car;
    @Column(nullable = false)
    private String provider;
    @Column(nullable = false)
    private LocalDate startDate;

//    @Column(nullable = false)
    private LocalDate endDate; // nullable == open-ended

    @Column(nullable = false)
    private boolean expiryNotified = false;

    public InsurancePolicy() {}
    public InsurancePolicy(Car car, String provider, LocalDate startDate, LocalDate endDate) {
        this.car = car; this.provider = provider; this.startDate = startDate; this.endDate = endDate;
//        if (endDate==null) this.endDate = startDate.plusYears(1);
    }

    public Long getId() { return id; }
    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public boolean isExpiryNotified() {
        return expiryNotified;
    }

    public void setExpiryNotified(boolean expiryNotified) {
        this.expiryNotified = expiryNotified;
    }
}
