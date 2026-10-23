package br.edu.principal;

/** Um aviso de risco, como "Chuva forte prevista", com o dia e o valor previsto. */
public record Aviso(NivelRisco nivel, String titulo, String detalhe) {
}
