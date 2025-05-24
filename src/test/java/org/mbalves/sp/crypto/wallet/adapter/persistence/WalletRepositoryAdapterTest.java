package org.mbalves.sp.crypto.wallet.adapter.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mbalves.sp.crypto.wallet.adapter.persistence.entity.AssetEntity;
import org.mbalves.sp.crypto.wallet.adapter.persistence.entity.TokenEntity;
import org.mbalves.sp.crypto.wallet.adapter.persistence.entity.WalletEntity;
import org.mbalves.sp.crypto.wallet.adapter.persistence.repository.WalletJpaRepository;
import org.mbalves.sp.crypto.wallet.domain.Asset;
import org.mbalves.sp.crypto.wallet.domain.Token;
import org.mbalves.sp.crypto.wallet.domain.Wallet;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletRepositoryAdapterTest {

    @Mock
    private WalletJpaRepository walletJpaRepository;

    @InjectMocks
    private WalletRepositoryAdapter walletRepositoryAdapter;

    private WalletEntity walletEntity;
    private Wallet wallet;
    private final Long walletId = 1L;
    private final String email = "test@example.com";

    @BeforeEach
    void setUp() {
        // Setup wallet entity
        walletEntity = new WalletEntity();
        walletEntity.setId(walletId);
        walletEntity.setEmail(email);
        walletEntity.setAssets(new ArrayList<>());

        // Add an asset to the wallet entity
        AssetEntity assetEntity = new AssetEntity();
        assetEntity.setId(1L);
        assetEntity.setQuantity(BigDecimal.valueOf(0.5));

        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setId("bitcoin");
        tokenEntity.setSymbol("BTC");
        tokenEntity.setPrice(BigDecimal.valueOf(50000.0));
        
        assetEntity.setToken(tokenEntity);
        assetEntity.setWallet(walletEntity);
        
        walletEntity.getAssets().add(assetEntity);

        // Setup domain objects
        wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setEmail(email);

        Token token = new Token();
        token.setId("ethereum");
        token.setSymbol("ETH");
        token.setPrice(BigDecimal.valueOf(3000.0));

        Asset asset = new Asset();
        asset.setId(2L);
        asset.setToken(token);
        asset.setQuantity(BigDecimal.valueOf(1.0));

    }

    @Test
    void save_ShouldConvertDomainToEntityAndSave() {
        // Arrange
        when(walletJpaRepository.save(any(WalletEntity.class))).thenReturn(walletEntity);
        ArgumentCaptor<WalletEntity> captor = ArgumentCaptor.forClass(WalletEntity.class);

        // Act
        walletRepositoryAdapter.save(wallet);

        // Assert
        verify(walletJpaRepository).save(captor.capture());
        WalletEntity capturedEntity = captor.getValue();

        assertNotNull(capturedEntity);
        assertEquals(wallet.getId(), capturedEntity.getId());
        assertEquals(wallet.getEmail(), capturedEntity.getEmail());
        assertEquals(0, capturedEntity.getAssets().size());
    }

    @Test
    void findByEmail_WhenWalletExists_ShouldReturnWallet() {
        // Arrange
        when(walletJpaRepository.findByEmail(email)).thenReturn(Optional.of(walletEntity));

        // Act
        Optional<Wallet> result = walletRepositoryAdapter.findByEmail(email);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(walletId, result.get().getId());
        assertEquals(email, result.get().getEmail());
        assertEquals(1, result.get().getAssets().size());
        
        verify(walletJpaRepository).findByEmail(email);
    }

    @Test
    void findByEmail_WhenWalletDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(walletJpaRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        Optional<Wallet> result = walletRepositoryAdapter.findByEmail(email);

        // Assert
        assertFalse(result.isPresent());
        verify(walletJpaRepository).findByEmail(email);
    }

    @Test
    void findById_WhenWalletExists_ShouldReturnWallet() {
        // Arrange
        when(walletJpaRepository.findById(walletId)).thenReturn(Optional.of(walletEntity));

        // Act
        Optional<Wallet> result = walletRepositoryAdapter.findById(walletId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(walletId, result.get().getId());
        assertEquals(email, result.get().getEmail());
        assertEquals(1, result.get().getAssets().size());
        
        verify(walletJpaRepository).findById(walletId);
    }

    @Test
    void findById_WhenWalletDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(walletJpaRepository.findById(walletId)).thenReturn(Optional.empty());

        // Act
        Optional<Wallet> result = walletRepositoryAdapter.findById(walletId);

        // Assert
        assertFalse(result.isPresent());
        verify(walletJpaRepository).findById(walletId);
    }

    @Test
    void findAll_ShouldReturnAllWallets() {
        // Arrange
        List<WalletEntity> walletEntities = List.of(walletEntity);
        when(walletJpaRepository.findAll()).thenReturn(walletEntities);

        // Act
        List<Wallet> result = walletRepositoryAdapter.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(walletId, result.getFirst().getId());
        assertEquals(email, result.getFirst().getEmail());
        assertEquals(1, result.getFirst().getAssets().size());
        
        verify(walletJpaRepository).findAll();
    }
}
