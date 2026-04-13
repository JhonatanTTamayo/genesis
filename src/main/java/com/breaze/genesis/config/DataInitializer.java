package com.breaze.genesis.config;

import com.breaze.genesis.entity.ExchangeRate;
import com.breaze.genesis.entity.OperationCatalog;
import com.breaze.genesis.entity.plan.Plan;
import com.breaze.genesis.entity.plan.PlanVersion;
import com.breaze.genesis.entity.Role;
import com.breaze.genesis.entity.tokens.TokenWallet;
import com.breaze.genesis.entity.User;
import com.breaze.genesis.repository.*;
import com.breaze.genesis.repository.plan.IPlanRepository;
import com.breaze.genesis.repository.plan.IPlanVersionRepository;
import com.breaze.genesis.repository.token.ITokenWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final int FREE_TOKEN_LIMIT = 200;
    private static final int PRO_TOKEN_LIMIT = 1000;
    private static final int ENTERPRISE_TOKEN_LIMIT = 5000;

    private final IPlanRepository planRepository;
    private final IPlanVersionRepository planVersionRepository;
    private final IOperationRepository operationRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final IUserRepository userRepository;
    private final ITokenWalletRepository tokenWalletRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initializePlans();
        initializePlanVersions();
        initializeOperations();
        initializeExchangeRate();
        initializeAdminUser();
    }

    private void initializePlans() {
        if (!planRepository.existsByName("Free")) {
            planRepository.save(
                    Plan.builder()
                            .name("Free")
                            .description("Plan base gratuito")
                            .build()
            );
        }

        if (!planRepository.existsByName("Pro")) {
            planRepository.save(
                    Plan.builder()
                            .name("Pro")
                            .description("Plan profesional")
                            .build()
            );
        }

        if (!planRepository.existsByName("Enterprise")) {
            planRepository.save(
                    Plan.builder()
                            .name("Enterprise")
                            .description("Plan empresarial")
                            .build()
            );
        }
    }

    private void initializeOperations() {
        if (!operationRepository.existsByCode("OP-01")) {
            operationRepository.save(
                    OperationCatalog.builder()
                            .code("OP-01")
                            .name("¿Cuánto me cuesta ese crédito?")
                            .description("Calcula la tabla de amortización y las cuotas mensuales para un crédito simulado.")
                            .baseCost(50)
                            .active(true)
                            .build()
            );
        }

        if (!operationRepository.existsByCode("OP-02")) {
            operationRepository.save(
                    OperationCatalog.builder()
                            .code("OP-02")
                            .name("Conversor COP ↔ USD")
                            .description("Convierte dinero entre Dólares y Pesos Colombianos usando la TRM actual.")
                            .baseCost(20)
                            .active(true)
                            .build()
            );
        }

        if (!operationRepository.existsByCode("OP-03")) {
            operationRepository.save(
                    OperationCatalog.builder()
                            .code("OP-03")
                            .name("Calculadora de IMC")
                            .description("Calcula el Índice de Masa Corporal (IMC) y clasifica tu estado de peso.")
                            .baseCost(15)
                            .active(true)
                            .build()
            );
        }

        if (!operationRepository.existsByCode("OP-04")) {
            operationRepository.save(
                    OperationCatalog.builder()
                            .code("OP-04")
                            .name("Calculadora de sueño")
                            .description("Determina los ciclos de sueño sugeridos con base en tu hora de descanso o despertar.")
                            .baseCost(20)
                            .active(true)
                            .build()
            );
        }
    }

    private void initializePlanVersions() {
        planRepository.findAll().forEach(plan -> {
            if (planVersionRepository.countByPlanId(plan.getId()) == 0) {
                planVersionRepository.save(
                        PlanVersion.builder()
                                .plan(plan)
                                .tokenLimit(resolveTokenLimit(plan.getName()))
                                .validFrom(LocalDateTime.now())
                                .build()
                );
            }
        });
    }

    private Integer resolveTokenLimit(String planName) {
        return switch (planName.toUpperCase()) {
            case "FREE" -> FREE_TOKEN_LIMIT;
            case "PRO" -> PRO_TOKEN_LIMIT;
            case "ENTERPRISE" -> ENTERPRISE_TOKEN_LIMIT;
            default -> throw new IllegalArgumentException("Plan sin token limit configurado: " + planName);
        };
    }

    private void initializeExchangeRate() {
        if (exchangeRateRepository.count() == 0) {
            exchangeRateRepository.save(
                    ExchangeRate.builder()
                            .copPerUsd(4000.00D)
                            .updatedAt(LocalDateTime.now())
                            .build()
            );
        }
    }

    private void initializeAdminUser() {
        if (!userRepository.existsByEmail("admin@genesis.com")) {
            User admin = userRepository.save(
                    User.builder()
                            .fullName("Administrador Genesis")
                            .email("admin@genesis.com")
                            .password(passwordEncoder.encode("Admin123*"))
                            .role(Role.ADMIN)
                            .active(true)
                            .build()
            );

            tokenWalletRepository.save(
                    TokenWallet.builder()
                            .user(admin)
                            .tokensAvailable(0)
                            .updatedAt(LocalDateTime.now())
                            .build()
            );
        }
    }
}