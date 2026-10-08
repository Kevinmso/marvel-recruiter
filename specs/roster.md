# Roster Curado — Marvel Recruiter

Lista de personagens e arcos que compõem o jogo. `cv_id` é o identificador na Comic Vine (formato `4005-XXXX` pra personagem, numérico simples pra arco).

## Regras de curadoria (o schema depende delas)
1. **Todo arco curado precisa de ≥1 personagem do roster que apareça em alguma issue dele** — senão `character_unlock` fica vazio pra ele e a missão nunca desbloqueia (missão morta).
2. **`cv_id` de personagem e de arco são únicos nesta lista** — duplicata quebra o seed (`@PrimaryKey` = `cv_id`).
3. **A limpeza de reimpressões/edições estrangeiras é registrada aqui, em texto** (coluna "Issues (limpo)"). O banco só guarda o `num_issues` final; não há tabela de issues.
4. **`p_max` do conjunto de teste é 30 (Carol), não 22** — a lista de poderes da Comic Vine NÃO vem truncada. Ver `specs/constantes-normalizacao.md`. Recalcular todos os limites com o roster final (T-26).

## Personagens

### Já testados com chamada real (dados confirmados)
| Nome | `cv_id` | Poder (nº poderes) | `count_of_issue_appearances` |
|---|---|---|---|
| Wolverine | 4005-1440 | 24 | 16924 |
| Capitão América | 4005-1442 | 18 | 12285 |
| Colossus | 4005-1460 | 8 | 7917 |
| Carol Danvers (Capitã Marvel) | 4005-21561 | 30 | 4688 |
| Black Goliath (Bill Foster) | 4005-3470 | 6 | 337 |

*(nº de poderes reconfirmado com chamada real — a lista não vinha truncada; Wolverine é 24 e Carol 30, não "22")*

### Expansão para 50 (cv_id confirmado por busca; nº de aparições medido, poderes ainda não)
| Nome | `cv_id` | `count_of_issue_appearances` |
|---|---|---|
| Thanos | 7607 | 1327 |
| Doutor Destino | 1468 | 4072 |
| Loki | 4324 | 2338 |
| Caveira Vermelha | 2250 | 1443 |
| Venom | 1486 | 3017 |
| Norman Osborn (Duende Verde) | 58812 | 2689 |
| Apocalipse | 7612 | 1938 |
| Galactus | 2149 | 1848 |
| Homem-Aranha | 1443 | 18141 |
| Doutor Estranho | 1456 | 5203 |
| Punho de Ferro | 1492 | 2340 |
| Demolidor | 24694 | 4992 |
| Senhor das Estrelas | 10957 | 914 |
| Gamora | 6806 | 945 |
| Drax | 6807 | 1005 |
| Rocket Raccoon | 32814 | 1114 |
| Groot | 24341 | 893 |
| Senhor Fantástico | 2151 | 7246 |
| Mulher Invisível | 2190 | 6449 |
| Tocha Humana | 2120 | 7172 |
| Coisa | 2114 | 8379 |
| Homem de Gelo | 1464 | 8273 |
| Sabretooth | 4563 | 3714 |
| Deadpool | 7606 | 3613 |
| Surfista Prateado | 2502 | 2489 |
| Motoqueiro Fantasma (Blaze) | 6108 | 1126 |

*Status: o teste ao vivo (`LiveRosterTest`) ainda não rodou — a build do módulo app falha em `ui/screens/PokedexScreen.kt:182` (`luminance` sem import). Os limites p/x/y do roster de 50 ficam pendentes até o teste passar.*

### Ainda sem `cv_id` confirmado (TODO — buscar via `/characters/?filter=name:...`)
**Avengers**: Thor, Hulk, Viúva Negra, Gavião Arqueiro, Visão, Feiticeira Escarlate, Pantera Negra, Homem-Formiga, Vespa

**X-Men**: Ciclope, Fênix (Jean Grey), Tempestade, Professor X, Gambit, Noturno, Kitty Pryde, Fera, Magneto, Mística

## Arcos (missões)

### Já testados com chamada real (dados confirmados)
| Nome | `cv_id` | Issues (bruto) | Issues (limpo) | Dificuldade calculada |
|---|---|---|---|---|
| "Avengers" Civil War | 40615 | 122 | 122 | 100,0 |
| "X-Men/Avengers" House of M | 40991 | 80 | 80 | 86,9 |
| "Infinity Trilogy" Infinity Gauntlet | 42233 | 32 | 32 | 58,7 |
| "Avengers" Age of Ultron | 57031 | 23 | 23 | 48,7 |
| "The Uncanny X-Men" Days of Future Past | 44425 | 8 | ~6 (tem reimpressão em holandês, revisar manualmente) | 10,0 |

### Ainda faltam ~10–15 arcos (TODO)
Priorizar arcos **pequenos** (5–15 issues) — os 5 já testados ficaram concentrados em Médio/Épico, faltam exemplos de Fácil de verdade (ver `specs/constantes-normalizacao.md`).

## Como completar
1. Pra cada nome de personagem/arco na lista TODO, buscar via `/characters/?filter=name:NOME` ou `/story_arcs/?filter=name:NOME`.
2. Confirmar o `id` certo entre os resultados (nomes parecidos aparecem — reimpressões, personagens homônimos).
3. Preencher a tabela acima com o `cv_id` real.
4. Depois que o roster estiver completo, rodar o seed (T-19) e atualizar `specs/constantes-normalizacao.md` com os valores finais de `p_min/p_max`, `x_min/x_max`, `y_min/y_max`.