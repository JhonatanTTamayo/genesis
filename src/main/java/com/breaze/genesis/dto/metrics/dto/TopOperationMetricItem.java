package com.breaze.genesis.dto.metrics.dto;

public class TopOperationMetricItem {

    private String operationCode;
    private String operationName;
    private Integer executions;

    public TopOperationMetricItem() {
    }

    public String getOperationCode() {
        return operationCode;
    }

    public void setOperationCode(String operationCode) {
        this.operationCode = operationCode;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public Integer getExecutions() {
        return executions;
    }

    public void setExecutions(Integer executions) {
        this.executions = executions;
    }
}
