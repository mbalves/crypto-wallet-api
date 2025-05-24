package org.mbalves.sp.crypto.wallet.application.port.out;

import org.mbalves.sp.crypto.wallet.domain.Token;

import java.util.List;
import java.util.Optional;

public interface TokenRepositoryPort {
    Token save(Token token);
    Optional<Token> findBySymbol(String symbol);
    List<Token> findAll();
}
