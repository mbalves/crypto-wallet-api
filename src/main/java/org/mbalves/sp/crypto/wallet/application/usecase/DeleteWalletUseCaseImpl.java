package org.mbalves.sp.crypto.wallet.application.usecase;

import lombok.RequiredArgsConstructor;
import org.mbalves.sp.crypto.wallet.application.port.in.DeleteWalletUseCase;
import org.mbalves.sp.crypto.wallet.application.port.out.WalletRepositoryPort;
import org.mbalves.sp.crypto.wallet.domain.exception.WalletNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the DeleteWalletUseCase interface.
 * This use case handles the deletion of cryptocurrency wallets.
 * It ensures that the wallet exists before deletion and handles the transaction.
 * It's aligned to the GDPR principles of data protection and user rights.
 *
 * @author Marcelo Alves
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class DeleteWalletUseCaseImpl implements DeleteWalletUseCase {
    private final WalletRepositoryPort walletRepository;

    /**
     * Deletes a wallet by its ID.
     * This method implements the following business rules:
     * 1. Validates wallet existence
     * 2. Deletes the wallet and all its associated assets
     * 3. Handles the operation in a transaction
     *
     * @param walletId The ID of the wallet to delete
     * @throws WalletNotFoundException if the wallet doesn't exist
     */
    @Override
    @Transactional
    public void deleteWallet(Long walletId) {
        if (walletRepository.findById(walletId).isEmpty()) {
            throw new WalletNotFoundException(walletId);
        }
        walletRepository.deleteById(walletId);
    }
}
