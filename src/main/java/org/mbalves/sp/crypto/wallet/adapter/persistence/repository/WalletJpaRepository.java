package org.mbalves.sp.crypto.wallet.adapter.persistence.repository;

import org.mbalves.sp.crypto.wallet.adapter.persistence.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletJpaRepository extends JpaRepository<WalletEntity, Long> {
    Optional<WalletEntity> findByEmail(String email);
}
