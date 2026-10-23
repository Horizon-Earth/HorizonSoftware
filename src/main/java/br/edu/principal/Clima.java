package br.edu.principal;

/** O clima de agora em um lugar. */
public record Clima(double temperatura, double sensacao, double umidade, double vento, int codigoTempo, boolean dia) {
}
