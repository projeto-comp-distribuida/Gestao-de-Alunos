package com.distrischool.student.config;

import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuração do Feign Client para comunicação entre microserviços
 * Inclui configurações de timeout, retry e tratamento de erros
 */
@Configuration
public class FeignConfig {

    /**
     * Configuração de timeout para requisições Feign
     * Connect timeout: 5 segundos
     * Read timeout: 10 segundos
     */
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
            5, TimeUnit.SECONDS,  // Connect timeout
            10, TimeUnit.SECONDS,  // Read timeout
            true                   // Follow redirects
        );
    }

    /**
     * Configuração de retry para requisições Feign
     * Retry até 3 vezes com intervalo exponencial
     */
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(
            100,                    // Initial interval: 100ms
            TimeUnit.SECONDS.toMillis(1),  // Max interval: 1s
            3                        // Max attempts: 3
        );
    }

    /**
     * Decodificador de erros customizado para melhor tratamento de exceções
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
}

