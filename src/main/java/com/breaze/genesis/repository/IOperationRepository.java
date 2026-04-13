package com.breaze.genesis.repository;

import com.breaze.genesis.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IOperationRepository extends JpaRepository<Operation, Long> {

    Optional<Operation> findByCode(String code);

    boolean existsByCode(String code);

    List<Operation> findByActiveTrue();
}