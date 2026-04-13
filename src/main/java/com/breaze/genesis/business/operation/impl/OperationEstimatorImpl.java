package com.breaze.genesis.business.operation.impl;

import org.springframework.stereotype.Service;

import com.breaze.genesis.business.operation.IOperationEstimator;

@Service
public class OperationEstimatorImpl implements IOperationEstimator {

    @Override
    public int estimateCost(int baseCost, Object request, Object response) {
        
        int reqLen = getPayloadLength(request);
        int resLen = getPayloadLength(response);

        // formula = costo_base + floor(req_len/4) + floor(res_len/4)
        return baseCost + (reqLen / 4) + (resLen / 4);
    }

    private int getPayloadLength(Object obj) {
        if (obj == null) return 0;
        return String.valueOf(obj).length();
    }
}
