package com.distrischool.student.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Decodificador de erros customizado para Feign
 * Trata diferentes tipos de erros HTTP e exceções de rede
 */
@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        log.warn("Erro na chamada Feign - Método: {}, Status: {}, Reason: {}", 
                 methodKey, response.status(), response.reason());

        // Trata erros específicos
        switch (response.status()) {
            case 400:
                return new RuntimeException("Requisição inválida para " + methodKey);
            case 401:
                return new RuntimeException("Não autorizado para " + methodKey);
            case 403:
                return new RuntimeException("Acesso negado para " + methodKey);
            case 404:
                return new RuntimeException("Recurso não encontrado: " + methodKey);
            case 500:
            case 502:
            case 503:
            case 504:
                return new RuntimeException("Erro no serviço remoto: " + methodKey + " - Status: " + response.status());
            default:
                return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}

