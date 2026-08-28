# Desafio Marvel — Comic Vine App

Trabalho de curso técnico — app Android (Kotlin + Jetpack Compose) tipo "manager", recrutando personagens da Marvel via dados da API da Comic Vine. Sem backend — tudo roda no app (ver specs/constitution.md).

## Contexto do projeto

@specs/constitution.md
@specs/spec.md
@specs/plan.md

## Como trabalhar aqui

- Antes de implementar qualquer coisa, leia **specs/tasks.md** pra saber a tarefa atual (não importado acima de propósito — ele muda a cada checkbox marcado, e imports só carregam uma vez no início da sessão; melhor reler fresco).
- Implemente **uma tarefa de specs/tasks.md por vez**, não o projeto inteiro numa tacada.
- Depois que os testes da tarefa passarem, marque o checkbox correspondente em specs/tasks.md.
- Se uma decisão de design mudar durante a implementação, atualize **specs/spec.md ou specs/constitution.md primeiro** — nunca só o código. O spec é a fonte da verdade; o código é gerado a partir dele.

## Comandos

- Build: `./gradlew build`
- Testes unitários (lógica de jogo, sem emulador): `./gradlew test`
- Testes instrumentados (Room, UI): `./gradlew connectedAndroidTest`
- Instalar no emulador/dispositivo: `./gradlew installDebug`

## Convenções de código

- Pacote `game/`: lógica pura de jogo (fórmulas de specs/spec.md). NÃO importar nada de `android.*` aqui — precisa compilar e rodar teste sem emulador.
- DTOs de rede e entidades Room usam `kotlinx.serialization` — nunca Gson/Moshi (specs/constitution.md C-07).
- Nomes de variável nas fórmulas seguem a notação de specs/spec.md (`ForçaTime`, `Dificuldade`, `Poder_ajustado` etc.) — facilita rastrear qual trecho de código implementa qual RF-XX.

## O que não fazer

- Não adicionar chamadas à Comic Vine fora da rotina de seed (specs/constitution.md C-02).
- Não commitar a API key — sempre via `local.properties` + `BuildConfig` (specs/constitution.md C-09).
- Não implementar autenticação, backend, ou progressão de herói por nível — fora de escopo (ver "Fora de escopo" em specs/spec.md).