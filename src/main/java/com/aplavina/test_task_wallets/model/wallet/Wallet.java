package com.aplavina.test_task_wallets.model.wallet;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "wallet")
public class Wallet {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "balance")
    private long balance;
}
