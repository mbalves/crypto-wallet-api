package org.mbalves.sp.crypto.wallet.adapter.rest.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoneyValueSerializerTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule()
                .addSerializer(BigDecimal.class, new MoneyValueSerializer()));
    }

    @Test
    void testSerialize() throws JsonProcessingException {
        String json = mapper.writeValueAsString(new BigDecimal("123.4506789"));
        assertEquals("123.45", json);
    }

}
