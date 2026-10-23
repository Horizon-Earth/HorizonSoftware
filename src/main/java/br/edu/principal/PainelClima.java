package br.edu.principal;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

/**
 * Painel que aparece só depois que um lugar é escolhido:
 * clima de agora, previsão de 7 dias e riscos dos próximos dias.
 */
public class PainelClima extends VBox {

    public static final double LARGURA = 320;

    private static final String FUNDO = "#0e1d3f";
    private static final String CARTAO = "#14264d";
    private static final String BORDA = "#243a6a";
    private static final String TEXTO = "#eaf2ff";
    private static final String APAGADO = "#9fb0cf";
    private static final String AZUL = "#6fa8ff";
    private static final String AMBAR = "#ffb547";
    private static final String VERDE = "#4ade80";
    private static final String VERMELHO = "#ff6b4a";

    private static final DecimalFormat COORDENADA =
            new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.forLanguageTag("pt-BR")));

    private final VBox corpo = new VBox(14);
    private final Label nome = rotulo("", 28, FontWeight.BOLD, TEXTO);
    private final Label subtitulo = rotulo("", 14, FontWeight.NORMAL, APAGADO);
    private final Label coordenadas = rotulo("", 13, FontWeight.MEDIUM, AZUL);

    public PainelClima(Runnable aoFechar) {
        setPrefWidth(LARGURA);
        setMinWidth(LARGURA);
        setMaxWidth(LARGURA);
        setStyle("-fx-background-color: " + FUNDO + "; -fx-border-color: " + BORDA + "; -fx-border-width: 0 0 0 1;");
        setPadding(new Insets(22, 24, 18, 24));
        setSpacing(14);

        Button fechar = new Button("×");
        fechar.setStyle("-fx-background-color: transparent; -fx-text-fill: " + APAGADO
                + "; -fx-font-size: 22px; -fx-cursor: hand; -fx-padding: 0 4 0 4;");
        fechar.setOnAction(e -> aoFechar.run());

        VBox titulos = new VBox(3, nome, subtitulo, coordenadas);
        nome.setWrapText(true);
        nome.setMaxWidth(LARGURA - 80);
        HBox.setHgrow(titulos, Priority.ALWAYS);
        HBox topo = new HBox(titulos, fechar);
        topo.setAlignment(Pos.TOP_LEFT);

        ScrollPane rolagem = new ScrollPane(corpo);
        rolagem.setFitToWidth(true);
        rolagem.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        rolagem.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(rolagem, Priority.ALWAYS);

        getChildren().addAll(topo, rolagem);
    }

    /** Mostra só o nome do lugar e a mensagem de carregamento. */
    public void mostrarCarregando(Local local) {
        cabecalho(local);
        corpo.getChildren().setAll(rotulo("Carregando o clima…", 15, FontWeight.NORMAL, APAGADO));
    }

    /** Mostra uma mensagem de erro com um botão para tentar de novo. */
    public void mostrarErro(Local local, Runnable tentarDeNovo) {
        cabecalho(local);
        Label aviso = rotulo("Não foi possível carregar o clima. Verifique a conexão com a internet.", 15, FontWeight.NORMAL, TEXTO);
        aviso.setWrapText(true);
        Button botao = new Button("Tentar de novo");
        botao.setStyle("-fx-background-color: transparent; -fx-border-color: #2c4478; -fx-border-radius: 10; -fx-background-radius: 10;"
                + " -fx-text-fill: " + TEXTO + "; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 9 16 9 16;");
        botao.setOnAction(e -> tentarDeNovo.run());
        corpo.getChildren().setAll(aviso, botao);
    }

    /** Mostra o clima de agora, os 7 dias e os riscos. */
    public void mostrar(Local local, Previsao previsao, AvaliadorRisco.Avaliacao avaliacao) {
        cabecalho(local);
        corpo.getChildren().setAll(
                cartaoAgora(previsao.agora()),
                blocoDias(previsao.dias()),
                blocoRiscos(avaliacao),
                rotulo("Dados: Open-Meteo.com", 11, FontWeight.NORMAL, APAGADO));
    }

    private void cabecalho(Local local) {
        nome.setText(local.nome());
        subtitulo.setText(local.subtitulo());
        coordenadas.setText(formatarCoordenadas(local.latitude(), local.longitude()));
    }

    private VBox cartaoAgora(Clima c) {
        Label temperatura = rotulo(graus(c.temperatura()), 46, FontWeight.BOLD, TEXTO);
        Label descricao = rotulo(CodigoTempo.descricao(c.codigoTempo()), 15, FontWeight.MEDIUM, TEXTO);
        VBox esquerda = new VBox(2, temperatura, descricao);
        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);
        HBox linhaPrincipal = new HBox(esquerda, espaco, IconeTempo.criar(CodigoTempo.tipo(c.codigoTempo()), 48));
        linhaPrincipal.setAlignment(Pos.CENTER_LEFT);

        HBox detalhes = new HBox(
                dado("Sensação", graus(c.sensacao())), crescer(),
                dado("Vento", numeroOuTraco(c.vento()) + " km/h"), crescer(),
                dado("Umidade", numeroOuTraco(c.umidade()) + "%"));

        VBox cartao = new VBox(12, linhaPrincipal, detalhes);
        cartao.setPadding(new Insets(14, 16, 14, 16));
        cartao.setStyle("-fx-background-color: " + CARTAO + "; -fx-background-radius: 14; -fx-border-color: " + BORDA
                + "; -fx-border-radius: 14;");
        return cartao;
    }

    private VBox blocoDias(List<PrevisaoDia> dias) {
        HBox linha = new HBox();
        for (int i = 0; i < dias.size(); i++) {
            PrevisaoDia d = dias.get(i);
            String nomeDia = i == 0 ? "Hoje" : AvaliadorRisco.diaDaSemana(d.data().getDayOfWeek(), true);
            VBox coluna = new VBox(5,
                    rotulo(nomeDia, 12, FontWeight.MEDIUM, APAGADO),
                    IconeTempo.criar(CodigoTempo.tipo(d.codigoTempo()), 20),
                    rotulo(graus(d.maxima()), 14, FontWeight.BOLD, TEXTO),
                    rotulo(graus(d.minima()), 12, FontWeight.NORMAL, APAGADO));
            coluna.setAlignment(Pos.TOP_CENTER);
            HBox.setHgrow(coluna, Priority.ALWAYS);
            coluna.setMaxWidth(Double.MAX_VALUE);
            linha.getChildren().add(coluna);
        }
        return new VBox(8, rotulo("Próximos 7 dias", 13, FontWeight.BOLD, APAGADO), linha);
    }

    private VBox blocoRiscos(AvaliadorRisco.Avaliacao avaliacao) {
        Label etiqueta = new Label(avaliacao.nivel().rotulo());
        etiqueta.setFont(Font.font(null, FontWeight.BOLD, 13));
        etiqueta.setTextFill(Color.web(avaliacao.nivel() == NivelRisco.ALTO ? "#2b0a02" : avaliacao.nivel() == NivelRisco.MEDIO ? "#3a2500" : "#032b12"));
        etiqueta.setPadding(new Insets(4, 12, 4, 12));
        etiqueta.setStyle("-fx-background-color: " + corDoNivel(avaliacao.nivel()) + "; -fx-background-radius: 999;");

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);
        HBox cabecalho = new HBox(rotulo("Riscos dos próximos dias", 13, FontWeight.BOLD, APAGADO), espaco, etiqueta);
        cabecalho.setAlignment(Pos.CENTER_LEFT);

        VBox bloco = new VBox(11, cabecalho);
        for (Aviso a : avaliacao.avisos()) {
            Region barra = new Region();
            barra.setMinWidth(3);
            barra.setMaxWidth(3);
            barra.setStyle("-fx-background-color: " + corDoNivel(a.nivel()) + "; -fx-background-radius: 2;");
            Label titulo = rotulo(a.titulo(), 15, FontWeight.BOLD, TEXTO);
            Label detalhe = rotulo(a.detalhe(), 13, FontWeight.NORMAL, APAGADO);
            titulo.setWrapText(true);
            detalhe.setWrapText(true);
            HBox item = new HBox(12, barra, new VBox(2, titulo, detalhe));
            bloco.getChildren().add(item);
        }
        return bloco;
    }

    private static String corDoNivel(NivelRisco nivel) {
        return switch (nivel) {
            case BAIXO -> VERDE;
            case MEDIO -> AMBAR;
            case ALTO -> VERMELHO;
        };
    }

    private static VBox dado(String titulo, String valor) {
        return new VBox(2, rotulo(titulo, 12, FontWeight.NORMAL, APAGADO), rotulo(valor, 14, FontWeight.BOLD, TEXTO));
    }

    private static Region crescer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }

    private static Label rotulo(String texto, double tamanho, FontWeight peso, String cor) {
        Label l = new Label(texto);
        l.setFont(Font.font(null, peso, tamanho));
        l.setTextFill(Color.web(cor));
        return l;
    }

    private static String graus(double valor) {
        return Double.isNaN(valor) ? "–" : Math.round(valor) + "°";
    }

    private static String numeroOuTraco(double valor) {
        return Double.isNaN(valor) ? "–" : String.valueOf(Math.round(valor));
    }

    /** Ex.: "23,55°S · 46,63°O". */
    static String formatarCoordenadas(double lat, double lon) {
        return COORDENADA.format(Math.abs(lat)) + "°" + (lat < 0 ? "S" : "N")
                + " · " + COORDENADA.format(Math.abs(lon)) + "°" + (lon < 0 ? "O" : "L");
    }
}
