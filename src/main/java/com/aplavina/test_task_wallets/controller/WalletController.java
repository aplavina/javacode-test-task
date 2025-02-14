package com.aplavina.test_task_wallets.controller;

import com.aplavina.test_task_wallets.dto.ChangeWalletBalanceDto;
import com.aplavina.test_task_wallets.dto.WalletDto;
import com.aplavina.test_task_wallets.service.WalletService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wallets")
public class WalletController {
    private final WalletService walletService;

    @PostMapping
    public long changeWalletBalance(@RequestBody @Valid ChangeWalletBalanceDto changeWalletBalanceDto) {
        return walletService.changeWalletBalance(changeWalletBalanceDto);
    }

    @GetMapping("/{walletId}")
    public long getWalletBalance(@PathVariable @NotNull UUID walletId) {
        return walletService.getWalletBalance(walletId);
    }

    @GetMapping
    public List<WalletDto> getAllWallets() {
        return walletService.getAll();
    }
}
