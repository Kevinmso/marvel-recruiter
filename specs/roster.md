# Roster Curado — Marvel Recruiter

Lista de personagens e arcos que compõem o jogo. `cv_id` é o identificador na Comic Vine (formato `4005-XXXX` pra personagem, numérico simples pra arco).

## Personagens

### Já testados com chamada real (dados confirmados)
| Nome | `cv_id` | Poder (nº poderes) | `count_of_issue_appearances` |
|---|---|---|---|
| Wolverine | 4005-1440 | 22 | 16924 |
| Capitão América | 4005-1442 | 18 | 12285 |
| Colossus | 4005-1460 | 8 | 7917 |
| Carol Danvers (Capitã Marvel) | 4005-21561 | 22+ (lista truncada) | 4688 |
| Black Goliath (Bill Foster) | 4005-3470 | 6 | 337 |

### Ainda sem `cv_id` confirmado (TODO — buscar via `/characters/?filter=name:...`)
**Avengers**: Thor, Hulk, Viúva Negra, Gavião Arqueiro, Visão, Feiticeira Escarlate, Pantera Negra, Homem-Formiga, Vespa

**X-Men**: Ciclope, Fênix (Jean Grey), Tempestade, Professor X, Gambit, Noturno, Vampira, Fera, Magneto, Mística

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