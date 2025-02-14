package com.aplavina.test_task_wallets.mapper;

import com.aplavina.test_task_wallets.dto.WalletDto;
import com.aplavina.test_task_wallets.model.wallet.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WalletMapper {
    WalletDto toDto(Wallet wallet);
}
