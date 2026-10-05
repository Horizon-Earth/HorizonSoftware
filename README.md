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

<!-- estrutura-guia:inicio -->
## 📂 Estrutura de arquivos

Os caminhos abaixo descrevem as pastas deste repositório. Cada pasta possui um README com finalidade e estado do conteúdo. O projeto Maven está na raiz; o código fica em src/main/java/ e as texturas em resources/. A pasta database/ está reservada para eventual persistência: o protótipo atual não utiliza banco de dados.

| Pasta | Finalidade |
| --- | --- |
| `.github/` | Organização dos pacotes e arquivos do projeto. |
| `.github/workflows/` | Automações do GitHub Actions. |
| `database/` | Modelagem e estrutura do banco de dados; reúne DER, diagrama lógico e scripts SQL. |
| `database/DER/` | Diagramas Entidade-Relacionamento e arquivos editáveis de modelagem do banco. |
| `database/DL/` | Diagrama lógico, tabelas, campos, chaves e relacionamentos do banco. |
| `database/scripts/` | Scripts SQL para criação, alteração e carga de dados do banco. |
| `docs/` | Documentação produzida pela equipe sobre este projeto. Materiais externos de consulta pertencem a support/. |
| `docs/diagrams/` | Fluxogramas e diagramas de arquitetura; UML fica em docs/uml/ e modelagem de banco em database/. |
| `docs/presentations/` | Slides e materiais de apresentação e demonstração do projeto. |
| `docs/ui-ux/` | Planejamento das interfaces e da experiência do usuário. |
| `docs/ui-ux/mockups/` | Representações visuais detalhadas da aparência das telas. |
| `docs/ui-ux/prototypes/` | Protótipos e registros da navegação e interação entre telas. |
| `docs/ui-ux/wireframes/` | Esboços da disposição dos componentes e da estrutura das telas. |
| `docs/uml/` | Diagramas UML de classes, casos de uso, sequência e atividades. |
| `resources/` | Recursos consumidos pela aplicação durante sua execução, como imagens, ícones, FXML e CSS. Documentos de consulta pertencem a support/. |
| `resources/icons/` | Ícones utilizados nas interfaces e nas janelas da aplicação. |
| `resources/images/` | Imagens e texturas utilizadas na interface ou na cena 3D. |
| `src/` | Código-fonte da aplicação, organizado em pacotes e classes. |
| `src/main/` | Organização dos pacotes e arquivos do projeto. |
| `src/main/java/` | Organização dos pacotes e arquivos do projeto. |
| `src/main/java/br/` | Organização dos pacotes e arquivos do projeto. |
| `src/main/java/br/edu/` | Organização dos pacotes e arquivos do projeto. |
| `src/main/java/br/edu/principal/` | Organização dos pacotes e arquivos do projeto. |
| `support/` | Materiais externos e auxiliares usados como apoio ao desenvolvimento. |
| `support/documents/` | Guias da disciplina, apostilas, artigos e manuais utilizados para consulta. |
| `support/references/` | Links de documentações oficiais, exemplos e outras referências técnicas consultadas. |
| `support/tutorials/` | Tutoriais e passos de instalação, configuração e execução das tecnologias do projeto. |
| `support/videos/` | Links de vídeos e videoaulas usados como apoio. Registre título e URL no README, evitando arquivos de vídeo grandes. |

Arquivos da raiz: `.gitignore`, `LICENSE`, `README.md`, `pom.xml`.
<!-- estrutura-guia:fim -->
