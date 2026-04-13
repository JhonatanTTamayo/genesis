package com.breaze.genesis.business.operation;

public interface IOperationEstimator {
    
    /**
     * Calcula el costo estimado en tokens basado en el costo base de la operación,
     * la entrada y salida de datos.
     */
    int estimateCost(int baseCost, Object request, Object response);
}
