package org.mbalves.sp.crypto.wallet.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "token")
@Data
public class TokenEntity {

    @Id
    @Column(nullable = false)
    private String id;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private BigDecimal price;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant lastUpdated;
    
    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        if (lastUpdated == null) {
            lastUpdated = Instant.now();
        }
    }
}
