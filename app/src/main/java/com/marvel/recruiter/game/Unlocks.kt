package com.marvel.recruiter.game

/** Par (personagem, arco) que desbloqueia a missão. */
data class Unlock(val characterCvId: Long, val arcCvId: Long)

/**
 * Um personagem desbloqueia um arco quando aparece em pelo menos uma issue do arco.
 * `characterIssues`: cvId do personagem -> ids das issues em que aparece.
 * `arcIssues`: cvId do arco -> ids das issues do arco (já limpas na curadoria).
 */
fun resolveUnlocks(
    characterIssues: Map<Long, Set<Long>>,
    arcIssues: Map<Long, Set<Long>>,
): List<Unlock> = buildList {
    for ((characterCvId, appearsIn) in characterIssues) {
        for ((arcCvId, issues) in arcIssues) {
            if (appearsIn.any { it in issues }) add(Unlock(characterCvId, arcCvId))
        }
    }
}
