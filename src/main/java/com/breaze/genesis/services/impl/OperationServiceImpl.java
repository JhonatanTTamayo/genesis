package com.breaze.genesis.services.impl;

import com.breaze.genesis.business.operation.IOperationEstimator;
import com.breaze.genesis.business.token.TokenConsumptionStrategy;
import com.breaze.genesis.dto.operations.dto.SleepCalculatorMode;
import com.breaze.genesis.dto.operations.dto.BMICategory;
import com.breaze.genesis.dto.operations.dto.ConversionDirection;
import com.breaze.genesis.dto.operations.dto.Currency;
import com.breaze.genesis.dto.operations.requests.BMIRequest;
import com.breaze.genesis.dto.operations.requests.UpdateOperationStatusRequest;
import com.breaze.genesis.dto.operations.requests.CreditExecutionRequest;
import com.breaze.genesis.dto.operations.requests.CurrencyConverterRequest;
import com.breaze.genesis.dto.operations.requests.ExchangeRateRequest;
import com.breaze.genesis.dto.operations.requests.SleepRequest;
import com.breaze.genesis.dto.operations.responses.BMIResponse;
import com.breaze.genesis.dto.operations.responses.CreditExecutionResponse;
import com.breaze.genesis.dto.operations.responses.CreditExecutionResponse.AmortizationItem;
import com.breaze.genesis.dto.operations.responses.CurrencyConverterResponse;
import com.breaze.genesis.dto.operations.responses.ExchangeRateResponse;
import com.breaze.genesis.dto.operations.responses.OperationCatalogResponse;
import com.breaze.genesis.dto.operations.responses.SleepResponse;
import com.breaze.genesis.dto.operations.responses.SleepResponse.SleepOption;
import com.breaze.genesis.entity.ExchangeRate;
import com.breaze.genesis.entity.Operation;
import com.breaze.genesis.entity.User;
import com.breaze.genesis.entity.tokens.TokenTransaction;
import com.breaze.genesis.exceptions.BusinessException;
import com.breaze.genesis.repository.ExchangeRateRepository;
import com.breaze.genesis.repository.IOperationRepository;
import com.breaze.genesis.services.IOperationService;
import com.breaze.genesis.services.IOperationTransactionService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperationServiceImpl implements IOperationService {

    private final IOperationEstimator operationEstimator;
    private final TokenConsumptionStrategy tokenConsumptionStrategy;
    private final ExchangeRateRepository exchangeRateRepository;
    private final IOperationRepository operationCatalogRepository;
    private final IOperationTransactionService transactionService;

    private static final double MIN_WEIGHT_KG = 2.0;
    private static final double MAX_WEIGHT_KG = 500.0;
    private static final double MIN_HEIGHT_CM = 40.0;
    private static final double MAX_HEIGHT_CM = 300.0;

    private static final double MIN_INTEREST_RATE = 1.0;
    private static final double MAX_INTEREST_RATE = 100.0;
    private static final int MIN_INSTALLMENTS = 1;
    private static final int MAX_INSTALLMENTS = 360;

    private static final double MIN_CONVERT_AMOUNT = 0.01;

    private static final int MIN_MINUTES_TO_FALL_ASLEEP = 1;
    private static final int MAX_MINUTES_TO_FALL_ASLEEP = 120;

    private Operation getOperationCatalogAndValidate(String operationCode) {
        Operation catalog = operationCatalogRepository.findByCode(operationCode)
                .orElseThrow(() -> new BusinessException("Operation " + operationCode + " not found in catalog."));

        if (!Boolean.TRUE.equals(catalog.getActive())) {
            throw new BusinessException("Operation " + operationCode + " is not active.");
        }
        return catalog;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OperationCatalogResponse> getAvailableOperations() {
        return operationCatalogRepository.findByActiveTrue().stream()
                .map(catalog -> OperationCatalogResponse.builder()
                        .id(catalog.getId())
                        .code(catalog.getCode())
                        .name(catalog.getName())
                        .description(catalog.getDescription())
                        .baseCost(catalog.getBaseCost())
                        .active(catalog.getActive())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OperationCatalogResponse updateOperationStatus(Long id, UpdateOperationStatusRequest request) {
        Operation catalog = operationCatalogRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Operation " + id + " not found in catalog."));
        
        catalog.setActive(request.getActive());
        catalog = operationCatalogRepository.save(catalog);

        return OperationCatalogResponse.builder()
                .id(catalog.getId())
                .code(catalog.getCode())
                .name(catalog.getName())
                .description(catalog.getDescription())
                .baseCost(catalog.getBaseCost())
                .active(catalog.getActive())
                .build();
    }

    @Override
    @Transactional
    public CreditExecutionResponse calculateCredit(CreditExecutionRequest request, User user) {
        Operation catalog = getOperationCatalogAndValidate("OP-01");
        try {
            Double price = request.getAmount();
            Double monthlyInterestRate = request.getMonthlyRate() / 100.0;
            int installments = getMeses(request);
            Double monthlyQuote = calculateMonthlyQuote(price, monthlyInterestRate, installments);
            Double totalPaid = monthlyQuote * installments;
            Double totalInterest = totalPaid - price;

            List<AmortizationItem> amortizationSchedule = new ArrayList<>();
            Double remainingBalance = price;

            for (int currMonth = 1; currMonth <= installments; currMonth++) {
                Double interest = remainingBalance * monthlyInterestRate;
                Double principal = monthlyQuote - interest;
                remainingBalance = remainingBalance - principal;
                if(remainingBalance < 0) {
                    remainingBalance = 0.0;
                }

                amortizationSchedule.add(AmortizationItem.builder()
                        .month(currMonth)
                        .interestPaid(Math.round(interest * 100.0) / 100.0)
                        .principalPaid(Math.round(principal * 100.0) / 100.0)
                        .remainingBalance(Math.round(remainingBalance * 100.0) / 100.0)
                        .build());
            }

            CreditExecutionResponse response = CreditExecutionResponse.builder()
                    .monthlyPayment(Math.round(monthlyQuote * 100.0) / 100.0)
                    .totalPaid(Math.round(totalPaid * 100.0) / 100.0)
                    .totalInterest(Math.round(totalInterest * 100.0) / 100.0)
                    .amortizationSchedule(amortizationSchedule)
                    .build();

            int cost = operationEstimator.estimateCost(catalog.getBaseCost(), request, response);
            TokenTransaction tokenTransaction = tokenConsumptionStrategy.consumeTokens(user, cost);
            transactionService.logSuccessfulOperation(
                    user,
                    catalog,
                    catalog.getBaseCost(),
                    cost,
                    tokenTransaction
            );
            return response;
                } catch (BusinessException ex) {
            transactionService.logFailedOperation(
                    user,
                    catalog,
                    catalog.getBaseCost()
            );
            throw ex;
        }
    }

    private static @NonNull Integer getMeses(CreditExecutionRequest request) {
        Integer installments = request.getInstallments();

        if (request.getMonthlyRate() < MIN_INTEREST_RATE || request.getMonthlyRate() > MAX_INTEREST_RATE) {
            throw new BusinessException("The monthly interest rate must be between " + MIN_INTEREST_RATE + " and " + MAX_INTEREST_RATE + "%.");
        }
        if (installments < MIN_INSTALLMENTS || installments > MAX_INSTALLMENTS) {
            throw new BusinessException("The number of installments (Months) must be between " + MIN_INSTALLMENTS + " and " + MAX_INSTALLMENTS + ".");
        }
        return installments;
    }

    private Double calculateMonthlyQuote(Double price, Double rate, Integer installments) {
        double onePlusRate = 1.0 + rate;
        Double base = Math.pow(onePlusRate, installments);
        Double numerator = price * rate * base;
        Double denominator = base - 1.0;
        return numerator / denominator;
    }

    @Override
    @Transactional
    public CurrencyConverterResponse convertCurrency(CurrencyConverterRequest request, User user) {
        Operation catalog = getOperationCatalogAndValidate("OP-02");

        try {
            ExchangeRate trmOpt = exchangeRateRepository.findTopByOrderByUpdatedAtDesc()
                    .orElseThrow(() -> new BusinessException("No exchange rate configured in database."));

            Double trm = trmOpt.getCopPerUsd();
            Double monto = request.getAmount();

            if (monto < MIN_CONVERT_AMOUNT) {
                throw new BusinessException("The conversion amount must be greater than or equal to " + MIN_CONVERT_AMOUNT + ".");
            }

            Currency monedaOrigen = resolveSourceCurrency(request.getSourceCurrency());

            Double montoConvertido;
            ConversionDirection conversionDirection;

            if (monedaOrigen == Currency.USD) {
                montoConvertido = monto * trm;
                conversionDirection = ConversionDirection.USD_TO_COP;
            } else if (monedaOrigen == Currency.COP) {
                montoConvertido = monto / trm;
                conversionDirection = ConversionDirection.COP_TO_USD;
            } else {
                throw new BusinessException("Source currency must be COP or USD.");
            }

            CurrencyConverterResponse response = CurrencyConverterResponse.builder()
                    .convertedAmount(Math.round(montoConvertido * 100.0) / 100.0)
                    .conversionDirection(conversionDirection)
                    .appliedRate(Math.round(trm * 100.0) / 100.0)
                    .rateLastUpdatedAt(trmOpt.getUpdatedAt())
                    .build();

            int cost = operationEstimator.estimateCost(catalog.getBaseCost(), request, response);
            TokenTransaction tokenTransaction = tokenConsumptionStrategy.consumeTokens(user, cost);
            transactionService.logSuccessfulOperation(
                    user,
                    catalog,
                    catalog.getBaseCost(),
                    cost,
                    tokenTransaction
            );
            return response;
                } catch (BusinessException ex) {
            transactionService.logFailedOperation(
                    user,
                    catalog,
                    catalog.getBaseCost()
            );
            throw ex;
        }
    }

    @Override
    @Transactional
    public BMIResponse calculateBMI(BMIRequest request, User user) {
        Operation catalog = getOperationCatalogAndValidate("OP-03");

        try {
            if (request.getWeightKg() < MIN_WEIGHT_KG || request.getWeightKg() > MAX_WEIGHT_KG) {
                throw new BusinessException("Weight must be between " + MIN_WEIGHT_KG + " and " + MAX_WEIGHT_KG + " kg.");
            }
            if (request.getHeightCm() < MIN_HEIGHT_CM || request.getHeightCm() > MAX_HEIGHT_CM) {
                throw new BusinessException("Height must be between " + MIN_HEIGHT_CM + " and " + MAX_HEIGHT_CM + " cm.");
            }

            double pesoKg = request.getWeightKg();
            double alturaM = request.getHeightCm() / 100.0;

            double alturaCuadrado = alturaM * alturaM;
            double imc = Math.round((pesoKg / alturaCuadrado) * 100.0) / 100.0;

            BMICategory categoria;
            double weightDifference = 0.0;

            double pesoMinimoNormal = Math.round((18.5 * alturaCuadrado) * 100.0) / 100.0;
            double pesoMaximoNormal = Math.round((24.9 * alturaCuadrado) * 100.0) / 100.0;

            if (imc < 18.5) {
                categoria = BMICategory.UNDERWEIGHT;
                weightDifference = Math.round((pesoMinimoNormal - pesoKg) * 100.0) / 100.0;
            } else if (imc >= 18.5 && imc <= 24.9) {
                categoria = BMICategory.NORMAL;
            } else if (imc >= 25.0 && imc <= 29.9) {
                categoria = BMICategory.OVERWEIGHT;
                weightDifference = Math.round((pesoKg - pesoMaximoNormal) * 100.0) / 100.0;
            } else {
                categoria = BMICategory.OBESE;
                weightDifference = Math.round((pesoKg - pesoMaximoNormal) * 100.0) / 100.0;
            }

            BMIResponse response = BMIResponse.builder()
                    .bmi(imc)
                    .category(categoria)
                    .minimumHealthyWeight(pesoMinimoNormal)
                    .maximumHealthyWeight(pesoMaximoNormal)
                    .weightDifferenceFromRange(weightDifference)
                    .build();

            int cost = operationEstimator.estimateCost(catalog.getBaseCost(), request, response);
            TokenTransaction tokenTransaction = tokenConsumptionStrategy.consumeTokens(user, cost);
            transactionService.logSuccessfulOperation(
                    user,
                    catalog,
                    catalog.getBaseCost(),
                    cost,
                    tokenTransaction
            );
            return response;
                } catch (BusinessException ex) {
            transactionService.logFailedOperation(
                    user,
                    catalog,
                    catalog.getBaseCost()
            );
            throw ex;
        }
    }

    @Override
    @Transactional
    public SleepResponse calculateSleep(SleepRequest request, User user) {
        Operation catalog = getOperationCatalogAndValidate("OP-04");

        try {
            LocalTime horaBase = request.getTime();
            int minDormir = request.getSleepDurationMinutes();

            if (minDormir < MIN_MINUTES_TO_FALL_ASLEEP || minDormir > MAX_MINUTES_TO_FALL_ASLEEP) {
                throw new BusinessException("The minutes entered to fall asleep must be between " + MIN_MINUTES_TO_FALL_ASLEEP + " and " + MAX_MINUTES_TO_FALL_ASLEEP + " minutes.");
            }

            SleepCalculatorMode mode = resolveSleepMode(request.getMode());

            SleepOption recomendado;
            SleepOption aceptable;
            SleepOption minimo;

            if (mode == SleepCalculatorMode.WAKE_UP_TIME) {
                recomendado = SleepOption.builder().calculatedTime(horaBase.minusMinutes(540 + minDormir).toString())
                    .totalSleepHours(9.0).quality("Ideal").build();
                aceptable = SleepOption.builder().calculatedTime(horaBase.minusMinutes(450 + minDormir).toString())
                    .totalSleepHours(7.5).quality("Acceptable").build();
                minimo = SleepOption.builder().calculatedTime(horaBase.minusMinutes(360 + minDormir).toString())
                    .totalSleepHours(6.0).quality("Minimum").build();
            } else {
                recomendado = SleepOption.builder().calculatedTime(horaBase.plusMinutes(540 + minDormir).toString())
                    .totalSleepHours(9.0).quality("Ideal").build();
                aceptable = SleepOption.builder().calculatedTime(horaBase.plusMinutes(450 + minDormir).toString())
                    .totalSleepHours(7.5).quality("Acceptable").build();
                minimo = SleepOption.builder().calculatedTime(horaBase.plusMinutes(360 + minDormir).toString())
                    .totalSleepHours(6.0).quality("Minimum").build();
            }

            List<SleepOption> options = new ArrayList<>();
            options.add(recomendado);
            options.add(aceptable);
            options.add(minimo);

            SleepResponse response = SleepResponse.builder()
                    .options(options)
                    .build();

            int cost = operationEstimator.estimateCost(catalog.getBaseCost(), request, response);
            TokenTransaction tokenTransaction = tokenConsumptionStrategy.consumeTokens(user, cost);
            transactionService.logSuccessfulOperation(
                    user,
                    catalog,
                    catalog.getBaseCost(),
                    cost,
                    tokenTransaction
            );
            return response;
                } catch (BusinessException ex) {
            transactionService.logFailedOperation(
                    user,
                    catalog,
                    catalog.getBaseCost()
            );
            throw ex;
        }
    }

    @Override
    public ExchangeRateResponse getExchangeRate() {
        ExchangeRate trmOpt = exchangeRateRepository.findTopByOrderByUpdatedAtDesc()
                .orElseThrow(() -> new BusinessException("No exchange rate available"));
        return mapToExchangeRateResponse(trmOpt);
    }

    @Override
    @Transactional
    public ExchangeRateResponse updateExchangeRate(ExchangeRateRequest request) {
        ExchangeRate newRate = new ExchangeRate();
        newRate.setCopPerUsd(request.getCopPerUsd());
        ExchangeRate savedRate = exchangeRateRepository.save(newRate);
        return mapToExchangeRateResponse(savedRate);
    }

    private ExchangeRateResponse mapToExchangeRateResponse(ExchangeRate rate) {
        return ExchangeRateResponse.builder()
                .id(rate.getId())
                .copPerUsd(rate.getCopPerUsd())
                .updatedAt(rate.getUpdatedAt())
                .build();
    }

    private Currency resolveSourceCurrency(String sourceCurrency) {
        if (sourceCurrency == null || sourceCurrency.isBlank()) {
            throw new BusinessException("Invalid sourceCurrency. Allowed values are: " + java.util.Arrays.toString(Currency.values()));
        }

        String normalized = sourceCurrency.trim();
        for (Currency currency : Currency.values()) {
            if (currency.name().equalsIgnoreCase(normalized)) {
                return currency;
            }
        }

        throw new BusinessException("Invalid sourceCurrency. Allowed values are: " + java.util.Arrays.toString(Currency.values()));
    }

    private SleepCalculatorMode resolveSleepMode(String mode) {
        if (mode == null || mode.isBlank()) {
            throw new BusinessException("Invalid mode. Allowed values are: " + java.util.Arrays.toString(SleepCalculatorMode.values()));
        }

        String normalized = mode.trim();
        for (SleepCalculatorMode sleepMode : SleepCalculatorMode.values()) {
            if (sleepMode.name().equalsIgnoreCase(normalized)) {
                return sleepMode;
            }
        }

        throw new BusinessException("Invalid mode. Allowed values are: " + java.util.Arrays.toString(SleepCalculatorMode.values()));
    }

}
