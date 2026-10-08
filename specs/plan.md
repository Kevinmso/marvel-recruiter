# Plan — Marvel Recruiter

## Arquitetura
Single Android app module. Sem backend (constitution.md C-01/C-02).

```
App Android (Compose) ──HTTPS──► Comic Vine API (só no seed, RF-01)
        │
        ▼
     Room (local)
```

## Stack
| Camada | Tecnologia |
|---|---|
| Linguagem | Kotlin |
| UI | Jetpack Compose |
| Arquitetura de apresentação | MVVM (ViewModel + StateFlow) |
| Navegação | Navigation Compose |
| Cliente HTTP | Ktor Client (engine CIO) |
| Serialização | kotlinx.serialization |
| Persistência | Room |
| Imagens | Coil |
| DI | **Koin** (BOM) — Kotlin puro, sem geração de código; escolhido em T-03 por menor boilerplate e build mais leve num projeto pequeno |

## Identidade do app
- **Package name**: `com.marvel.recruiter`
- **Minimum SDK**: API 33 (Tiramisu)

## Estrutura de módulos
```
app/
├── src/main/java/com/marvel/recruiter/   # AS gerou 'java/'; contém Kotlin normalmente
│   ├── ui/              # composables das 5 telas (spec.md)
│   ├── viewmodel/
│   ├── data/
│   │   ├── remote/      # Ktor Client — só usado na rotina de seed (RF-01)
│   │   ├── local/       # Room — entidades, DAOs
│   │   └── repository/  # mapeia Entity → data class pura antes de chamar game/
│   ├── game/            # RF-04 a RF-19 — lógica pura, sem import de android.*
│   ├── di/              # AppModule.kt (Koin)
│   ├── sync/            # rotina de seed (RF-01/02/03)
│   ├── AppApplication.kt
│   └── MainActivity.kt
└── build.gradle.kts
```

## Schema (Room) — v4

Validado contra chamadas reais à Comic Vine (5 personagens + 2 arcos do roster de teste). 11 tabelas, divididas em **catálogo imutável** (vem do seed) e **save-state mutável**.

**Regra de chave primária:** entidade que espelha um recurso da Comic Vine usa o `cv_id` dele como `@PrimaryKey` (`Long`, sem `autoGenerate`) — o `cv_id` é global e estável, e isso deixa o seed numa fase só (não precisa reler ids gerados pra montar as FKs). Registro puramente local usa surrogate `@PrimaryKey(autoGenerate = true)`.

Toda FK leva `@Index` (o Room exige) e `onDelete = CASCADE`. As tabelas N:N usam `@Entity(primaryKeys = [...])` (PK composta).

Identificadores em inglês (CLAUDE.md). Notação snake_case aqui; as `@Entity` usam a versão camelCase da mesma propriedade (ex: `adjusted_power` ↔ `adjustedPower`), sem `@ColumnInfo`.

