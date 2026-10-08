# Design — Marvel Recruiter

Documento de design visual e de telas. Não contém código de app. Fonte da verdade do comportamento continua em `specs/spec.md`; este arquivo define aparência, tokens, componentes e microinterações. Identificadores de código em inglês; prosa em pt-BR.

Referência de dimensão: tela de referência 360 × 800 dp (phone); validação extra em 412 × 915 dp e 320 × 640 dp (menor comum, com fonte 200%).

---

## 1. Conceito e direção visual

**Conceito: "Dossiê de Recrutamento".** O app é uma pasta de agência secreta: cada herói é um dossiê com foto grande, carimbos ("RECRUTADO", "EM MISSÃO", "DESCANSO"), fichas técnicas em fonte mono e bordas de quadrinho (ticks, colchetes, meios-tons). A tela de missão é um painel de operações com um "dado" de sorteio. O clima é de papel envelhecido em modo claro e de sala de comando em modo escuro. Nada usa logos, símbolos ou cores protegidas de editoras; a identidade vem de tipografia condensada pesada, meios-tons, carimbos e uma paleta crimson/ouro/tinta-azul própria.

**Palavras-chave:** dossiê, carimbo, meios-tons, tinta e papel, alto contraste, tipografia condensada, painel de operações, recompensa em ouro, sóbrio mas com energia, tático.

---

## 2. Tokens

### 2.1 Paleta

Todas as cores são tokens em `ColorScheme` (Material3) mais extensão `RecruiterColors` para papéis que o Material não cobre (dificuldades, sinergia, facção, moeda, XP). Ambos os temas são definidos; o app segue o tema do sistema (sem toggle manual nesta versão).

**Superfícies e texto**

| Papel (token) | Claro | Escuro | Uso |
|---|---|---|---|
| `background` | `#F4F1EA` | `#0E0F12` | Fundo da tela (papel / sala escura) |
| `surface` | `#FBF9F4` | `#17191E` | Cards, sheets, app bar |
| `surfaceVariant` | `#E8E2D3` | `#22252C` | Faixa de dossiê, chips neutros, trilhos de barra |
| `outline` | `#6B6358` | `#8A8F9C` | Bordas de card, divisores (≥3:1 sobre surface) |
| `outlineVariant` | `#CFC7B5` | `#2E323B` | Divisores decorativos (não são o único indicador) |
| `onBackground` / `onSurface` | `#16130F` | `#F3EFE6` | Texto principal |
| `onSurfaceVariant` | `#4D463C` | `#B9BEC9` | Texto secundário (≥7:1 sobre surface) |
| `inverseSurface` | `#16130F` | `#F3EFE6` | Snackbar / toast |

**Marca e ação**

| Papel | Claro | Escuro | `on` (texto sobre a cor) |
|---|---|---|---|
| `primary` (crimson, ação principal) | `#B3121F` | `#FF5A4E` | `#FFFFFF` / `#0E0F12` |
| `primaryContainer` (chip ativo, seleção) | `#F5D5D2` | `#3A1412` | `#5C0A12` / `#FFD9D5` |
| `secondary` (tinta azul, dossiê, XP) | `#1C2B4A` | `#8EB4FF` | `#FFFFFF` / `#0E0F12` |
| `secondaryContainer` | `#D9E2F5` | `#17263F` | `#0F1C33` / `#D9E6FF` |
| `danger` (falha, erro) | `#B00020` | `#FF6B6B` | `#FFFFFF` / `#0E0F12` |
| `success` (sucesso, disponível) | `#1E7B4A` | `#4ADE80` | `#FFFFFF` / `#0E0F12` |

**Recursos de jogo**

| Papel | Claro | Escuro | Uso |
|---|---|---|---|
| `coin` (Moeda, `coinBalance`) | `#8A5A00` | `#F5C451` | Texto/ícone de Moeda |
| `xp` (placar, `xpTotal`) | `#1C2B4A` | `#8EB4FF` | Mesmo papel de `secondary` |
| `difficultyEasy` (Fácil) | `#1E7B4A` | `#4ADE80` | Tag, borda de card |
| `difficultyMedium` (Médio) | `#8A5A00` | `#F2B33D` | Tag, borda de card |
| `difficultyEpic` (Épico) | `#6B2FA0` | `#B98CFF` | Tag, borda de card |
| `synergy` (par de amizade, B_sin) | `#0B6E78` | `#3FD0DC` | Linha de conexão, ícone de elo |
| `cooldown` | `#6B6358` | `#8A8F9C` | Badge de descanso (texto com `onSurface`) |

**Facção** (seis tons, atribuídos por `teamCvId % 6`; só cor de UI, nunca afeta a lógica de `game/`)

| Slot | Claro | Escuro |
|---|---|---|
| `faction1` | `#1F4E9A` | `#6FA0FF` |
| `faction2` | `#7A1F5C` | `#E07CC0` |
| `faction3` | `#2E6B3A` | `#7BD389` |
| `faction4` | `#8A3B12` | `#FFA271` |
| `faction5` | `#3D3D8F` | `#A3A3FF` |
| `faction6` | `#5E5A1F` | `#E6DC6E` |

Chip de facção: fundo na cor do slot a 14% de alpha, borda 1dp na cor do slot, texto na cor do slot (escuro/claro conforme o tema) — sempre acompanhado do nome da equipe, nunca só cor.

**Texturas**

| Textura | Claro | Escuro | Onde |
|---|---|---|---|
| Meios-tons (`HalftoneOverlay`, PNG 64×64 tile) | `#16130F` a 4% | `#FFFFFF` a 3% | Fundo de Telas 1, 3, 4 (topo) |
| Ruído de papel (opcional, PNG 128×128) | `#16130F` a 3% | não usar | Cards de dossiê claros |
| Scanlines (`ScanlineOverlay`) | não usar | `#FFFFFF` a 2%, passo 4dp | Painel de missão (Tela 4) |

**Verificação de contraste (WCAG AA):** texto normal ≥ 4,5:1; texto grande (≥ 24sp ou 18,66sp negrito) e componentes/indicadores gráficos ≥ 3:1. Os pares de texto acima foram estimados pela luminância relativa; validar com o Accessibility Scanner antes de fechar a Fase de UI.

### 2.2 Tipografia

Fontes do Google Fonts, licença OFL (uso comercial e embarque permitidos). Empacotar os `.ttf` em `res/font/` (dependência `androidx.compose.ui:ui-text-google-fonts` é alternativa, mas exige Play Services; preferir `res/font/` para funcionar offline).

| Família | Pesos usados | Papel |
|---|---|---|
| **Anton** | Regular 400 | Display e nomes de herói (caixa alta, peso visual de capa de quadrinho) |
| **Barlow Condensed** | 500, 600, 700 | Títulos de seção, labels, tags, carimbos |
| **Inter** | 400, 500, 600 | Corpo, bio, descrições (leitura) |
| **JetBrains Mono** | 500 | Valores numéricos (Poder, Veterania, ForçaTime, chance, cooldown), códigos de dossiê |

