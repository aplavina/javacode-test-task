package com.aplavina.test_task_wallets.validator;

import com.aplavina.test_task_wallets.exception.wallet.InsufficientBalanceException;
import com.aplavina.test_task_wallets.model.wallet.Wallet;
import org.springframework.stereotype.Component;

@Component
public class WalletOperationValidator {

    public void validateCanWithdraw(Wallet wallet, long amount) {
        if (wallet.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient amount");
        }
    }
}
