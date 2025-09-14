package com.example.carins.jobs;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.InsurancePolicyRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.List;

@Component
public class PolicyExpiryJob {

    private static final Logger log = LoggerFactory.getLogger(PolicyExpiryJob.class);
    private final InsurancePolicyRepository policyRepo;

    public PolicyExpiryJob(InsurancePolicyRepository policyRepo) { this.policyRepo = policyRepo; }

    @Scheduled(cron = "0 * * * * *", zone = "Europe/Bucharest")
    @Transactional
    public void logRecentlyExpiredPolicies() {
        LocalTime t = LocalTime.now();
        if (t.isAfter(LocalTime.of(0, 59, 59))) return;

        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<InsurancePolicy> toLog = policyRepo.findByEndDateAndExpiryNotifiedFalse(yesterday);
        if (toLog.isEmpty()) return;

        for (InsurancePolicy p : toLog) {
            log.info("Policy {} for car {} expired on {}", p.getId(), p.getCar().getId(), p.getEndDate());
            System.out.println("Policy " + p.getId() + " for car " + p.getCar().getId() + " expired on " + p.getEndDate());

            p.setExpiryNotified(true);
            policyRepo.save(p);
        }
    }
}
