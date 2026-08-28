# Constitution — Marvel Recruiter

Regras não-negociáveis do projeto. Código ou decisão que viole isto deve ser rejeitado, ou este arquivo deve ser atualizado explicitamente primeiro (nunca silenciosamente).

## Arquitetura
- **C-01**: O sistema DEVE rodar inteiramente no app Android — NÃO DEVE existir backend/servidor separado.
- **C-02**: O app NÃO DEVE chamar a API da Comic Vine durante o uso normal do jogo — só durante a rotina de seed inicial (spec.md RF-01).
- **C-03**: A lógica de jogo (cálculo de ForçaTime, Dificuldade, chance, recompensa, cooldown) DEVE viver num pacote `game/` sem depender de classes do Android — DEVE ser testável com JUnit puro, sem emulador.
- **C-04**: A UI DEVE ser construída com Jetpack Compose. NÃO DEVE ser usado o sistema de Views/XML legado.
- **C-05**: A arquitetura de apresentação DEVE seguir MVVM (ViewModel + StateFlow).

## Dados e persistência
- **C-06**: Toda persistência local DEVE usar Room. NÃO DEVE ser usado banco de servidor (Postgres etc.) — não existe servidor.
- **C-07**: Toda serialização DEVE usar `kotlinx.serialization`. NÃO DEVE ser introduzido Gson, Moshi ou serializador concorrente.
- **C-08**: Os limites de normalização (mínimos/máximos de Poder, Veterania, Dificuldade) DEVEM ser calculados uma vez, na curadoria do roster, e gravados como constantes fixas — NÃO DEVEM ser recalculados dinamicamente a cada execução.

## Segurança
- **C-09**: A API key da Comic Vine NÃO DEVE ser commitada no controle de versão. DEVE ficar em `local.properties` e ser exposta via `BuildConfig`.

## Regras de jogo
- **C-10**: A recompensa de uma missão DEVE depender do resultado (sucesso ou falha) — NÃO DEVE ser um valor fixo independente do resultado.
- **C-11**: O sistema NÃO DEVE implementar progressão de herói (nível/XP alterando atributos) — fora de escopo por decisão de projeto.
- **C-12**: O app NÃO DEVE implementar autenticação/conta de usuário — é local, de um usuário só.

## Qualidade
- **C-13**: Toda fórmula de jogo em `game/` DEVE ter cobertura de teste unitário antes de ser considerada concluída.
- **C-14**: Mudanças que contradigam esta constituição DEVEM atualizar este arquivo explicitamente, não só o código.
