package com.financemanager.financemanager.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wallet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class WalletEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wallet_id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private int balance;
}
