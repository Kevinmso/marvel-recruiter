package com.marvel.recruiter.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.marvel.recruiter.R
import com.marvel.recruiter.data.repository.ShopOffer
import com.marvel.recruiter.game.Rarity
import com.marvel.recruiter.game.SHOP_UNLOCK_HEROES
import com.marvel.recruiter.ui.components.CoinGlyph
import com.marvel.recruiter.ui.components.EmptyState
import com.marvel.recruiter.ui.components.CoinPill
import com.marvel.recruiter.ui.components.HeroImage
import com.marvel.recruiter.ui.components.MainTab
import com.marvel.recruiter.ui.components.PackRevealDialog
import com.marvel.recruiter.ui.components.RecruiterBottomBar
import com.marvel.recruiter.ui.components.StampLabel
import com.marvel.recruiter.ui.components.TopBarDossier
import com.marvel.recruiter.ui.theme.LocalRecruiterColors
import com.marvel.recruiter.ui.theme.Space
import com.marvel.recruiter.ui.theme.StatValueSmall
import com.marvel.recruiter.viewmodel.ShopMessage
import com.marvel.recruiter.viewmodel.ShopUiState
import com.marvel.recruiter.viewmodel.ShopViewModel
import org.koin.androidx.compose.koinViewModel

/** Tela 8: vitrine diária da loja (RF-28). Layout inspirado na loja do Clash Royale: destaque no topo, linhas de 3 cartas e botão de preço com moeda. */
@Composable
fun ShopScreen(
    onTabSelected: (MainTab) -> Unit,
    viewModel: ShopViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val notEnough = stringResource(R.string.msg_not_enough_coins)
    val unavailable = stringResource(R.string.msg_shop_unavailable)
    val failed = stringResource(R.string.msg_purchase_failed)

    LaunchedEffect(Unit) {
        viewModel.messages.collect { message ->
            snackbar.showSnackbar(
                when (message) {
                    ShopMessage.NOT_ENOUGH_COINS -> notEnough
                    ShopMessage.UNAVAILABLE -> unavailable
                    ShopMessage.PURCHASE_FAILED -> failed
                },
            )
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopBarDossier(
                title = stringResource(R.string.shop_title),
                actions = { CoinPill(coins = state.coinBalance) },
            )
        },
        bottomBar = { RecruiterBottomBar(selected = MainTab.SHOP, onSelect = onTabSelected) },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        if (state.unlocked) {
            ShopList(state = state, onBuy = viewModel::buy, modifier = Modifier.padding(padding))
        } else {
            EmptyState(
                title = stringResource(R.string.shop_locked_title),
                body = stringResource(R.string.shop_locked_body, SHOP_UNLOCK_HEROES),
                modifier = Modifier.padding(padding).padding(Space.s4),
            )
        }
    }

    state.revealed?.let { reveal ->
        PackRevealDialog(hero = reveal.hero, cost = reveal.price, onDismiss = viewModel::dismissReveal)
    }
}

@Composable
private fun ShopList(state: ShopUiState, onBuy: (ShopOffer) -> Unit, modifier: Modifier) {
    val legendary = state.offers.filter { it.rarity == Rarity.LEGENDARY }
    val common = state.offers.filter { it.rarity == Rarity.COMMON }
    val rare = state.offers.filter { it.rarity == Rarity.RARE }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = Space.s4, vertical = Space.s3),
        verticalArrangement = Arrangement.spacedBy(Space.s3),
    ) {
        item(key = "timer") { RefreshTimer(state.nextRefreshAt - state.now) }

        legendary.forEach { offer ->
            item(key = "legendary-${offer.id}") {
                FeaturedOffer(offer, state.coinBalance >= offer.price, state.buyingId == offer.id) { onBuy(offer) }
            }
        }

        if (common.isNotEmpty()) {
            item(key = "header-common") { SectionTitle(stringResource(R.string.shop_section_common)) }
            item(key = "row-common") { OfferRow(common, state, onBuy) }
        }
        if (rare.isNotEmpty()) {
            item(key = "header-rare") { SectionTitle(stringResource(R.string.shop_section_rare)) }
            item(key = "row-rare") { OfferRow(rare, state, onBuy) }
        }
        if (state.offers.isEmpty()) {
            item(key = "empty") { Text(stringResource(R.string.shop_empty), style = MaterialTheme.typography.bodyMedium) }
        }
    }
}