**Escala** (`sp`; `lineHeight` em `sp`; `letterSpacing` em `em`)

| Token (Material) | Família | Tamanho / linha | Peso | Tracking | Uso |
|---|---|---|---|---|---|
| `displayLarge` | Anton | 48 / 52 | 400 | 0.02em | Nome do herói no perfil (Tela 5), título de resultado |
| `displayMedium` | Anton | 36 / 40 | 400 | 0.02em | Títulos de tela ("ROSTER", "MISSÕES") |
| `headlineSmall` | Barlow Condensed | 26 / 30 | 700 | 0.04em | Nome do arco, nome do herói em card |
| `titleLarge` | Barlow Condensed | 22 / 26 | 700 | 0.03em | Cabeçalhos de seção |
| `titleMedium` | Barlow Condensed | 18 / 22 | 600 | 0.04em | Título de card, nome de equipe |
| `bodyLarge` | Inter | 16 / 24 | 400 | 0 | Bio, descrição do arco |
| `bodyMedium` | Inter | 14 / 20 | 400 | 0 | Texto auxiliar |
| `labelLarge` | Barlow Condensed | 16 / 20 | 600 | 0.08em, CAIXA ALTA | Botões |
| `labelMedium` | Barlow Condensed | 13 / 16 | 600 | 0.08em, CAIXA ALTA | Tags de dificuldade, carimbos, legendas |
| `labelSmall` | Inter | 12 / 16 | 500 | 0.02em | Metadados, rodapé de atribuição |
| `statValue` | JetBrains Mono | 22 / 26 | 500 | 0 | Valores grandes de atributo, ForçaTime |
| `statValueSmall` | JetBrains Mono | 15 / 20 | 500 | 0 | Valores em chips e cards |

Regras: todo número de atributo usa `statValue`/`statValueSmall`. Casas decimais: 0 em cards e chips; 1 na Tela 4 (ex.: "Veterania 80,6", com vírgula, pt-BR). Textos sem `maxLines` fixo: deixar quebrar ou usar `Ellipsis` só em nome de herói com `maxLines = 2`.

### 2.3 Espaçamento

Escala de 4dp.

| Token | dp | Uso |
|---|---|---|
| `space1` | 4 | Espaço entre ícone e texto |
| `space2` | 8 | Entre chips, dentro de tags |
| `space3` | 12 | Padding interno de card compacto |
| `space4` | 16 | **Gutter lateral do phone** e padding de card |
| `space5` | 24 | Entre seções |
| `space6` | 32 | Entre blocos de Tela 5 |
| `space7` | 48 | Respiro de estado vazio |

Gutter de tela: 16dp em todos os lados (phone). Largura ≥ 600dp (tablet/dobrável): gutter 24dp e grade de 2 colunas.

### 2.4 Raios

| Token | dp | Uso |
|---|---|---|
| `radiusTag` | 4 | Tags de dificuldade, carimbos, chips de facção |
| `radiusControl` | 8 | Botões, inputs, chips de seleção |
| `radiusCard` | 12 | Hero card, mission card |
| `radiusSheet` | 16 | Dialog de pacote, bottom sheet |
| `radiusPill` | 999 | Badge de cooldown, contador de Moeda |

Dossiê: todo card tem 4 "ticks" de canto (linhas de 8dp de comprimento, 1,5dp, cor `outline`) fora do conteúdo, como marcações de pasta. São decorativos (`contentDescription` nulo).

### 2.5 Elevação e sombra

Material3 usa tonal elevation; no tema claro adicionamos uma "sombra dura" de quadrinho.

| Nível | Claro | Escuro | Uso |
|---|---|---|---|
| `elevation0` | sem sombra, borda 1dp `outline` | sem sombra, borda 1dp `outlineVariant` | Cards padrão |
| `elevation1` | sombra dura: offset (0, 3dp), cor `#16130F` 100% (sem blur) | tonal 2dp (surface +4%) | Hero card selecionado, mission card |
| `elevation2` | sombra dura offset (0, 6dp) | tonal 6dp | Pack dialog, bottom sheet |
| `elevationPressed` | offset (0, 1dp) — o card "afunda" | surface −2% | Estado pressionado |

### 2.6 Movimento

**Durações**

| Token | ms | Uso |
|---|---|---|
| `durationPress` | 120 | Feedback de toque (escala, cor) |
| `durationState` | 200 | Troca de estado (cooldown ↔ disponível) |
| `durationEnter` | 320 | Entrada de tela, sheet |
| `durationFill` | 600 | Barra de força enchendo, barra de chance |
| `durationPackShake` | 900 | Pacote tremendo (3 oscilações) |
| `durationFlip` | 500 | Virada do dossiê (3D) |
| `durationDice` | 1400 | Rolagem do sorteio |
| `durationBurst` | 450 | Brilho de vitória |

**Curvas**

| Token | Curva (Compose) | Uso |
|---|---|---|
| `easeStandard` | `CubicBezierEasing(0.2f, 0f, 0f, 1f)` | Maioria das transições |
| `easeDecelerate` | `CubicBezierEasing(0f, 0f, 0.2f, 1f)` | Entradas |
| `easeAccelerate` | `CubicBezierEasing(0.4f, 0f, 1f, 1f)` | Saídas |
| `easeDice` | `CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)` | Desaceleração da rolagem (começa rápido, para com peso) |
| `springPack` | `spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMediumLow)` | Tremor do pacote e "pop" do herói |

**Regra de movimento reduzido:** se `Settings.Global.ANIMATOR_DURATION_SCALE == 0` (ou a preferência de sistema "remover animações"), todas as durações viram 0 e os valores finais aparecem direto. Rolagem do dado vira número estático; confete/brilho vira faixa estática. Nunca bloquear a ação do usuário esperando animação: o botão de ação já reflete o resultado final antes da animação terminar, ou o usuário pode tocar "Pular".

---

## 3. Componentes reutilizáveis

Convenção: nome Compose sugerido, pacote `ui/components/`. Todos recebem estado por parâmetro (sem ViewModel dentro do componente). Tamanho mínimo de qualquer toque: 48 × 48 dp.

