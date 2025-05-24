package org.mbalves.sp.crypto.wallet.adapter.pricing;

import lombok.extern.slf4j.Slf4j;
import org.mbalves.sp.crypto.wallet.adapter.pricing.dto.CoinCapListResponse;
import org.mbalves.sp.crypto.wallet.adapter.pricing.dto.CoinCapPriceHistoryResponse;
import org.mbalves.sp.crypto.wallet.adapter.pricing.dto.CoinCapPriceResponse;
import org.mbalves.sp.crypto.wallet.application.port.out.PriceProviderPort;
import org.mbalves.sp.crypto.wallet.domain.Token;
import org.mbalves.sp.crypto.wallet.infrastructure.logging.LoggingUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Adapter for interacting with the CoinCap API to fetch token information and prices.
 * Implements the {@link PriceProviderPort} interface.
 * Handles current and historical price retrieval, as well as token metadata.
 * Uses a REST client with authentication headers.
 *
 * @author Marcelo Alves
 * @version 1.0
 */
@Component
@Slf4j
public class CoinCapAdapter implements PriceProviderPort {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${crypto.pricing-api.url-symbol:}")
    private String apiUrlSymbol;

    @Value("${crypto.pricing-api.url-price:}")
    private String apiUrlPrice;

    @Value("${crypto.pricing-api.url-history:}")
    private String apiUrlPriceHistory;

    @Value("${crypto.pricing-api.key:}")
    private String apiKey;

    /**
     * Fetches token metadata and current price by symbol.
     *
     * @param symbol The token symbol (e.g., BTC)
     * @return The {@link Token} object, or null if not found
     */
    @Override
    public Token getToken(String symbol) {
        try {
            LoggingUtils.setTokenSymbol(symbol);
            log.debug("Fetching token information for symbol: {}", symbol);
            
            CoinCapListResponse response = getApiWithAuth(apiUrlSymbol, CoinCapListResponse.class, symbol.toUpperCase());
            if (response != null && !response.getData().isEmpty() && response.getData().getFirst().getSymbol().equals(symbol)) {
                Token token = new Token();
                token.setSymbol(response.getData().getFirst().getSymbol());
                token.setId(response.getData().getFirst().getId());
                token.setPrice(new BigDecimal(response.getData().getFirst().getPriceUsd()));
                log.info("Successfully fetched token information for {}: price={}", symbol, token.getPrice());
                return token;
            }
            log.warn("No token information found for symbol: {}", symbol);
        } catch (Exception e) {
            log.error("Failed to fetch token information for {}: {}", symbol, e.getMessage(), e);
        }
        return null;
    }

    /**
     * Fetches the current price for a token by its ID.
     *
     * @param tokenId The token ID (e.g., bitcoin)
     * @return The current price in USD, or null if not found
     */
    @Override
    public Double getTokenPrice(String tokenId) {
        try {
            LoggingUtils.setTokenSymbol(tokenId);
            log.debug("Fetching current price for token: {}", tokenId);
            
            String id = tokenId.toLowerCase();
            CoinCapPriceResponse response = getApiWithAuth(apiUrlPrice, CoinCapPriceResponse.class, id);
            if (response != null && response.getData() != null) {
                Double price = Double.parseDouble(response.getData().getPriceUsd());
                log.info("Successfully fetched current price for {}: {}", tokenId, price);
                return price;
            }
            log.warn("No price information found for token: {}", tokenId);
        } catch (Exception e) {
            log.error("Failed to fetch current price for {}: {}", tokenId, e.getMessage(), e);
        }
        return null;
    }

    /**
     * Fetches the historical price for a token by its ID and date.
     *
     * @param tokenId The token ID (e.g., bitcoin)
     * @param date The date for the historical price
     * @return The price in USD on the given date, or null if not found
     */
    @Override
    public Double getTokenPrice(String tokenId, LocalDate date) {
        try {
            LoggingUtils.setTokenSymbol(tokenId);
            log.debug("Fetching historical price for token: {} on date: {}", tokenId, date);
            
            String id = tokenId.toLowerCase();
            Long timestamp = date.atStartOfDay(java.time.ZoneOffset.UTC).toInstant().toEpochMilli();
            CoinCapPriceHistoryResponse response = getApiWithAuth(apiUrlPriceHistory, CoinCapPriceHistoryResponse.class, id, timestamp, timestamp);
            if (response != null && response.getData() != null) {
                Double price = Double.parseDouble(response.getData().getFirst().getPriceUsd());
                log.info("Successfully fetched historical price for {} on {}: {}", tokenId, date, price);
                return price;
            }
            log.warn("No historical price information found for token: {} on date: {}", tokenId, date);
        } catch (Exception e) {
            log.error("Failed to fetch historical price for {} on {}: {}", tokenId, date, e.getMessage(), e);
        }
        return null;
    }

    /**
     * Helper method to call the CoinCap API with authentication headers.
     *
     * @param url The API endpoint URL
     * @param responseType The expected response type
     * @param uriVariables URI variables for the endpoint
     * @return The response body, or null if the call fails
     * @param <T> The response type
     */
    private <T> T getApiWithAuth(String url, Class<T> responseType, Object... uriVariables) {
        return restTemplate.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<>(createAuthHeader(apiKey)),
            responseType,
            uriVariables
        ).getBody();
    }

    /**
     * Creates HTTP headers with the API authentication token.
     *
     * @param apiToken The API key
     * @return The HTTP headers with the Authorization field set
     */
    private HttpHeaders createAuthHeader(String apiToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);
        return headers;
    }
}
