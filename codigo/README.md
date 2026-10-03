# Horizon Earth — Globo 3D (primeira versão)

Esqueleto inicial do projeto: só o modelo 3D da Terra, sem login, sem agenda,
sem dados de país ainda. É a base pra você ir construindo o resto em cima.

## O que já funciona
- Esfera 3D texturizada com mapa-múndi
- Rotação do globo arrastando o mouse
- Zoom com o scroll

## O que falta (próximos passos)
- Tela de login
- Menu (agenda de contatos / globo)
- Detecção de clique em país (mapa de cores ou GeoJSON — como conversamos)
- Busca de clima/população/notícias por país

## Como rodar no Eclipse

O projeto já é um projeto Maven (tem `pom.xml`), então o Eclipse consegue
rodar ele sem precisar baixar o SDK do JavaFX separado nem mexer em
VM arguments — o Maven cuida disso.

1. Confirme que seu Eclipse tem o **m2e** instalado (vem por padrão no
   "Eclipse IDE for Java Developers"). Se não tiver, instale via
   `Help > Eclipse Marketplace > pesquisar "m2e"`.
2. No Eclipse: `File > Import... > Maven > Existing Maven Projects`.
3. Aponte pra pasta `horizon-earth` (a que contém o `pom.xml`) e finalize
   a importação.
4. Baixe uma imagem de mapa-múndi em **projeção equirretangular**
   (proporção 2:1, ex: 2048x1024) — dá pra achar buscando por
   "equirectangular world map texture" em bancos de imagem livres tipo
   NASA Visible Earth ou Wikimedia Commons.
5. Salve essa imagem em `src/main/resources/earth_texture.jpg` dentro do
   projeto importado (pode arrastar o arquivo direto pra pasta pelo
   próprio Package Explorer do Eclipse).
6. Clique com o botão direito no projeto → `Run As > Maven build...`.
   Em "Goals", digite:
   ```
   javafx:run
   ```
   e clique em Run. O Eclipse vai baixar as dependências do JavaFX
   automaticamente na primeira vez (pode demorar um pouco) e abrir a
   janela do globo.
7. Da próxima vez, é só usar `Run As > Maven build...` de novo, ou criar
   uma "Run Configuration" salva com o goal `javafx:run` pra não ter que
   digitar toda vez.

**Se aparecer erro de "Maven executable not found" ou parecido:**
verifique se o Maven Wrapper/instalação do Maven está reconhecido pelo
Eclipse em `Window > Preferences > Maven > Installations`.

Se você abrir sem colocar a textura, o globo aparece como uma esfera azul
sólida (fallback), só pra confirmar que a estrutura 3D está funcionando.

## Alternativa: linha de comando

Se preferir rodar fora do Eclipse, com Java 17+ e Maven instalados:
```
mvn javafx:run
```

## Estrutura

```
horizon-earth/
├── pom.xml
├── README.md
└── src/main/
    ├── java/br/edu/principal/Main.java
    └── resources/earth_texture.jpg   <- você adiciona
```