### 3.1 `HeroCard`
- **Aparência:** card `radiusCard`, `elevation0`. Topo: imagem do herói 1:1 (recorte no busto), 16dp de padding interno. Abaixo: nome em `headlineSmall` (máx. 2 linhas), linha de atributos com `StatChip` (Poder, Veterania) e `CooldownBadge` quando aplicável. Carimbo "RECRUTADO" em `labelMedium` rotacionado −4°, cor `success`, borda 1,5dp, no canto superior direito da imagem.
- **Tamanho:** 2 colunas em phone (largura ≈ 164dp, altura ≈ 252dp); largura total em lista compacta (altura 96dp, imagem 72dp quadrada).
- **Estados:**
  - `normal`: borda `outline` 1dp.
  - `pressionado`: `elevationPressed`, escala 0,98 em 120ms.
  - `desabilitado`: imagem em escala de cinza + overlay `surface` 50%; sem ação de toque (ainda abre Perfil? **Sim**: continua tocável para ver o dossiê; o que fica desabilitado é a seleção para time).
  - `cooldown`: overlay de `CooldownBadge` ocupando a base; carimbo "DESCANSO"; barra de progresso fina (4dp) mostrando tempo restante.
  - `selecionado` (Tela 2): borda 2dp `primary`, `elevation1`, marca de check circular 24dp no canto superior esquerdo.
- **Conteúdo:** imagem (Coil, placeholder = silhueta sobre meios-tons com iniciais), nome, Poder, Veterania, estado (disponível/descanso/selecionado).

### 3.2 `PowerBar`
- **Aparência:** barra horizontal de 8dp de altura, `radiusPill`, trilho `surfaceVariant`, preenchimento `primary` com gradiente sutil de 0% a 12% mais claro. Marcações a cada 25% (linhas de 1dp).
- **Uso:** valores 0–100 (Poder ajustado, Veterania, Dificuldade). Valor numérico ao lado em `statValueSmall`.
- **Estados:** `normal`; `animando` (preenche de 0 até o valor em `durationFill`, `easeDecelerate`); `desabilitado` (trilho e preenchimento em `outline`).
- **Conteúdo:** rótulo (`labelMedium`), valor, barra. `semantics { progressBarRangeInfo }` com descrição "Poder 8 de 100".

### 3.3 `StatChip`
- **Aparência:** pill de 32dp de altura, `radiusControl`, fundo `surfaceVariant`, texto `labelMedium` (rótulo em `onSurfaceVariant`) + valor em `statValueSmall` (`onSurface`).
- **Estados:** `normal`; `destaque` (fundo `primaryContainer`, valor em `onPrimaryContainer`) para o atributo maior do herói.
- **Conteúdo:** "PODER 8", "VETERANIA 80". Altura mínima 32dp mas a área de toque, se clicável, é ampliada para 48dp.

### 3.4 `CooldownBadge`
- **Aparência:** pill `radiusPill`, fundo `cooldown` a 16% de alpha, borda 1dp `cooldown`, ícone de ampulheta 14dp + texto `statValueSmall` "1h 25m".
- **Estados:** `ativo` (contagem regressiva, atualiza a cada minuto; a cada 1s só nos últimos 60s); `pronto` (vira `success` "DISPONÍVEL" com transição de 200ms, ícone de check).
- **Conteúdo:** tempo restante formatado (`h`/`min`); texto sempre visível (não só ícone) para acessibilidade.

### 3.5 `DifficultyTag`
- **Aparência:** tag `radiusTag`, altura 24dp, texto `labelMedium` CAIXA ALTA, fundo da cor da dificuldade a 12% de alpha, borda 1dp na cor cheia, e **pips** (1, 2 ou 3 losangos de 6dp) à esquerda.
- **Mapa:** Fácil = 1 pip, `difficultyEasy`; Médio = 2 pips, `difficultyMedium`; Épico = 3 pips, `difficultyEpic`.
- **Regra de acessibilidade:** o rótulo de texto e os pips existem para não depender só da cor.
- **Estados:** `normal`; `bloqueado` (não usado — missões bloqueadas não aparecem na Tela 3).

### 3.6 `MissionCard`
- **Aparência:** card `radiusCard`, `elevation1`, borda esquerda de 4dp na cor da dificuldade. Topo: `DifficultyTag` e "Y issues" em `labelSmall` (`y` = número de issues). Título do arco em `headlineSmall` (máx. 2 linhas). Descrição (`deck`) em `bodyMedium` máx. 2 linhas. Rodapé: `Dificuldade 86,9` em `statValueSmall`, e avatares 24dp dos heróis recrutados que desbloqueiam a missão (máx. 3 + "+N").
- **Estados:** `normal`; `pressionado`; `em cooldown do time?` não se aplica (cooldown é por herói, não por missão — RF-22). `sem heróis elegíveis` (todos os desbloqueadores estão em cooldown): card com carimbo "EM MISSÃO" ou "DESCANSO" leve e texto "Heróis que desbloqueiam: em descanso" — o card continua tocável para ver o time.
- **Conteúdo:** título, `deck`, dificuldade, número de issues, heróis desbloqueadores.

### 3.7 `PrimaryButton`
- **Aparência:** `radiusControl`, altura 52dp, largura total (ou mínima 160dp), fundo `primary`, texto `labelLarge` `onPrimary`, ícone opcional 20dp à esquerda. Sombra dura de 3dp (só claro).
- **Estados:**
  - `normal`: fundo `primary`.
  - `pressionado`: escala 0,97 (120ms), fundo `primary` escurecido 8%, sombra offset 1dp.
  - `desabilitado`: fundo `outline` a 30% de alpha, texto `onSurfaceVariant` (≥ 4,5:1 sobre o fundo desabilitado não é exigido por WCAG; mantemos legível mas o motivo é mostrado em texto ao lado).
  - `carregando`: spinner 20dp no lugar do ícone; largura mantida.
- **Conteúdo:** rótulo em CAIXA ALTA. Quando desabilitado por regra de negócio, o motivo aparece em texto de apoio logo abaixo (ex.: "Escolha ao menos 3 heróis disponíveis").

### 3.8 `SecondaryButton` / `GhostButton`
- **SecondaryButton:** borda 1,5dp `secondary`, texto `secondary`, fundo transparente. Pressionado: fundo `secondaryContainer`.
- **GhostButton:** texto `secondary` sem borda, área de toque 48dp. Usado em "Ver perfil", "Pular".

### 3.9 `CoinPill` e `XpPill`
- **CoinPill:** pill 36dp, ícone de moeda (círculo `coin` com marca de cunhagem em `surface`), valor `statValueSmall` em `coin`. Atualiza com contagem (`animateIntAsState`, 300ms).
- **XpPill:** igual, ícone de losango com barra, cor `xp`, texto "XP 1.250".
- **Estados:** `normal`; `incremento` (flash de 200ms com fundo `success` a 16%, valor sobe).

### 3.10 `PackRevealDialog`
- **Aparência:** dialog fullscreen-ish (margem 16dp), `radiusSheet`, fundo `surface`, faixa "PACOTE DE RECRUTAMENTO" no topo em `labelMedium`. Centro: área de revelação de 260 × 340dp.
- **Sequência (ver Tela 1, seção 4):** capa de dossiê fechado → tremor → virada 3D → dossiê aberto com herói → carimbo "RECRUTADO" → botão "Continuar".
- **Estados:** `fechado`; `tremendo`; `revelando`; `revelado`; `sem pool` (todos os heróis já recrutados — botão de compra fica desabilitado antes de abrir, esse estado não entra no dialog).
- **Conteúdo:** imagem do herói, nome, Veterania, mensagem "Novo recruta" e o custo pago ("−100 Moeda").

