package com.example.spring.repository;

import com.example.spring.entity.transaction.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {

    @Query(value = """
            SELECT JSON_OBJECT(
               'chargetime', DATE_FORMAT(created_at, '%Y%m%d%H%i%s'),
               'channel', channel,
               'serviceid', product_id,
               'msisdn', user_id,
               'transactionid', transaction_id
            )
            FROM payment_transaction
            WHERE DATE(created_at) >= "2025-02-15" and DATE(created_at) <= "2025-03-08"
            LIMIT 10
            """, nativeQuery = true)
    List<String> getTransaction();
}
