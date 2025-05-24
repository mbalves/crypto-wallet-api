package org.mbalves.sp.crypto.wallet.application.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mbalves.sp.crypto.wallet.application.port.out.PriceProviderPort;
import org.mbalves.sp.crypto.wallet.domain.AssetSimulation;
import org.mbalves.sp.crypto.wallet.domain.Token;
import org.mbalves.sp.crypto.wallet.domain.WalletSimulationResult;
import org.mbalves.sp.crypto.wallet.domain.exception.InvalidTokenException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimulateWalletProfitUseCaseImplTest {

    @Mock
    private PriceProviderPort priceProvider;

    @InjectMocks
    private SimulateWalletProfitUseCaseImpl simulateWalletProfitUseCase;

    private AssetSimulation asset1;
    private AssetSimulation asset2;
    private Token btcToken;
    private Token ethToken;

    @BeforeEach
    void setUp() {
        asset1 = new AssetSimulation();
        asset1.setSymbol("BTC");
        asset1.setQuantity(1.0);
        asset1.setValue(BigDecimal.valueOf(30000.0)); // historical value is 30000.0

        asset2 = new AssetSimulation();
        asset2.setSymbol("ETH");
        asset2.setQuantity(2.0);
        asset2.setValue(BigDecimal.valueOf(4800.0)); // historical value is 2400.0

        btcToken = new Token();
        btcToken.setId("bitcoin");
        btcToken.setSymbol("BTC");
        btcToken.setPrice(BigDecimal.valueOf(35000.0));

        ethToken = new Token();
        ethToken.setId("ethereum");
        ethToken.setSymbol("ETH");
        ethToken.setPrice(BigDecimal.valueOf(2500.0));
    }

    @Test
    void simulateProfit_WhenAssetsListIsEmpty_ShouldReturnZeroTotalAndNulls() {
        List<AssetSimulation> assets = List.of();
        LocalDate date = LocalDate.now();

        WalletSimulationResult result = simulateWalletProfitUseCase.simulateProfit(assets, date);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotal());
        assertNull(result.getBestAsset());
        assertNull(result.getWorstAsset());
        assertNull(result.getBestPerformance());
        assertNull(result.getWorstPerformance());
    }

    @Test
    void simulateProfit_currentDate_ShouldReturnCorrectResult() {
        when(priceProvider.getToken("BTC")).thenReturn(btcToken);
        when(priceProvider.getToken("ETH")).thenReturn(ethToken);

        List<AssetSimulation> assets = Arrays.asList(asset1, asset2);
        LocalDate date = LocalDate.now();

        WalletSimulationResult result = simulateWalletProfitUseCase.simulateProfit(assets, date);

        assertNotNull(result);
        assertEquals(new BigDecimal("40000.00"), result.getTotal());
        assertEquals("BTC", result.getBestAsset());
        assertEquals("ETH", result.getWorstAsset());
        assertNotNull(result.getBestPerformance());
        assertNotNull(result.getWorstPerformance());
        assertTrue(result.getBestPerformance().compareTo(result.getWorstPerformance()) > 0);

        verify(priceProvider).getToken("BTC");
        verify(priceProvider).getToken("ETH");
    }

    @Test
    void simulateProfit_historicalDate_ShouldReturnCorrectResult() {
        when(priceProvider.getToken("BTC")).thenReturn(btcToken);
        when(priceProvider.getToken("ETH")).thenReturn(ethToken);

        LocalDate date = LocalDate.of(2025,1,1);
        when(priceProvider.getTokenPrice("bitcoin", date)).thenReturn(30000.0);
        when(priceProvider.getTokenPrice("ethereum", date)).thenReturn(3500.0);

        List<AssetSimulation> assets = Arrays.asList(asset1, asset2);

        WalletSimulationResult result = simulateWalletProfitUseCase.simulateProfit(assets, date);

        assertNotNull(result);
        assertEquals(new BigDecimal("37000.00"), result.getTotal());
        assertEquals("ETH", result.getBestAsset());
        assertEquals("BTC", result.getWorstAsset());
        assertNotNull(result.getBestPerformance());
        assertNotNull(result.getWorstPerformance());
        assertTrue(result.getBestPerformance().compareTo(result.getWorstPerformance()) > 0);

        verify(priceProvider).getToken("BTC");
        verify(priceProvider).getToken("ETH");
    }

    @Test
    void simulateProfit_historicalDate_WhenHistoricalPriceIsNull_ShouldThrowInvalidTokenException() {
        when(priceProvider.getToken("BTC")).thenReturn(btcToken);
        LocalDate date = LocalDate.of(2020, 1, 1);
        when(priceProvider.getTokenPrice("bitcoin", date)).thenReturn(null);

        List<AssetSimulation> assets = List.of(asset1);

        InvalidTokenException exception = assertThrows(
            InvalidTokenException.class,
            () -> simulateWalletProfitUseCase.simulateProfit(assets, date)
        );

        assertEquals("Invalid token or price not found for symbol: BTC", exception.getMessage());
        verify(priceProvider).getToken("BTC");
        verify(priceProvider).getTokenPrice("bitcoin", date);
    }

    @Test
    void simulateProfit_WhenTokenPriceIsNull_ShouldThrowInvalidTokenException() {
        when(priceProvider.getToken("BTC")).thenReturn(null);

        List<AssetSimulation> assets = List.of(asset1);

        InvalidTokenException exception = assertThrows(
                InvalidTokenException.class,
                () -> simulateWalletProfitUseCase.simulateProfit(assets, LocalDate.now())
        );

        assertEquals("Invalid token or price not found for symbol: BTC", exception.getMessage());
        verify(priceProvider).getToken("BTC");
    }
}
