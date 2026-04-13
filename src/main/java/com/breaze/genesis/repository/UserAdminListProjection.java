package com.breaze.genesis.repository;

public interface UserAdminListProjection {

    Long getUserId();

    String getFullName();

    String getEmail();

    Boolean getActive();

    Integer getTokenBalance();

    Long getActivePlanId();

    String getActivePlanName();

    Integer getActivePlanTokenAmount();
}