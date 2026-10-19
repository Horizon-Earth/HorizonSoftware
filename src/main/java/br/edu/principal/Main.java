package br.edu.principal;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

/**
 * Horizon Earth - V.1.2.0: câmera orbital, como no Google Earth.
 *
 * O planeta fica PARADO no centro da cena. Quem se move é a câmera, que
 * anda em volta dele olhando sempre para o centro:
 *  - arrastar o mouse leva a câmera para os lados e para cima/baixo;
 *  - o scroll aproxima ou afasta a câmera;
 *  - ao soltar o mouse, a câmera continua deslizando e vai parando aos poucos;
 *  - sem mexer por alguns segundos, a câmera gira devagar sozinha;
 *  - duplo clique volta para a vista inicial (Atlântico, com o Brasil).
 *
 * Como o planeta não gira, cada ponto dele mantém a mesma latitude e
 * longitude, o que facilita descobrir a região clicada nas próximas versões.
 *
 * Imagens em resources (opcionais): earth_texture.jpg, earth_specular.png, stars.jpg.
 */
public class Main extends Application {

    private static final double RADIUS = 200;

    // Distâncias da câmera ao centro do planeta
    private static final double MIN_DIST = 250;
    private static final double MAX_DIST = 1600;
    private static final double START_DIST = 720;

    // Vista inicial: câmera sobre o Atlântico, perto do Brasil
    private static final double START_LON = -40;
    private static final double START_LAT = 5;

    // Ajustes de movimento
    private static final double DEGREES_PER_PIXEL = 0.30;   // à distância inicial
    private static final double INERTIA_DECAY = 3.2;         // maior = para mais rápido
    private static final double FOLLOW_SPEED = 11;           // suavização do giro
    private static final double ZOOM_FOLLOW_SPEED = 8;       // suavização do zoom
    private static final double AUTO_ROTATE_DEG_PER_SEC = 1.6;
    private static final double AUTO_ROTATE_DELAY_SEC = 5;
    private static final double MAX_PITCH = 85;

    // A câmera orbita com: giro em Y (yaw), giro em X (pitch) e afastamento em Z
    private final Rotate yaw = new Rotate(0, Rotate.Y_AXIS);
    private final Rotate pitch = new Rotate(0, Rotate.X_AXIS);
    private final Translate cameraOffset = new Translate(0, 0, -START_DIST);

    // A luz acompanha a câmera (mesmos giros), um pouco acima e à esquerda
    private final Rotate lightYaw = new Rotate(0, Rotate.Y_AXIS);
    private final Rotate lightPitch = new Rotate(0, Rotate.X_AXIS);
    private final Translate lightOffset = new Translate(-420, -320, -START_DIST - 100);

    // Valores alvo (o que o usuário pediu) e atuais (o que aparece na tela)
    private double targetYaw = -START_LON;
    private double targetPitch = -START_LAT;
    private double targetDist = START_DIST;
    private double curYaw = targetYaw;
    private double curPitch = targetPitch;
    private double curDist = targetDist;

    // Estado do mouse e da inércia
    private boolean dragging;
    private double lastX;
    private double lastY;
    private long lastDragNanos;
    private long lastInteractionNanos = System.nanoTime();
    private double velYaw;    // graus por segundo
    private double velPitch;

    @Override
    public void start(Stage stage) {
        Sphere sky = buildSky();
        Sphere earth = buildEarth();

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(1);
        camera.setFarClip(10000);
        camera.getTransforms().addAll(yaw, pitch, cameraOffset);

        PointLight sun = new PointLight(Color.WHITE);
        sun.getTransforms().addAll(lightYaw, lightPitch, lightOffset);

        AmbientLight ambient = new AmbientLight(Color.rgb(45, 50, 65));

        // O planeta e o céu ficam parados: só a câmera (e a luz) se movem
        Group root = new Group(sky, earth, sun, ambient, camera);

        Scene scene = new Scene(root, 900, 650, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.BLACK);
        scene.setCamera(camera);

        attachMouseControls(scene);
        attachZoomControl(scene);
        applyCamera();

        new AnimationTimer() {
            private long last;

            @Override
            public void handle(long now) {
                if (last == 0) {
                    last = now;
                    return;
                }
                double dt = Math.min((now - last) / 1e9, 0.05);
                last = now;
                update(dt, now);
            }
        }.start();

        stage.setTitle("Horizon Earth - Planeta 3D");
        stage.setScene(scene);
        stage.show();
    }

    /** Roda a cada quadro: inércia, rotação automática e suavização. */
    private void update(double dt, long now) {
        if (!dragging) {
            // inércia: a câmera continua andando e vai desacelerando
            targetYaw += velYaw * dt;
            targetPitch += velPitch * dt;
            double decay = Math.exp(-INERTIA_DECAY * dt);
            velYaw *= decay;
            velPitch *= decay;

            // rotação automática lenta depois de um tempo sem mexer
            double idle = (now - lastInteractionNanos) / 1e9;
            if (idle > AUTO_ROTATE_DELAY_SEC && Math.abs(velYaw) < 1 && Math.abs(velPitch) < 1) {
                double ease = Math.min(1, (idle - AUTO_ROTATE_DELAY_SEC) / 2);
                targetYaw += AUTO_ROTATE_DEG_PER_SEC * ease * dt;
            }
        }
        targetPitch = clamp(targetPitch, -MAX_PITCH, MAX_PITCH);

        // suavização: o valor atual persegue o alvo com uma curva exponencial
        double follow = 1 - Math.exp(-FOLLOW_SPEED * dt);
        double zoomFollow = 1 - Math.exp(-ZOOM_FOLLOW_SPEED * dt);
        curYaw += (targetYaw - curYaw) * follow;
        curPitch += (targetPitch - curPitch) * follow;
        curDist += (targetDist - curDist) * zoomFollow;

        applyCamera();
    }

