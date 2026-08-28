# Spec — Marvel Recruiter

*Versão limpa dos requisitos, sem histórico de decisão. Para o "porquê" de cada escolha, ver as docs de organização pessoal (design de jogo e doc técnica).*

## Nome do projeto
**Marvel Recruiter** — ver `specs/sugestoes-nome.md` pro histórico de opções consideradas.

## Visão geral
App mobile "manager" onde o jogador recruta personagens da Marvel (dados da Comic Vine), monta um time, resolve missões baseadas em arcos de história reais por cálculo de atributos (sem física/simulação), ganha recursos, e desbloqueia conteúdo de wiki (bio, HQs) do personagem como recompensa — não como o produto principal do app.

## Requisitos funcionais

### Dados e onboarding
- **RF-01**: Na primeira execução, o sistema DEVE popular o banco local com um roster curado de personagens e arcos buscados da Comic Vine.
- **RF-02**: O sistema DEVE calcular e persistir, no momento do seed, os atributos derivados de cada personagem e arco (Poder, Veterania, Dificuldade).
- **RF-03**: O sistema DEVE gravar os limites de normalização usados no seed como constantes fixas (ver constitution.md C-08, valores em `specs/constantes-normalizacao.md`).
- **RF-20**: Se o seed inicial (RF-01) falhar por falta de rede ou erro da Comic Vine, o sistema DEVE mostrar uma tela de erro explícita com botão "Tentar novamente" — NÃO DEVE tentar de novo automaticamente em loop, e NÃO DEVE deixar o app numa tela em branco/travada. Sem seed completo, o app não tem roster nem missões — não há fallback offline nesta versão (fora de escopo).

### Atributos de personagem
- **RF-04**: Poder é normalizado linearmente (0–100) a partir do nº de poderes listados no roster curado:
  `Poder = 100 * (p - p_min) / (p_max - p_min)`
  *Exemplo real testado (roster de 5, p_min=6, p_max=22): Colossus tem p=8 → Poder = 100*(8-6)/(22-6) = 12,5*
- **RF-05**: Veterania é normalizada por log a partir de `count_of_issue_appearances`:
  `Veterania = 100 * (ln(x+1) - ln(x_min+1)) / (ln(x_max+1) - ln(x_min+1))`
  *Exemplo real testado (roster de 5, x_min=337, x_max=16924): Colossus tem x=7917 → Veterania ≈ 80,6*
- **RF-06**: Heróis com Veterania < 50 recebem bônus de Poder proporcional (compensação de raridade):
  `Poder_compensado = min(100, Poder + γ * max(0, 50 - Veterania))`, com **γ = 0.4** (valor inicial — calibrar em T-25)
  *Exemplo real testado: Black Goliath tem Poder=0, Veterania=0 → Poder_compensado = min(100, 0 + 0.4*50) = 20*
- **RF-07**: Poder ajustado final tem piso de segurança:
  `Poder_ajustado = max(15, Poder_compensado)`
  *Exemplo real testado: Colossus (Veterania=80,6 ≥ 50, sem compensação) → Poder_compensado=12,5 → Poder_ajustado = max(15, 12,5) = 15*

### Montagem de time e força
- **RF-08**: O jogador DEVE poder montar um time de 3 a 5 heróis recrutados e disponíveis (fora de cooldown). Se o jogador tiver menos de 3 heróis disponíveis no momento, a ação de iniciar missão DEVE ficar desabilitada (não existe missão com menos de 3 heróis).
- **RF-09**: A força do time é:
  `ForçaTime = Σ(0.6*Poder_ajustado + 0.4*Veterania) + min(B_sin, 40) + B_fac`
  - `B_sin = 10 * nº de pares não-ordenados de heróis no time onde PELO MENOS UM dos dois lista o outro em `character_friends`` (não precisa ser mútuo — basta uma direção, já que os dados da Comic Vine não garantem simetria)
  - `B_fac = 3 * nº de heróis do time que compartilham a equipe (teams) mais representada no time`. Em caso de empate entre duas ou mais equipes com o mesmo número de heróis, usar a equipe cujo `cv_id` for numericamente menor (regra arbitrária só pra determinismo).
