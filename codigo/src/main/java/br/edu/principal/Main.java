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
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

/**
 * Horizon Earth - primeira versão: apenas o globo 3D.
 *
 * Mostra uma esfera com o mapa-múndi que pode ser girada arrastando o mouse
 * e ter o zoom ajustado com o scroll.
 *
 * A textura fica em src/main/resources/earth_texture.jpg (imagem
 * equirretangular, proporção 2:1). Sem a imagem, o globo aparece azul sólido.
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
        Sphere earth = buildEarth();

        // Só o globo gira; as luzes ficam paradas para dar a sensação de volume
        Group globe = new Group(earth);
        globe.getTransforms().addAll(rotateX, rotateY);

        PointLight sun = new PointLight(Color.WHITE);
        sun.setTranslateX(-400);
        sun.setTranslateY(-300);
        sun.setTranslateZ(-700);

        AmbientLight ambient = new AmbientLight(Color.rgb(50, 55, 70));

        Group root = new Group(globe, sun, ambient);

        Scene scene = new Scene(root, 900, 650, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.rgb(10, 10, 20));

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-700);
        camera.setNearClip(0.1);
        camera.setFarClip(2000);
        scene.setCamera(camera);

        attachMouseControls(scene);
        attachZoomControl(scene, camera);

        stage.setTitle("Horizon Earth - Globo 3D");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Cria a esfera e aplica a textura do planeta.
     * Se a imagem não for encontrada, usa uma cor sólida como fallback,
     * assim o projeto ainda roda mesmo sem a textura configurada.
     */
    private Sphere buildEarth() {
        Sphere sphere = new Sphere(SPHERE_RADIUS);
        PhongMaterial material = new PhongMaterial();

        Image texture = loadTexture("/earth_texture.jpg");
        if (texture != null) {
            material.setDiffuseMap(texture);
        } else {
            material.setDiffuseColor(Color.rgb(40, 90, 160));
        }
        material.setSpecularColor(Color.rgb(180, 200, 255));
        material.setSpecularPower(25);

        sphere.setMaterial(material);

        // Opcional: descomente a linha abaixo (e o import de DrawMode)
        // para ver o globo como uma malha de linhas e notar a rotação.
        // sphere.setDrawMode(javafx.scene.shape.DrawMode.LINE);

        return sphere;
    }

    private Image loadTexture(String resourcePath) {
        try {
            var stream = getClass().getResourceAsStream(resourcePath);
            if (stream == null) {
                System.out.println("Aviso: textura não encontrada em " + resourcePath
                        + " -- usando cor sólida como fallback.");
                return null;
            }
            return new Image(stream);
        } catch (Exception e) {
            System.out.println("Erro ao carregar textura: " + e.getMessage());
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
            newZ = Math.max(-1500, Math.min(-250, newZ));
            camera.setTranslateZ(newZ);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
