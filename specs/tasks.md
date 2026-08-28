# Tasks — Marvel Recruiter

Ordem sugerida. Cada tarefa é atômica o bastante pra pedir de uma vez pra um agente de IA implementar (uma tarefa por vez, não o arquivo inteiro de uma vez).

## Fase 0 — Setup
- [ ] T-01: Criar projeto Android (Compose), configurar Gradle com as libs de plan.md
- [ ] T-02: Configurar `local.properties` + `BuildConfig` pra API key (constitution.md C-09)
- [ ] T-03: Escolher e configurar DI (Hilt ou Koin — plan.md)

## Fase 1 — Dados locais (Room)
- [ ] T-04: Criar entidades Room: `Character`, `CharacterTeam`, `CharacterFriend`, `StoryArc`, `CharacterUnlock`, `GameState`, `UserRoster`, `Mission`, `MissionResult`, `MissionResultHero` (schema em plan.md)
- [ ] T-05: Criar DAOs correspondentes

## Fase 2 — Lógica de jogo (`game/`, sem Android)
- [ ] T-06: Poder (RF-04) + testes unitários
- [ ] T-07: Veterania (RF-05) + testes
- [ ] T-08: Compensação de raridade + piso de Poder (RF-06/07) + testes
- [ ] T-09: ForçaTime, B_sin, B_fac (RF-09) + testes, incluindo guarda de divisão por zero (RF-10)
- [ ] T-10: Dificuldade (RF-11) + testes
- [ ] T-11: Chance de sucesso e sorteio (RF-12/13) + testes
- [ ] T-12: Recompensa dependente de resultado (RF-14) + testes
- [ ] T-13: Cooldown (RF-15) + testes
- [ ] T-14: Sorteio ponderado de pacote (RF-16/17) + testes

## Fase 3 — Rede + Seed (RF-01/02/03)
- [ ] T-15: Configurar Ktor Client + kotlinx.serialization pra Comic Vine
- [ ] T-16: Buscar personagens curados (`/character/{id}/`)
- [ ] T-17: Buscar arcos curados + issues (`/story_arc/{id}/`)
- [ ] T-18: Resolver unlock via `/issue/{id}/` → `character_credits` (RF-19)
- [ ] T-19: Rotina de seed completa (roda 1x, popula Room, congela constantes de normalização — constitution.md C-08)

## Fase 4 — UI (Compose)
- [ ] T-20: Tela de Roster/Recrutamento
- [ ] T-21: Tela de Montagem de time
- [ ] T-22: Tela de Lista de missões
- [ ] T-23: Tela de Resultado da missão
- [ ] T-24: Tela de Perfil do herói (wiki)

## Fase 5 — Polimento
- [ ] T-25: Calibrar constantes (k, α, β, γ, custo de pacote, constante de cooldown) jogando de verdade
- [ ] T-26: Completar roster de arcos curados (faltam ~10-15 além dos 5 já testados)
- [ ] T-27: Revisão manual de issues com nome/idioma estrangeiro na curadoria final