```
── CATÁLOGO (imutável; PK = cvId) ──────────────────────────────

team
  cvId (PK), name

character
  cvId (PK), name, realName?, deck?, aliases?, imageUrl?
  numPowers          -- p bruto, RF-04 (display/debug)
  numAppearances     -- x bruto, RF-05 (display/debug)
  adjustedPower      -- RF-07 final (usado no teamStrength)
  veterancy          -- RF-05 (teamStrength + sorteio de pacote RF-17)

characterTeam
  characterCvId (FK→character), teamCvId (FK→team)
  PK(characterCvId, teamCvId)

characterSynergy               -- pares de amizade, NÃO-direcionado (synergyBonus)
  lowCvId (FK→character), highCvId (FK→character)   -- invariante: low < high
  PK(lowCvId, highCvId)
  -- seed: par {A,B} só se AMBOS estão no roster curado E A lista B ou B lista A
  --       em character_friends; grava normalizado (min,max), dedup

storyArc
  cvId (PK), name, deck?
  numIssues    -- y limpo (pós-curadoria manual)
  difficulty   -- RF-11, congelada (C-08)

characterUnlock               -- RF-19, resolvido no seed p/ os 24
  characterCvId (FK→character), arcCvId (FK→storyArc)
  PK(characterCvId, arcCvId)

seedMeta                      -- RF-03: grava os limites usados no seed
  key (PK), value   -- pMin, pMax, xMin, xMax, yMin, yMax, gamma, seededAt

── SAVE-STATE (mutável; surrogate PK) ──────────────────────────

gameState                     -- linha única, criada no onCreate do Room
  id (PK = 1)
  coinBalance     -- inicial 300 (RF-21)
  xpTotal         -- inicial 0   (RF-21, placar vitalício)
  seedCompleted   -- inicial false (RF-01/RF-20: marcador de seed concluído)

userRoster
  id (PK auto), characterCvId (FK→character, UNIQUE)
  availableAt     -- epoch millis; <= now ⇒ disponível (RF-15). default 0
  recruitedAt     -- epoch millis, ordenação Tela 1

missionResult
  id (PK auto), arcCvId (FK→storyArc)
  teamStrength, difficulty, chance   -- snapshots (Double)
  rollValue                          -- o sorteio de RF-13
  success                            -- Boolean
  xpEarned, coinsEarned              -- Int, roundToInt (RF-14)
  createdAt

missionResultHero
  missionResultId (FK→missionResult), characterCvId (FK→character)
  PK(missionResultId, characterCvId)
```

Nomes de tabela (`@Entity(tableName=...)`): `team`, `character`, `character_team`, `character_synergy`, `story_arc`, `character_unlock`, `seed_meta`, `game_state`, `user_roster`, `mission_result`, `mission_result_hero`.

Tipos: ids `Long`; `coinBalance`/`xp*`/recompensas `Int`; `adjustedPower`/`veterancy`/`difficulty`/`chance`/`teamStrength` `Double`; `success`/`seedCompleted` `Boolean`; timestamps `Long` (epoch millis).

**Sem tabela `mission`**: "missão" é 1:1 com `story_arc`; missão desbloqueada é query derivada (`story_arc ∩ character_unlock ∩ user_roster`).

**Fronteira `game/` ↔ Room:** as `@Entity` NUNCA entram em `game/` (C-03). Uma camada de mapeamento em `data/repository/` converte `CharacterEntity` → `HeroStats` (data class pura) antes de chamar as fórmulas. Assim os testes de `game/` não conhecem Room, e mudar entidade não quebra teste de fórmula.

**Migração:** `fallbackToDestructiveMigration(true)` durante o desenvolvimento (roster/fórmulas ainda mudam; re-seed é barato). Migração real só perto do release.

**Seed em 2 fases:** (1) baixar tudo da rede pra memória (retry/resume na camada de repo); (2) UMA transação Room grava tudo + `seed_completed = true`. Não segurar lock de escrita durante a rede.

## API externa — Comic Vine
- Base: `https://comicvine.gamespot.com/api/`
- Autenticação: `api_key` como query param (guardar conforme constitution.md C-09)
- Autenticação: `api_key` + `format=json` como query params; mandar `User-Agent` próprio (sem ele a API pode responder 403).
- `id` nos payloads é inteiro puro (`1440`). O prefixo do slug (`4005-` personagem, `4000-` issue) é fixo por tipo e só entra na URL da chamada — reconstruir em código, não guardar.
- Endpoints usados no seed:
  - `/character/4005-{id}/` — único jeito de pegar `powers`, `character_friends`, `teams`, `count_of_issue_appearances` (array/objeto fields só existem no singular, não em `/characters/`)
  - `/story_arc/{id}/` — único jeito de pegar `issues`
  - `/issue/4000-{id}/` — pra pegar `character_credits`, usado no unlock (RF-19)
  - `/characters/` e `/story_arcs/` (plural) — só pra busca/filtro por campo escalar (`name`), nunca pra pegar os arrays
