package org.mbalves.sp.crypto.wallet.adapter.rest;

import lombok.RequiredArgsConstructor;
import org.mbalves.sp.crypto.wallet.adapter.rest.dto.*;
import org.mbalves.sp.crypto.wallet.application.port.in.*;
import org.mbalves.sp.crypto.wallet.domain.AssetSimulation;
import org.mbalves.sp.crypto.wallet.domain.Wallet;
import org.mbalves.sp.crypto.wallet.domain.WalletSimulationResult;
import org.mbalves.sp.crypto.wallet.infrastructure.logging.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {
    private static final Logger log = LoggerFactory.getLogger(WalletController.class);
    
    private final CreateWalletUseCase createWalletUseCase;
    private final AddAssetUseCase addAssetUseCase;
    private final GetWalletUseCase getWalletUseCase;
    private final DeleteWalletUseCase deleteWalletUseCase;
    private final DeleteAssetUseCase deleteAssetUseCase;
    private final SimulateWalletProfitUseCase simulateWalletProfitUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WalletResponse createWallet(@RequestBody WalletRequest request) {
        log.info("Creating new wallet for email: {}", request.getEmail());

        Wallet wallet = createWalletUseCase.createWallet(request.getEmail());
        log.info("Wallet created successfully with id: {}", wallet.getId());
        
        return toResponse(wallet);
    }

    @GetMapping("/{walletId}")
    public WalletResponse getWallet(@PathVariable Long walletId) {
        log.info("Fetching wallet with id: {}", walletId);
        LoggingUtils.setWalletId(walletId);
        
        Wallet wallet = getWalletUseCase.getWallet(walletId);
        log.info("Wallet retrieved successfully with {} assets", wallet.getAssets().size());
        
        return toResponse(wallet);
    }

    @DeleteMapping("/{walletId}")
    public ResponseEntity<Void> deleteWallet(@PathVariable Long walletId) {
        log.info("Deleting wallet with id: {}", walletId);
        LoggingUtils.setWalletId(walletId);
        
        deleteWalletUseCase.deleteWallet(walletId);
        log.info("Wallet deleted successfully");
        
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{walletId}/assets")
    public WalletResponse addAsset(@PathVariable Long walletId, @RequestBody AssetRequest request) {
        log.info("Adding asset {} to wallet {} with quantity {}", 
            request.getSymbol(), walletId, request.getQuantity());
        LoggingUtils.setWalletId(walletId);
        LoggingUtils.setTokenSymbol(request.getSymbol());
        
        Wallet wallet = addAssetUseCase.addAsset(walletId, request.getSymbol(), request.getQuantity());
        log.info("Asset added successfully to wallet");
        
        return toResponse(wallet);
    }

    @DeleteMapping("/{walletId}/assets/{symbol}")
    public WalletResponse deleteAsset(@PathVariable Long walletId, @PathVariable String symbol) {
        log.info("Deleting asset {} from wallet {}", symbol, walletId);
        LoggingUtils.setWalletId(walletId);
        LoggingUtils.setTokenSymbol(symbol);
        
        Wallet wallet = deleteAssetUseCase.deleteAsset(walletId, symbol);
        log.info("Asset deleted successfully from wallet");
        
        return toResponse(wallet);
    }

    @PostMapping("/simulate")
    public WalletSimulationResponse simulateWallet(@RequestBody WalletSimulationRequest request) {
        log.info("Simulating wallet with {} assets for date: {}", 
            request.getAssets().size(), 
            request.getDate() != null ? request.getDate() : "current date");
        
        List<AssetSimulation> assetSimulations = request.getAssets().stream()
                .map(asset -> {
                    AssetSimulation simulation = new AssetSimulation();
                    simulation.setSymbol(asset.getSymbol());
                    simulation.setQuantity(asset.getQuantity());
                    simulation.setValue(asset.getValue());
                    return simulation;
                })
                .collect(Collectors.toList());

        WalletSimulationResult result = simulateWalletProfitUseCase.simulateProfit(
                assetSimulations,
                request.getDate() != null ? request.getDate() : LocalDate.now()
        );

        log.info("Simulation completed - Total: {}, Best Asset: {} ({}%), Worst Asset: {} ({}%)",
            result.getTotal(),
            result.getBestAsset(),
            result.getBestPerformance(),
            result.getWorstAsset(),
            result.getWorstPerformance());

        WalletSimulationResponse response = new WalletSimulationResponse();
        response.setTotal(result.getTotal());
        response.setBestAsset(result.getBestAsset());
        response.setBestPerformance(result.getBestPerformance());
        response.setWorstAsset(result.getWorstAsset());
        response.setWorstPerformance(result.getWorstPerformance());
        return response;
    }

    private WalletResponse toResponse(Wallet wallet) {
        WalletResponse response = new WalletResponse();
        response.setId(wallet.getId());
        response.setAssets(wallet.getAssets().stream()
                .map(asset -> {
                    AssetResponse ar = new AssetResponse();
                    ar.setSymbol(asset.getToken().getSymbol());
                    ar.setPrice(asset.getToken().getPrice());
                    ar.setQuantity(asset.getQuantity());
                    ar.setValue(asset.getValue());
                    return ar;
                })
                .toList());
        response.setTotal(wallet.getTotal());
        return response;
    }
}
