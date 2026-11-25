package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.SavedProductEntity
import com.example.data.model.LuxuryProduct
import com.example.data.model.RonalCatalog
import com.example.ui.components.Glass3DCard
import com.example.ui.components.PremiumShimmerButton
import com.example.ui.components.ProductVarietyArtwork
import com.example.ui.theme.ErrorCrimson
import com.example.ui.theme.InkObsidian
import com.example.ui.theme.LocalRonalColors
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.buildFilteredProducts
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CatalogScreen(
    searchQuery: String,
    selectedCategory: String,
    savedMap: Map<String, SavedProductEntity>,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onAddToCart: (LuxuryProduct) -> Unit,
    onToggleFavorite: (LuxuryProduct) -> Unit,
    onToggleBookmark: (LuxuryProduct) -> Unit,
    modifier: Modifier = Modifier
) {
    val extra = LocalRonalColors.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val showBackToTop by remember { derivedStateOf { listState.firstVisibleItemIndex > 2 } }
    var inspectedProduct by remember { mutableStateOf<LuxuryProduct?>(null) }

    val filteredProducts = remember(searchQuery, selectedCategory, savedMap) {
        buildFilteredProducts(searchQuery, selectedCategory, savedMap)
    }

    val liveSuggestions = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            listOf("TRJ VVIP", "UK Ultra Luxury", "1KG", "Cinnamon Perfume", "GR8", "Grand Villa")
        } else {
            RonalCatalog.products
                .filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                        it.varietyLabel.contains(searchQuery, ignoreCase = true)
                }
                .take(4)
                .map { "${it.name} ${it.varietyLabel}" }
        }
    }

    val categories = listOf(
        "ALL" to "All (17)",
        "CARS" to "TRJ Cars (6)",
        "HOTELS" to "UK Hotels (4)",
        "TEA" to "Ceylon Tea (4)",
        "PERFUME" to "Perfume (3)",
        "FAVORITES" to "Favorites",
        "BOOKMARKS" to "Bookmarks"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Bar & Live Suggestions
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search TRJ cars, UK villas, Ceylon tea, perfume...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = extra.accent)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = extra.accent,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = PureWhite,
                        unfocusedContainerColor = PureWhite
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("catalog_search_input")
                )

                if (liveSuggestions.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(liveSuggestions) { suggestion ->
                            Surface(
                                onClick = { onSearchChange(suggestion.split(" ").take(2).joinToString(" ")) },
                                shape = RoundedCornerShape(50),
                                color = extra.accent.copy(alpha = 0.10f),
                                border = BorderStroke(1.dp, extra.accent.copy(alpha = 0.25f))
                            ) {
                                Text(
                                    text = suggestion,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = InkObsidian,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { (key, label) ->
                        val selected = selectedCategory == key
                        FilterChip(
                            selected = selected,
                            onClick = { onCategoryChange(key) },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = InkObsidian,
                                selectedLabelColor = PureWhite,
                                containerColor = PureWhite
                            )
                        )
                    }
                }
            }

            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Glass3DCard {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No matching varieties found",
                                style = MaterialTheme.typography.titleLarge,
                                color = InkObsidian
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "Try clearing your search or choosing All (17) varieties.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredProducts, key = { it.id }) { product ->
                        val saved = savedMap[product.id]
                        val isFav = saved?.isFavorite == true
                        val isBook = saved?.isBookmarked == true

                        Glass3DCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("product_card_${product.id}"),
                            onClick = { inspectedProduct = product }
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                ProductVarietyArtwork(
                                    product = product,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(172.dp)
                                )
                                Spacer(Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = product.category.title.uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = extra.accent
                                        )
                                        Text(
                                            text = "${product.name} · ${product.varietyLabel}",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = InkObsidian
                                        )
                                    }
                                    Row {
                                        IconButton(onClick = { onToggleFavorite(product) }) {
                                            Icon(
                                                imageVector = if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                                contentDescription = "Favorite ${product.name}",
                                                tint = if (isFav) ErrorCrimson else InkObsidian
                                            )
                                        }
                                        IconButton(onClick = { onToggleBookmark(product) }) {
                                            Icon(
                                                imageVector = if (isBook) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                                contentDescription = "Bookmark ${product.name}",
                                                tint = if (isBook) extra.accent else InkObsidian
                                            )
                                        }
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = "Location",
                                        tint = extra.glow,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = product.ukLocation,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = product.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = product.priceDisplay,
                                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                            color = InkObsidian
                                        )
                                        Text(
                                            text = product.unitSuffix,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = extra.accent
                                        )
                                    }
                                    PremiumShimmerButton(
                                        text = "Add to Bag",
                                        onClick = { onAddToCart(product) },
                                        testTag = "add_bag_${product.id}",
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.AddShoppingCart,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showBackToTop,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 18.dp, bottom = 100.dp)
        ) {
            FloatingActionButton(
                onClick = { scope.launch { listState.animateScrollToItem(0) } },
                shape = CircleShape,
                containerColor = InkObsidian,
                contentColor = PureWhite,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.ArrowUpward, contentDescription = "Back to Top")
            }
        }
    }

    val detail = inspectedProduct
    if (detail != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { inspectedProduct = null },
            sheetState = sheetState,
            containerColor = PureWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                ProductVarietyArtwork(
                    product = detail,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "${detail.name} — ${detail.varietyLabel}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = InkObsidian
                )
                Text(
                    text = "${detail.priceDisplay} · ${detail.unitSuffix} · ${detail.ukLocation}",
                    style = MaterialTheme.typography.labelLarge,
                    color = extra.accent
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = detail.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    detail.specs.forEach { spec ->
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = extra.accent.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = spec,
                                style = MaterialTheme.typography.labelSmall,
                                color = InkObsidian,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))
                PremiumShimmerButton(
                    text = "Reserve & Add to Bag",
                    onClick = {
                        onAddToCart(detail)
                        inspectedProduct = null
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}
