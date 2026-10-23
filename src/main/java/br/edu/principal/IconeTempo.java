package br.edu.principal;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.transform.Scale;

/** Desenha os ícones do tempo (sol, nuvem, chuva, neve e tempestade) com linhas finas. */
public final class IconeTempo {

    private static final String NUVEM_BAIXA = "M7 18h10a4 4 0 0 0 .4-8 5.5 5.5 0 0 0-10.6-1.2A4.5 4.5 0 0 0 7 18z";
    private static final String NUVEM_ALTA = "M7 16h10a3.8 3.8 0 0 0 .4-7.6A5.5 5.5 0 0 0 6.9 9.4 3.3 3.3 0 0 0 7 16z";

    private IconeTempo() {
    }

    /** Cria o ícone em um quadrado de tamanho x tamanho pixels. */
    public static Pane criar(CodigoTempo.Tipo tipo, double tamanho) {
        String desenho;
        Color cor;
        switch (tipo) {
            case SOL -> {
                desenho = "M8 12a4 4 0 1 0 8 0a4 4 0 1 0 -8 0 M12 3v2 M12 19v2 M3 12h2 M19 12h2 "
                        + "M5.6 5.6l1.4 1.4 M17 17l1.4 1.4 M5.6 18.4L7 17 M17 7l1.4-1.4";
                cor = Color.web("#ffb547");
            }
            case NUVEM -> {
                desenho = NUVEM_BAIXA;
                cor = Color.web("#9fb0cf");
            }
            case CHUVA -> {
                desenho = NUVEM_ALTA + " M8 19l-1 2 M12 19l-1 2 M16 19l-1 2";
                cor = Color.web("#6fa8ff");
            }
            case NEVE -> {
                desenho = NUVEM_ALTA + " M8 19h.01 M12 21h.01 M16 19h.01";
                cor = Color.web("#cfe0ff");
            }
            default -> {
                desenho = NUVEM_ALTA + " M13 16l-3 4h3.5l-2 3";
                cor = Color.web("#ffb547");
            }
        }
        SVGPath forma = new SVGPath();
        forma.setContent(desenho);
        forma.setFill(null);
        forma.setStroke(cor);
        forma.setStrokeWidth(1.8);
        forma.setStrokeLineCap(StrokeLineCap.ROUND);
        forma.setStrokeLineJoin(StrokeLineJoin.ROUND);

        Group grupo = new Group(forma);
        double escala = tamanho / 24.0;
        grupo.getTransforms().add(new Scale(escala, escala, 0, 0));

        Pane caixa = new Pane(grupo);
        caixa.setMinSize(tamanho, tamanho);
        caixa.setPrefSize(tamanho, tamanho);
        caixa.setMaxSize(tamanho, tamanho);
        return caixa;
    }
}
