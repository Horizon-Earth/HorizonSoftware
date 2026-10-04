# 🌍 Terra3d — Horizon Earth

Protótipo do planeta 3D em JavaFX, desenvolvido como base da visualização ambiental do Horizon Earth.

## Funcionalidades atuais

- Esfera com textura do planeta.
- Mapa especular para o brilho dos oceanos.
- Céu estrelado, iluminação e câmera em perspectiva.
- Rotação e inclinação ao arrastar o mouse.
- Zoom pela roda do mouse.
- Visual simples de fallback quando uma textura não é encontrada.

O código atual não consulta APIs nem identifica regiões clicadas. A seleção de regiões e a consulta de clima e desastres são objetivos da próxima etapa.

## Tecnologias

Java 17, JavaFX 21.0.2 e Maven. É necessário um ambiente gráfico com suporte a JavaFX 3D.

## Como executar

```bash
git clone https://github.com/Horizon-Earth/Terra3d.git
cd Terra3d/codigo
mvn clean compile javafx:run
```

No NetBeans ou em outra IDE com suporte Maven, abra a pasta `codigo/`, que contém `pom.xml`, e execute a meta `javafx:run`.

## Organização

| Caminho | Conteúdo |
| --- | --- |
| `codigo/pom.xml` | Dependências e execução Maven |
| `codigo/src/main/java/br/edu/principal/Main.java` | Cena 3D e controles |
| `codigo/src/main/resources/earth_texture.jpg` | Textura do planeta |
| `codigo/src/main/resources/earth_specular.png` | Mapa de brilho |
| `codigo/src/main/resources/stars.jpg` | Fundo estrelado |

## Próximas etapas

Identificar coordenadas no globo, selecionar regiões, integrar APIs e conectar o protótipo ao login e ao menu principal. Documentar a origem e as condições de uso de cada textura utilizada.

## Equipe

Projeto acadêmico de Programação Orientada a Objetos — IFCE, Campus Maranguape, 2026.2.

| Integrante | Área | GitHub |
| --- | --- | --- |
| MiguelStack | FullStack | [MiguelStack](https://github.com/MiguelStack) |

## Contribuição

Crie uma branch para a alteração, faça commits claros e abra um pull request. Confira a execução e os testes disponíveis antes de integrar à `main`.

## Licença

Código e documentação próprios da Horizon Earth distribuídos sob a [licença MIT](LICENSE). Materiais externos, imagens, texturas e dados de APIs mantêm suas licenças e atribuições originais.
