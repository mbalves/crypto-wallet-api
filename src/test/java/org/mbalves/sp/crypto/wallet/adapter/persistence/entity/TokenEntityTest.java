package org.mbalves.sp.crypto.wallet.adapter.persistence.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TokenEntityTest {

    @Test
    void updateTimestamp_nullValue() {
        TokenEntity token = new TokenEntity();
        assertNull(token.getLastUpdated());

        token.updateTimestamp();

        assertNotNull(token.getLastUpdated());
    }
    @Test
    void updateTimestamp_nonNullValue() {
        TokenEntity token = new TokenEntity();
        Instant previousTimestamp = Instant.now().minus(1, ChronoUnit.DAYS);
        token.setLastUpdated(previousTimestamp);

        token.updateTimestamp();

        assertEquals(previousTimestamp, token.getLastUpdated());
    }
}
