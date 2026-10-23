package br.edu.principal;

import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SubScene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * Câmera que orbita o planeta, como no Google Earth: arrastar leva a câmera em volta,
 * o scroll aproxima e afasta, e há inércia, rotação automática e voo suave até um lugar.
 * A luz acompanha a câmera, então o lado visível fica sempre iluminado.
 */
public class CameraOrbital {

    private static final double RAIO = Planeta.RAIO;
    private static final double DIST_MINIMA = 250;
    private static final double DIST_MAXIMA = 1600;
    private static final double DIST_INICIAL = 720;
    private static final double DIST_LUGAR = 520;

    private static final double LON_INICIAL = -40;
    private static final double LAT_INICIAL = 5;

    private static final double GRAUS_POR_PIXEL = 0.30;
    private static final double DECAIMENTO_INERCIA = 3.2;
    private static final double VELOCIDADE_SEGUIR = 11;
    private static final double VELOCIDADE_ZOOM = 8;
    private static final double VELOCIDADE_VOO = 3.4;
    private static final double GIRO_AUTO_GRAUS_POR_SEG = 1.6;
    private static final double GIRO_AUTO_ESPERA_SEG = 5;
    private static final double INCLINACAO_MAXIMA = 85;

    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final PointLight luz = new PointLight(Color.WHITE);

    // A câmera orbita com: giro em Y (yaw), giro em X (pitch) e afastamento em Z
    private final Rotate yaw = new Rotate(0, Rotate.Y_AXIS);
    private final Rotate pitch = new Rotate(0, Rotate.X_AXIS);
    private final Translate afastamento = new Translate(0, 0, -DIST_INICIAL);

    // A luz usa os mesmos giros, um pouco acima e à esquerda da câmera
    private final Rotate luzYaw = new Rotate(0, Rotate.Y_AXIS);
    private final Rotate luzPitch = new Rotate(0, Rotate.X_AXIS);
    private final Translate luzPosicao = new Translate(-420, -320, -DIST_INICIAL - 100);

    private double alvoYaw = -LON_INICIAL;
    private double alvoPitch = -LAT_INICIAL;
    private double alvoDist = DIST_INICIAL;
    private double atualYaw = alvoYaw;
    private double atualPitch = alvoPitch;
    private double atualDist = alvoDist;

    private boolean arrastando;
    private double ultimoX;
    private double ultimoY;
    private long ultimoArrastoNanos;
    private long ultimaInteracaoNanos = System.nanoTime();
    private double velYaw;
    private double velPitch;

    private boolean giroAutomatico = true;
    private boolean voando;
    private long inicioVooNanos;

    public CameraOrbital() {
        camera.setNearClip(1);
        camera.setFarClip(10000);
        camera.getTransforms().addAll(yaw, pitch, afastamento);
        luz.getTransforms().addAll(luzYaw, luzPitch, luzPosicao);
        aplicar();
    }

    public PerspectiveCamera camera() {
        return camera;
    }

    public PointLight luz() {
        return luz;
    }

    /** Liga o mouse (arrastar, scroll e duplo clique) a uma subcena 3D. */
    public void conectar(SubScene cena) {
        cena.setOnMousePressed((MouseEvent e) -> {
            arrastando = true;
            voando = false;
            ultimoX = e.getSceneX();
            ultimoY = e.getSceneY();
            ultimoArrastoNanos = System.nanoTime();
            ultimaInteracaoNanos = ultimoArrastoNanos;
            velYaw = 0;
            velPitch = 0;
        });

        cena.setOnMouseDragged((MouseEvent e) -> {
            long agora = System.nanoTime();
            double dx = e.getSceneX() - ultimoX;
            double dy = e.getSceneY() - ultimoY;
            ultimoX = e.getSceneX();
            ultimoY = e.getSceneY();

            // perto do planeta o arrasto fica mais lento e preciso
            double fator = limitar((atualDist - RAIO) / (DIST_INICIAL - RAIO), 0.12, 2.0);
            double sens = GRAUS_POR_PIXEL * fator;

            double dYaw = dx * sens;      // arrastar para a direita: a câmera vai para a esquerda
            double dPitch = -dy * sens;   // arrastar para cima: a câmera desce
            alvoYaw += dYaw;
            alvoPitch = limitar(alvoPitch + dPitch, -INCLINACAO_MAXIMA, INCLINACAO_MAXIMA);

            double dtEvento = Math.max((agora - ultimoArrastoNanos) / 1e9, 0.008);
            velYaw = 0.5 * velYaw + 0.5 * (dYaw / dtEvento);
            velPitch = 0.5 * velPitch + 0.5 * (dPitch / dtEvento);
            ultimoArrastoNanos = agora;
            ultimaInteracaoNanos = agora;
        });

        cena.setOnMouseReleased((MouseEvent e) -> {
            long agora = System.nanoTime();
            arrastando = false;
            ultimaInteracaoNanos = agora;
            if ((agora - ultimoArrastoNanos) / 1e9 > 0.08) { // parou antes de soltar: sem inércia
                velYaw = 0;
                velPitch = 0;
            }
        });

        // duplo clique: volta para a vista inicial
        cena.setOnMouseClicked((MouseEvent e) -> {
            if (e.getClickCount() == 2) {
                alvoYaw = equivalenteMaisPerto(-LON_INICIAL, atualYaw);
                alvoPitch = -LAT_INICIAL;
                alvoDist = DIST_INICIAL;
                velYaw = 0;
                velPitch = 0;
                voando = false;
                ultimaInteracaoNanos = System.nanoTime();
            }
        });

        cena.addEventHandler(ScrollEvent.SCROLL, e -> {
            voando = false;
            alvoDist = limitar(alvoDist * Math.exp(-e.getDeltaY() * 0.002), DIST_MINIMA, DIST_MAXIMA);
            ultimaInteracaoNanos = System.nanoTime();
        });
    }

