package br.edu.principal;

import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Horizon Earth - V.1.3.0: busca de lugares e clima real.
 *
 * O usuário busca uma cidade, estado ou país; a câmera voa até o lugar, um marcador aparece
 * e o painel mostra o clima de agora, a previsão de 7 dias e os riscos dos próximos dias.
 * O painel só aparece depois que um lugar é escolhido.
 *
 * Dados: Open-Meteo.com (busca de lugares e clima).
 */
public class Main extends Application {

    private final ServicoGeocodificacao geocodificacao = new ServicoGeocodificacao();
    private final ServicoClima servicoClima = new ServicoClima();

    // Quanto da largura está reservado para o painel (0 = fechado). O planeta se recentra sozinho.
    private final DoubleProperty reservado = new SimpleDoubleProperty(0);

    private Planeta planeta;
    private CameraOrbital camera;
    private PainelClima painel;
    private BarraBusca barra;
    private Local localAtual;

    @Override
    public void start(Stage stage) {
        planeta = new Planeta();
        camera = new CameraOrbital();

        AmbientLight ambiente = new AmbientLight(Color.rgb(45, 50, 65));
        Group raiz3d = new Group(planeta, camera.luz(), ambiente, camera.camera());

        SubScene cena3d = new SubScene(raiz3d, 1100, 700, true, SceneAntialiasing.BALANCED);
        cena3d.setFill(Color.BLACK);
        cena3d.setCamera(camera.camera());
        camera.conectar(cena3d);

        painel = new PainelClima(this::fecharPainel);
        painel.setVisible(false);
        painel.translateXProperty().bind(reservado.multiply(-1).add(PainelClima.LARGURA));

        barra = new BarraBusca(geocodificacao, this::escolher);

        Label nomeDoProjeto = new Label("Horizon Earth");
        nomeDoProjeto.setFont(Font.font(null, FontWeight.BOLD, 22));
        nomeDoProjeto.setTextFill(Color.web("#eaf2ff"));
        HBox topo = new HBox(24, nomeDoProjeto, barra);
        topo.setAlignment(Pos.TOP_LEFT);
        topo.setPickOnBounds(false);

        AnchorPane sobreposicao = new AnchorPane(topo, painel);
        sobreposicao.setPickOnBounds(false);
        AnchorPane.setTopAnchor(topo, 20.0);
        AnchorPane.setLeftAnchor(topo, 32.0);
        AnchorPane.setTopAnchor(painel, 0.0);
        AnchorPane.setBottomAnchor(painel, 0.0);
        AnchorPane.setRightAnchor(painel, 0.0);
        nomeDoProjeto.setTranslateY(10);

        StackPane raiz = new StackPane(cena3d, sobreposicao);
        StackPane.setAlignment(cena3d, Pos.TOP_LEFT);
        cena3d.widthProperty().bind(raiz.widthProperty().subtract(reservado));
        cena3d.heightProperty().bind(raiz.heightProperty());

        Scene cena = new Scene(raiz, 1100, 700);
        cena.setFill(Color.BLACK);

        new AnimationTimer() {
            private long ultimo;
            private double tempo;

            @Override
            public void handle(long agora) {
                if (ultimo == 0) {
                    ultimo = agora;
                    return;
                }
                double dt = Math.min((agora - ultimo) / 1e9, 0.05);
                ultimo = agora;
                tempo += dt;
                camera.atualizar(dt, agora);
                planeta.animar(tempo);
            }
        }.start();

        stage.setTitle("Horizon Earth - Planeta 3D");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setScene(cena);
        stage.show();
    }

    /** Chamado quando o usuário escolhe um lugar na lista da busca. */
    private void escolher(Local local) {
        localAtual = local;
        planeta.marcar(local.latitude(), local.longitude());
        camera.definirGiroAutomatico(false);
        camera.voarPara(local.latitude(), local.longitude());
        painel.mostrarCarregando(local);
        abrirPainel();
        carregarClima(local);
    }

    private void carregarClima(Local local) {
        servicoClima.buscar(local).whenComplete((previsao, erro) -> Platform.runLater(() -> {
            if (local != localAtual) {
                return; // o usuário já escolheu outro lugar
            }
            if (erro != null) {
                painel.mostrarErro(local, () -> {
                    painel.mostrarCarregando(local);
                    carregarClima(local);
                });
            } else {
                painel.mostrar(local, previsao, AvaliadorRisco.avaliar(previsao.dias()));
            }
        }));
    }

    private void abrirPainel() {
        painel.setVisible(true);
        animarReservado(PainelClima.LARGURA, null);
    }

    private void fecharPainel() {
        localAtual = null;
        planeta.limparMarcador();
        camera.definirGiroAutomatico(true);
        barra.limpar();
        animarReservado(0, () -> painel.setVisible(false));
    }

    private void animarReservado(double alvo, Runnable aoTerminar) {
        Timeline linha = new Timeline(new KeyFrame(Duration.millis(450), new KeyValue(reservado, alvo, Interpolator.EASE_BOTH)));
        if (aoTerminar != null) {
            linha.setOnFinished(e -> aoTerminar.run());
        }
        linha.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
