package org.mbalves.sp.crypto.wallet.application.usecase;

import lombok.RequiredArgsConstructor;
import org.mbalves.sp.crypto.wallet.application.port.in.GetWalletUseCase;
import org.mbalves.sp.crypto.wallet.application.port.out.WalletRepositoryPort;
import org.mbalves.sp.crypto.wallet.domain.Wallet;
import org.mbalves.sp.crypto.wallet.domain.exception.WalletNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of the GetWalletUseCase interface.
 * This use case handles retrieving wallet information by ID.
 * It ensures that the wallet exists before returning it.
 *
 * @author Marcelo Alves
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class GetWalletUseCaseImpl implements GetWalletUseCase {
    private final WalletRepositoryPort walletRepository;

    /**
     * Retrieves the wallet for the given ID.
     * If the wallet does not exist, it throws a WalletNotFoundException.
     *
     * @param walletId The ID of the wallet to retrieve
     * @return The wallet with the specified ID
     * @throws WalletNotFoundException if the wallet does not exist
     */
    @Override
    public Wallet getWallet(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));
    }
}
