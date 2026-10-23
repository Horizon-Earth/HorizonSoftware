package br.edu.principal;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Transforma a previsão dos próximos dias em avisos de risco.
 * Os limites abaixo são uma regra simples da equipe e podem ser ajustados.
 */
public final class AvaliadorRisco {

    // Chuva acumulada em um dia (mm)
    static final double CHUVA_FORTE = 50;
    static final double CHUVA_MUITO_FORTE = 100;
    // Rajadas de vento (km/h)
    static final double VENTO_FORTE = 60;
    static final double VENTO_MUITO_FORTE = 90;
    // Temperaturas (°C)
    static final double CALOR_EXTREMO = 38;
    static final double CALOR_MUITO_EXTREMO = 42;
    static final double FRIO_INTENSO = 0;
    static final double FRIO_MUITO_INTENSO = -10;

    public record Avaliacao(NivelRisco nivel, List<Aviso> avisos) {
    }

    private AvaliadorRisco() {
    }

    public static Avaliacao avaliar(List<PrevisaoDia> dias) {
        List<Aviso> avisos = new ArrayList<>();
        if (dias.isEmpty()) {
            return new Avaliacao(NivelRisco.BAIXO, avisos);
        }

        // Chuva: o dia com mais chuva
        int iChuva = indiceDoMaior(dias, true, PrevisaoDia::chuvaMm);
        if (iChuva >= 0) {
            double mm = dias.get(iChuva).chuvaMm();
            if (mm >= CHUVA_MUITO_FORTE) {
                avisos.add(new Aviso(NivelRisco.ALTO, "Chuva muito forte prevista", nomeDoDia(dias, iChuva) + " · cerca de " + Math.round(mm) + " mm em 24 h"));
            } else if (mm >= CHUVA_FORTE) {
                avisos.add(new Aviso(NivelRisco.MEDIO, "Chuva forte prevista", nomeDoDia(dias, iChuva) + " · cerca de " + Math.round(mm) + " mm em 24 h"));
            }
        }

        // Vento: o dia com a maior rajada
        int iVento = indiceDoMaior(dias, true, PrevisaoDia::rajadaKmh);
        if (iVento >= 0) {
            double kmh = dias.get(iVento).rajadaKmh();
            if (kmh >= VENTO_MUITO_FORTE) {
                avisos.add(new Aviso(NivelRisco.ALTO, "Vento muito forte", nomeDoDia(dias, iVento) + " · rajadas de até " + Math.round(kmh) + " km/h"));
            } else if (kmh >= VENTO_FORTE) {
                avisos.add(new Aviso(NivelRisco.MEDIO, "Vento forte", nomeDoDia(dias, iVento) + " · rajadas de até " + Math.round(kmh) + " km/h"));
            }
        }

        // Calor: o dia mais quente
        int iCalor = indiceDoMaior(dias, true, PrevisaoDia::maxima);
        if (iCalor >= 0) {
            double graus = dias.get(iCalor).maxima();
            if (graus >= CALOR_MUITO_EXTREMO) {
                avisos.add(new Aviso(NivelRisco.ALTO, "Calor extremo", nomeDoDia(dias, iCalor) + " · máxima de " + Math.round(graus) + "°"));
            } else if (graus >= CALOR_EXTREMO) {
                avisos.add(new Aviso(NivelRisco.MEDIO, "Calor intenso", nomeDoDia(dias, iCalor) + " · máxima de " + Math.round(graus) + "°"));
            }
        }

        // Frio: o dia mais frio
        int iFrio = indiceDoMaior(dias, false, PrevisaoDia::minima);
        if (iFrio >= 0) {
            double graus = dias.get(iFrio).minima();
            if (graus <= FRIO_MUITO_INTENSO) {
                avisos.add(new Aviso(NivelRisco.ALTO, "Frio extremo", nomeDoDia(dias, iFrio) + " · mínima de " + Math.round(graus) + "°"));
            } else if (graus <= FRIO_INTENSO) {
                avisos.add(new Aviso(NivelRisco.MEDIO, "Frio intenso", nomeDoDia(dias, iFrio) + " · mínima de " + Math.round(graus) + "°"));
            }
        }

        // Tempestade: o primeiro dia com trovoada
        for (int i = 0; i < dias.size(); i++) {
            int codigo = dias.get(i).codigoTempo();
            if (codigo >= 95) {
                String detalhe = codigo == 95 ? "raios e trovoadas" : "raios, trovoadas e granizo";
                avisos.add(new Aviso(NivelRisco.MEDIO, "Tempestade prevista", nomeDoDia(dias, i) + " · " + detalhe));
                break;
            }
        }

        if (avisos.isEmpty()) {
            avisos.add(new Aviso(NivelRisco.BAIXO, "Nenhum risco previsto",
                    "Sem chuva forte, vento forte, calor ou frio extremos nos próximos " + dias.size() + " dias"));
            return new Avaliacao(NivelRisco.BAIXO, avisos);
        }

        // Os mais graves primeiro; mostramos no máximo 3
        avisos.sort(Comparator.comparing(Aviso::nivel).reversed());
        List<Aviso> principais = new ArrayList<>(avisos.subList(0, Math.min(3, avisos.size())));
        NivelRisco geral = principais.stream().map(Aviso::nivel).max(Comparator.naturalOrder()).orElse(NivelRisco.BAIXO);
        return new Avaliacao(geral, principais);
    }

    /** Posição do dia com o maior (ou menor) valor, ignorando dias sem dado. -1 se não houver nenhum. */
    private interface Medida {
        double de(PrevisaoDia dia);
    }

    private static int indiceDoMaior(List<PrevisaoDia> dias, boolean maior, Medida medida) {
        int melhor = -1;
        for (int i = 0; i < dias.size(); i++) {
            double v = medida.de(dias.get(i));
            if (Double.isNaN(v)) {
                continue;
            }
            if (melhor < 0 || (maior ? v > medida.de(dias.get(melhor)) : v < medida.de(dias.get(melhor)))) {
                melhor = i;
            }
        }
        return melhor;
    }

    /** "Hoje", "Amanhã" ou o dia da semana ("Quinta"). */
    static String nomeDoDia(List<PrevisaoDia> dias, int indice) {
        if (indice == 0) return "Hoje";
        if (indice == 1) return "Amanhã";
        return diaDaSemana(dias.get(indice).data().getDayOfWeek(), false);
    }

    /** Nome do dia da semana em português: "Quinta" (completo) ou "Qui" (curto). */
    static String diaDaSemana(DayOfWeek dia, boolean curto) {
        Locale pt = Locale.forLanguageTag("pt-BR");
        String texto = dia.getDisplayName(curto ? TextStyle.SHORT : TextStyle.FULL, pt);
        texto = texto.replace(".", "");
        int hifen = texto.indexOf('-');
        if (hifen > 0) {
            texto = texto.substring(0, hifen);
        }
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }
}