    private void applyCamera() {
        yaw.setAngle(curYaw);
        pitch.setAngle(curPitch);
        cameraOffset.setZ(-curDist);

        lightYaw.setAngle(curYaw);
        lightPitch.setAngle(curPitch);
        lightOffset.setZ(-curDist - 100);
    }

    /** Arrastar com o mouse leva a câmera em volta do planeta. */
    private void attachMouseControls(Scene scene) {
        scene.setOnMousePressed((MouseEvent e) -> {
            dragging = true;
            lastX = e.getSceneX();
            lastY = e.getSceneY();
            lastDragNanos = System.nanoTime();
            lastInteractionNanos = lastDragNanos;
            velYaw = 0;
            velPitch = 0;
        });

        scene.setOnMouseDragged((MouseEvent e) -> {
            long now = System.nanoTime();
            double dx = e.getSceneX() - lastX;
            double dy = e.getSceneY() - lastY;
            lastX = e.getSceneX();
            lastY = e.getSceneY();

            // perto do planeta o arrasto fica mais lento e preciso
            double factor = clamp((curDist - RADIUS) / (START_DIST - RADIUS), 0.12, 2.0);
            double sens = DEGREES_PER_PIXEL * factor;

            double dYaw = dx * sens;      // arrastar para a direita: a câmera vai para a esquerda
            double dPitch = -dy * sens;   // arrastar para cima: a câmera desce
            targetYaw += dYaw;
            targetPitch = clamp(targetPitch + dPitch, -MAX_PITCH, MAX_PITCH);

            // velocidade do arrasto, usada na inércia ao soltar
            double dtEvent = Math.max((now - lastDragNanos) / 1e9, 0.008);
            velYaw = 0.5 * velYaw + 0.5 * (dYaw / dtEvent);
            velPitch = 0.5 * velPitch + 0.5 * (dPitch / dtEvent);
            lastDragNanos = now;
            lastInteractionNanos = now;
        });

        scene.setOnMouseReleased((MouseEvent e) -> {
            long now = System.nanoTime();
            dragging = false;
            lastInteractionNanos = now;
            // se o usuário parou antes de soltar, não há inércia
            if ((now - lastDragNanos) / 1e9 > 0.08) {
                velYaw = 0;
                velPitch = 0;
            }
        });

        // duplo clique: volta para a vista inicial
        scene.setOnMouseClicked((MouseEvent e) -> {
            if (e.getClickCount() == 2) {
                targetYaw = nearestEquivalent(-START_LON, curYaw);
                targetPitch = -START_LAT;
                targetDist = START_DIST;
                velYaw = 0;
                velPitch = 0;
                lastInteractionNanos = System.nanoTime();
            }
        });
    }

    /** O scroll aproxima ou afasta a câmera, com transição suave. */
    private void attachZoomControl(Scene scene) {
        scene.addEventHandler(ScrollEvent.SCROLL, e -> {
            targetDist = clamp(targetDist * Math.exp(-e.getDeltaY() * 0.002), MIN_DIST, MAX_DIST);
            lastInteractionNanos = System.nanoTime();
        });
    }

    /** Cria a esfera do planeta com a textura de satélite (ela não gira). */
    private Sphere buildEarth() {
        Sphere sphere = new Sphere(RADIUS, 128);
        PhongMaterial material = new PhongMaterial();

        Image texture = loadImage("/earth_texture.jpg");
        if (texture != null) {
            material.setDiffuseMap(texture);
        } else {
            material.setDiffuseColor(Color.rgb(40, 90, 160));
        }

        // Brilho só no oceano: o mapa especular é branco na água e preto na terra
        Image specular = loadImage("/earth_specular.png");
        if (specular != null) {
            material.setSpecularMap(specular);
            material.setSpecularColor(Color.WHITE);
            material.setSpecularPower(40);
        }

        sphere.setMaterial(material);
        return sphere;
    }

    /** Esfera enorme em volta de tudo, com o céu estrelado por dentro. */
    private Sphere buildSky() {
        Sphere dome = new Sphere(4000, 64);
        dome.setCullFace(CullFace.FRONT);

        PhongMaterial material = new PhongMaterial(Color.BLACK);
        Image stars = loadImage("/stars.jpg");
        if (stars != null) {
            material.setSelfIlluminationMap(stars);
        }
        dome.setMaterial(material);
        return dome;
    }

    private Image loadImage(String resourcePath) {
        try {
            var stream = getClass().getResourceAsStream(resourcePath);
            if (stream == null) {
                System.out.println("Aviso: imagem não encontrada em " + resourcePath
                        + " -- usando visual simples no lugar.");
                return null;
            }
            return new Image(stream);
        } catch (Exception e) {
            System.out.println("Erro ao carregar " + resourcePath + ": " + e.getMessage());
            return null;
        }
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /** Escolhe o ângulo equivalente (mais 360 graus) mais perto do atual, para não dar voltas à toa. */
    private static double nearestEquivalent(double angle, double reference) {
        double turns = Math.round((reference - angle) / 360.0);
        return angle + turns * 360.0;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
