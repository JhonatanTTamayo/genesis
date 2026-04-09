package com.breaze.genesis.repository;

import com.breaze.genesis.entity.Transaction;
import com.breaze.genesis.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByUser(User user, Pageable pageable);
}