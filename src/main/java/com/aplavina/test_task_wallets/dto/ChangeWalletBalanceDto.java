package com.aplavina.test_task_wallets.dto;

import com.aplavina.test_task_wallets.model.wallet.OperationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class ChangeWalletBalanceDto {
    @NotNull(message = "Wallet id must not be null")
    private UUID walletId;
    @NotNull(message = "Operation type must not be null")
    private OperationType operationType;
    @Positive(message = "Amount must be positive")
    private long amount;
}
