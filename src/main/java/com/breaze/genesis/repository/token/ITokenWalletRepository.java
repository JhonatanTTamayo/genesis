package com.breaze.genesis.repository.token;

import com.breaze.genesis.entity.tokens.TokenWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface ITokenWalletRepository extends JpaRepository<TokenWallet, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<TokenWallet> findByUserId(Long userId);
}
