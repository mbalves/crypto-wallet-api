package org.mbalves.sp.crypto.wallet.application.usecase;

import lombok.RequiredArgsConstructor;
import org.mbalves.sp.crypto.wallet.application.port.in.CreateWalletUseCase;
import org.mbalves.sp.crypto.wallet.application.port.out.WalletRepositoryPort;
import org.mbalves.sp.crypto.wallet.domain.Wallet;
import org.mbalves.sp.crypto.wallet.domain.exception.WalletAlreadyExistsException;
import org.springframework.stereotype.Service;

/**
 * Implementation of the CreateWalletUseCase interface.
 * This use case handles the creation of new cryptocurrency wallets.
 * It ensures that each user (identified by email) can only have one wallet.
 *
 * @author Marcelo Alves
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class CreateWalletUseCaseImpl implements CreateWalletUseCase {
    private final WalletRepositoryPort walletRepository;

    /**
     * Creates a new wallet for the given email address.
     * This method implements the following business rules:
     * 1. Validates that the user doesn't already have a wallet
     * 2. Creates a new wallet with the provided email
     * 3. Persists the wallet in the repository
     *
     * @param email The email address of the wallet owner
     * @return The newly created wallet
     * @throws WalletAlreadyExistsException if a wallet already exists for the email
     */
    @Override
    public Wallet createWallet(String email) {
        // A user has only one wallet
        if (walletRepository.findByEmail(email).isPresent()) {
            throw new WalletAlreadyExistsException(email);
        }
        Wallet wallet = new Wallet();
        wallet.setEmail(email);
        return walletRepository.save(wallet);
    }
}
