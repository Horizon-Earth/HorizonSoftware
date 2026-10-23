package br.edu.principal;

import java.util.List;

/** Tudo que o painel precisa: o clima de agora e a previsão dos próximos dias. */
public record Previsao(Clima agora, List<PrevisaoDia> dias) {
}
