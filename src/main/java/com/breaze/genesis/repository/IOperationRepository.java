package com.breaze.genesis.repository;

import com.breaze.genesis.entity.OperationCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IOperationRepository extends JpaRepository<OperationCatalog, Long> {

    Optional<OperationCatalog> findByCode(String code);

    boolean existsByCode(String code);

    List<OperationCatalog> findByActiveTrue();
}