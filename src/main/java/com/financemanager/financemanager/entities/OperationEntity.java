package com.financemanager.financemanager.entities;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import com.financemanager.financemanager.enums.OperationType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "operation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OperationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long operationId;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "walletId")
    private WalletEntity wallet;

    private int operationValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationType operationType;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private CategoryEntity operationCategory;
}

