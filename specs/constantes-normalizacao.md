# Constantes de Normalização — Marvel Recruiter

Valores congelados no momento do seed (constitution.md C-08, spec.md RF-03). **NÃO recalcular dinamicamente** — só atualizar este arquivo manualmente se o roster curado mudar, e nesse caso avaliar impacto em todos os personagens/arcos já testados.

## Status atual
Os valores abaixo vêm do **conjunto de teste de 5 heróis e 5 arcos** (testados com chamadas reais à API durante a fase de design). **NÃO são os valores finais** — o roster real terá ~24 heróis e ~15-20 arcos (ver `specs/roster.md`). Recalcular assim que o roster final estiver completo, e substituir os valores abaixo.

## Poder (`p_min`, `p_max`) — RF-04
| | Valor (teste, 5 heróis) | Valor final (TODO) |
|---|---|---|
| `p_min` | 6 (Black Goliath) | — |
| `p_max` | 22 (Wolverine/Carol — lista truncada, piso não teto real) | — |

## Veterania (`x_min`, `x_max`) — RF-05
| | Valor (teste, 5 heróis) | Valor final (TODO) |
|---|---|---|
| `x_min` | 337 (Black Goliath) | — |
| `x_max` | 16924 (Wolverine) | — |

## Dificuldade (`y_min`, `y_max`) — RF-11
| | Valor (teste, 5 arcos) | Valor final (TODO) |
|---|---|---|
| `y_min` | 6 (Days of Future Past, já limpo de reimpressões) | — |
| `y_max` | 122 (Guerra Civil) | — |

## Como atualizar quando o roster final estiver pronto
1. Rodar o script/rotina de seed (T-19) contra o roster completo de `specs/roster.md`.
2. Pegar o menor e maior valor real de `p`, `x`, `y` entre todos os personagens/arcos curados.
3. Substituir os valores "TODO" acima.
4. Commitar esse arquivo junto com o commit que popula o banco pela primeira vez — ele é a fonte da verdade dessas constantes, não o banco em si (o banco pode ser apagado/recriado; este arquivo não).