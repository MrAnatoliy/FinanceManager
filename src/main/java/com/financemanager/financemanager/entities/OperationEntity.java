package com.financemanager.financemanager.entities;

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
    private Long operation_id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "wallet_id")
    private WalletEntity wallet;

    private int operation_value;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationType operation_type;

    @OneToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity operation_category;
}

