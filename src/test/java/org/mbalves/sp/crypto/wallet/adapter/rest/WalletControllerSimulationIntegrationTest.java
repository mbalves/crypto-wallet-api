package org.mbalves.sp.crypto.wallet.adapter.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mbalves.sp.crypto.wallet.adapter.rest.dto.WalletSimulationRequest;
import org.mbalves.sp.crypto.wallet.adapter.rest.dto.WalletSimulationRequest.AssetSimulationRequest;
import org.mbalves.sp.crypto.wallet.application.port.in.*;
import org.mbalves.sp.crypto.wallet.domain.WalletSimulationResult;
import org.mbalves.sp.crypto.wallet.domain.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WalletController.class)
class WalletControllerSimulationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SimulateWalletProfitUseCase simulateWalletProfitUseCase;

    @MockitoBean
    private CreateWalletUseCase createWalletUseCase;

    @MockitoBean
    private AddAssetUseCase addAssetUseCase;

    @MockitoBean
    private GetWalletUseCase getWalletUseCase;

    @MockitoBean
    private DeleteAssetUseCase deleteAssetUseCase;

    @MockitoBean
    private DeleteWalletUseCase deleteWalletUseCase;

    @Test
    void simulateWallet_OneAsset_ShouldReturnSimulationResult() throws Exception {
        WalletSimulationRequest request = new WalletSimulationRequest();
        AssetSimulationRequest asset = new AssetSimulationRequest();
        asset.setSymbol("BTC");
        asset.setQuantity(1.0);
        asset.setValue(BigDecimal.valueOf(30000.0));
        request.setAssets(List.of(asset));

        WalletSimulationResult result = new WalletSimulationResult();
        result.setTotal(BigDecimal.valueOf(35000.0));
        result.setBestAsset("BTC");
        result.setBestPerformance(BigDecimal.valueOf(16.67));
        result.setWorstAsset("BTC");
        result.setWorstPerformance(BigDecimal.valueOf(16.67));

        when(simulateWalletProfitUseCase.simulateProfit(any(), any())).thenReturn(result);

        mockMvc.perform(post("/api/wallets/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(35000.0))
                .andExpect(jsonPath("$.bestAsset").value("BTC"))
                .andExpect(jsonPath("$.bestPerformance").value(16.67))
                .andExpect(jsonPath("$.worstAsset").value("BTC"))
                .andExpect(jsonPath("$.worstPerformance").value(16.67));
    }

    @Test
    void simulateWallet_WhenAssetsEmpty_ShouldReturnOkWithZeroTotal() throws Exception {
        WalletSimulationRequest request = new WalletSimulationRequest();
        request.setAssets(List.of());
        request.setDate(LocalDate.now());

        WalletSimulationResult result = new WalletSimulationResult();
        result.setTotal(BigDecimal.ZERO);

        when(simulateWalletProfitUseCase.simulateProfit(any(), any())).thenReturn(result);

        mockMvc.perform(post("/api/wallets/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void simulateWallet_WhenInvalidToken_ShouldReturnBadRequest() throws Exception {
        WalletSimulationRequest request = new WalletSimulationRequest();
        AssetSimulationRequest asset = new AssetSimulationRequest();
        asset.setSymbol("XYZ");
        asset.setQuantity(1.0);
        asset.setValue(BigDecimal.valueOf(1000.0));
        request.setAssets(List.of(asset));
        request.setDate(LocalDate.now());

        when(simulateWalletProfitUseCase.simulateProfit(any(), any()))
                .thenThrow(new InvalidTokenException("XYZ"));

        mockMvc.perform(post("/api/wallets/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid token or price not found for symbol: XYZ"));
    }
}
