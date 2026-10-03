# Horizon Earth — Globo 3D (primeira versão)

Esqueleto inicial do projeto: só o modelo 3D da Terra, sem login, sem agenda
e sem dados de país ainda.

## O que já funciona
- Esfera 3D com mapa-múndi (continentes e fronteiras) e iluminação
- Rotação do globo arrastando o mouse
- Zoom com o scroll

## O que falta
- Tela de login
- Menu (agenda de contatos / globo)
- Detecção de clique em país
- Busca de clima, população e notícias por país

## Como rodar no Eclipse
1. `File > Import... > Maven > Existing Maven Projects` e escolha a pasta `horizon-earth` (a que tem o `pom.xml`).
2. Botão direito no projeto > `Run As > Maven build...` (com os três pontinhos).
3. No campo **Goals**, digite: `clean compile javafx:run`
4. Clique em **Run**. Na primeira vez o Maven baixa o JavaFX, então precisa de internet.

Requer Java 17 ou superior.

## Textura
O projeto já inclui `src/main/resources/earth_texture.jpg`, gerado a partir de
dados públicos do Natural Earth. Dá para trocar por outra imagem equirretangular
(proporção 2:1) com o mesmo nome. Sem ela, o globo fica azul sólido.

## Estrutura
```
horizon-earth/
├── pom.xml
├── README.md
└── src/main/
    ├── java/br/edu/principal/Main.java
    └── resources/earth_texture.jpg
```
