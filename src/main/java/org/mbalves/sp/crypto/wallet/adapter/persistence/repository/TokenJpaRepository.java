package org.mbalves.sp.crypto.wallet.adapter.persistence.repository;

import org.mbalves.sp.crypto.wallet.adapter.persistence.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenJpaRepository extends JpaRepository<TokenEntity, String> {
    Optional<TokenEntity> findBySymbol(String symbol);
}
