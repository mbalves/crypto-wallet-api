package org.mbalves.sp.crypto.wallet.adapter.rest.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class JacksonConfig {
    @Bean
    public SimpleModule bigDecimalModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(BigDecimal.class, new BigDecimalSerializer());
        module.addSerializer(BigDecimal.class, new MoneyValueSerializer());
        return module;
    }
}
