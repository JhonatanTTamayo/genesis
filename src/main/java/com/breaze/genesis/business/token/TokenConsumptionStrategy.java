package com.breaze.genesis.business.token;

import com.breaze.genesis.entity.User;
import com.breaze.genesis.entity.tokens.TokenTransaction;

/**
 * Strategy interface to implement different behaviors when tokens are consumed (used).
 */
public interface TokenConsumptionStrategy {
    
    /**
     * Consumes a specific amount of tokens for the given user.
     *
     * @param user The user consuming the tokens.
     * @param amount The total amount of tokens requested to consume.
     * @throws IllegalStateException or BusinessException if the user doesn't have enough balance.
     */
    TokenTransaction consumeTokens(User user, int amount);
}
