package org.mbalves.sp.crypto.wallet.adapter.persistence;

import lombok.RequiredArgsConstructor;
import org.mbalves.sp.crypto.wallet.adapter.persistence.entity.TokenEntity;
import org.mbalves.sp.crypto.wallet.adapter.persistence.repository.TokenJpaRepository;
import org.mbalves.sp.crypto.wallet.application.port.out.TokenRepositoryPort;
import org.mbalves.sp.crypto.wallet.domain.Token;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter for token persistence operations.
 * Implements the {@link TokenRepositoryPort} interface.
 * Handles conversion between domain and entity models for tokens.
 * Delegates database operations to the {@link TokenJpaRepository}.
 *
 * @author Marcelo Alves
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class TokenRepositoryAdapter implements TokenRepositoryPort {
    private final TokenJpaRepository tokenJpaRepository;

    @Override
    public Token save(Token token) {
        TokenEntity entity = toEntity(token);
        entity = tokenJpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<Token> findBySymbol(String symbol) {
        return tokenJpaRepository.findById(symbol).map(this::toDomain);
    }

    @Override
    public List<Token> findAll() {
        return tokenJpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    private TokenEntity toEntity(Token token) {
        TokenEntity entity = new TokenEntity();
        entity.setId(token.getId());
        entity.setSymbol(token.getSymbol());
        entity.setPrice(token.getPrice());
        if (token.getLastUpdated() != null) {
            entity.setLastUpdated(token.getLastUpdated());
        }
        return entity;
    }

    private Token toDomain(TokenEntity entity) {
        Token token = new Token();
        token.setId(entity.getId());
        token.setSymbol(entity.getSymbol());
        token.setPrice(entity.getPrice());
        token.setLastUpdated(entity.getLastUpdated());
        return token;
    }

}
