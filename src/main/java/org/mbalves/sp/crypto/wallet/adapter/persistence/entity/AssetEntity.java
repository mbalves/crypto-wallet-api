package org.mbalves.sp.crypto.wallet.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "asset")
@Data
public class AssetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal quantity;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private WalletEntity wallet;

    @ManyToOne
    @JoinColumn(name = "token_id", nullable = false)
    private TokenEntity token;
}
