package com.marvel.recruiter.game

data class TeamRef(val cvId: Long, val name: String)

data class CharacterInput(
    val cvId: Long,
    val name: String,
    val realName: String?,
    val deck: String?,
    val aliases: String?,
    val imageUrl: String?,
    val powerCount: Int,
    val issueAppearances: Int,
    val teams: List<TeamRef>,
    val friendCvIds: Set<Long>,
    val issueIds: Set<Long>,
)

data class ArcInput(
    val cvId: Long,
    val name: String,
    val deck: String?,
    val imageUrl: String?,
    val issueIds: Set<Long>,
    val story: String? = null,
)

data class CharacterPlan(
    val cvId: Long,
    val name: String,
    val realName: String?,
    val deck: String?,
    val aliases: String?,
    val imageUrl: String?,
    val numPowers: Int,
    val numAppearances: Int,
    val power: Double,
    val adjustedPower: Double,
    val veterancy: Double,
    val teamCvIds: Set<Long>,
)

data class ArcPlan(
    val cvId: Long,
    val name: String,
    val deck: String?,
    val imageUrl: String?,
    val numIssues: Int,
    val difficulty: Double,
    val story: String? = null,
)

data class SeedPlan(
    val characters: List<CharacterPlan>,
    val teams: List<TeamRef>,
    val synergies: Set<SynergyPair>,
    val arcs: List<ArcPlan>,
    val unlocks: List<Unlock>,
    val meta: Map<String, String>,
)

/**
 * Calcula tudo que o seed grava: atributos normalizados (RF-02), limites congelados
 * (RF-03), sinergias (amizade não-direcionada entre curados), unlocks (RF-19).
 */
fun buildSeedPlan(
    characters: List<CharacterInput>,
    arcs: List<ArcInput>,
    seededAt: Long,
): SeedPlan {
    val pMin = characters.minOf { it.powerCount }
    val pMax = characters.maxOf { it.powerCount }
    val xMin = characters.minOf { it.issueAppearances }
    val xMax = characters.maxOf { it.issueAppearances }
    val yMin = arcs.minOf { it.issueIds.size }
    val yMax = arcs.maxOf { it.issueIds.size }

    val curatedIds = characters.map { it.cvId }.toSet()

    val characterPlans = characters.map { c ->
        val power = Normalization.power(c.powerCount, pMin, pMax)
        val veterancy = Normalization.veterancy(c.issueAppearances, xMin, xMax)
        val compensated = Normalization.compensatedPower(power, veterancy)
        CharacterPlan(
            cvId = c.cvId,
            name = c.name,
            realName = c.realName,
            deck = c.deck,
            aliases = c.aliases,
            imageUrl = c.imageUrl,
            numPowers = c.powerCount,
            numAppearances = c.issueAppearances,
            power = power,
            adjustedPower = Normalization.adjustedPower(compensated),
            veterancy = veterancy,
            teamCvIds = c.teams.map { it.cvId }.toSet(),
        )
    }

    val synergies = buildSet {
        for (a in characters) for (b in characters) {
            if (a.cvId < b.cvId && (b.cvId in a.friendCvIds || a.cvId in b.friendCvIds)) {
                add(SynergyPair(a.cvId, b.cvId))
            }
        }
    }

    val arcPlans = arcs.map { a ->
        ArcPlan(
            cvId = a.cvId,
            name = a.name,
            deck = a.deck,
            imageUrl = a.imageUrl,
            story = a.story,
            numIssues = a.issueIds.size,
            difficulty = Normalization.difficulty(a.issueIds.size, yMin, yMax),
        )
    }

    val unlocks = resolveUnlocks(
        characterIssues = characters.associate { it.cvId to it.issueIds },
        arcIssues = arcs.associate { it.cvId to it.issueIds },
    )

    val teams = characters.flatMap { it.teams }.distinctBy { it.cvId }

    val meta = mapOf(
        "pMin" to pMin.toString(),
        "pMax" to pMax.toString(),
        "xMin" to xMin.toString(),
        "xMax" to xMax.toString(),
        "yMin" to yMin.toString(),
        "yMax" to yMax.toString(),
        "gamma" to Normalization.GAMMA.toString(),
        "seededAt" to seededAt.toString(),
    )

    check(curatedIds.size == characters.size) { "personagens curados duplicados" }

    return SeedPlan(
        characters = characterPlans,
        teams = teams,
        synergies = synergies,
        arcs = arcPlans,
        unlocks = unlocks,
        meta = meta,
    )
}
