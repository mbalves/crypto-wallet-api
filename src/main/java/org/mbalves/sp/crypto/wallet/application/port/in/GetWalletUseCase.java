package org.mbalves.sp.crypto.wallet.application.port.in;

import org.mbalves.sp.crypto.wallet.domain.Wallet;

public interface GetWalletUseCase {
    Wallet getWallet(Long walletId);
}
