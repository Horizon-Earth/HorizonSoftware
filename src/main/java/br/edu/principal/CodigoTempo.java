package br.edu.principal;

/** Traduz os códigos de tempo da OMM (usados pelo Open-Meteo) para texto e tipo de ícone. */
public final class CodigoTempo {

    public enum Tipo { SOL, NUVEM, CHUVA, NEVE, TEMPESTADE }

    private CodigoTempo() {
    }

    public static String descricao(int codigo) {
        return switch (codigo) {
            case 0 -> "Céu limpo";
            case 1 -> "Quase sem nuvens";
            case 2 -> "Parcialmente nublado";
            case 3 -> "Nublado";
            case 45, 48 -> "Neblina";
            case 51, 53, 55 -> "Garoa";
            case 56, 57 -> "Garoa congelante";
            case 61 -> "Chuva fraca";
            case 63 -> "Chuva moderada";
            case 65 -> "Chuva forte";
            case 66, 67 -> "Chuva congelante";
            case 71 -> "Neve fraca";
            case 73 -> "Neve moderada";
            case 75 -> "Neve forte";
            case 77 -> "Grãos de neve";
            case 80 -> "Pancadas de chuva fracas";
            case 81 -> "Pancadas de chuva";
            case 82 -> "Pancadas de chuva fortes";
            case 85, 86 -> "Pancadas de neve";
            case 95 -> "Tempestade";
            case 96, 99 -> "Tempestade com granizo";
            default -> "Tempo variável";
        };
    }

    public static Tipo tipo(int codigo) {
        if (codigo <= 1) return Tipo.SOL;
        if (codigo <= 3 || codigo == 45 || codigo == 48) return Tipo.NUVEM;
        if (codigo >= 95) return Tipo.TEMPESTADE;
        if ((codigo >= 71 && codigo <= 77) || codigo == 85 || codigo == 86) return Tipo.NEVE;
        return Tipo.CHUVA;
    }
}
