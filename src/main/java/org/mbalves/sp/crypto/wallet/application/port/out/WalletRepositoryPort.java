package org.mbalves.sp.crypto.wallet.application.port.out;

import org.mbalves.sp.crypto.wallet.domain.Wallet;

import java.util.List;
import java.util.Optional;

public interface WalletRepositoryPort {
    Wallet save(Wallet wallet);
    Optional<Wallet> findByEmail(String email);
    Optional<Wallet> findById(Long id);
    List<Wallet> findAll();
    void deleteById(Long id);
}