    /** Leva a câmera suavemente até ficar de frente para o ponto indicado. */
    public void voarPara(double latitude, double longitude) {
        // com a esfera do JavaFX, a câmera olha para (lat, lon) quando yaw = -lon e pitch = -lat
        alvoYaw = equivalenteMaisPerto(-longitude, atualYaw);
        alvoPitch = limitar(-latitude, -INCLINACAO_MAXIMA, INCLINACAO_MAXIMA);
        alvoDist = DIST_LUGAR;
        velYaw = 0;
        velPitch = 0;
        voando = true;
        inicioVooNanos = System.nanoTime();
        ultimaInteracaoNanos = inicioVooNanos;
    }

    /** Liga ou desliga a rotação lenta que acontece quando ninguém mexe. */
    public void definirGiroAutomatico(boolean ligado) {
        giroAutomatico = ligado;
        ultimaInteracaoNanos = System.nanoTime();
    }

    /** Roda a cada quadro: inércia, rotação automática e suavização do movimento. */
    public void atualizar(double dt, long agora) {
        if (!arrastando && !voando) {
            alvoYaw += velYaw * dt;
            alvoPitch += velPitch * dt;
            double decaimento = Math.exp(-DECAIMENTO_INERCIA * dt);
            velYaw *= decaimento;
            velPitch *= decaimento;

            double parado = (agora - ultimaInteracaoNanos) / 1e9;
            if (giroAutomatico && parado > GIRO_AUTO_ESPERA_SEG && Math.abs(velYaw) < 1 && Math.abs(velPitch) < 1) {
                double suave = Math.min(1, (parado - GIRO_AUTO_ESPERA_SEG) / 2);
                alvoYaw += GIRO_AUTO_GRAUS_POR_SEG * suave * dt;
            }
        }
        alvoPitch = limitar(alvoPitch, -INCLINACAO_MAXIMA, INCLINACAO_MAXIMA);

        double seguir = 1 - Math.exp(-(voando ? VELOCIDADE_VOO : VELOCIDADE_SEGUIR) * dt);
        double seguirZoom = 1 - Math.exp(-(voando ? VELOCIDADE_VOO : VELOCIDADE_ZOOM) * dt);
        atualYaw += (alvoYaw - atualYaw) * seguir;
        atualPitch += (alvoPitch - atualPitch) * seguir;
        atualDist += (alvoDist - atualDist) * seguirZoom;

        if (voando) {
            boolean chegou = Math.abs(alvoYaw - atualYaw) < 0.3 && Math.abs(alvoPitch - atualPitch) < 0.3
                    && Math.abs(alvoDist - atualDist) < 2;
            if (chegou || (agora - inicioVooNanos) / 1e9 > 3.5) {
                voando = false;
            }
        }
        aplicar();
    }

    private void aplicar() {
        yaw.setAngle(atualYaw);
        pitch.setAngle(atualPitch);
        afastamento.setZ(-atualDist);

        luzYaw.setAngle(atualYaw);
        luzPitch.setAngle(atualPitch);
        luzPosicao.setZ(-atualDist - 100);
    }

    private static double limitar(double valor, double minimo, double maximo) {
        return Math.max(minimo, Math.min(maximo, valor));
    }

    /** Escolhe o ângulo equivalente (mais voltas de 360°) mais perto do atual, para não dar voltas à toa. */
    private static double equivalenteMaisPerto(double angulo, double referencia) {
        double voltas = Math.round((referencia - angulo) / 360.0);
        return angulo + voltas * 360.0;
    }
}
