package br.edu.principal;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/** Faz as consultas à internet em segundo plano, para a janela nunca travar. */
public final class ClienteHttp {

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .build();

    private ClienteHttp() {
    }

    /** Baixa o texto de um endereço sem bloquear. Falha com IOException se a resposta não for 200. */
    public static CompletableFuture<String> baixar(URI endereco) {
        HttpRequest pedido = HttpRequest.newBuilder(endereco)
                .timeout(Duration.ofSeconds(12))
                .header("Accept", "application/json")
                .GET()
                .build();
        return HTTP.sendAsync(pedido, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .thenApply(resposta -> {
                    if (resposta.statusCode() != 200) {
                        throw new RuntimeException(new IOException("Resposta HTTP " + resposta.statusCode()));
                    }
                    return resposta.body();
                });
    }
}
