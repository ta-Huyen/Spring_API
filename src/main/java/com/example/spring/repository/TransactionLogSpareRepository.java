package com.example.spring.repository;

import com.example.spring.entity.transaction.TransactionLogSpare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TransactionLogSpareRepository extends JpaRepository<TransactionLogSpare, Long> {
}
