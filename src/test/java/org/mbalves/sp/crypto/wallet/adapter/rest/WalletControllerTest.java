package org.mbalves.sp.crypto.wallet.adapter.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mbalves.sp.crypto.wallet.adapter.rest.dto.AssetRequest;
import org.mbalves.sp.crypto.wallet.adapter.rest.dto.WalletRequest;
import org.mbalves.sp.crypto.wallet.adapter.rest.dto.WalletResponse;
import org.mbalves.sp.crypto.wallet.adapter.rest.dto.WalletSimulationRequest;
import org.mbalves.sp.crypto.wallet.application.port.in.*;
import org.mbalves.sp.crypto.wallet.domain.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@ExtendWith(MockitoExtension.class)
class WalletControllerTest {

    @Mock
    private CreateWalletUseCase createWalletUseCase;

    @Mock
    private AddAssetUseCase addAssetUseCase;

    @Mock
    private GetWalletUseCase getWalletUseCase;
    
    @Mock
    private DeleteAssetUseCase deleteAssetUseCase;

    @Mock
    private DeleteWalletUseCase deleteWalletUseCase;

    @InjectMocks
    private WalletController walletController;

    private Wallet wallet;
    private final Long walletId = 1L;
    private final String email = "test@example.com";

    @BeforeEach
    void setUp() {
        wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setEmail(email);
        wallet.setAssets(new ArrayList<>());

        Token token = new Token();
        token.setId("bitcoin");
        token.setSymbol("BTC");
        token.setPrice(BigDecimal.valueOf(50000.00));

        Asset asset = new Asset();
        asset.setId(1L);
        asset.setToken(token);
        asset.setQuantity(BigDecimal.valueOf(0.50));

        wallet.getAssets().add(asset);
    }

    @Test
    void createWallet_ShouldReturnWalletResponse() {
        // Arrange
        WalletRequest request = new WalletRequest();
        request.setEmail(email);
        
        when(createWalletUseCase.createWallet(email)).thenReturn(wallet);

        // Act
        WalletResponse response = walletController.createWallet(request);

        // Assert
        assertNotNull(response);
        assertEquals(walletId, response.getId());
        assertEquals(1, response.getAssets().size());
        assertEquals("BTC", response.getAssets().getFirst().getSymbol());
        assertEquals(0.5, response.getAssets().getFirst().getQuantity().doubleValue());
        assertEquals(50000.00, response.getAssets().getFirst().getPrice().doubleValue());
        assertEquals(25000.00, response.getAssets().getFirst().getValue().doubleValue());
        assertEquals(25000.00, response.getTotal().doubleValue());
        
        verify(createWalletUseCase).createWallet(email);
    }

    @Test
    void addAsset_ShouldReturnUpdatedWalletResponse() {
        // Arrange
        AssetRequest request = new AssetRequest();
        request.setSymbol("ETH");
        request.setQuantity(2.0);
        
        // Add another asset to the wallet for this test
        Token ethToken = new Token();
        ethToken.setId("ethereum");
        ethToken.setSymbol("ETH");
        ethToken.setPrice(BigDecimal.valueOf(3000.0));

        Asset ethAsset = new Asset();
        ethAsset.setId(2L);
        ethAsset.setToken(ethToken);
        ethAsset.setQuantity(BigDecimal.valueOf(2.0));
        wallet.getAssets().add(ethAsset);
        
        when(addAssetUseCase.addAsset(walletId, "ETH", 2.0)).thenReturn(wallet);

        // Act
        WalletResponse response = walletController.addAsset(walletId, request);

        // Assert
        assertNotNull(response);
        assertEquals(walletId, response.getId());
        assertEquals(2, response.getAssets().size());
        
        // Check BTC asset
        assertEquals("BTC", response.getAssets().getFirst().getSymbol());
        assertEquals(0.5, response.getAssets().getFirst().getQuantity().doubleValue());
        assertEquals(50000.0, response.getAssets().get(0).getPrice().doubleValue());
        assertEquals(25000.0, response.getAssets().get(0).getValue().doubleValue());
        
        // Check ETH asset
        assertEquals("ETH", response.getAssets().get(1).getSymbol());
        assertEquals(2.0, response.getAssets().get(1).getQuantity().doubleValue());
        assertEquals(3000.0, response.getAssets().get(1).getPrice().doubleValue());
        assertEquals(6000.0, response.getAssets().get(1).getValue().doubleValue());
        
        // Check total value (BTC + ETH)
        assertEquals(31000.0, response.getTotal().doubleValue());
        
        verify(addAssetUseCase).addAsset(walletId, "ETH", 2.0);
    }

