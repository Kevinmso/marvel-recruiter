package com.marvel.recruiter.data.remote

/** Curadoria do roster (specs/roster.md). Fonte da rotina de seed. */
object CuratedRoster {

    val characterCvIds: List<Long> = listOf(
        1440, // Wolverine
        1442, // Capitão América
        1460, // Colossus
        21561, // Carol Danvers
        3470, // Black Goliath
        2268, // Thor
        2267, // Hulk
        3200, // Viúva Negra
        1475, // Gavião Arqueiro
        1504, // Visão
        1466, // Feiticeira Escarlate
        1477, // Pantera Negra
        1502, // Vespa
        1459, // Ciclope
        3552, // Jean Grey
        1444, // Tempestade
        1505, // Professor X
        1499, // Gambit
        1461, // Noturno
        3548, // Kitty Pryde
        1462, // Fera
        1441, // Magneto
        1469, // Mística
        1455, // Homem de Ferro
        7607, // Thanos
        1468, // Doutor Destino
        4324, // Loki
        2250, // Caveira Vermelha
        1486, // Venom
        58812, // Norman Osborn (Duende Verde)
        7612, // Apocalipse
        2149, // Galactus
        1443, // Homem-Aranha
        1456, // Doutor Estranho
        1492, // Punho de Ferro
        24694, // Demolidor
        10957, // Senhor das Estrelas
        6806, // Gamora
        6807, // Drax
        32814, // Rocket Raccoon
        24341, // Groot
        2151, // Senhor Fantástico
        2190, // Mulher Invisível
        2120, // Tocha Humana
        2114, // Coisa
        1464, // Homem de Gelo
        4563, // Sabretooth
        7606, // Deadpool
        2502, // Surfista Prateado
        6108, // Motoqueiro Fantasma (Blaze)
    )

    /** Reimpressões e registros lixo da curadoria (Days of Future Past). */
    val excludedIssueIds: Set<Long> = setOf(398323L, 398379L, 477955L)

    val arcCvIds: List<Long> = listOf(
        40615, // Civil War
        40991, // House of M
        42233, // Infinity Gauntlet
        57031, // Age of Ultron
        44425, // Days of Future Past
        47264, // Secret Invasion
        55985, // Siege
        40978, // Secret Wars
        55979, // Fall of the Hulks
        42183, // Dark Phoenix Saga
        41407, // Mutant Massacre
        41408, // Inferno
        43814, // Onslaught
        41931, // Acts of Vengeance
        41213, // Planet Hulk
        48995, // Messiah Complex
        56404, // Schism
        56504, // The Death of Captain America
        55740, // Original Sin
        42234, // Infinity War
        27758, // Operation: Galactic Storm
    )
}
