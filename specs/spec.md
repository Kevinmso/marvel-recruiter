# Spec — Marvel Recruiter

*Versão limpa dos requisitos, sem histórico de decisão. Para o "porquê" de cada escolha, ver as docs de organização pessoal (design de jogo e doc técnica).*

## Nome do projeto
**Marvel Recruiter** — ver `specs/sugestoes-nome.md` pro histórico de opções consideradas.

## Visão geral
App mobile "manager" onde o jogador recruta personagens da Marvel (dados da Comic Vine), monta um time, resolve missões baseadas em arcos de história reais por cálculo de atributos (sem física/simulação), ganha recursos, e desbloqueia conteúdo de wiki (bio, HQs) do personagem como recompensa — não como o produto principal do app.

## Requisitos funcionais

### Notação ↔ identificadores de código
As fórmulas abaixo usam notação matemática em pt. No código, use o identificador en correspondente (CLAUDE.md → "Convenções de código"):

| Fórmula (pt) | Código (en) | | Fórmula (pt) | Código (en) |
|---|---|---|---|---|
| `Poder` | `power` | | `ForçaTime` | `teamStrength` |
| `Poder_compensado` | `compensatedPower` | | `B_sin` | `synergyBonus` |
| `Poder_ajustado` | `adjustedPower` | | `B_fac` | `factionBonus` |
| `Veterania` | `veterancy` | | `Dificuldade` | `difficulty` |
| `p_min`/`p_max` | `pMin`/`pMax` | | `m` (multiplicador) | `rewardMultiplier` |
| `x_min`/`x_max` | `xMin`/`xMax` | | `Moeda` (saldo / ganho) | `coinBalance` / `coinsEarned` |
| `y_min`/`y_max` | `yMin`/`yMax` | | `XP` (placar / ganho) | `xpTotal` / `xpEarned` |
| `γ` | `gamma` | | `chance`, `roll`, `k`, `α`, `β` | iguais (`alpha`, `beta`) |

### Dados e onboarding
- **RF-01**: Na primeira execução, o sistema DEVE popular o banco local com o roster curado de personagens e arcos. Os dados vêm de um snapshot empacotado no app (`assets/roster_snapshot.json`), gerado uma única vez a partir da Comic Vine por `tools/generate_roster_snapshot.py`. Assim o seed não depende da rede nem do limite de 200 requisições/hora da API, e reinstalações de teste ficam rápidas. Para atualizar o roster, regenera-se o snapshot.
- **RF-02**: O sistema DEVE calcular e persistir, no momento do seed, os atributos derivados de cada personagem e arco (Poder, Veterania, Dificuldade).
- **RF-03**: O sistema DEVE gravar os limites de normalização usados no seed como constantes fixas (ver constitution.md C-08, valores em `specs/constantes-normalizacao.md`).
- **RF-20**: Se o seed inicial (RF-01) falhar por falta de rede ou erro da Comic Vine, o sistema DEVE mostrar uma tela de erro explícita com botão "Tentar novamente" — NÃO DEVE tentar de novo automaticamente em loop, e NÃO DEVE deixar o app numa tela em branco/travada. Sem seed completo, o app não tem roster nem missões — não há fallback offline nesta versão (fora de escopo).

### Atributos de personagem
- **RF-04**: Poder é normalizado linearmente (0–100) a partir do nº de poderes listados no roster curado:
  `Poder = 100 * (p - p_min) / (p_max - p_min)`
  *Exemplo real testado (roster de 5, p_min=6, p_max=30): Colossus tem p=8 → Poder = 100*(8-6)/(30-6) ≈ 8,33*
  *(a lista de poderes da Comic Vine NÃO vem truncada — o `p_max` real do conjunto de teste é 30, da Carol Danvers, não 22; ver `specs/constantes-normalizacao.md`)*
- **RF-05**: Veterania é normalizada por log a partir de `count_of_issue_appearances`:
  `Veterania = 100 * (ln(x+1) - ln(x_min+1)) / (ln(x_max+1) - ln(x_min+1))`
  *Exemplo real testado (roster de 5, x_min=337, x_max=16924): Colossus tem x=7917 → Veterania ≈ 80,6*
- **RF-06**: Heróis com Veterania < 50 recebem bônus de Poder proporcional (compensação de raridade):
  `Poder_compensado = min(100, Poder + γ * max(0, 50 - Veterania))`, com **γ = 0.4** (valor inicial — calibrar em T-25)
  *Exemplo real testado: Black Goliath tem Poder=0, Veterania=0 → Poder_compensado = min(100, 0 + 0.4*50) = 20*