- Formato dos campos (confirmado com chamada real ao roster de teste):
  - `character.powers` / `teams` / `character_friends`: arrays de `{id, name, ...}`. Contagens reais do teste: Wolverine 24 poderes / 58 times / 648 amigos; Colossus 8 / 20 / 183; Black Goliath 6 / 9 / 42; Carol 30 / 18 / 91; Cap 18 / 31 / 548.
  - `character.image`: objeto com ~10 URLs (`icon_url`…`original_url`). `aliases`: string única separada por `\n`. `deck`: resumo curto. `publisher`: objeto `{id, name}` (não é usado pelo schema v4).
  - `story_arc.issues`: array de `{id, name, ...}`. `story_arc.deck`: presente.
  - `issue.character_credits`: array de `{id, name}` — é o caminho do RF-19.
- Quirks confirmados:
  - `story_arc.count_of_issue_appearances` **nem retorna** no payload (confirmado no arco 40615). Usar `len(issues)`.
  - `character.story_arc_credits` existe na doc oficial mas retorna vazio na prática — não usar; usar `story_arc.issues` → `issue.character_credits` (RF-19).
  - Rate limit: 200 requisições/recurso/hora.
  - Uso restrito a fins não comerciais, exige atribuição à Comic Vine.
- **Resolução do unlock (RF-19) — decisão da T-18:** a rota por issue (`/issue/4000-{id}/` para cada issue de cada arco) custaria 500–1500 chamadas, acima do rate limit de 200/h. Adotada a rota inversa, testada com chamada real:
  1. Para cada personagem curado: `/character/4005-{id}/?field_list=id,issue_credits` → conjunto `I(c)` de issues em que ele aparece (uma chamada retorna a lista inteira; Colossus: 7928 issues).
  2. Para cada arco curado: conjunto `J(a)` = ids de `story_arc.issues` (já vem do seed, T-17).
  3. Unlock: `(c, a)` existe quando `I(c) ∩ J(a) ≠ ∅`.
  Custo: ~24 + ~20 chamadas no seed. Respeita a curadoria (só conta issues que sobraram no arco limpo).
  - O endpoint plural `/issues/` **não** retorna `character_credits` (testado) — não usar pra isso.
  - Batch por `filter=id:a|b|c` no plural serve só pra metadados de issue, não pra créditos.

## Configuração
```properties
# local.properties (não commitado, cada dev configura o seu)
COMIC_VINE_API_KEY=...
```

```kotlin
// build.gradle.kts (app)
import java.util.Properties

// O Gradle não carrega local.properties sozinho — precisa ler o arquivo na mão.
// Fallback pra variável de ambiente (COMIC_VINE_API_KEY) pra permitir CI sem local.properties.
val comicVineApiKey: String = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}.getProperty("COMIC_VINE_API_KEY")
    ?: System.getenv("COMIC_VINE_API_KEY")
    ?: ""

android {
    buildFeatures { buildConfig = true }
    defaultConfig {
        buildConfigField("String", "COMIC_VINE_API_KEY", "\"$comicVineApiKey\"")
    }
}
```

Se a key estiver ausente, `BuildConfig.COMIC_VINE_API_KEY` fica `""` — a build passa, mas o seed (RF-01) falha em runtime e cai na tela de erro (RF-20). Não quebrar a build por falta de key é proposital: permite `./gradlew build` em CI e clonar o repo sem configurar nada pra rodar os testes de `game/`.

## Testes
- `game/`: JUnit puro (sem Android) — cobre RF-04 a RF-19.
- UI: Compose UI Testing (`createComposeRule`).
- Room: testes instrumentados ou banco in-memory.

## Rodando localmente
1. Configurar `COMIC_VINE_API_KEY` no `local.properties`.
2. Rodar num emulador/dispositivo Android.
3. Aguardar o seed inicial (RF-01) antes de testar recrutamento — mostrar loading, não travar a UI.

## Snapshot do roster (decisão de 2026-10-03)
- Fonte da verdade do seed: `app/src/main/assets/roster_snapshot.json`.
- Gerado por `tools/generate_roster_snapshot.py` (chama a Comic Vine com a key de `local.properties`). Só roda manualmente, quando o roster muda.
- O app lê o JSON com kotlinx.serialization e monta o mesmo `SeedPlan` de antes; a gravação transacional e `seedCompleted` não mudam.
- Motivo: limite de 200 requisições/hora por recurso da Comic Vine. Com 50 personagens, o seed por rede consome 100 chamadas por instalação.
