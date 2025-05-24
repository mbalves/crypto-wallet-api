package org.mbalves.sp.crypto.wallet.application.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mbalves.sp.crypto.wallet.application.port.out.PriceProviderPort;
import org.mbalves.sp.crypto.wallet.application.port.out.TokenRepositoryPort;
import org.mbalves.sp.crypto.wallet.domain.Token;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdatePricesUseCaseImplTest {

    @Mock
    private TokenRepositoryPort tokenRepository;

    @Mock
    private PriceProviderPort priceProvider;

    @InjectMocks
    private UpdatePricesUseCaseImpl updatePricesUseCase;

    private Token btcToken;
    private Token ethToken;

    @BeforeEach
    void setUp() {
        btcToken = new Token();
        btcToken.setId("bitcoin");
        btcToken.setSymbol("BTC");
        btcToken.setPrice(BigDecimal.valueOf(50000.0));
        btcToken.setLastUpdated(Instant.now());

        ethToken = new Token();
        ethToken.setId("ethereum");
        ethToken.setSymbol("ETH");
        ethToken.setPrice(BigDecimal.valueOf(3000.0));
        ethToken.setLastUpdated(Instant.now());
    }

    @Test
    void updatePrices_WhenTokensExistAndPricesAreAvailable_ShouldUpdateAllTokens() throws InterruptedException {
        // Arrange
        List<Token> tokens = Arrays.asList(btcToken, ethToken);
        when(tokenRepository.findAll()).thenReturn(tokens);
        when(priceProvider.getTokenPrice("bitcoin")).thenReturn(51000.0);
        when(priceProvider.getTokenPrice("ethereum")).thenReturn(3100.0);
        when(tokenRepository.save(any(Token.class))).thenReturn(btcToken).thenReturn(ethToken);

        // Act
        updatePricesUseCase.updatePrices();
        
        // Wait for async tasks to complete
        Thread.sleep(500);

        // Assert
        verify(tokenRepository).findAll();
        verify(priceProvider).getTokenPrice("bitcoin");
        verify(priceProvider).getTokenPrice("ethereum");
        verify(tokenRepository, times(2)).save(any(Token.class));
    }

    @Test
    void updatePrices_WhenNoTokensExist_ShouldNotUpdateAnything() {
        // Arrange
        when(tokenRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        updatePricesUseCase.updatePrices();

        // Assert
        verify(tokenRepository).findAll();
        verify(priceProvider, never()).getTokenPrice(anyString());
        verify(tokenRepository, never()).save(any(Token.class));
    }

    @Test
    void updatePrices_WhenPriceProviderReturnsNull_ShouldNotUpdateToken() throws InterruptedException {
        // Arrange
        List<Token> tokens = Collections.singletonList(btcToken);
        when(tokenRepository.findAll()).thenReturn(tokens);
        when(priceProvider.getTokenPrice("bitcoin")).thenReturn(null);

        // Act
        updatePricesUseCase.updatePrices();
        
        // Wait for async tasks to complete
        Thread.sleep(500);

        // Assert
        verify(tokenRepository).findAll();
        verify(priceProvider).getTokenPrice("bitcoin");
        verify(tokenRepository, never()).save(any(Token.class));
    }

    @Test
    void updatePrices_WhenPriceProviderThrowsException_ShouldHandleExceptionAndContinue() throws InterruptedException {
        // Arrange
        List<Token> tokens = Arrays.asList(btcToken, ethToken);
        when(tokenRepository.findAll()).thenReturn(tokens);
        when(priceProvider.getTokenPrice("bitcoin")).thenThrow(new RuntimeException("API Error"));
        when(priceProvider.getTokenPrice("ethereum")).thenReturn(3100.0);
        when(tokenRepository.save(any(Token.class))).thenReturn(ethToken);

        // Act
        updatePricesUseCase.updatePrices();
        
        // Wait for async tasks to complete
        Thread.sleep(500);

        // Assert
        verify(tokenRepository).findAll();
        verify(priceProvider).getTokenPrice("bitcoin");
        verify(priceProvider).getTokenPrice("ethereum");
        verify(tokenRepository, times(1)).save(any(Token.class));
    }
}
