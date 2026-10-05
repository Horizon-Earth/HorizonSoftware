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

## Créditos
Textura da Terra: NASA Visible Earth (Blue Marble), domínio público.

---

<!-- estrutura-guia:inicio -->
## 🗂️ Organização do repositório

As pastas abaixo separam as responsabilidades do projeto. Abra uma delas para ver seus arquivos, suas subpastas e o estado do conteúdo.

| Pasta | O que você encontra |
| :--- | :--- |
| ⚙️ [.github/](.github/README.md) | Templates de colaboração e configurações do GitHub Actions. |
| 🗄️ [database/](database/README.md) | Modelagem e estrutura do banco de dados; reúne DER, diagrama lógico e scripts SQL. |
| 📚 [docs/](docs/README.md) | Documentação produzida pela equipe sobre este projeto. Materiais externos de consulta pertencem a support/. |
| 🎨 [resources/](resources/README.md) | Recursos consumidos pela aplicação durante sua execução, como imagens, ícones, FXML e CSS. Documentos de consulta pertencem a support/. |
| 💻 [src/](src/README.md) | Código-fonte da aplicação, organizado em pacotes e classes. |
| 🧰 [support/](support/README.md) | Materiais externos e auxiliares usados como apoio ao desenvolvimento. |

### Arquivos da raiz

| Arquivo | Finalidade |
| :--- | :--- |
| [.gitignore](.gitignore) | Arquivos locais e gerados que o Git deve ignorar. |
| [LICENSE](LICENSE) | Condições de uso e distribuição sob a licença MIT. |
| [pom.xml](pom.xml) | Dependências, compilação e execução do projeto Maven. |

**Execução:** abra a raiz como projeto Maven e use `mvn clean compile javafx:run`. Fontes em `src/main/java/`; texturas em `resources/`.
<!-- estrutura-guia:fim -->