- **RF-07**: Poder ajustado final tem piso de segurança:
  `Poder_ajustado = max(15, Poder_compensado)`
  *Exemplo real testado: Colossus (Veterania=80,6 ≥ 50, sem compensação) → Poder_compensado≈8,33 → Poder_ajustado = max(15, 8,33) = 15*

### Montagem de time e força
- **RF-08**: O jogador DEVE poder montar um time de 3 a 5 heróis recrutados e disponíveis (fora de cooldown). Se o jogador tiver menos de 3 heróis disponíveis no momento, a ação de iniciar missão DEVE ficar desabilitada (não existe missão com menos de 3 heróis).
- **RF-09**: A força do time é:
  `ForçaTime = média(0.6*Poder_ajustado + 0.4*Veterania) + min(B_sin, 12) + min(B_fac, 5)`
  *(média sobre os heróis do time, não soma — calibração T-25: com soma, a chance satura em 95% em quase todas as missões; com média e k=1, a chance média fica em ~47% e cobre o intervalo completo. Simulado com os atributos reais dos 24 curados.)*
  - `B_sin = 4 * nº de pares não-ordenados de heróis no time onde PELO MENOS UM dos dois lista o outro em `character_friends`` (não precisa ser mútuo — basta uma direção, já que os dados da Comic Vine não garantem simetria)
  - `B_fac = 1 * nº de heróis do time que compartilham a equipe (teams) mais representada no time`. Em caso de empate entre duas ou mais equipes com o mesmo número de heróis, usar a equipe cujo `cv_id` for numericamente menor (regra arbitrária só pra determinismo). Se a equipe mais representada tiver apenas 1 herói do time (ninguém compartilha equipe), `B_fac = 0`.
- **RF-10**: Se todos os heróis do roster tiverem o mesmo valor de um atributo (divisão por zero na normalização), o sistema DEVE usar valor fixo 50 em vez de calcular.

### Missões
- **RF-11**: A Dificuldade de uma missão é calculada a partir do nº de issues do arco (`y` = tamanho do array `issues` — NÃO usar `count_of_issue_appearances`, campo quebrado pra story arcs):
  `Dificuldade = 30 + 100 * (ln(y+1) - ln(y_min+1)) / (ln(y_max+1) - ln(y_min+1))`
  Rótulo derivado (usado na Tela 3): Fácil se `Dificuldade < 60`; Médio se `60 ≤ Dificuldade < 100`; Épico se `Dificuldade ≥ 100`. Intervalo da dificuldade: 30–130 (revisão de equilíbrio: piso de 10 deixava as fáceis com 70–80% de chance; topo de 100 deixava as épicas com ~40% para times fortes).
  *Exemplo real testado (conjunto de 5 arcos, y_min=6, y_max=122): House of M tem y=80 → Dificuldade ≈ 86,9 (Épico)*
- **RF-12**: A chance de sucesso é uma curva logística sobre a diferença entre ForçaTime e Dificuldade:
  `chance = clamp(1 / (1 + e^(−(ForçaTime − Dificuldade) / 30)), 0.05, 0.95)`
  *(Revisão de equilíbrio, em duas etapas: a razão anterior `0,5 + k·(F−D)/(F+D)` não passava de ~25% em missões épicas, mesmo com times fracos. A logística com escala 25 dá ~11% de chance numa épica com time médio de 3 heróis, ~68% numa fácil e ~37% em média.)*
- **RF-13**: O resultado é definido por sorteio: `sucesso = (roll(0,100) < chance*100)`. A função de sorteio (`roll`) DEVE receber a fonte de aleatoriedade como parâmetro injetável (ver constitution.md C-15) — nunca chamar um gerador global direto, pra permitir testes determinísticos.
- **RF-14**: A recompensa DEVE depender do resultado:
  `XP = α * Dificuldade * m`, `Moeda = β * Dificuldade * m`, com **α = 10**, **β = 5** (valores iniciais — calibrar em T-25), onde `m = 1.0` em sucesso e `m = 0.2` em falha.
  XP e Moeda ganhos são arredondados pro inteiro mais próximo (`roundToInt`) antes de persistir/creditar.
  *Exemplo: missão de Dificuldade=50, sucesso → XP=500, Moeda=250. Mesma missão, falha → XP=100, Moeda=50. (β reduzido de 5 para 1,5 após a subida da escala de dificuldade; elevado para 2,5 em revisão de economia por feedback de teste no aparelho; elevado para 5 em nova revisão pedida pelo jogador: uma vitória típica (Dificuldade≈60) passa a pagar ~3 pacotes de 100 moedas.)*
  O XP ganho é somado a um placar vitalício do jogador (`xpTotal`) — NÃO altera atributo de herói nenhum (C-11). A Moeda ganha é somada ao saldo gastável `coinBalance` (RF-16).