### 3.11 `DiceRoll`
- **Aparência:** círculo de 160dp, borda 3dp `outline`, fundo `surface`. Dentro: número grande em `statValue` (56sp) que gira/conta de 0 a 99. Ao redor: arco de 270° segmentado em duas cores: **verde** de 0 a `chance×100` (sucesso) e **vermelho** do restante. Um ponteiro de 2dp aponta para o valor final.
- **Animação:** número conta rápido com `easeDice`, 1400ms, para no valor `roll`. Ponteiro se move junto. Ao parar: a faixa correspondente ao resultado recebe brilho de 300ms.
- **Estados:** `pronto` (aguardando, mostra "?" e botão "Rolar"); `rolando`; `parado` (valor final e cor do resultado); `estático` (reduced motion, mostra valor final direto).
- **Conteúdo:** valor, faixa de chance, rótulo "Rolagem 42 de 100 · Chance 57%".

### 3.12 `ForceBar` (barra de força do time)
- **Aparência:** barra segmentada de 12dp, `radiusPill`, com 5 segmentos (um por herói) e marcação de Dificuldade como linha vertical `primary` 2dp.
- **Animação:** enche de 0 até `teamStrength` em `durationFill`; a linha de Dificuldade é desenhada antes. Se `teamStrength > difficulty`, a região à direita da linha fica verde-claro (zona de vantagem); senão, vermelho-claro.
- **Estados:** `normal`; `estimada` (Tela 2, sem linha de animação); `final` (Tela 4).
- **Conteúdo:** `statValue` "ForçaTime 142", e rótulo "vs Dificuldade 86,9".

### 3.13 `ResultBanner`
- **Aparência:** faixa de largura total, altura 96dp, `radiusCard`, fundo: `success` (sucesso) ou `danger` (falha) em sólido, texto `onPrimary`-equivalente. Texto grande em `displayMedium` "MISSÃO CUMPRIDA" ou "MISSÃO FRACASSADA". Carimbo "SUCESSO"/"FALHA" rotacionado −6°.
- **Animação de vitória:** brilho radial dourado (`coin`, de 0% a 35% de alpha) expandindo de 0 a 1,2× da faixa em `durationBurst`, mais 12 raios de meio-tom que giram 15° e desaparecem. Falha: sem brilho; faixa "treme" 2 vezes (4dp).
- **Reduced motion:** sem brilho, sem tremor.

### 3.14 `RewardRow`
- **Aparência:** duas colunas iguais: XP e Moeda. Cada uma: ícone 24dp, número `statValue` 22sp, legenda `labelSmall` ("XP ganho", "Moeda ganha").
- **Conteúdo:** valores já arredondados (`roundToInt`). Falha mostra "×0,2" em `labelSmall` para explicar a redução (RF-14).

### 3.15 `StampLabel`
- **Aparência:** texto `labelMedium` CAIXA ALTA, borda 1,5dp, `radiusTag`, rotação fixa por instância (−6° a +4°), opacidade 0,9. Cores: `success` (RECRUTADO / SUCESSO), `danger` (FALHA), `secondary` (DESCANSO / EM MISSÃO), `coin` (NOVO).
- **Uso:** carimbo de herói, resultado, perfil.

### 3.16 `EmptyState` e `ErrorState`
- **EmptyState:** ilustração simples (silhueta em meios-tons, 120dp), título `titleLarge`, texto `bodyLarge` (máx. 2 linhas), ação `PrimaryButton` ou `GhostButton`.
- **ErrorState:** ícone de alerta `danger`, título "Não foi possível carregar o roster", texto curto em `bodyLarge` ("Verifique sua conexão e tente de novo."), `PrimaryButton` "TENTAR NOVAMENTE". Sem retry automático (RF-20).

### 3.17 `TeamSlot`
- **Aparência:** quadro de 72 × 96dp, `radiusCard`, borda tracejada 1dp `outline` quando vazio, com "+" centralizado. Preenchido: miniatura do herói + nome abreviado + botão remover (48dp de área).
- **Estados:** `vazio`; `preenchido`; `invalido` (herói caiu em cooldown após seleção — removido com aviso); `foco` (borda 2dp `primary`).

### 3.18 `SynergyLink`
- **Aparência:** linha de 2dp `synergy` ligando dois `TeamSlot` com ícone de elo 16dp no meio. Quando há N pares, mostra badge "+10 ×N" (B_sin = 10 por par, RF-09) em `synergy`.
- **Estados:** `ativo` (linha sólida); `inativo` (tracejada `outline`) — não aparece em `inativo` pois só mostramos pares existentes.
- **Conteúdo:** contagem de pares e bônus total aplicado (até 40, cap do RF-09 mostrado como "máx. 40").

### 3.19 `FactionChip`
- **Aparência:** ver seção 2.1 (Facção). Altura 24dp, `radiusTag`, texto `labelMedium` com o nome da equipe.
- **Uso:** Tela 2 (agrupamento por facção) e Tela 5 (equipes do herói). Badge "+3 facção" quando `B_fac` > 0, em `statValueSmall`.

### 3.20 `StrengthSummary`
- **Aparência:** painel `surface` de 16dp de padding com `ForceBar`, linhas de decomposição (Poder/Veterania base, Sinergia `+N`, Facção `+N`) em `bodyMedium` com valores `statValueSmall`, e total em `statValue`.
- **Uso:** Tela 2 (estimativa) e Tela 4 (final). Mesma estrutura, com "estimado" vs "final".

### 3.21 `HeroSilhouette` (placeholder)
- **Aparência:** silhueta genérica em `outline` sobre meios-tons, iniciais em `titleLarge`. Usado quando a imagem falha ou não chegou. Nunca inventar imagem.

### 3.22 `TopBarDossier`
- **Aparência:** `surface`, altura 56dp, título em `titleLarge` CAIXA ALTA, ícone de voltar 48dp (quando aplicável), linha inferior de 2dp `outlineVariant` com dois ticks de 8dp nas extremidades (decorativo). Sem elevação.

### 3.23 `BottomNavBar`
- **Itens:** Roster (1), Missões (3), Perfil? **Não** — o Perfil é acessado por herói. Montagem (2) é contextual (FAB). Portanto barra com **2 itens**: "Roster" e "Missões".
- **Aparência:** altura 72dp (inclui inset), ícone 24dp + rótulo `labelMedium`; item ativo: pill `primaryContainer` atrás do ícone, cor `onPrimaryContainer`.
- **Estados:** `ativo`, `inativo` (`onSurfaceVariant`), `pressionado` (ripple).

