package org.mbalves.sp.crypto.wallet.application.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mbalves.sp.crypto.wallet.application.port.out.PriceProviderPort;
import org.mbalves.sp.crypto.wallet.application.port.out.TokenRepositoryPort;
import org.mbalves.sp.crypto.wallet.application.port.out.WalletRepositoryPort;
import org.mbalves.sp.crypto.wallet.domain.Asset;
import org.mbalves.sp.crypto.wallet.domain.Token;
import org.mbalves.sp.crypto.wallet.domain.Wallet;
import org.mbalves.sp.crypto.wallet.domain.exception.InsufficientFundsException;
import org.mbalves.sp.crypto.wallet.domain.exception.InvalidTokenException;
import org.mbalves.sp.crypto.wallet.domain.exception.WalletNotFoundException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddAssetUseCaseImplTest {

    @Mock
    private WalletRepositoryPort walletRepository;

    @Mock
    private TokenRepositoryPort tokenRepository;

    @Mock
    private PriceProviderPort priceProvider;

    @InjectMocks
    private AddAssetUseCaseImpl addAssetUseCase;

    private Long walletId;
    private String symbol;
    private Double quantity;
    private Wallet wallet;
    private Token token;

    @BeforeEach
    void setUp() {
        walletId = 1L;
        symbol = "btc";
        quantity = 0.5;
        double price = 50000.0;

        wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setEmail("test@example.com");

        token = new Token();
        token.setId("bitcoin");
        token.setSymbol("BTC");
        token.setPrice(BigDecimal.valueOf(price));
        token.setLastUpdated(Instant.now());
    }

    @Test
    void addAsset_WhenWalletExistsAndTokenIsValid_ShouldAddAssetAndReturnWallet() {
        // Arrange
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(priceProvider.getToken("BTC")).thenReturn(token);
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(tokenRepository.save(any(Token.class))).thenReturn(token);

        ArgumentCaptor<Wallet> captor = ArgumentCaptor.forClass(Wallet.class);

        // Act
        addAssetUseCase.addAsset(walletId, symbol, quantity);

        // Assert
        verify(priceProvider).getToken("BTC");
        verify(tokenRepository).save(token);
        verify(walletRepository).save(captor.capture());
        Wallet captured = captor.getValue();

        assertNotNull(captured);
        assertEquals(1, captured.getAssets().size());
        Asset addedAsset = captured.getAssets().getFirst();
        assertEquals("bitcoin", addedAsset.getToken().getId());
        assertEquals("BTC", addedAsset.getToken().getSymbol());
        assertEquals(token.getPrice(), addedAsset.getToken().getPrice());
        assertEquals(quantity, addedAsset.getQuantity().doubleValue());

    }


    @Test
    void addAsset_WhenTokenIsInvalid_ShouldThrowException() {
        // Arrange
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(priceProvider.getToken("BTC")).thenReturn(null);

        // Act & Assert
        InvalidTokenException exception = assertThrows(
            InvalidTokenException.class,
            () -> addAssetUseCase.addAsset(walletId, symbol, quantity)
        );

        assertEquals("Invalid token or price not found for symbol: BTC", exception.getMessage());

        verify(priceProvider).getToken("BTC");
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void addAsset_WhenWalletDoesNotExist_ShouldThrowWalletNotFoundException() {
        // Arrange
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        // Act & Assert
        WalletNotFoundException exception = assertThrows(
                WalletNotFoundException.class,
                () -> addAssetUseCase.addAsset(walletId, symbol, quantity)
        );
        assertEquals("Wallet not found with ID: 1", exception.getMessage());
        verify(walletRepository).findById(walletId);
        verifyNoMoreInteractions(walletRepository, priceProvider);
    }

    @Test
    void addAsset_WhenAssetAlreadyExists_ShouldUpdateQuantity() {
        // Arrange
        Asset existingAsset = new Asset();
        existingAsset.setId(10L);
        existingAsset.setToken(token);
        existingAsset.setQuantity(BigDecimal.valueOf(1.0));
        wallet.addAsset(existingAsset);
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(priceProvider.getToken("BTC")).thenReturn(token);
        when(tokenRepository.save(any(Token.class))).thenReturn(token);
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        ArgumentCaptor<Wallet> captor = ArgumentCaptor.forClass(Wallet.class);

        // Act
        addAssetUseCase.addAsset(walletId, "BTC", 2.0);

        // Assert
        verify(priceProvider).getToken("BTC");
        verify(tokenRepository).save(token);
        verify(walletRepository).save(captor.capture());
        Wallet captured = captor.getValue();

        assertNotNull(captured);
        assertEquals(1, captured.getAssets().size());
        assertEquals(3.0, captured.getAssets().getFirst().getQuantity().doubleValue());
    }

    @Test
    void addAsset_WhenAssetAlreadyExistsAndQuantityBecomesNegative_ShouldThrowInsufficientFundsException() {
        // Arrange
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(priceProvider.getToken("BTC")).thenReturn(token);
        when(tokenRepository.save(any(Token.class))).thenReturn(token);

        Asset asset = new Asset();
        asset.setId(10L);
        asset.setToken(token);
        asset.setQuantity(BigDecimal.valueOf(1.0));
        wallet.addAsset(asset);

        // Act & Assert
        assertThrows(
                InsufficientFundsException.class,
                () -> addAssetUseCase.addAsset(walletId, "BTC", -2.0)
        );
        verify(priceProvider).getToken("BTC");
        verify(tokenRepository).save(token);
        verify(walletRepository, never()).save(any());
    }

}
