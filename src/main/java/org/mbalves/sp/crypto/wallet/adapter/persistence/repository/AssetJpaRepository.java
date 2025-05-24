package org.mbalves.sp.crypto.wallet.adapter.persistence.repository;

import org.mbalves.sp.crypto.wallet.adapter.persistence.entity.AssetEntity;
import org.mbalves.sp.crypto.wallet.adapter.persistence.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AssetJpaRepository extends JpaRepository<AssetEntity, Long> {
    @Query("SELECT a FROM AssetEntity a WHERE a.wallet.id = :walletId AND a.token.symbol = :symbol")
    Optional<AssetEntity> findByWalletIdAndTokenSymbol(@Param("walletId") Long walletId, @Param("symbol") String symbol);
}
