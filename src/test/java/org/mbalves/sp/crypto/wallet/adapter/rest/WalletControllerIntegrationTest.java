package org.mbalves.sp.crypto.wallet.adapter.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mbalves.sp.crypto.wallet.adapter.rest.dto.AssetRequest;
import org.mbalves.sp.crypto.wallet.adapter.rest.dto.WalletRequest;
import org.mbalves.sp.crypto.wallet.application.port.in.*;
import org.mbalves.sp.crypto.wallet.domain.Asset;
import org.mbalves.sp.crypto.wallet.domain.Token;
import org.mbalves.sp.crypto.wallet.domain.Wallet;
import org.mbalves.sp.crypto.wallet.domain.exception.AssetNotFoundException;
import org.mbalves.sp.crypto.wallet.domain.exception.InvalidTokenException;
import org.mbalves.sp.crypto.wallet.domain.exception.WalletAlreadyExistsException;
import org.mbalves.sp.crypto.wallet.domain.exception.WalletNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WalletController.class)
class WalletControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateWalletUseCase createWalletUseCase;

    @MockitoBean
    private AddAssetUseCase addAssetUseCase;

    @MockitoBean
    private DeleteAssetUseCase deleteAssetUseCase;

    @MockitoBean
    private GetWalletUseCase getWalletUseCase;

    @MockitoBean
    private DeleteWalletUseCase deleteWalletUseCase;

    @MockitoBean
    private SimulateWalletProfitUseCase simulateWalletProfitUseCase;

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
        token.setPrice(BigDecimal.valueOf(50000.0));

        Asset asset = new Asset();
        asset.setId(1L);
        asset.setToken(token);
        asset.setQuantity(BigDecimal.valueOf(0.5));

        wallet.getAssets().add(asset);
    }

    @Test
    void createWallet_ShouldReturnCreatedStatus() throws Exception {
        // Arrange
        WalletRequest request = new WalletRequest();
        request.setEmail(email);
        
        when(createWalletUseCase.createWallet(email)).thenReturn(wallet);

        // Act & Assert
        mockMvc.perform(post("/api/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(walletId))
                .andExpect(jsonPath("$.assets[0].symbol").value("BTC"))
                .andExpect(jsonPath("$.assets[0].quantity").value(0.5))
                .andExpect(jsonPath("$.assets[0].price").value(50000.0))
                .andExpect(jsonPath("$.assets[0].value").value(25000.0))
                .andExpect(jsonPath("$.total").value(25000.0));
        
        verify(createWalletUseCase).createWallet(email);
    }

    @Test
    void createWallet_WhenWalletAlreadyExists_ShouldReturnConflictStatus() throws Exception {
        // Arrange
        WalletRequest request = new WalletRequest();
        request.setEmail(email);
        
        when(createWalletUseCase.createWallet(email))
            .thenThrow(new WalletAlreadyExistsException(email));

        // Act & Assert
        mockMvc.perform(post("/api/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Wallet already exists for email: " + email));
        
        verify(createWalletUseCase).createWallet(email);
    }

    @Test
    void addAsset_ShouldReturnOkStatus() throws Exception {
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

        // Act & Assert
        mockMvc.perform(post("/api/wallets/{walletId}/assets", walletId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(walletId))
                .andExpect(jsonPath("$.assets.length()").value(2))
                .andExpect(jsonPath("$.assets[0].symbol").value("BTC"))
                .andExpect(jsonPath("$.assets[1].symbol").value("ETH"))
                .andExpect(jsonPath("$.total").value(31000.0));
        
        verify(addAssetUseCase).addAsset(walletId, "ETH", 2.0);
    }

    @Test
    void addAsset_WhenWalletNotFound_ShouldReturnNotFoundStatus() throws Exception {
        // Arrange
        AssetRequest request = new AssetRequest();
        request.setSymbol("ETH");
        request.setQuantity(2.0);
        
        when(addAssetUseCase.addAsset(walletId, "ETH", 2.0))
            .thenThrow(new WalletNotFoundException(walletId));

        // Act & Assert
        mockMvc.perform(post("/api/wallets/{walletId}/assets", walletId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Wallet not found with ID: " + walletId));
        
        verify(addAssetUseCase).addAsset(walletId, "ETH", 2.0);
    }

    @Test
    void addAsset_WhenInvalidToken_ShouldReturnBadRequestStatus() throws Exception {
        // Arrange
        AssetRequest request = new AssetRequest();
        request.setSymbol("XYZ");
        request.setQuantity(2.0);
        
        when(addAssetUseCase.addAsset(walletId, "XYZ", 2.0))
            .thenThrow(new InvalidTokenException("XYZ"));

        // Act & Assert
        mockMvc.perform(post("/api/wallets/{walletId}/assets", walletId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid token or price not found for symbol: XYZ"));
        
        verify(addAssetUseCase).addAsset(walletId, "XYZ", 2.0);
    }

    @Test
    void getWallet_ShouldReturnOkStatus() throws Exception {
        // Arrange
        when(getWalletUseCase.getWallet(walletId)).thenReturn(wallet);

        // Act & Assert
        mockMvc.perform(get("/api/wallets/{walletId}", walletId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(walletId))
                .andExpect(jsonPath("$.assets[0].symbol").value("BTC"))
                .andExpect(jsonPath("$.assets[0].quantity").value(0.5))
                .andExpect(jsonPath("$.assets[0].price").value(50000.0))
                .andExpect(jsonPath("$.assets[0].value").value(25000.0))
                .andExpect(jsonPath("$.total").value(25000.0));
        
        verify(getWalletUseCase).getWallet(walletId);
    }

    @Test
    void getWallet_WhenWalletNotFound_ShouldReturnNotFoundStatus() throws Exception {
        // Arrange
        when(getWalletUseCase.getWallet(walletId))
            .thenThrow(new WalletNotFoundException(walletId));

        // Act & Assert
        mockMvc.perform(get("/api/wallets/{walletId}", walletId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Wallet not found with ID: " + walletId));
        
        verify(getWalletUseCase).getWallet(walletId);
    }

    @Test
    void getWallet_WhenErrorOccurs_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        when(getWalletUseCase.getWallet(walletId))
            .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        mockMvc.perform(get("/api/wallets/{walletId}", walletId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An unexpected error occurred"));

        verify(getWalletUseCase).getWallet(walletId);
    }

    @Test
    void deleteWallet_ShouldReturnNoContent() throws Exception {
        Long walletId = 1L;
        doNothing().when(deleteWalletUseCase).deleteWallet(walletId);

        mockMvc.perform(delete("/api/wallets/{walletId}", walletId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteWallet_WhenNotFound_ShouldReturnNotFound() throws Exception {
        Long walletId = 2L;
        doThrow(new WalletNotFoundException(walletId))
                .when(deleteWalletUseCase).deleteWallet(walletId);

        mockMvc.perform(delete("/api/wallets/{walletId}", walletId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAsset_ShouldReturnOkStatus() throws Exception {
        // Arrange
        String symbol = "BTC";
        wallet.getAssets().removeIf(asset -> asset.getToken().getSymbol().equals(symbol));
        when(deleteAssetUseCase.deleteAsset(walletId, symbol)).thenReturn(wallet);

        // Act & Assert
        mockMvc.perform(delete("/api/wallets/{walletId}/assets/{symbol}", walletId, symbol))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(walletId))
                .andExpect(jsonPath("$.assets.length()").value(0));

        verify(deleteAssetUseCase).deleteAsset(walletId, symbol);
    }

    @Test
    void deleteAsset_WhenWalletNotFound_ShouldReturnNotFoundStatus() throws Exception {
        // Arrange
        String symbol = "BTC";
        when(deleteAssetUseCase.deleteAsset(walletId, symbol))
            .thenThrow(new WalletNotFoundException(walletId));

        // Act & Assert
        mockMvc.perform(delete("/api/wallets/{walletId}/assets/{symbol}", walletId, symbol))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Wallet not found with ID: " + walletId));

        verify(deleteAssetUseCase).deleteAsset(walletId, symbol);
    }

    @Test
    void deleteAsset_WhenAssetNotFound_ShouldReturnBadRequestStatus() throws Exception {
        // Arrange
        String symbol = "XYZ";
        when(deleteAssetUseCase.deleteAsset(walletId, symbol))
            .thenThrow(new AssetNotFoundException(walletId,"XYZ"));

        // Act & Assert
        mockMvc.perform(delete("/api/wallets/{walletId}/assets/{symbol}", walletId, symbol))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Asset [" + symbol + "] not found in Wallet: " + walletId));

        verify(deleteAssetUseCase).deleteAsset(walletId, symbol);
    }
}
