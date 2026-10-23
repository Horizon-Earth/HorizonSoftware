package br.edu.principal;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Sphere;

/**
 * O planeta Terra e o céu estrelado. O planeta fica parado no centro da cena;
 * quem se move é a câmera. Um marcador amarelo mostra o lugar escolhido.
 */
public class Planeta extends Group {

    public static final double RAIO = 200;

    private final Sphere marcador = criarMarcador();

    public Planeta() {
        getChildren().addAll(criarCeu(), criarTerra(), marcador);
    }

    /** Põe o marcador sobre a latitude e a longitude indicadas. */
    public void marcar(double latitude, double longitude) {
        double lat = Math.toRadians(latitude);
        double lon = Math.toRadians(longitude);
        // Mesma correspondência da textura da esfera do JavaFX: longitude 0 de frente para a câmera inicial, norte para cima
        double r = RAIO * 1.012;
        marcador.setTranslateX(r * Math.cos(lat) * Math.sin(lon));
        marcador.setTranslateY(-r * Math.sin(lat));
        marcador.setTranslateZ(-r * Math.cos(lat) * Math.cos(lon));
        marcador.setVisible(true);
    }

    public void limparMarcador() {
        marcador.setVisible(false);
    }

    /** O marcador "pulsa" devagar para chamar atenção. */
    public void animar(double segundos) {
        double escala = 1 + 0.3 * Math.sin(segundos * 4);
        marcador.setScaleX(escala);
        marcador.setScaleY(escala);
        marcador.setScaleZ(escala);
    }

    private Sphere criarTerra() {
        Sphere esfera = new Sphere(RAIO, 128);
        PhongMaterial material = new PhongMaterial();

        Image textura = carregar("/earth_texture.jpg");
        if (textura != null) {
            material.setDiffuseMap(textura);
        } else {
            material.setDiffuseColor(Color.rgb(40, 90, 160));
        }

        // Brilho só no oceano: o mapa especular é branco na água e preto na terra
        Image especular = carregar("/earth_specular.png");
        if (especular != null) {
            material.setSpecularMap(especular);
            material.setSpecularColor(Color.WHITE);
            material.setSpecularPower(40);
        }
        esfera.setMaterial(material);
        return esfera;
    }

    /** Esfera enorme em volta de tudo, com o céu estrelado por dentro. */
    private Sphere criarCeu() {
        Sphere cupula = new Sphere(4000, 64);
        cupula.setCullFace(CullFace.FRONT);
        PhongMaterial material = new PhongMaterial(Color.BLACK);
        Image estrelas = carregar("/stars.jpg");
        if (estrelas != null) {
            material.setSelfIlluminationMap(estrelas);
        }
        cupula.setMaterial(material);
        return cupula;
    }

    private Sphere criarMarcador() {
        Sphere bolinha = new Sphere(3.6, 24);
        PhongMaterial material = new PhongMaterial(Color.web("#ffb547"));
        material.setSpecularColor(Color.web("#fff3d6"));
        bolinha.setMaterial(material);
        bolinha.setVisible(false);
        return bolinha;
    }

    private Image carregar(String caminho) {
        try {
            var fluxo = getClass().getResourceAsStream(caminho);
            if (fluxo == null) {
                System.out.println("Aviso: imagem não encontrada em " + caminho + " -- usando visual simples no lugar.");
                return null;
            }
            return new Image(fluxo);
        } catch (Exception e) {
            System.out.println("Erro ao carregar " + caminho + ": " + e.getMessage());
            return null;
        }
    }
}
