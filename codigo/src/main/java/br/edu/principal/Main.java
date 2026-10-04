package br.edu.principal;

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
import javafx.stage.Stage;

/**
 * Horizon Earth - V.1.1.0: globo mais realista.
 *
 * Novidades desta versão:
 *  - Textura de satélite da NASA (Blue Marble) em alta resolução
 *  - Esfera mais lisa (mais divisões na malha)
 *  - Oceano com brilho e continentes foscos (mapa especular)
 *  - Céu estrelado ao fundo
 *
 * Controles: arraste o mouse para girar/inclinar e use o scroll para o zoom.
 *
 * Arquivos em src/main/resources (todos opcionais; sem eles o programa
 * continua rodando com cores simples):
 *   earth_texture.jpg   - mapa-múndi equirretangular (proporção 2:1)
 *   earth_specular.png  - mapa de brilho (branco = oceano brilhante)
 *   stars.jpg           - céu estrelado
 *
 * Para rodar no Eclipse: Run As > Maven build... > Goals: clean compile javafx:run
 */
public class Main extends Application {

    private static final double SPHERE_RADIUS = 200;

    // Controle de rotação do globo
    private final Rotate rotateX = new Rotate(-20, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(-30, Rotate.Y_AXIS);

    private double mouseAnchorX;
    private double mouseAnchorY;
    private double anchorAngleX;
    private double anchorAngleY;

    @Override
    public void start(Stage stage) {
        Sphere sky = buildSky();
        Sphere earth = buildEarth();

        // Só o globo gira; céu e luzes ficam parados
        Group globe = new Group(earth);
        globe.getTransforms().addAll(rotateX, rotateY);

        PointLight sun = new PointLight(Color.WHITE);
        sun.setTranslateX(-500);
        sun.setTranslateY(-300);
        sun.setTranslateZ(-800);

        AmbientLight ambient = new AmbientLight(Color.rgb(45, 50, 65));

        Group root = new Group(sky, globe, sun, ambient);

        Scene scene = new Scene(root, 900, 650, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.BLACK);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-700);
        camera.setNearClip(1);
        camera.setFarClip(10000);
        scene.setCamera(camera);

        attachMouseControls(scene);
        attachZoomControl(scene, camera);

        stage.setTitle("Horizon Earth - Globo 3D");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Cria a esfera do planeta com a textura de satélite.
     * Se a imagem não for encontrada, usa uma cor sólida como fallback.
     */
    private Sphere buildEarth() {
        Sphere sphere = new Sphere(SPHERE_RADIUS, 128);
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

    /**
     * Esfera enorme em volta de tudo, com o céu estrelado por dentro.
     * CullFace.FRONT faz aparecer o lado de dentro da esfera.
     */
    private Sphere buildSky() {
        Sphere dome = new Sphere(4000, 64);
        dome.setCullFace(CullFace.FRONT);

        PhongMaterial material = new PhongMaterial(Color.BLACK);
        Image stars = loadImage("/stars.jpg");
        if (stars != null) {
            // Autoiluminação: as estrelas brilham sem depender da luz da cena
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

    /** Permite girar o globo arrastando o mouse. */
    private void attachMouseControls(Scene scene) {
        scene.setOnMousePressed((MouseEvent event) -> {
            mouseAnchorX = event.getSceneX();
            mouseAnchorY = event.getSceneY();
            anchorAngleX = rotateX.getAngle();
            anchorAngleY = rotateY.getAngle();
        });

        scene.setOnMouseDragged((MouseEvent event) -> {
            double deltaX = event.getSceneX() - mouseAnchorX;
            double deltaY = event.getSceneY() - mouseAnchorY;

            // Arrastar para os lados gira o globo em volta do eixo dos polos
            rotateY.setAngle(anchorAngleY + deltaX * 0.3);

            // Arrastar para cima/baixo inclina o globo, limitado a +-90 graus
            // para ele nunca virar de cabeça para baixo (como no Google Earth)
            double tilt = anchorAngleX - deltaY * 0.3;
            rotateX.setAngle(Math.max(-90, Math.min(90, tilt)));
        });
    }

    /** Permite dar zoom com o scroll do mouse. */
    private void attachZoomControl(Scene scene, PerspectiveCamera camera) {
        scene.addEventHandler(ScrollEvent.SCROLL, event -> {
            double zoomDelta = event.getDeltaY() * 0.8;
            double newZ = camera.getTranslateZ() + zoomDelta;

            // Limita o quanto dá pra aproximar/afastar
            newZ = Math.max(-1500, Math.min(-300, newZ));
            camera.setTranslateZ(newZ);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
