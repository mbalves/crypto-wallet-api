package org.mbalves.sp.crypto.wallet.adapter.rest.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Custom serializer for MoneyValue to format it to 2 decimal places.
 */
public class MoneyValueSerializer extends JsonSerializer<BigDecimal> {
    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeNumber(value.setScale(2, RoundingMode.HALF_UP));
    }
}
