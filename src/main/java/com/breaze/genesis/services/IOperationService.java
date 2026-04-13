package com.breaze.genesis.services;

import com.breaze.genesis.dto.operations.requests.BMIRequest;
import com.breaze.genesis.dto.operations.requests.UpdateOperationStatusRequest;
import com.breaze.genesis.dto.operations.requests.CreditExecutionRequest;
import com.breaze.genesis.dto.operations.requests.CurrencyConverterRequest;
import com.breaze.genesis.dto.operations.requests.SleepRequest;
import com.breaze.genesis.dto.operations.responses.BMIResponse;
import com.breaze.genesis.dto.operations.responses.CreditExecutionResponse;
import com.breaze.genesis.dto.operations.responses.CurrencyConverterResponse;
import com.breaze.genesis.dto.operations.responses.OperationCatalogResponse;
import com.breaze.genesis.dto.operations.requests.ExchangeRateRequest;
import com.breaze.genesis.dto.operations.responses.ExchangeRateResponse;
import com.breaze.genesis.dto.operations.responses.SleepResponse;
import com.breaze.genesis.entity.User;

import java.util.List;

public interface IOperationService {

    List<OperationCatalogResponse> getAvailableOperations();

    OperationCatalogResponse updateOperationStatus(Long id, UpdateOperationStatusRequest request);

    CreditExecutionResponse calculateCredit(CreditExecutionRequest request, User user);

    CurrencyConverterResponse convertCurrency(CurrencyConverterRequest request, User user);

    BMIResponse calculateBMI(BMIRequest request, User user);

    SleepResponse calculateSleep(SleepRequest request, User user);

    ExchangeRateResponse getExchangeRate();

    ExchangeRateResponse updateExchangeRate(ExchangeRateRequest request);
}
