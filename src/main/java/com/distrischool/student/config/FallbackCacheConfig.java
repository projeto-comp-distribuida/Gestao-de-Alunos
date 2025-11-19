package com.distrischool.student.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;

/**
 * Configuração de fallback para cache quando Redis não está disponível.
 * Usa cache em memória (ConcurrentMapCacheManager) como alternativa.
 */
@Configuration
@ConditionalOnMissingBean(name = "redisCacheManager")
@Slf4j
public class FallbackCacheConfig {

    @Bean(name = "fallbackCacheManager")
    @Primary
    public CacheManager fallbackCacheManager() {
        log.warn("Redis não está disponível. Usando cache em memória (ConcurrentMapCacheManager) como fallback.");
        log.warn("O cache será local à instância da aplicação e não será compartilhado entre instâncias.");
        
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(Arrays.asList("students"));
        cacheManager.setAllowNullValues(false);
        
        log.info("ConcurrentMapCacheManager configurado com sucesso. Cache 'students' disponível.");
        return cacheManager;
    }
}

