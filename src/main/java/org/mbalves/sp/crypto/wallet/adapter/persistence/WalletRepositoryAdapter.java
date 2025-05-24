package org.mbalves.sp.crypto.wallet.adapter.persistence;

import lombok.RequiredArgsConstructor;
import org.mbalves.sp.crypto.wallet.adapter.persistence.entity.AssetEntity;
import org.mbalves.sp.crypto.wallet.adapter.persistence.entity.TokenEntity;
import org.mbalves.sp.crypto.wallet.adapter.persistence.entity.WalletEntity;
import org.mbalves.sp.crypto.wallet.adapter.persistence.repository.WalletJpaRepository;
import org.mbalves.sp.crypto.wallet.application.port.out.WalletRepositoryPort;
import org.mbalves.sp.crypto.wallet.domain.Asset;
import org.mbalves.sp.crypto.wallet.domain.Token;
import org.mbalves.sp.crypto.wallet.domain.Wallet;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter for wallet persistence operations.
 * Implements the {@link WalletRepositoryPort} interface.
 * Handles conversion between domain and entity models for wallets and their assets.
 * Delegates database operations to the {@link WalletJpaRepository}.
 *
 * @author Marcelo Alves
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class WalletRepositoryAdapter implements WalletRepositoryPort {
    private final WalletJpaRepository walletJpaRepository;

    @Override
    public Wallet save(Wallet wallet) {
        WalletEntity entity = toEntity(wallet);
        entity = walletJpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<Wallet> findByEmail(String email) {
        return walletJpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<Wallet> findById(Long id) {
        return walletJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Wallet> findAll() {
        return walletJpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        walletJpaRepository.deleteById(id);
    }

    private WalletEntity toEntity(Wallet wallet) {
        WalletEntity entity = new WalletEntity();
        entity.setId(wallet.getId());
        entity.setEmail(wallet.getEmail());
        entity.setAssets(wallet.getAssets().stream()
                .map(asset -> {
                    AssetEntity assetEntity = new AssetEntity();
                    assetEntity.setWallet(entity);
                    assetEntity.setId(asset.getId());
                    assetEntity.setQuantity(asset.getQuantity());
                    TokenEntity tokenEntity = new TokenEntity();
                    tokenEntity.setId(asset.getToken().getId());
                    tokenEntity.setSymbol(asset.getToken().getSymbol());
                    tokenEntity.setPrice(asset.getToken().getPrice());
                    assetEntity.setToken(tokenEntity);
                    return assetEntity;
                })
                .collect(Collectors.toList()));
        return entity;
    }

    private Wallet toDomain(WalletEntity entity) {
        Wallet wallet = new Wallet();
        wallet.setId(entity.getId());
        wallet.setEmail(entity.getEmail());
        wallet.setAssets(entity.getAssets().stream()
                .map(assetEntity -> {
                    Asset asset = new Asset();
                    asset.setId(assetEntity.getId());
                    asset.setQuantity(assetEntity.getQuantity());
                    Token token = new Token();
                    token.setId(assetEntity.getToken().getId());
                    token.setSymbol(assetEntity.getToken().getSymbol());
                    token.setPrice(assetEntity.getToken().getPrice());
                    asset.setToken(token);
                    return asset;
                })
                .collect(Collectors.toList()));
        return wallet;
    }
}
