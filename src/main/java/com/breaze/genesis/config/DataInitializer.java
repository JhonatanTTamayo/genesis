package com.breaze.genesis.config;

import com.breaze.genesis.entity.ExchangeRate;
import com.breaze.genesis.entity.OperationCatalog;
import com.breaze.genesis.entity.Plan;
import com.breaze.genesis.entity.Role;
import com.breaze.genesis.entity.User;
import com.breaze.genesis.repository.ExchangeRateRepository;
import com.breaze.genesis.repository.OperationCatalogRepository;
import com.breaze.genesis.repository.PlanRepository;
import com.breaze.genesis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PlanRepository planRepository;
    private final OperationCatalogRepository operationCatalogRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initializePlans();
        initializeOperations();
        initializeExchangeRate();
        initializeAdminUser();
    }

    private void initializePlans() {
        if (!planRepository.existsByName("Free")) {
            planRepository.save(
                    Plan.builder()
                            .name("Free")
                            .tokenAmount(200)
                            .active(true)
                            .build()
            );
        }

        if (!planRepository.existsByName("Pro")) {
            planRepository.save(
                    Plan.builder()
                            .name("Pro")
                            .tokenAmount(1000)
                            .active(true)
                            .build()
            );
        }

        if (!planRepository.existsByName("Enterprise")) {
            planRepository.save(
                    Plan.builder()
                            .name("Enterprise")
                            .tokenAmount(5000)
                            .active(true)
                            .build()
            );
        }
    }

    private void initializeOperations() {
        if (!operationCatalogRepository.existsByCode("OP-01")) {
            operationCatalogRepository.save(
                    OperationCatalog.builder()
                            .code("OP-01")
                            .name("¿Cuánto me cuesta ese crédito?")
                            .baseCost(50)
                            .active(true)
                            .build()
            );
        }

        if (!operationCatalogRepository.existsByCode("OP-02")) {
            operationCatalogRepository.save(
                    OperationCatalog.builder()
                            .code("OP-02")
                            .name("Conversor COP ↔ USD")
                            .baseCost(20)
                            .active(true)
                            .build()
            );
        }

        if (!operationCatalogRepository.existsByCode("OP-03")) {
            operationCatalogRepository.save(
                    OperationCatalog.builder()
                            .code("OP-03")
                            .name("Calculadora de IMC")
                            .baseCost(15)
                            .active(true)
                            .build()
            );
        }

        if (!operationCatalogRepository.existsByCode("OP-04")) {
            operationCatalogRepository.save(
                    OperationCatalog.builder()
                            .code("OP-04")
                            .name("Calculadora de sueño")
                            .baseCost(20)
                            .active(true)
                            .build()
            );
        }
    }

    private void initializeExchangeRate() {
        if (exchangeRateRepository.count() == 0) {
            exchangeRateRepository.save(
                    ExchangeRate.builder()
                            .copPerUsd(new BigDecimal("4000.00"))
                            .build()
            );
        }
    }

    private void initializeAdminUser() {
        if (!userRepository.existsByEmail("admin@genesis.com")) {
            userRepository.save(
                    User.builder()
                            .fullName("Administrador Genesis")
                            .email("admin@genesis.com")
                            .password(passwordEncoder.encode("Admin123*"))
                            .role(Role.ADMIN)
                            .active(true)
                            .tokenBalance(0)
                            .build()
            );
        }
    }
}