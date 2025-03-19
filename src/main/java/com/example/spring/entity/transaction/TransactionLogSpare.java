package com.example.spring.entity.transaction;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "transaction_log_spare")
public class TransactionLogSpare implements Serializable {
    private static final long serialVersionUID = -297553281792804396L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "mode")
    private String mode;
    @Column(name = "amount")
    private int amount;
    @Column(name = "chargetime")
    private String chargeTime;
    @Column(name = "channel")
    private String channel;
    @Column(name = "service_id")
    private String serviceId;
    @Column(name = "msisdn")
    private String msisdn;
    @Column(name = "params")
    private String params;
    @Column(name = "type")
    private String type;
    @Column(name = "command")
    private String command;
    @Column(name = "transaction_id")
    private String transactionId;
}
