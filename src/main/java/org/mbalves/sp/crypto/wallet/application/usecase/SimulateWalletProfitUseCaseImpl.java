package org.mbalves.sp.crypto.wallet.application.usecase;

import lombok.RequiredArgsConstructor;
import org.mbalves.sp.crypto.wallet.application.port.in.SimulateWalletProfitUseCase;
import org.mbalves.sp.crypto.wallet.application.port.out.PriceProviderPort;
import org.mbalves.sp.crypto.wallet.domain.AssetSimulation;
import org.mbalves.sp.crypto.wallet.domain.Token;
import org.mbalves.sp.crypto.wallet.domain.WalletSimulationResult;
import org.mbalves.sp.crypto.wallet.domain.exception.InvalidTokenException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of the SimulateWalletProfitUseCase interface.
 * This use case handles the simulation of wallet profit based on current or historical prices.
 * It calculates the total value, best and worst performing assets, and their performances.
 *
 * @author Marcelo Alves
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class SimulateWalletProfitUseCaseImpl implements SimulateWalletProfitUseCase {
    private final PriceProviderPort priceProvider;

    /**
     * Simulates the profit of a wallet based on the current or historical price of the assets.
     * It calculates the total value of the wallet, the best and worst performing assets,
     * and their respective performances.
     *
     * @param assets The list of assets to simulate
     * @param date The date for the simulation (current or historical)
     * @return The result of the wallet profit simulation
     * @throws InvalidTokenException if a token is invalid or price cannot be fetched
     */
    @Override
    public WalletSimulationResult simulateProfit(List<AssetSimulation> assets, LocalDate date) {
        WalletSimulationResult result = new WalletSimulationResult();
        BigDecimal total = BigDecimal.ZERO;
        String bestAsset = null;
        BigDecimal bestPerformance = null;
        String worstAsset = null;
        BigDecimal worstPerformance = null;

        for (AssetSimulation asset : assets) {
            BigDecimal initialPrice = asset.getValue()
                    .divide(BigDecimal.valueOf(asset.getQuantity()), 8, RoundingMode.HALF_UP);
            Token token = priceProvider.getToken(asset.getSymbol());
            if (token == null) {
                throw new InvalidTokenException(asset.getSymbol());
            }

            BigDecimal currentPrice = token.getPrice();
            // If the date is in the past, fetch the historical price
            if (date.isBefore(LocalDate.now())) {
                Double historicalPriceValue = priceProvider.getTokenPrice(token.getId(), date);
                if (historicalPriceValue == null) {
                    throw new InvalidTokenException(asset.getSymbol());
                }
                currentPrice = BigDecimal.valueOf(historicalPriceValue);
            }

            // Calculate percentage change: ((current - initial) / initial) * 100
            BigDecimal performance = currentPrice
                    .subtract(initialPrice)
                    .divide(initialPrice, 8, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);

            // Calculate asset value: currentPrice * quantity
            BigDecimal assetValue = currentPrice
                    .multiply(BigDecimal.valueOf(asset.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);
            total = total.add(assetValue);

            // Update best/worst assets
            if (bestPerformance == null || performance.compareTo(bestPerformance) > 0) {
                bestAsset = asset.getSymbol();
                bestPerformance = performance;
            }
            if (worstPerformance == null || performance.compareTo(worstPerformance) < 0) {
                worstAsset = asset.getSymbol();
                worstPerformance = performance;
            }
        }

        result.setTotal(total);
        result.setBestAsset(bestAsset);
        result.setWorstAsset(worstAsset);
        result.setBestPerformance(bestPerformance);
        result.setWorstPerformance(worstPerformance);
        return result;
    }
}
