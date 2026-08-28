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
├── src/main/kotlin/
│   ├── ui/              # composables das 5 telas (spec.md)
│   ├── viewmodel/
│   ├── data/
│   │   ├── remote/      # Ktor Client — só usado na rotina de seed (RF-01)
│   │   ├── local/       # Room — entidades, DAOs
│   │   └── repository/
│   ├── game/            # RF-04 a RF-19 — lógica pura, sem import de android.*
│   ├── sync/            # rotina de seed (RF-01/02/03)
│   └── MainActivity.kt
└── build.gradle.kts
```

## Schema (Room)
Pseudocódigo conceitual — na implementação vira `@Entity` com `@PrimaryKey(autoGenerate = true)`.

```
character
  id, cv_id (unique), name, image_url, publisher
  poder_bruto, poder_ajustado, veterania
  created_at

character_team          -- N:N (teams é array)
  character_id, team_name

character_friend        -- usado em B_sin
  character_id, friend_cv_id

story_arc
  id, cv_id (unique), name, publisher
  dificuldade
  created_at

character_unlock        -- N:N, RF-19
  character_id, story_arc_id

game_state               -- linha única (id=1), sem tabela de usuário
  id, moeda_balance

user_roster
  id, character_id (unique), available_at (cooldown, RF-15)

mission
  id, story_arc_id (unique)

mission_result
  id, mission_id
  forca_time, chance, sucesso, xp_ganho, moeda_ganha
  created_at

mission_result_hero      -- N:N
  mission_result_id, character_id
```

## API externa — Comic Vine
- Base: `https://comicvine.gamespot.com/api/`
- Autenticação: `api_key` como query param (guardar conforme constitution.md C-09)
- Endpoints usados no seed:
  - `/character/{id}/` — único jeito de pegar `powers`, `character_friends`, `teams`, `count_of_issue_appearances` (array/objeto fields só existem no singular, não em `/characters/`)
  - `/story_arc/{id}/` — único jeito de pegar `issues`
  - `/issue/{id}/` — pra pegar `character_credits`, usado no unlock (RF-19)
  - `/characters/` e `/story_arcs/` (plural) — só pra busca/filtro por campo escalar (`name`, `sort=count_of_issue_appearances`), nunca pra pegar os arrays
- Quirks confirmados (afetam RF-11):
  - `count_of_issue_appearances` funciona em `character`, mas retorna sempre 0 em `story_arc` (bug confirmado — mesmo com o nome de campo com typo conhecido `count_of_isssue_appearances`). Usar `len(issues)` em vez disso.
  - `character.story_arc_credits` existe na documentação oficial mas retorna vazio na prática — não usar; usar o caminho `story_arc.issues` → `issue.character_credits` (RF-19).
  - Rate limit: 200 requisições/recurso/hora.
  - Uso restrito a fins não comerciais, exige atribuição à Comic Vine.

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
