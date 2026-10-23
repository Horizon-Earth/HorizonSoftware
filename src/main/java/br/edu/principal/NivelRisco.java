package br.edu.principal;

/** Nível de risco de uma área ou de um aviso. */
public enum NivelRisco {
    BAIXO("Risco baixo"),
    MEDIO("Risco médio"),
    ALTO("Risco alto");

    private final String rotulo;

    NivelRisco(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }
}