### 3.24 `TeamFab`
- **Aparência:** extended FAB, `primary`, 56dp de altura, ícone de grupo + texto "MONTAR TIME" (CAIXA ALTA). Sombra dura (claro).
- **Estados:** `normal`; `com contagem` ("MONTAR TIME · 2/5") ao haver seleção em andamento; `desabilitado` quando não há ≥3 heróis disponíveis (texto muda para "PRECISA DE 3 HERÓIS DISPONÍVEIS" em tooltip/linha de apoio, não só cor).

### 3.25 `SeedProgress`
- **Aparência:** indicador linear de 4dp com trilho `surfaceVariant`, preenchimento `primary`, texto `labelMedium` com a etapa ("Baixando personagens 12/24", "Montando arcos"). Indeterminado se a etapa não tiver total.
- **Reconhecimento:** nunca sugerir progresso falso; se a etapa não tem total, usar indeterminado.

---

## 4. Telas

Ordem de navegação ao iniciar: Seed → (erro → Tela 0) → Roster. Todas as telas são portrait; a Tela 4 aceita landscape em phone grande como limitação conhecida (sem layout dedicado nesta versão).

### Tela 0a — Seed (loading)
**Quando:** primeira execução, enquanto o seed (RF-01) baixa e grava o roster.

**Estrutura (topo → baixo):**
1. Espaço de 1/4 da altura, logotipo textual "MARVEL RECRUITER" em `displayMedium` (texto, não logo), subtítulo "Dossiê de recrutamento" em `labelMedium`.
2. Ilustração: pasta de dossiê fechada com meios-tons (160dp), levemente animada (respiração de escala 1,00 → 1,02 em 1600ms, desligada em reduced motion).
3. `SeedProgress` com texto de etapa, 16dp abaixo da ilustração.
4. Rodapé: `labelSmall` "Dados: Comic Vine. Uso não comercial, com atribuição." (exigência da Comic Vine).

**Ações:** nenhuma (não há como sair; o app está bloqueado até o seed completar — não é uma tela em branco).

**Estado de erro:** transição para Tela 0b.

### Tela 0b — Erro de seed
**Quando:** falha de rede ou erro da Comic Vine (RF-20).

**Estrutura:**
1. Mesmo cabeçalho da Tela 0a.
2. `ErrorState`: ícone de alerta `danger` 48dp; título "Não foi possível preparar o recrutamento"; texto "Sem conexão ou a Comic Vine não respondeu. O jogo precisa do roster para começar."
3. `PrimaryButton` "TENTAR NOVAMENTE" (largura total, 52dp).
4. Abaixo, `labelSmall` com código de erro curto (ex.: "Erro de rede" / "Limite de requisições"), sem detalhe técnico.

**Regras:**
- Nenhum retry automático (RF-20). Ao tocar, volta para a Tela 0a e tenta uma única vez.
- Nada de fallback offline (fora de escopo).
- O estado de erro persiste se o app for reaberto sem seed concluído.

### Tela 1 — Roster / Recrutamento
**Objetivo:** ver quem foi recrutado, o saldo, e comprar pacotes (RF-16/17).

**Estrutura (topo → baixo):**
1. `TopBarDossier` "ROSTER". À direita: `CoinPill` (coinBalance) e `XpPill` (xpTotal) — na largura de 360dp cabem os dois se o título for curto; senão, pills vão para uma faixa abaixo do título (linha de 40dp, fundo `surfaceVariant`).
2. Faixa de dossiê (`surfaceVariant`, 56dp): "RECRUTADOS 3/24" em `labelMedium` + barra fina (`PowerBar` não; usar barra de 4dp simples).
3. Bloco "Pacote de recrutamento" (card `elevation1`, 16dp de padding):
   - Título `titleLarge` "PACOTE DE RECRUTAMENTO".
   - Texto `bodyMedium` "Sorteia um herói ainda não recrutado. Chance maior para veteranos."
   - `PrimaryButton` "COMPRAR · 100 MOEDA" (52dp). Mostra "Saldo insuficiente" em texto abaixo quando `coinBalance < 100`, e o botão fica desabilitado.
   - Se não há mais heróis não recrutados: botão desabilitado com texto "TODOS RECRUTADOS".
4. Título de seção `titleLarge` "SEUS HERÓIS" + contagem à direita.
5. Grade de `HeroCard`:
   - Phone: 2 colunas, `Arrangement.spacedBy(12dp)`.
   - ≥ 600dp: 3 colunas.
   - Ordenação: `recruitedAt` desc (RF-ordenação Tela 1 em `plan.md`); opção de filtro "Disponíveis" em chip.
6. Abaixo da grade: `HalftoneOverlay` no fundo, não interfere no conteúdo.

**Ações:**
- Toque no card → Tela 5 (Perfil).
- Toque em "COMPRAR" → `PackRevealDialog` (ver microinterações).
- Toque em chip "Disponíveis" → filtra a grade.

**Estados:**
- **Vazio (nenhum recruta ainda; estado inicial, 300 Moeda = 3 pacotes):** `EmptyState` acima da grade: silhueta em meios-tons, título "Nenhum herói recrutado", texto "Você começa com 300 Moeda, o bastante para 3 pacotes. Compre o primeiro para montar seu time.", ação "COMPRAR PACOTE" (scroll/abre dialog). Grade mostra 3 slots vazios tracejados (`TeamSlot` estilo vazio) para indicar o alvo de 3.
- **Parcial (1–2 recrutados):** banner `surfaceVariant` "Faltam N para montar um time de 3" em `bodyMedium`.
- **Erro:** `ErrorState` inline se o repositório falhar ao ler (raro, não bloqueia o resto).
- **Carregando:** skeleton de cards (retângulos `surfaceVariant` com brilho de 1200ms em loop, desligado em reduced motion).

**Microinterações:**
- Pressionar "COMPRAR": botão escala 0,97; saldo `CoinPill` desconta com contagem reversa (300 → 200) em 300ms.
- Ver seção "Revelação do pacote" abaixo.
- Novo herói entra na grade com `springPack` (escala 0,8 → 1) e carimbo "NOVO" (`coin`).

**Revelação do pacote (`PackRevealDialog`), sequência:**
| Tempo | Evento |
|---|---|
| 0 ms | Dialog abre (`durationEnter`, fade + escala 0,96 → 1). Capa de dossiê fechado no centro. |
| 0–900 ms | Pacote treme: 3 oscilações de rotação ±4° com `springPack`. Som opcional (não nesta versão). |
| 900–1400 ms | Pacote "estoura": 8 raios de meio-tom saem do centro (450ms) e a capa some. |
| 1400–1900 ms | Dossiê vira (rotateY 0 → 180°, `durationFlip`). Em 90° troca a face. |
| 1900–2200 ms | Foto do herói aparece com `springPack`; nome em `displayLarge` entra da esquerda. |
| 2200–2500 ms | Carimbo `RECRUTADO` (`success`) cai com rotação −8°, pequeno "impacto" (escala 1,15 → 1). |
| 2500 ms+ | Botão "CONTINUAR" aparece (fade 200ms). |