    @Test
    void getWallet_ShouldReturnWalletResponse() {
        // Arrange
        when(getWalletUseCase.getWallet(walletId)).thenReturn(wallet);

        // Act
        WalletResponse response = walletController.getWallet(walletId);

        // Assert
        assertNotNull(response);
        assertEquals(walletId, response.getId());
        assertEquals(1, response.getAssets().size());
        assertEquals("BTC", response.getAssets().getFirst().getSymbol());
        assertEquals(0.5, response.getAssets().getFirst().getQuantity().doubleValue());
        assertEquals(50000.0, response.getAssets().getFirst().getPrice().doubleValue());
        assertEquals(25000.0, response.getAssets().getFirst().getValue().doubleValue());
        assertEquals(25000.0, response.getTotal().doubleValue());
        
        verify(getWalletUseCase).getWallet(walletId);
    }

    @Test
    void deleteWallet_ShouldReturnNoContent() {

        // Act
        var response = walletController.deleteWallet(walletId);

        // Assert
        assertNotNull(response);
        assertEquals(NO_CONTENT, response.getStatusCode());

        verify(deleteWalletUseCase).deleteWallet(walletId);
    }

    @Test
    void deleteAsset_ShouldReturnUpdatedWalletResponse() {
        // Arrange
        String symbol = "BTC";
        
        // Create a wallet with no assets (after deletion)
        Wallet emptyWallet = new Wallet();
        emptyWallet.setId(walletId);
        emptyWallet.setEmail(email);
        emptyWallet.setAssets(new ArrayList<>());
        
        when(deleteAssetUseCase.deleteAsset(walletId, symbol)).thenReturn(emptyWallet);

        // Act
        WalletResponse response = walletController.deleteAsset(walletId, symbol);

        // Assert
        assertNotNull(response);
        assertEquals(walletId, response.getId());
        assertEquals(0, response.getAssets().size());
        assertEquals(BigDecimal.ZERO, response.getTotal());
        
        verify(deleteAssetUseCase).deleteAsset(walletId, symbol);
    }
    
    @Test
    void simulateWallet_ShouldReturnSimulationResponse() {
        // Arrange
        var simulationRequest = new WalletSimulationRequest();
        var assetSim = new WalletSimulationRequest.AssetSimulationRequest();
        assetSim.setSymbol("BTC");
        assetSim.setQuantity(1.0);
        assetSim.setValue(BigDecimal.valueOf(50000));
        simulationRequest.setAssets(List.of(assetSim));
        simulationRequest.setDate(java.time.LocalDate.of(2024, 1, 1));

        var assetSimulation = new AssetSimulation();
        assetSimulation.setSymbol("BTC");
        assetSimulation.setQuantity(1.0);
        assetSimulation.setValue(BigDecimal.valueOf(50000));

        var simulationResult = new WalletSimulationResult();
        simulationResult.setTotal(BigDecimal.valueOf(50000));
        simulationResult.setBestAsset("BTC");
        simulationResult.setBestPerformance(BigDecimal.valueOf(10));
        simulationResult.setWorstAsset("BTC");
        simulationResult.setWorstPerformance(BigDecimal.valueOf(-5));

        var simulateWalletProfitUseCase = org.mockito.Mockito.mock(
            SimulateWalletProfitUseCase.class
        );
        var controller = new WalletController(
            createWalletUseCase, addAssetUseCase, getWalletUseCase, deleteWalletUseCase, deleteAssetUseCase, simulateWalletProfitUseCase
        );

        org.mockito.Mockito.when(
            simulateWalletProfitUseCase.simulateProfit(
                org.mockito.ArgumentMatchers.anyList(),
                org.mockito.ArgumentMatchers.any(LocalDate.class)
            )
        ).thenReturn(simulationResult);

        // Act
        var response = controller.simulateWallet(simulationRequest);

        // Assert
        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(50000), response.getTotal());
        assertEquals("BTC", response.getBestAsset());
        assertEquals(BigDecimal.valueOf(10), response.getBestPerformance());
        assertEquals("BTC", response.getWorstAsset());
        assertEquals(BigDecimal.valueOf(-5), response.getWorstPerformance());
    }
}
