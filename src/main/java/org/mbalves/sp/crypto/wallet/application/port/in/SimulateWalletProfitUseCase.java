package org.mbalves.sp.crypto.wallet.application.port.in;

import org.mbalves.sp.crypto.wallet.domain.AssetSimulation;
import org.mbalves.sp.crypto.wallet.domain.WalletSimulationResult;

import java.time.LocalDate;
import java.util.List;

public interface SimulateWalletProfitUseCase {
    WalletSimulationResult simulateProfit(List<AssetSimulation> assets, LocalDate date);
}