- **Pular:** GhostButton "PULAR" disponível desde 0 ms; revela direto.
- **Reduced motion:** sem tremor, sem virada: mostra o herói com carimbo imediatamente.
- Fechar o dialog por toque fora só depois de revelado (evita cancelar a compra visualmente; a compra já foi debitada).

**Estado de erro na compra:** se a transação falhar, dialog não abre, Snackbar "Não foi possível comprar agora. Nada foi cobrado." (o saldo não muda).

### Tela 2 — Montagem de time
**Objetivo:** escolher de 3 a 5 heróis disponíveis, ver sinergia, facção, cooldown e força estimada (RF-08/09).

**Entrada:** a partir de Tela 1 (botão `TeamFab`) ou de Tela 3 (com arco pré-selecionado, mostrado no topo).

**Estrutura (topo → baixo):**
1. `TopBarDossier` "MONTAR TIME" com botão voltar.
2. Cabeçalho do arco (se vindo da Tela 3): `MissionCard` compacto (altura 88dp) com nome e `DifficultyTag`. Se não há arco, faixa "Escolha um arco na aba Missões" em `labelMedium` (a ação de iniciar fica desabilitada).
3. `StrengthSummary` (estimado): `ForceBar` com `teamStrength` e linha de Dificuldade do arco. Atualiza a cada seleção (animação 300ms).
4. Linha de `TeamSlot` (5 slots: 3 obrigatórios marcados com "•", 2 opcionais), com `SynergyLink` entre slots que são amigos.
5. Título `titleLarge` "HERÓIS DISPONÍVEIS" + legenda "Toque para escalar · Máx. 5".
6. Grade de `HeroCard` (modo seleção). Cards em cooldown ficam desabilitados com `CooldownBadge` ativo e aparecem no fim da lista (ordenação: disponíveis primeiro).
7. Agrupamento por facção (opcional, chip de filtro "Agrupar por equipe"): cabeçalho com `FactionChip` antes de cada grupo.
8. Barra fixa inferior (acima da nav, 88dp de altura, `surface`, sombra de topo 1dp): `PrimaryButton` "INICIAR MISSÃO" + linha de apoio com contagem "3/5 escalados".

**Ações:**
- Toque em `HeroCard` disponível: seleciona/desseleciona (limite 5).
- Toque em `TeamSlot` preenchido: remove.
- Toque em "Ver perfil" (ícone i no card, área 48dp): → Tela 5.
- "INICIAR MISSÃO" → Tela 4 (após confirmação? **Não**: a missão é executada direto; RF-22 permite repetir).

**Regras de estado do botão "INICIAR MISSÃO" (RF-08):**
- `desabilitado` se `selectedCount < 3` (texto de apoio: "Escale ao menos 3 heróis").
- `desabilitado` se `availableHeroes < 3` no roster inteiro (texto de apoio: "Você precisa de 3 heróis disponíveis. Aguarde o descanso ou recrute mais.").
- `desabilitado` se não houver arco selecionado.
- `habilitado` com 3–5 selecionados.

**Estados:**
- **Menos de 3 disponíveis (todos ou quase todos em descanso):** topo mostra `EmptyState` compacto: "Só X herói(s) disponível(is)". Mostra o menor `CooldownBadge` restante e a hora de liberação do próximo herói. Botão desabilitado.
- **Nenhum herói recrutado:** redireciona para Tela 1 com `Snackbar` "Recrute ao menos 3 heróis primeiro."
- **Erro de cálculo:** `ErrorState` inline improvável; se o `StrengthSummary` falhar, mostra "Força indisponível" e mantém a seleção.

**Microinterações:**
- Selecionar card: borda `primary` 2dp, check circular entra com `springPack`, `TeamSlot` recebe o herói com escala 0,8 → 1.
- Sinergia: ao formar par de amigos, a linha `synergy` "desenha" (animação de 300ms do ponto A ao B) e badge "+10" aparece.
- Facção: ao somar o 2º herói da mesma equipe, o `FactionChip` ganha "+3" com pulso de 200ms.
- `ForceBar` anima o novo total em `durationFill` (600ms) — com 300ms no uso normal para não atrasar a seleção.

### Tela 3 — Lista de missões
**Objetivo:** arcos desbloqueados (RF-19: pelo menos um herói recrutado desbloqueia), com dificuldade (Fácil/Médio/Épico, RF-11).

**Estrutura (topo → baixo):**
1. `TopBarDossier` "MISSÕES".
2. Faixa de filtros (chips de seleção, altura 40dp, scroll horizontal): "Todas", "Fácil", "Médio", "Épico". Chip ativo = `primaryContainer`.
3. Resumo do dia (opcional, fica em `surfaceVariant`): "Missões disponíveis: 7 · Heróis em descanso: 2".
4. Lista vertical de `MissionCard`, ordenada por Dificuldade asc (padrão) ou por número de issues.
5. Rodapé: `labelSmall` com atribuição da Comic Vine.

**Ações:**
- Toque no `MissionCard` → Tela 2 com arco fixo no topo (leva o arco como argumento de navegação).
- Toque em chip → filtra.

**Estados:**
- **Vazio (nenhum herói recrutado, ou nenhum arco desbloqueado):** `EmptyState` "Nenhuma missão desbloqueada", texto "Recrute um herói para liberar as HQs dele como missões.", ação "IR PARA ROSTER" (`PrimaryButton`).
- **Filtro sem resultado:** texto "Nenhuma missão nessa dificuldade" + `GhostButton` "Limpar filtro".
- **Carregando:** 3 cards skeleton.
- **Erro:** `ErrorState` local (não tela cheia).

**Microinterações:**
- Ao desbloquear uma missão após recrutar um herói, o card entra com `springPack` e carimbo "NOVO" (`coin`).
- Pressionar card: `elevationPressed`, escala 0,98.

### Tela 4 — Resultado da missão
**Objetivo:** mostrar o cálculo (ForçaTime, Dificuldade, chance, sorteio), a rolagem animada e o resultado com recompensa (RF-12/13/14).

**Chegada:** automática após "INICIAR MISSÃO". O cálculo é feito antes da animação (o resultado já existe; a animação apenas revela). Assim a tela pode ser reaberta depois via histórico.

