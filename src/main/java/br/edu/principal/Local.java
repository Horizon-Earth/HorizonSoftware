package br.edu.principal;

/** Um lugar do planeta, encontrado pela busca. */
public record Local(String nome, String estado, String pais, double latitude, double longitude) {

    /** Texto completo, como aparece na caixa de busca: "Cidade, Estado, País". */
    public String rotulo() {
        StringBuilder sb = new StringBuilder(nome);
        if (estado != null && !estado.isBlank() && !estado.equals(nome)) {
            sb.append(", ").append(estado);
        }
        if (pais != null && !pais.isBlank()) {
            sb.append(", ").append(pais);
        }
        return sb.toString();
    }

    /** Texto curto, abaixo do nome: "Estado · País". */
    public String subtitulo() {
        boolean temEstado = estado != null && !estado.isBlank() && !estado.equals(nome);
        boolean temPais = pais != null && !pais.isBlank();
        if (temEstado && temPais) return estado + " · " + pais;
        if (temPais) return pais;
        if (temEstado) return estado;
        return "";
    }
}
