package com.aplavina.test_task_wallets.service;

import com.aplavina.test_task_wallets.dto.ChangeWalletBalanceDto;
import com.aplavina.test_task_wallets.dto.WalletDto;
import com.aplavina.test_task_wallets.mapper.WalletMapper;
import com.aplavina.test_task_wallets.model.wallet.OperationType;
import com.aplavina.test_task_wallets.model.wallet.Wallet;
import com.aplavina.test_task_wallets.repository.WalletRepository;
import com.aplavina.test_task_wallets.validator.WalletOperationValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final WalletOperationValidator walletOperationValidator;
    private final WalletMapper walletMapper;

    @Transactional
    public long changeWalletBalance(@Valid ChangeWalletBalanceDto changeWalletBalanceDto) {
        Wallet wallet = walletRepository.findByIdForUpdate(changeWalletBalanceDto.getWalletId());
        if (wallet == null) {
            throw new EntityNotFoundException(
                    "Wallet with id " + changeWalletBalanceDto.getWalletId() + " doesn't exist"
            );
        }
        if (changeWalletBalanceDto.getOperationType() == OperationType.DEPOSIT) {
            wallet.setBalance(wallet.getBalance() + changeWalletBalanceDto.getAmount());
        } else {
            walletOperationValidator.validateCanWithdraw(wallet, changeWalletBalanceDto.getAmount());
            wallet.setBalance(wallet.getBalance() - changeWalletBalanceDto.getAmount());
        }
        return wallet.getBalance();
    }

    public long getWalletBalance(UUID walletId) {
        return walletRepository
                .getBalanceByWalletId(walletId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Wallet with id " + walletId + " doesn't exist"
                ));
    }

    public List<WalletDto> getAll() {
        List<WalletDto> res = new ArrayList<>();
        walletRepository.findAll().forEach(wallet -> res.add(walletMapper.toDto(wallet)));
        return res;
    }
}
