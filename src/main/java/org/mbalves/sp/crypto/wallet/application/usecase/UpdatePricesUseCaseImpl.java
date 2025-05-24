package org.mbalves.sp.crypto.wallet.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mbalves.sp.crypto.wallet.application.port.in.UpdatePricesUseCase;
import org.mbalves.sp.crypto.wallet.application.port.out.PriceProviderPort;
import org.mbalves.sp.crypto.wallet.application.port.out.TokenRepositoryPort;
import org.mbalves.sp.crypto.wallet.domain.Token;
import org.mbalves.sp.crypto.wallet.infrastructure.logging.LoggingUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implementation of the {@link UpdatePricesUseCase} interface.
 * This use case handles the scheduled update of all token prices in the database.
 * It fetches the latest prices from the price provider and updates each token concurrently.
 * The update runs at a fixed interval defined by the 'crypto.price-update-interval' property.
 *
 * <p>
 * The update process:
 * <ul>
 *   <li>Fetches all tokens from the repository</li>
 *   <li>Fetches the latest price for each token using the price provider</li>
 *   <li>Updates and saves the tokens with new prices</li>
 *   <li>Runs updates concurrently for better performance</li>
 * </ul>
 * </p>
 *
 * @author Marcelo Alves
 * @version 1.0
 */
@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class UpdatePricesUseCaseImpl implements UpdatePricesUseCase {
    private final TokenRepositoryPort tokenRepository;
    private final PriceProviderPort priceProvider;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    /**
     * Scheduled method to update prices of all tokens.
     * It runs at a fixed rate defined by the 'crypto.price-update-interval' property.
     * The default interval is set to 60 seconds (60000 milliseconds).
     */
    @Override
    @Scheduled(fixedRateString = "${crypto.price-update-interval:60000}")
    public void updatePrices() {
        log.info("Starting scheduled price update");
        List<Token> tokens = tokenRepository.findAll();
        log.info("Found {} tokens to update", tokens.size());

        // Process all tokens concurrently in batches
        List<CompletableFuture<Token>> futures = tokens.stream()
            .map(token -> CompletableFuture.supplyAsync(() -> updateAssetPrice(token), executorService))
            .toList();

        // Wait for all futures to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // Collect and save updated tokens in a single batch
        List<Token> updatedTokens = futures.stream()
            .map(CompletableFuture::join)
            .filter(Objects::nonNull)
            .toList();

        if (!updatedTokens.isEmpty()) {
            updatedTokens.forEach(token -> {
                LoggingUtils.setTokenSymbol(token.getSymbol());
                tokenRepository.save(token);
                log.info("Updated price for {} to {}", token.getSymbol(), token.getPrice());
            });
            log.info("Completed price update for {} tokens", updatedTokens.size());
        } else {
            log.warn("No tokens were updated in this cycle");
        }
    }

    /**
     * Updates the price of a single token.
     * It fetches the latest price from the price provider and updates the token.
     * If the price is successfully updated, it returns the updated token; otherwise, it returns null.
     *
     * @param token The token to update
     * @return The updated token or null if no update was made
     */
    private Token updateAssetPrice(Token token) {
        try {
            LoggingUtils.setTokenSymbol(token.getSymbol());
            Double newPrice = priceProvider.getTokenPrice(token.getId());
            if (newPrice != null) {
                token.setPrice(BigDecimal.valueOf(newPrice));
                log.debug("Fetched new price for {}: {}", token.getSymbol(), newPrice);
                return token;
            } else {
                log.warn("No price update available for {}", token.getSymbol());
            }
        } catch (Exception e) {
            log.error("Error updating price for {}: {}", token.getSymbol(), e.getMessage(), e);
        }
        return null;
    }
}