**Estrutura (topo → baixo), fase "revelação":**
1. `TopBarDossier` "RELATÓRIO" (sem voltar durante a rolagem; ação de voltar aparece após o resultado).
2. Título do arco em `headlineSmall` + `DifficultyTag`.
3. Bloco "Cálculo" (painel `surface`, `elevation0`), linhas em `bodyMedium` com valores `statValueSmall`:
   - ForçaTime `142,0` (`teamStrength`)
   - Dificuldade `86,9` (`difficulty`)
   - Chance `57%` (`chance`), com fórmula curta "(ForçaTime − Dificuldade) / Dificuldade × k + 0,5, limitada a 5%–95%" em `labelSmall` (sem RF-XX; o conteúdo técnico fica em texto).
4. `ForceBar` final (ver 3.12).
5. `DiceRoll` (160dp), centralizado, com botão "ROLAR" (`PrimaryButton`) enquanto `pronto`; a animação começa ao tocar (ou automaticamente após 600ms se o usuário não tocar — opção de config não nesta versão; **decidido: automático após 600ms**, para não exigir toque a cada missão).
6. Texto de apoio: "Rolagem 42 de 100. Chance de 57% → sucesso."

**Fase "resultado" (após a rolagem, ~1,5 s):**
7. `ResultBanner` (96dp): "MISSÃO CUMPRIDA" (sucesso) ou "MISSÃO FRACASSADA" (falha), com brilho (vitória) ou tremor (falha).
8. `RewardRow`: XP e Moeda ganhos (valores arredondados). Falha exibe "×0,2".
9. Bloco "Descanso": lista dos heróis usados com `CooldownBadge` ativo e o tempo (`15 × Dificuldade/10` min, mostrado já convertido em h/min).
10. Botões (lado a lado, 48dp cada, ou empilhados se fonte 200%):
    - `PrimaryButton` "NOVA MISSÃO" → volta para Tela 3.
    - `SecondaryButton` "MONTAR TIME" → Tela 2 com o mesmo arco (RF-22: repetir é normal).

**Ações:**
- Toque em hero na lista de descanso → Tela 5.
- Voltar: disponível só após o resultado (botão de voltar só depois do resultado; antes, o gesto de voltar é bloqueado para não interromper a animação — com aviso "Aguarde a rolagem" se pressionado).

**Estados:**
- **Carregando (cálculo ainda não pronto):** `SeedProgress` indeterminado "Calculando" (raro, <300ms).
- **Erro ao salvar resultado:** `ErrorState` inline com botão "TENTAR SALVAR"; não repete a rolagem (o sorteio é o que foi calculado).
- **Histórico (reabrir):** pula a animação e mostra o resultado final direto.

**Microinterações:**
- Rolagem: ver `DiceRoll` (1400ms, `easeDice`).
- Vitória: brilho dourado + 12 raios de meio-tom (450ms), haptic `success` se disponível (`HapticFeedbackType` não tem success em todas as APIs; usar `LongPress` leve ou nada em API 33 sem vibrador).
- Falha: tremor 2× de 4dp (300ms), sem brilho.
- Contadores de XP e Moeda sobem de 0 ao valor em 600ms (`animateIntAsState`).

### Tela 5 — Perfil do herói (wiki)
**Objetivo:** dossiê completo do herói recrutado (RF-18). Herói não recrutado: wiki bloqueada (ver estados).

**Estrutura (topo → baixo):**
1. Imagem grande (largura total, altura 360dp, `contentScale = Crop`, alinhada ao topo). Sobre a imagem: gradiente de `surface` (de 0% a 100% de alpha nos últimos 40%) e carimbo `RECRUTADO` ou `DESCANSO`/`EM MISSÃO` (`StampLabel`).
2. Bloco de título (sobre o gradiente, 16dp de padding):
   - Nome do herói em `displayLarge` (máx. 2 linhas).
   - Nome real em `titleMedium` ("Nome real: …") — se `realName` não existir, linha omitida.
   - Aliases em `bodyMedium` ("Também conhecido como: …") — se vazio, omitido.
3. Seção "Atributos" (`titleLarge`):
   - `PowerBar` Poder (valor 0–100, com "8" em mono).
   - `PowerBar` Veterania (valor 0–100).
   - Linha de `StatChip`: "PODERES 8" (`numPowers`), "APARIÇÕES 7917" (`numAppearances`), em mono, sem arredondar.
4. Seção "Bio" (`titleLarge` "DOSSIÊ"): `deck` em `bodyLarge`. Se vazio: "Sem resumo disponível." em `onSurfaceVariant`.
5. Seção "Equipes" (`titleLarge`): `FactionChip` por equipe (`teams` do herói, lista horizontal com scroll; máx. visível 6 + "+N").
6. Seção "Participou de" (`titleLarge`): lista de `MissionCard` compactos (RF-18: arcos curados em que o herói aparece), 1 linha cada com `DifficultyTag` e título. Toque → Tela 3 com o arco destacado? **Não**: abre o detalhe do arco na Tela 2 (escolha de time) — decisão simples: abre Tela 3 filtrada.
7. Seção "Sinergia" (opcional, se houver amigos no roster): lista de `SynergyLink` textuais: "Amigo no time: Colossus" (chips de heróis do roster).

**Ações:**
- Botão de voltar (`TopBarDossier`) e swipe de voltar.
- Toque em arco → Tela 3/2.
- Toque em herói amigo → Tela 5 do amigo (apenas se recrutado; se não, chip desabilitado com texto "Bloqueado").

**Estados:**
- **Herói não recrutado (wiki bloqueada, RF-18):** imagem em escala de cinza com overlay de meios-tons, carimbo "ARQUIVO BLOQUEADO" (`secondary`); seções de bio, equipes e arcos substituídas por um `EmptyState` com cadeado: "Recrute este herói para desbloquear o dossiê." e botão "IR PARA ROSTER". Atributos ficam visíveis (Poder/Veterania são dados públicos do roster curado; a decisão de mostrar ou esconder deve ser confirmada no spec — **proposta: mostrar**, pois ajuda a decidir o recrutamento). Esta proposta não altera regras; se preferido esconder, atualizar `specs/spec.md` antes (CLAUDE.md).
- **Sem imagem:** `HeroSilhouette` grande.
- **Sem deck:** texto de apoio "Sem resumo disponível.".
- **Carregando:** skeleton da imagem e linhas de texto.

**Microinterações:**
- Entrada: imagem faz zoom 1,05 → 1,00 em 600ms.
- Carimbo do herói cai com rotação (`springPack`) na primeira vez que a tela abre após o recrutamento.

---

## 5. Fluxo de navegação

Navigation Compose, rotas tipadas (nomes sugeridos):

