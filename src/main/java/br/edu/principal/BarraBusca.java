package br.edu.principal;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Caixa de busca de lugares, com a lista de resultados logo abaixo. */
public class BarraBusca extends VBox {

    private static final String ESTILO_CAMPO = "-fx-background-color: rgba(20,38,77,0.92); -fx-text-fill: #eaf2ff;"
            + " -fx-prompt-text-fill: #8190ad; -fx-font-size: 15px; -fx-background-radius: 23; -fx-border-radius: 23;"
            + " -fx-border-color: #2c4478; -fx-padding: 0 18 0 18;";
    private static final String ESTILO_LINHA = "-fx-background-color: transparent; -fx-cursor: hand;";
    private static final String ESTILO_LINHA_ATIVA = "-fx-background-color: #2c4478; -fx-cursor: hand; -fx-background-radius: 8;";

    private final ServicoGeocodificacao servico;
    private final Consumer<Local> aoEscolher;
    private final TextField campo = new TextField();
    private final VBox lista = new VBox(2);
    private final PauseTransition espera = new PauseTransition(Duration.millis(350));
    private final List<Local> resultados = new ArrayList<>();
    private final List<VBox> linhas = new ArrayList<>();

    private boolean mudandoPorCodigo;
    private long busca;
    private int ativo = -1;

    public BarraBusca(ServicoGeocodificacao servico, Consumer<Local> aoEscolher) {
        this.servico = servico;
        this.aoEscolher = aoEscolher;
        setSpacing(6);
        setPickOnBounds(false);
        setPrefWidth(440);
        setMaxWidth(440);

        campo.setPromptText("Buscar cidade, estado ou país");
        campo.setPrefHeight(46);
        campo.setStyle(ESTILO_CAMPO);

        lista.setPadding(new Insets(6));
        lista.setStyle("-fx-background-color: #14264d; -fx-background-radius: 14; -fx-border-color: #2c4478; -fx-border-radius: 14;");
        lista.setVisible(false);
        lista.setManaged(false);

        getChildren().addAll(campo, lista);

        espera.setOnFinished(e -> pesquisar());
        campo.textProperty().addListener((obs, antes, depois) -> {
            if (mudandoPorCodigo) {
                return;
            }
            busca++;
            if (depois.trim().length() < 2) {
                espera.stop();
                esconderLista();
            } else {
                espera.playFromStart();
            }
        });

        campo.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (!resultados.isEmpty()) {
                    escolher(resultados.get(ativo >= 0 ? ativo : 0));
                } else {
                    espera.stop();
                    pesquisar();
                }
            } else if (e.getCode() == KeyCode.DOWN) {
                mover(1);
                e.consume();
            } else if (e.getCode() == KeyCode.UP) {
                mover(-1);
                e.consume();
            } else if (e.getCode() == KeyCode.ESCAPE) {
                esconderLista();
            }
        });
    }

    /** Apaga o texto e fecha a lista. */
    public void limpar() {
        mudandoPorCodigo = true;
        campo.clear();
        mudandoPorCodigo = false;
        busca++;
        esconderLista();
    }

    private void pesquisar() {
        String texto = campo.getText();
        long minha = ++busca;
        servico.buscar(texto).whenComplete((lugares, erro) -> Platform.runLater(() -> {
            if (minha != busca) {
                return; // chegou depois de o usuário já ter digitado outra coisa
            }
            if (erro != null) {
                mostrarMensagem("Não foi possível buscar. Verifique a internet.");
            } else if (lugares.isEmpty()) {
                mostrarMensagem("Nenhum lugar encontrado.");
            } else {
                mostrarResultados(lugares);
            }
        }));
    }

    private void mostrarResultados(List<Local> lugares) {
        resultados.clear();
        resultados.addAll(lugares);
        linhas.clear();
        lista.getChildren().clear();
        ativo = -1;
        for (int i = 0; i < lugares.size(); i++) {
            Local l = lugares.get(i);
            Label nome = texto(l.nome(), 15, FontWeight.BOLD, "#eaf2ff");
            Label sub = texto(l.subtitulo(), 12, FontWeight.NORMAL, "#9fb0cf");
            VBox linha = new VBox(1, nome, sub);
            linha.setPadding(new Insets(7, 12, 7, 12));
            linha.setStyle(ESTILO_LINHA);
            final int indice = i;
            linha.setOnMouseEntered(e -> destacar(indice));
            linha.setOnMouseClicked(e -> escolher(lugares.get(indice)));
            linhas.add(linha);
            lista.getChildren().add(linha);
        }
        exibirLista();
    }

    private void mostrarMensagem(String mensagem) {
        resultados.clear();
        linhas.clear();
        lista.getChildren().setAll(texto(mensagem, 14, FontWeight.NORMAL, "#9fb0cf"));
        lista.getChildren().get(0).setStyle("-fx-padding: 8 12 8 12;");
        ativo = -1;
        exibirLista();
    }

    private void mover(int passo) {
        if (linhas.isEmpty()) {
            return;
        }
        int novo = ativo + passo;
        if (novo < 0) novo = linhas.size() - 1;
        if (novo >= linhas.size()) novo = 0;
        destacar(novo);
    }

    private void destacar(int indice) {
        if (ativo >= 0 && ativo < linhas.size()) {
            linhas.get(ativo).setStyle(ESTILO_LINHA);
        }
        ativo = indice;
        linhas.get(indice).setStyle(ESTILO_LINHA_ATIVA);
    }

    private void escolher(Local local) {
        espera.stop();
        busca++;
        mudandoPorCodigo = true;
        campo.setText(local.rotulo());
        campo.positionCaret(campo.getText().length());
        mudandoPorCodigo = false;
        esconderLista();
        aoEscolher.accept(local);
    }

    private void exibirLista() {
        lista.setVisible(true);
        lista.setManaged(true);
    }

    private void esconderLista() {
        resultados.clear();
        linhas.clear();
        ativo = -1;
        lista.setVisible(false);
        lista.setManaged(false);
    }

    private static Label texto(String t, double tamanho, FontWeight peso, String cor) {
        Label l = new Label(t);
        l.setFont(Font.font(null, peso, tamanho));
        l.setTextFill(Color.web(cor));
        return l;
    }
}
