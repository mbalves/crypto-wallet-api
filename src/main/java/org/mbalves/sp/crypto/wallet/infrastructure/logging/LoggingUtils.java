package org.mbalves.sp.crypto.wallet.infrastructure.logging;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LoggingUtils {
    private static final String REQUEST_ID = "requestId";
    private static final String WALLET_ID = "walletId";
    private static final String TOKEN_SYMBOL = "tokenSymbol";

    public static void setRequestId() {
        MDC.put(REQUEST_ID, UUID.randomUUID().toString());
    }

    public static void setWalletId(Long walletId) {
        MDC.put(WALLET_ID, String.valueOf(walletId));
    }

    public static void setTokenSymbol(String symbol) {
        MDC.put(TOKEN_SYMBOL, symbol);
    }

    public static void clear() {
        MDC.clear();
    }
} 