- **RF-10**: Se todos os heróis do roster tiverem o mesmo valor de um atributo (divisão por zero na normalização), o sistema DEVE usar valor fixo 50 em vez de calcular.

### Missões
- **RF-11**: A Dificuldade de uma missão é calculada a partir do nº de issues do arco (`y` = tamanho do array `issues` — NÃO usar `count_of_issue_appearances`, campo quebrado pra story arcs):
  `Dificuldade = 10 + 90 * (ln(y+1) - ln(y_min+1)) / (ln(y_max+1) - ln(y_min+1))`
  Rótulo derivado (usado na Tela 3): Fácil se `Dificuldade < 40`; Médio se `40 ≤ Dificuldade < 70`; Épico se `Dificuldade ≥ 70`.
  *Exemplo real testado (conjunto de 5 arcos, y_min=6, y_max=122): House of M tem y=80 → Dificuldade ≈ 86,9 (Épico)*
- **RF-12**: A chance de sucesso é:
  `chance = clamp((ForçaTime - Dificuldade) / Dificuldade * k + 0.5, 0.05, 0.95)`, com **k = 1** (valor inicial — calibrar em T-25)
- **RF-13**: O resultado é definido por sorteio: `sucesso = (roll(0,100) < chance*100)`. A função de sorteio (`roll`) DEVE receber a fonte de aleatoriedade como parâmetro injetável (ver constitution.md C-15) — nunca chamar um gerador global direto, pra permitir testes determinísticos.
- **RF-14**: A recompensa DEVE depender do resultado:
  `XP = α * Dificuldade * m`, `Moeda = β * Dificuldade * m`, com **α = 10**, **β = 5** (valores iniciais — calibrar em T-25), onde `m = 1.0` em sucesso e `m = 0.2` em falha.
  *Exemplo: missão de Dificuldade=50, sucesso → XP=500, Moeda=250. Mesma missão, falha → XP=100, Moeda=50.*
- **RF-15**: Ao concluir uma missão (sucesso ou falha), cada herói que participou fica indisponível por `15 * (Dificuldade/10)` minutos.
  *Exemplo: missão de Dificuldade=100 → cooldown de 150 minutos (2h30) por herói usado.*

### Recrutamento e economia
*(personagens e arcos curados: ver `specs/roster.md`)*
- **RF-16**: O jogador DEVE poder gastar Moeda (custo fixo: 100) para comprar um pacote que sorteia um herói ainda não recrutado.
- **RF-17**: A probabilidade de cada herói sair no pacote é proporcional à sua Veterania:
  `P(h) = (Veterania_h + 5) / Σ(Veterania_h' + 5)` sobre o pool não recrutado.
- **RF-18**: Um herói recrutado DEVE desbloquear a wiki dele (bio, HQs principais) e as missões dos arcos em que participou (RF-19).

### Unlock de missões
- **RF-19**: Um herói desbloqueia uma missão se aparecer em pelo menos uma issue do array `issues` daquele arco (via `character_credits` de cada issue).

## Telas (comportamento esperado, não layout)
1. **Roster/Recrutamento** — heróis recrutados + compra de pacote (RF-16/17)
2. **Montagem de time** — seleção de 3–5 heróis disponíveis, com indicação de sinergia e cooldown
3. **Lista de missões** — arcos desbloqueados, com rótulo de dificuldade (Fácil/Médio/Épico)
4. **Resultado da missão** — mostra o cálculo (ForçaTime, Dificuldade, chance) e o resultado
5. **Perfil do herói** — atributos + wiki desbloqueada

## Fora de escopo (explícito)
- Progressão de herói por nível/XP (C-11)
- Autenticação/múltiplos usuários (C-12)
- Backend/servidor (C-01)
- Filtro automático de reimpressões internacionais — feito manualmente na curadoria, não em runtime