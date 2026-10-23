package br.edu.principal;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

/** Busca o clima de agora e a previsão de 7 dias de um lugar, usando o Open-Meteo. */
public class ServicoClima {

    private static final String ENDERECO = "https://api.open-meteo.com/v1/forecast";

    public CompletableFuture<Previsao> buscar(Local local) {
        String url = String.format(Locale.ROOT,
                "%s?latitude=%.4f&longitude=%.4f"
                        + "&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,weather_code,wind_speed_10m"
                        + "&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum,wind_gusts_10m_max"
                        + "&timezone=auto&forecast_days=7",
                ENDERECO, local.latitude(), local.longitude());
        return ClienteHttp.baixar(URI.create(url)).thenApply(ServicoClima::interpretar);
    }

    /** Lê o JSON da resposta e monta o clima de agora e os dias. */
    static Previsao interpretar(String json) {
        JsonObject raiz = JsonParser.parseString(json).getAsJsonObject();

        JsonObject c = raiz.getAsJsonObject("current");
        Clima agora = new Clima(
                numero(c, "temperature_2m"),
                numero(c, "apparent_temperature"),
                numero(c, "relative_humidity_2m"),
                numero(c, "wind_speed_10m"),
                (int) Math.round(numero(c, "weather_code")),
                numero(c, "is_day") == 1);

        JsonObject d = raiz.getAsJsonObject("daily");
        JsonArray datas = d.getAsJsonArray("time");
        List<PrevisaoDia> dias = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            dias.add(new PrevisaoDia(
                    LocalDate.parse(datas.get(i).getAsString()),
                    (int) Math.round(valor(d, "weather_code", i)),
                    valor(d, "temperature_2m_max", i),
                    valor(d, "temperature_2m_min", i),
                    valor(d, "precipitation_sum", i),
                    valor(d, "wind_gusts_10m_max", i)));
        }
        return new Previsao(agora, dias);
    }

    /** Número de um campo simples; NaN se estiver faltando. */
    private static double numero(JsonObject o, String chave) {
        JsonElement e = o.get(chave);
        return (e == null || e.isJsonNull()) ? Double.NaN : e.getAsDouble();
    }

    /** Número da posição i de uma lista; NaN se estiver faltando. */
    private static double valor(JsonObject o, String chave, int i) {
        JsonElement lista = o.get(chave);
        if (lista == null || !lista.isJsonArray() || i >= lista.getAsJsonArray().size()) {
            return Double.NaN;
        }
        JsonElement e = lista.getAsJsonArray().get(i);
        return e.isJsonNull() ? Double.NaN : e.getAsDouble();
    }
}