```
seed ──(ok)──────────────────────────► roster
  │
  └─(erro)─► seedError ──(tentar)──► seed

roster (BottomNav)
  ├─ card herói ───────────► hero/{cvId}
  ├─ COMPRAR ──────────────► PackRevealDialog (sobre roster)
  └─ MONTAR TIME ──────────► team            (sem arco)

missions (BottomNav)
  ├─ card arco ────────────► team?arcId={arcId}
  └─ sem heróis ───────────► roster (CTA)

team?arcId={arcId}
  ├─ INICIAR MISSÃO ───────► result/{missionResultId}
  ├─ ver perfil ───────────► hero/{cvId}
  └─ voltar ───────────────► missions

result/{missionResultId}
  ├─ NOVA MISSÃO ──────────► missions (limpa pilha até missions)
  ├─ MONTAR TIME ──────────► team?arcId={arcId}
  └─ herói em descanso ────► hero/{cvId}

hero/{cvId}
  ├─ arco participado ─────► missions?highlight={arcId}
  ├─ amigo recrutado ──────► hero/{friendCvId}
  └─ bloqueado: IR PARA ROSTER ► roster
```

Regras:
- BottomNav aparece em `roster` e `missions` apenas. `team`, `result`, `hero` usam `TopBarDossier` com voltar.
- `seed` e `seedError` não mostram BottomNav nem TopBar.
- Back em `result` durante rolagem: bloqueado (ver Tela 4).
- Após "NOVA MISSÃO", a pilha volta para `missions` (não acumula `result`).
- Estado de `team?arcId` sobrevive a rotação (`rememberSaveable` / estado no ViewModel); seleção não é perdida ao girar a tela.

---

## 6. Acessibilidade e tamanho de tela

### 6.1 Contraste e cor
- Texto normal ≥ 4,5:1; texto grande e componentes/indicadores ≥ 3:1 (WCAG AA).
- Cor nunca é o único indicador: dificuldade tem rótulo e pips; sucesso/falha tem texto e ícone; cooldown tem texto de tempo; sinergia tem badge com valor; facção tem nome.
- Vermelho (`primary`) e verde (`success`) não são as únicas cores de resultado: o banner tem texto e a barra de chance tem rótulo "57%".
- Daltonismo: pares crimson/verde da barra de chance são separados por posição e rótulo; validar com simulação de deuteranopia.

### 6.2 Toque e foco
- Alvo mínimo 48 × 48 dp para todo elemento interativo (inclui ícones de "ver perfil", botão remover de `TeamSlot`, chips de filtro).
- Espaço entre alvos adjacentes ≥ 8dp.
- Foco de teclado/switch access visível: anel de 2dp `primary` com 2dp de offset.

### 6.3 Leitor de tela (TalkBack)
- Todo ícone com ação tem `contentDescription` (ex.: "Ver perfil de Colossus").
- Decorativos (ticks, meios-tons, carimbo de fundo) têm `contentDescription = null`.
- `HeroCard` agrupa nome + atributos com `mergeDescendants`: "Colossus. Poder 8. Veterania 81. Disponível."
- `DiceRoll` anuncia o resultado final, não cada número: "Rolagem 42 de 100. Sucesso."
- `ForceBar` usa `ProgressBarRangeInfo`: "ForçaTime 142 de 200" (máximo de referência 200 apenas para acessibilidade; não é regra de jogo).
- Ordem de foco segue o layout de cima para baixo; `PrimaryButton` fixo (Tela 2) é o último item.
- Estados desabilitados anunciam o motivo (texto de apoio é ligado ao botão com `stateDescription`).

### 6.4 Texto e escala
- Suporte a fonte do sistema até 200% (`fontScale` 2.0): textos quebram, nunca truncam nomes de herói ou títulos de arco sem `maxLines` + reticências.
- Botões empilham em coluna quando largura < 360dp ou fonte > 130%.
- Chips horizontais: scroll horizontal, nunca quebram linha dentro da barra.
- Testes obrigatórios em 320 × 640 dp com fonte 200%.

### 6.5 Movimento
- Respeitar a preferência de remover animações (seção 2.6).
- Nenhuma animação passa de 1,5 s ou pisca mais de 3 vezes por segundo (limite de segurança para fotossensibilidade).
- Confete/brilho não tem flash branco abrupto: usar fade de 200ms.

### 6.6 Tamanho de tela e orientação
- **Referência:** 360 × 800 dp (phone comum). Validar em 320 × 640 (menor), 412 × 915 (grande) e 600 × 960 (dobrável/tablet pequeno).
- **Phone (< 600dp):** 1 coluna para listas de texto; grade de heróis 2 colunas; gutter 16dp.
- **Tablet / dobrável (≥ 600dp):** grade de heróis 3 colunas; Tela 2 com painel de time à esquerda (40%) e grade à direita (60%); gutter 24dp.
- **Orientação:** portrait travado em phone (<600dp). Em ≥ 600dp liberar landscape e usar o layout de duas colunas.
- **Safe area:** respeitar `WindowInsets.systemBars` e `displayCutout`; barra fixa da Tela 2 usa `navigationBarsPadding()`.
- **Dark mode:** segue o sistema; todos os tokens têm par claro/escuro (seção 2.1); nenhuma imagem de herói recebe filtro automático de escurecimento além do gradiente da Tela 5.

### 6.7 Qualidade de imagem e dados externos
- Imagens do herói via Coil com cache; placeholder `HeroSilhouette` enquanto carrega ou em erro.
- Textos vindos da Comic Vine são exibidos como estão (sem tradução automática nesta versão); mostrar atribuição "Dados: Comic Vine" no rodapé de Tela 0a, Tela 3 e Tela 5.
- Nenhum texto ou imagem é inventado pelo app: campos ausentes são omitidos (ver Tela 5, "Sem resumo disponível").

---

## 7. Decisões em aberto (confirmar antes da implementação)

1. Mostrar Poder/Veterania de herói não recrutado na Tela 5 (proposta: sim). Se não, atualizar `specs/spec.md`.
2. Faixas de "Comum / Raro / Lendário" para o pacote: apenas rótulo visual baseado em Veterania (proposta: <40, 40–70, ≥70). Não é regra de jogo; se for usado, registrar no spec.
3. Rolagem automática após 600ms na Tela 4 (proposta: sim, sem toque obrigatório).
4. Escolha de fontes: Anton + Barlow Condensed + Inter + JetBrains Mono (OFL), empacotadas em `res/font/`.
5. Logotipo: texto "MARVEL RECRUITER" em Anton, sem arte de marca; não usar logos ou símbolos de editoras.

## 8. Decisões tomadas (resolvem a seção 7)

1. Poder e Veterania de herói não recrutado aparecem na Tela 5 — sim.
2. Faixas Comum/Raro/Lendário são só rótulo visual de Veterania (<40 / 40–70 / ≥70), sem efeito de regra.
3. Rolagem automática na Tela 4 após 600ms, sem toque obrigatório.
4. Fontes Anton, Barlow Condensed, Inter e JetBrains Mono (OFL), empacotadas em `res/font/`.
5. Logotipo em texto "MARVEL RECRUITER" (Anton), sem arte de marca ou símbolo de editora.
