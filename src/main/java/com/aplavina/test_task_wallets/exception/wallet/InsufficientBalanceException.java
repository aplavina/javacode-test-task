package com.aplavina.test_task_wallets.exception.wallet;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
