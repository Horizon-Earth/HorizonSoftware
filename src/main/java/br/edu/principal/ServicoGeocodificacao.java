package br.edu.principal;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/** Transforma o texto digitado ("São Paulo") em lugares com coordenadas, usando o Open-Meteo. */
public class ServicoGeocodificacao {

    private static final String ENDERECO = "https://geocoding-api.open-meteo.com/v1/search";

    public CompletableFuture<List<Local>> buscar(String texto) {
        String termo = texto == null ? "" : texto.trim();
        if (termo.length() < 2) {
            return CompletableFuture.completedFuture(List.of());
        }
        URI uri = URI.create(ENDERECO + "?name=" + URLEncoder.encode(termo, StandardCharsets.UTF_8)
                + "&count=8&language=pt&format=json");
        return ClienteHttp.baixar(uri).thenApply(ServicoGeocodificacao::interpretar);
    }

    /** Lê o JSON da resposta. Quando nada é encontrado, a chave "results" nem existe. */
    static List<Local> interpretar(String json) {
        List<Local> lugares = new ArrayList<>();
        JsonObject raiz = JsonParser.parseString(json).getAsJsonObject();
        if (!raiz.has("results") || raiz.get("results").isJsonNull()) {
            return lugares;
        }
        for (JsonElement item : raiz.getAsJsonArray("results")) {
            JsonObject o = item.getAsJsonObject();
            if (!o.has("latitude") || !o.has("longitude")) {
                continue;
            }
            lugares.add(new Local(
                    texto(o, "name"),
                    texto(o, "admin1"),
                    texto(o, "country"),
                    o.get("latitude").getAsDouble(),
                    o.get("longitude").getAsDouble()));
        }
        return lugares;
    }

    private static String texto(JsonObject o, String chave) {
        JsonElement e = o.get(chave);
        return (e == null || e.isJsonNull()) ? null : e.getAsString();
    }
}
