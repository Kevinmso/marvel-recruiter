# Tasks — Marvel Recruiter

Ordem sugerida. Cada tarefa é atômica o bastante pra pedir de uma vez pra um agente de IA implementar (uma tarefa por vez, não o arquivo inteiro de uma vez).

## Fase 0 — Setup
- [x] T-01: Criar projeto Android (Compose), configurar Gradle com as libs de plan.md
- [x] T-02: Configurar `local.properties` + `BuildConfig` pra API key (constitution.md C-09)
- [x] T-03: Escolher e configurar DI (Koin — ver plan.md)

## Fase 1 — Dados locais (Room)
- [x] T-04: Criar entidades Room (schema v4 em plan.md): `Team`, `Character`, `CharacterTeam`, `CharacterSynergy`, `StoryArc`, `CharacterUnlock`, `SeedMeta`, `GameState`, `UserRoster`, `MissionResult`, `MissionResultHero`. PK = `cv_id` no catálogo; FK com `@Index` + CASCADE; PK composta nas N:N.
- [x] T-05: DAOs (6) + `AppDatabase` (11 entidades, `exportSchema` → `app/schemas/`, `fallbackToDestructiveMigration(true)`) + `InitialStateCallback` (`onCreate` insere `game_state` default via `SQLiteConnection`) + wiring no Koin (`appModule`)

## Fase 2 — Lógica de jogo (`game/`, sem Android)
- [x] T-06: Poder (RF-04) + testes unitários — `Normalization.power()` + `PowerTest` (5 casos)
- [x] T-07: Veterania (RF-05) + testes — `Normalization.veterancy()` + `VeterancyTest` (5 casos)
- [x] T-08: Compensação de raridade + piso de Poder (RF-06/07) + testes — `Normalization.compensatedPower()` / `adjustedPower()` + `AdjustedPowerTest` (8 casos)
- [x] T-09: ForçaTime, B_sin, B_fac (RF-09) + testes — `teamStrength/synergyBonus/factionBonus` + `HeroStats`/`SynergyPair` data classes + `TeamStrengthTest` (11 casos, inclui `B_fac=0` sem compartilhamento e cap de 40). RF-10 já coberto em T-06/07.
- [x] T-10: Dificuldade (RF-11) + testes — `Normalization.difficulty()` + `enum DifficultyLabel` + `DifficultyTest` (7 casos)
- [x] T-11: Chance de sucesso e sorteio (RF-12/13) + testes — `game/Mission.kt`: `successChance()` + `resolveMission(chance, random)` (RNG injetável, C-15) + `MissionTest` (9 casos)
- [ ] T-12: Recompensa dependente de resultado (RF-14) + testes
- [ ] T-13: Cooldown (RF-15) + testes
- [ ] T-14: Sorteio ponderado de pacote (RF-16/17) + testes

## Fase 3 — Rede + Seed (RF-01/02/03)
- [ ] T-15: Configurar Ktor Client + kotlinx.serialization pra Comic Vine
- [ ] T-16: Buscar personagens curados (`/character/{id}/`)
- [ ] T-17: Buscar arcos curados + issues (`/story_arc/{id}/`)
- [ ] T-18: Resolver unlock via `/issue/4000-{id}/` → `character_credits` (RF-19). Definir mitigação do custo de chamadas (ver plan.md: parar quando todos os curados achados / amostrar / etc.)
- [ ] T-19: Rotina de seed completa (roda 1x, popula Room, congela constantes de normalização — constitution.md C-08)

## Fase 4 — UI (Compose)
- [ ] T-19b: Navegação (Navigation Compose) + Tela 0 de erro de seed (RF-20) + gate de loading enquanto o seed roda
- [ ] T-20: Tela de Roster/Recrutamento (inclui saldo de Moeda e placar de XP — RF-21)
- [ ] T-21: Tela de Montagem de time
- [ ] T-22: Tela de Lista de missões
- [ ] T-23: Tela de Resultado da missão
- [ ] T-24: Tela de Perfil do herói (wiki)

## Fase 5 — Polimento
- [ ] T-25: Calibrar constantes (k, α, β, γ, custo de pacote, constante de cooldown — valores iniciais em spec.md) jogando de verdade
- [ ] T-26: Completar roster de personagens e arcos curados em `specs/roster.md`, depois atualizar `specs/constantes-normalizacao.md` com os valores finais
- [ ] T-27: Revisão manual de issues com nome/idioma estrangeiro na curadoria final