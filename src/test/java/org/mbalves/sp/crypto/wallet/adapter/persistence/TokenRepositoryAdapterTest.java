package org.mbalves.sp.crypto.wallet.adapter.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mbalves.sp.crypto.wallet.adapter.persistence.entity.TokenEntity;
import org.mbalves.sp.crypto.wallet.adapter.persistence.repository.TokenJpaRepository;
import org.mbalves.sp.crypto.wallet.domain.Token;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenRepositoryAdapterTest {

    @Mock
    private TokenJpaRepository tokenRepository;

    @InjectMocks
    private TokenRepositoryAdapter tokenRepositoryAdapter;

    private Token token;
    private TokenEntity tokenEntity;

    @BeforeEach
    void setUp() {
        token = new Token();
        token.setId("bitcoin");
        token.setSymbol("BTC");
        token.setPrice(BigDecimal.valueOf(50000.0));
        token.setLastUpdated(Instant.now());

        tokenEntity = new TokenEntity();
        tokenEntity.setId("bitcoin");
        tokenEntity.setSymbol("BTC");
        tokenEntity.setPrice(BigDecimal.valueOf(50000.0));
        tokenEntity.setLastUpdated(token.getLastUpdated());
    }

    @Test
    void save_ShouldConvertDomainToEntityAndBack() {
        when(tokenRepository.save(any(TokenEntity.class))).thenReturn(tokenEntity);

        Token saved = tokenRepositoryAdapter.save(token);

        assertNotNull(saved);
        assertEquals(token.getId(), saved.getId());
        assertEquals(token.getSymbol(), saved.getSymbol());
        assertEquals(token.getPrice(), saved.getPrice());
        verify(tokenRepository).save(any(TokenEntity.class));
    }

    @Test
    void save_WithoutDate_ShouldConvertDomainToEntityAndBack() {
        token.setLastUpdated(null);
        when(tokenRepository.save(any(TokenEntity.class))).thenReturn(tokenEntity);

        Token saved = tokenRepositoryAdapter.save(token);

        assertNotNull(saved);
        assertEquals(token.getId(), saved.getId());
        assertEquals(token.getSymbol(), saved.getSymbol());
        assertEquals(token.getPrice(), saved.getPrice());
        verify(tokenRepository).save(any(TokenEntity.class));
    }

    @Test
    void findBySymbol_WhenTokenExists_ShouldReturnToken() {
        when(tokenRepository.findById("BTC")).thenReturn(Optional.of(tokenEntity));

        Optional<Token> result = tokenRepositoryAdapter.findBySymbol("BTC");

        assertTrue(result.isPresent());
        assertEquals("BTC", result.get().getSymbol());
        verify(tokenRepository).findById("BTC");
    }

    @Test
    void findBySymbol_WhenTokenDoesNotExist_ShouldReturnEmpty() {
        when(tokenRepository.findById("BTC")).thenReturn(Optional.empty());

        Optional<Token> result = tokenRepositoryAdapter.findBySymbol("BTC");

        assertFalse(result.isPresent());
        verify(tokenRepository).findById("BTC");
    }

    @Test
    void findAll_WhenTokensExist_ShouldReturnList() {
        when(tokenRepository.findAll()).thenReturn(List.of(tokenEntity));

        List<Token> result = tokenRepositoryAdapter.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("BTC", result.getFirst().getSymbol());
        verify(tokenRepository).findAll();
    }

    @Test
    void findAll_WhenNoTokensExist_ShouldReturnEmptyList() {
        when(tokenRepository.findAll()).thenReturn(Collections.emptyList());

        List<Token> result = tokenRepositoryAdapter.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(tokenRepository).findAll();
    }
}
