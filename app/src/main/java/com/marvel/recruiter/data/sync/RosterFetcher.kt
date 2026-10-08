package com.marvel.recruiter.data.sync

import com.marvel.recruiter.game.ArcInput
import com.marvel.recruiter.game.CharacterInput
import com.marvel.recruiter.game.SeedPlan
import com.marvel.recruiter.game.TeamRef
import com.marvel.recruiter.game.buildSeedPlan
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** Snapshot do roster empacotado no app (gerado por tools/generate_roster_snapshot.py). */
@Serializable
data class RosterSnapshot(
    val excludedIssueIds: List<Long> = emptyList(),
    val characters: List<SnapshotCharacter>,
    val arcs: List<SnapshotArc>,
)

@Serializable
data class SnapshotCharacter(
    val cvId: Long,
    val name: String,
    val realName: String? = null,
    val deck: String? = null,
    val aliases: String? = null,
    val imageUrl: String? = null,
    val powerCount: Int,
    val issueAppearances: Int,
    val teams: List<SnapshotTeam> = emptyList(),
    val friendCvIds: List<Long> = emptyList(),
    val issueIds: List<Long> = emptyList(),
)

@Serializable
data class SnapshotTeam(val cvId: Long, val name: String)

@Serializable
data class SnapshotArc(
    val cvId: Long,
    val name: String,
    val deck: String? = null,
    val imageUrl: String? = null,
    val story: String? = null,
    val issueIds: List<Long> = emptyList(),
)

/** Monta o plano do seed a partir do snapshot. Não usa rede. */
class RosterFetcher(
    private val readSnapshot: () -> String,
    private val now: () -> Long = System::currentTimeMillis,
) {

    fun fetchPlan(): SeedPlan {
        val snapshot = Json { ignoreUnknownKeys = true }.decodeFromString<RosterSnapshot>(readSnapshot())
        val excluded = snapshot.excludedIssueIds.toSet()
        val characters = snapshot.characters.map { c ->
            CharacterInput(
                cvId = c.cvId,
                name = c.name,
                realName = c.realName,
                deck = c.deck,
                aliases = c.aliases,
                imageUrl = c.imageUrl,
                powerCount = c.powerCount,
                issueAppearances = c.issueAppearances,
                teams = c.teams.map { TeamRef(it.cvId, it.name) },
                friendCvIds = c.friendCvIds.toSet(),
                issueIds = c.issueIds.toSet() - excluded,
            )
        }
        val arcs = snapshot.arcs.map { a ->
            ArcInput(
                cvId = a.cvId,
                name = a.name,
                deck = a.deck,
                imageUrl = a.imageUrl,
                story = a.story,
                issueIds = a.issueIds.toSet() - excluded,
            )
        }
        return buildSeedPlan(characters, arcs, seededAt = now())
    }
}
