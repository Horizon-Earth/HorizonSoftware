package br.edu.principal;

import java.time.LocalDate;

/** A previsão de um dia: tempo, temperaturas, chuva acumulada e rajadas de vento. */
public record PrevisaoDia(LocalDate data, int codigoTempo, double maxima, double minima, double chuvaMm, double rajadaKmh) {
}
