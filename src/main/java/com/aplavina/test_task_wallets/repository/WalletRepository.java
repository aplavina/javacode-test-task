package com.aplavina.test_task_wallets.repository;

import com.aplavina.test_task_wallets.model.wallet.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends CrudRepository<Wallet, UUID> {
    @Query(nativeQuery = true, value = """
            SELECT amount FROM wallet WHERE id = :walletId
            """)
    Optional<Long> getBalanceByWalletId(UUID walletId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.id = :walletId")
    Wallet findByIdForUpdate(@Param("walletId") UUID walletId);
}
