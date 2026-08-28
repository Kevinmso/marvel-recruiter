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
- **RF-03**: O sistema DEVE gravar os limites de normalização usados no seed como constantes fixas (ver constitution.md C-08).

### Atributos de personagem
- **RF-04**: Poder é normalizado linearmente (0–100) a partir do nº de poderes listados no roster curado:
  `Poder = 100 * (p - p_min) / (p_max - p_min)`
- **RF-05**: Veterania é normalizada por log a partir de `count_of_issue_appearances`:
  `Veterania = 100 * (ln(x+1) - ln(x_min+1)) / (ln(x_max+1) - ln(x_min+1))`
- **RF-06**: Heróis com Veterania < 50 recebem bônus de Poder proporcional (compensação de raridade):
  `Poder_compensado = min(100, Poder + 0.4 * max(0, 50 - Veterania))`
- **RF-07**: Poder ajustado final tem piso de segurança:
  `Poder_ajustado = max(15, Poder_compensado)`

### Montagem de time e força
- **RF-08**: O jogador DEVE poder montar um time de 3 a 5 heróis recrutados e disponíveis (fora de cooldown).
- **RF-09**: A força do time é:
  `ForçaTime = Σ(0.6*Poder_ajustado + 0.4*Veterania) + min(B_sin, 40) + B_fac`
  - `B_sin = 10 * nº de pares não-ordenados de heróis no time que são amigos entre si`
  - `B_fac = 3 * nº de heróis do time que compartilham a equipe (teams) mais representada no time`
- **RF-10**: Se todos os heróis do roster tiverem o mesmo valor de um atributo (divisão por zero na normalização), o sistema DEVE usar valor fixo 50 em vez de calcular.

### Missões
- **RF-11**: A Dificuldade de uma missão é calculada a partir do nº de issues do arco (`y` = tamanho do array `issues` — NÃO usar `count_of_issue_appearances`, campo quebrado pra story arcs):
  `Dificuldade = 10 + 90 * (ln(y+1) - ln(y_min+1)) / (ln(y_max+1) - ln(y_min+1))`
- **RF-12**: A chance de sucesso é:
  `chance = clamp((ForçaTime - Dificuldade) / Dificuldade * k + 0.5, 0.05, 0.95)`
- **RF-13**: O resultado é definido por sorteio: `sucesso = (roll(0,100) < chance*100)`.
- **RF-14**: A recompensa DEVE depender do resultado:
  `XP = α * Dificuldade * m`, `Moeda = β * Dificuldade * m`, onde `m = 1.0` em sucesso e `m = 0.2` em falha.
- **RF-15**: Ao concluir uma missão (sucesso ou falha), cada herói que participou fica indisponível por `15 * (Dificuldade/10)` minutos.

### Recrutamento e economia
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