- **RF-15**: Ao concluir uma missão (sucesso ou falha), cada herói que participou fica indisponível por `3 * (Dificuldade/10)` minutos.
  *Exemplo: missão de Dificuldade=100 → cooldown de 30 minutos por herói usado. (Reduzido de 15 para 5 após a subida da escala de dificuldade, e de 5 para 3 em revisão de equilíbrio por feedback de teste no aparelho.)*

### Recrutamento e economia
*(personagens e arcos curados: ver `specs/roster.md`)*
- **RF-16**: O jogador DEVE poder gastar Moeda (custo fixo: 100) para comprar um pacote que sorteia um herói ainda não recrutado.
- **RF-17**: A probabilidade de cada herói sair no pacote é proporcional à sua Veterania, ponderada pela raridade da força do herói:
  `peso_h = (Veterania_h + 5) × m(Força_h)`, onde `Força_h = 0,6·Poder_ajustado + 0,4·Veterania` e `m` depende da faixa: Comum (< 45) → 1,0; Raro (45 a 65) → 0,4; Lendário (≥ 65) → 0,12. `P(h) = peso_h / Σ peso_h'` sobre o pool não recrutado.
  *(Revisão de equilíbrio: heróis muito fortes quebravam a dificuldade dos arcos. Com a raridade, os mais fortes saem raramente nos pacotes.)*
- **RF-18**: Um herói recrutado DEVE desbloquear a wiki dele e as missões dos arcos em que participou (RF-19). A wiki do herói (Tela 5) consiste em:
  - **bio**: `deck` (resumo curto da Comic Vine) + nome real + aliases;
  - **imagem**;
  - **"HQs principais"**: a lista de arcos curados em que o herói aparece (mesma relação do RF-19), exibida como "Participou de: …". A Comic Vine não tem um campo limpo de "HQs principais"; os arcos curados são o equivalente adotado.
  Enquanto o herói não é recrutado, a wiki dele fica bloqueada.

### Unlock de missões
- **RF-19**: Um herói desbloqueia uma missão se aparecer em pelo menos uma issue do array `issues` daquele arco (via `character_credits` de cada issue). O unlock é resolvido no seed para TODOS os personagens curados; uma missão fica visível na Tela 3 quando pelo menos um herói **recrutado** a desbloqueia.

### Economia e estado
- **RF-21**: O jogo começa (após o seed) com um estado inicial fixo: `coinBalance = 300` (suficiente pra 3 pacotes → mínimo de 3 heróis do RF-08), `xpTotal = 0`, nenhum herói recrutado. Valores iniciais — calibrar em T-25.
- **RF-22**: Uma missão PODE ser repetida quantas vezes o jogador quiser — não existe estado de "missão concluída". Cada execução gera um novo registro de resultado e coloca os heróis usados em cooldown (RF-15).

### Mecânicas de jogo (revisão de equilíbrio e novas funções)
- **RF-23**: Minijogo "Golpe no tempo certo": na montagem de time, um marcador oscila continuamente numa barra; ao iniciar a missão ele congela. Posição central (0,42–0,58) dá +10% na ForçaTime; bordas (0,30–0,42 e 0,58–0,70) dão +5%; fora disso, 0%. Regra em `game/Minigames.kt` (`coordinationBonus`).
- **RF-24**: Sequência de vitórias: cada vitória consecutiva anterior aumenta a recompensa de missões bem-sucedidas em 5%, até +25% (5 vitórias). Falha zera a sequência e não tem bônus. Regra em `game/Minigames.kt` (`streakMultiplier`).
- **RF-25**: Cada arco mostra a imagem oficial da Comic Vine (campo `image` do story arc), guardada no seed. Sem imagem, usa textura de placeholder.
- **RF-26**: A tela de heróis chama-se "Equipe" (não "Roster"). Textos da Comic Vine (dossiê, resumos) são exibidos como vêm, com legenda "em inglês".

