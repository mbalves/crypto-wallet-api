package org.mbalves.sp.crypto.wallet.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WalletTest {

    @Test
    void testAddAsset() {
        // Arrange
        Wallet wallet = new Wallet();

        Token token = new Token();
        token.setId("bitcoin");
        token.setSymbol("BTC");
        token.setPrice(BigDecimal.valueOf(50000.0));

        Asset asset = new Asset();
        asset.setToken(token);
        asset.setQuantity(BigDecimal.valueOf(1.0));

        // Act
        wallet.addAsset(asset);
        
        // Assert
        assertEquals(1, wallet.getAssets().size());
        assertEquals(asset, wallet.getAssets().getFirst());
    }
    
    @Test
    void testGetTotal() {
        // Arrange
        Wallet wallet = new Wallet();

        Token token1 = new Token();
        token1.setId("bitcoin");
        token1.setSymbol("BTC");
        token1.setPrice(BigDecimal.valueOf(50000.0));

        Asset asset1 = new Asset();
        asset1.setToken(token1);
        asset1.setQuantity(BigDecimal.valueOf(1.0));
        wallet.addAsset(asset1);

        Token token2 = new Token();
        token2.setId("ethereum");
        token2.setSymbol("ETH");
        token2.setPrice(BigDecimal.valueOf(3000.0));

        Asset asset2 = new Asset();
        asset2.setToken(token2);
        asset2.setQuantity(BigDecimal.valueOf(10.0));
        wallet.addAsset(asset2);
        
        // Act
        BigDecimal totalValue = wallet.getTotal();
        
        // Assert
        assertEquals(80000.0, totalValue.doubleValue());
    }
    
    @Test
    void testGetTotalWithEmptyWallet() {
        // Arrange
        Wallet wallet = new Wallet();
        
        // Act
        BigDecimal totalValue = wallet.getTotal();
        
        // Assert
        assertEquals(BigDecimal.ZERO, totalValue);
    }
}