@Composable
private fun RefreshTimer(remainingMs: Long) {
    val colors = MaterialTheme.colorScheme
    val safe = remainingMs.coerceAtLeast(0L)
    val totalSeconds = safe / 1_000
    val text = "%02d:%02d:%02d".format(totalSeconds / 3600, (totalSeconds / 60) % 60, totalSeconds % 60)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceVariant)
            .padding(horizontal = Space.s3, vertical = Space.s2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.shop_refresh_timer),
            style = MaterialTheme.typography.labelMedium,
            color = colors.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(text = text, style = StatValueSmall, color = colors.onSurface)
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = Space.s2),
    )
}

@Composable
private fun OfferRow(offers: List<ShopOffer>, state: ShopUiState, onBuy: (ShopOffer) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Space.s2),
    ) {
        offers.forEach { offer ->
            OfferTile(
                offer = offer,
                canAfford = state.coinBalance >= offer.price,
                buying = state.buyingId == offer.id,
                onBuy = { onBuy(offer) },
                modifier = Modifier.weight(1f),
            )
        }
        // Mantém a largura de 3 colunas mesmo com menos cartas na linha.
        repeat(3 - offers.size) { Spacer(Modifier.weight(1f)) }
    }
}

@Composable
private fun OfferTile(
    offer: ShopOffer,
    canAfford: Boolean,
    buying: Boolean,
    onBuy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val frame = rarityFrame(offer.rarity)
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(2.dp, frame, RoundedCornerShape(12.dp))
            .padding(Space.s1),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(104.dp)
                .clip(RoundedCornerShape(8.dp)),
        ) {
            HeroImage(name = offer.hero.name, imageUrl = offer.hero.imageUrl, modifier = Modifier.fillMaxSize())
            if (offer.purchased) SoldVeil()
        }
        Text(
            text = offer.hero.name,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Space.s1),
        )
        Spacer(Modifier.height(Space.s1))
        if (offer.purchased) {
            StampLabel(text = stringResource(R.string.shop_sold), color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            PriceButton(price = offer.price, enabled = canAfford && !buying, onClick = onBuy)
        }
    }
}

@Composable
private fun FeaturedOffer(offer: ShopOffer, canAfford: Boolean, buying: Boolean, onBuy: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val frame = rarityFrame(Rarity.LEGENDARY)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(2.dp, frame, RoundedCornerShape(16.dp))
            .padding(Space.s2),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp)),
        ) {
            HeroImage(name = offer.hero.name, imageUrl = offer.hero.imageUrl, modifier = Modifier.fillMaxSize())
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))),
            )
            Text(
                text = stringResource(R.string.shop_section_legendary).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = frame,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(Space.s2),
            )
            Text(
                text = offer.hero.name,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(Space.s3),
            )
            if (offer.purchased) SoldVeil()
        }
        Spacer(Modifier.height(Space.s2))
        if (offer.purchased) {
            StampLabel(text = stringResource(R.string.shop_sold), color = colors.onSurfaceVariant)
        } else {
            PriceButton(price = offer.price, enabled = canAfford && !buying, onClick = onBuy)
        }
    }
}

@Composable
private fun PriceButton(price: Int, enabled: Boolean, onClick: () -> Unit) {
    val semantic = LocalRecruiterColors.current
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (enabled) semantic.coin else colors.surfaceVariant)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = Space.s3, vertical = Space.s2 + 2.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CoinGlyph(
            color = if (enabled) Color(0xFFB7791F) else colors.onSurfaceVariant,
            markColor = if (enabled) Color(0xFFFFE08A) else colors.surfaceVariant,
            size = 16.dp,
        )
        Spacer(Modifier.width(Space.s2))
        Text(
            text = price.toString(),
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) Color(0xFF2B1D05) else colors.onSurfaceVariant,
        )
    }
}

@Composable
private fun SoldVeil() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center,
    ) {
        StampLabel(text = stringResource(R.string.shop_sold), color = Color.White)
    }
}

private fun rarityFrame(rarity: Rarity): Color = when (rarity) {
    Rarity.COMMON -> Color(0xFF9AA5B1)
    Rarity.RARE -> Color(0xFF4DA3FF)
    Rarity.LEGENDARY -> Color(0xFFF2C94C)
}