- **RF-28**: Loja diária. Existe uma vitrine de heróis à venda que renova todo dia, à meia-noite do fuso local. Cada vitrine tem 7 ofertas em 3 seções: **Comum** (3 ofertas), **Rara** (3) e **Lendária** (1), sorteadas do pool de heróis ainda não recrutados, pela raridade do RF-17 (`rarityOf`). Preços fixos por seção: Comum 150, Rara 300, Lendária 750 moedas (valores iniciais — calibrar em T-25). A loja só libera para quem tem pelo menos 3 heróis recrutados (`SHOP_UNLOCK_HEROES`); antes disso a aba mostra a tela de loja fechada e a vitrine não é gerada. Comprar uma oferta recruta o herói na hora e a oferta fica marcada como vendida. Se uma seção não tiver heróis suficientes no pool, mostra menos ofertas. A vitrine do dia é gravada no banco (não é recalculada ao abrir a tela), então recarregar a tela não muda as ofertas.
- **RF-29**: Nível do jogador. `level = 1 + floor(xpTotal / XP_PER_LEVEL)`, com `XP_PER_LEVEL = 200` (valor inicial — calibrar em T-25). A cada 5 níveis (níveis 5, 10, 15…) o jogador ganha 1 **pacote de nível**, que sorteia um herói igual ao RF-17, sem custo em Moeda. Pacotes pendentes = `floor(level / 5) - packsOpened`, com `packsOpened` gravado em `game_state`. O pacote pendente é aberto manualmente na Tela 1 e continua pendente enquanto não houver herói disponível para sortear. O nível só existe para o jogador: não altera atributo de herói (C-11).
- **RF-27**: O resultado da missão é apresentado em rodadas (só apresentação; não altera o resultado). Três rodadas: em cada uma, os heróis do time entram em cena em ordem de força, e a barra de pressão do arco cai proporcionalmente à contribuição de cada herói (força individual / soma do time). Na última rodada, o dado é rolado e o resultado é revelado com a mesma rolagem e chance já gravadas. Com "movimento reduzido" ativo, a sequência é pulada e o resultado aparece direto.

## Telas (comportamento esperado, não layout)
0. **Erro de seed** — mostrada quando o seed inicial falha (RF-20); botão "Tentar novamente", sem retry automático
1. **Roster/Recrutamento** — heróis recrutados + compra de pacote (RF-16/17), saldo de Moeda e placar de XP (RF-21)
2. **Montagem de time** — seleção de 3–5 heróis disponíveis, com indicação de sinergia e cooldown
3. **Lista de missões** — arcos desbloqueados, com rótulo de dificuldade (Fácil/Médio/Épico)
4. **Resultado da missão** — mostra o cálculo (ForçaTime, Dificuldade, chance, sorteio) e o resultado
5. **Perfil do herói** — atributos + wiki desbloqueada (RF-18)
6. **Pokédex** — grade de todos os personagens curados, com contador "N/total descobertos" e filtro Todos/Recrutados. Recrutado: imagem e nome, abre o perfil (Tela 5). Não recrutado: textura escura com "?" e nome oculto; toque mostra aviso "Recrute para ver a ficha" e não abre nada.
8. **Loja** — aba própria com as 3 seções da vitrine diária (RF-28): nome, raridade, preço e botão "Comprar"; ofertas vendidas ficam marcadas. Compra mostra a revelação do herói (mesma da Tela 1).
7. **História do arco** — aberta ao tocar num card da Tela 3: imagem, nome, dificuldade, texto original da Comic Vine (`story`, ou "Sem resumo disponível." + deck quando não há texto) e botão "MONTAR TIME" que leva à Tela 2 com o arco escolhido. Iniciar missão continua só na Tela 2.

## Fora de escopo (explícito)
- Progressão de herói por nível/XP (C-11)
- Autenticação/múltiplos usuários (C-12)
- Backend/servidor (C-01)
- Filtro automático de reimpressões internacionais — feito manualmente na curadoria, não em runtime
*(Revisão de equilíbrio: bônus de equipe antes podiam somar ~36 pontos a um time médio de 43, e um time de 3 comuns chegava a 31% numa épica. Os bônus foram reduzidos para manter a força próxima dos atributos individuais.)*
