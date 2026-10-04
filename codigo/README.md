# Horizon Earth — Globo 3D (V.1.1.0)

Modelo 3D da Terra em JavaFX. Nesta versão o globo ficou mais realista.

## O que já funciona
- Esfera 3D com textura de satélite da NASA (Blue Marble)
- Oceano com brilho e continentes foscos
- Céu estrelado ao fundo
- Rotação e inclinação arrastando o mouse (inclinação limitada a 90 graus)
- Zoom com o scroll

## O que falta (próximos passos)
- Movimento mais suave (inércia, rotação automática, zoom suave)
- Países clicáveis (destacar o país escolhido)
- Tela de login
- Menu (agenda de contatos / globo)
- Busca de clima, população e notícias por país

## Como rodar no Eclipse
1. `File > Import... > Maven > Existing Maven Projects` e escolha a pasta que contém o `pom.xml`.
2. Botão direito no projeto > `Run As > Maven build...` (com os três pontinhos).
3. No campo **Goals**, digite: `clean compile javafx:run`
4. Clique em **Run**. Na primeira vez o Maven baixa o JavaFX, então precisa de internet.

Requer Java 17 ou superior.

## Imagens (src/main/resources)
- `earth_texture.jpg` — mapa-múndi equirretangular (4096x2048), NASA Blue Marble
- `earth_specular.png` — mapa de brilho (branco = oceano brilhante)
- `stars.jpg` — céu estrelado, gerado para o projeto

Todas são opcionais: sem elas o programa roda com cores simples.

## Estrutura
```
projeto/
├── pom.xml
├── README.md
└── src/main/
    ├── java/br/edu/principal/Main.java
    └── resources/
        ├── earth_texture.jpg
        ├── earth_specular.png
        └── stars.jpg
```

## Créditos
Textura da Terra: NASA Visible Earth (Blue Marble), domínio público.
