package com.waqas028.platzifakestoreapi

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.waqas028.platzifakestoreapi.response.Category
import com.waqas028.platzifakestoreapi.response.Product
import com.waqas028.platzifakestoreapi.ui.theme.PlatziFakeStoreAPITheme
import com.waqas028.platzifakestoreapi.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.flowOf

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = hiltViewModel()) {
    val productsList = homeViewModel.getProducts.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        homeViewModel.getProducts()
    }

    HomeScreen(productsList)
}

@Composable
private fun HomeScreen(productsList: LazyPagingItems<Product>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.LightGray)
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        )
        if (productsList.loadState.append is LoadState.Loading || productsList.loadState.refresh is LoadState.Loading) LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round
        )
        val state = rememberLazyGridState()
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = state,
            contentPadding = PaddingValues(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            content = { peopleGridContent(productsList, 2, state) }
        )
    }
}

private fun LazyGridScope.peopleGridContent(productsList:  LazyPagingItems<Product>, columns: Int, state: LazyGridState) {
    items(productsList.itemCount) { item ->
        val (delay, easing) = state.calculateDelayAndEasing(item, columns)
        val animation = tween<Float>(durationMillis = 500, delayMillis = delay, easing = easing)
        val args = ScaleAndAlphaArgs(fromScale = 2f, toScale = 1f, fromAlpha = 0f, toAlpha = 1f)
        val (scale, alpha) = scaleAndAlpha(args = args, animation = animation)
        productsList[item]?.let { productDetail ->
            ProductCard(modifier = Modifier.graphicsLayer(alpha = alpha, scaleX = scale, scaleY = scale), productDetail)
        }
    }
}

@Composable
private fun ProductCard(modifier: Modifier, product: Product) {
    Column(modifier = modifier.background(color = Color.White, shape = RoundedCornerShape(12.dp))) {
        AsyncImage(
            model = product.images.getOrNull(0),
            contentDescription = "",
            placeholder = painterResource(id = R.drawable.ic_store_placeholder),
            error = painterResource(id = R.drawable.ic_store_placeholder),
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .padding(5.dp)
                //.heightIn(min = 200.dp)
                .clip(RoundedCornerShape(12.dp))
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = "$${product.price}",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 10.dp, top = 10.dp)
        )
        Text(
            text = product.title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
            maxLines = 2,
            lineHeight = 20.sp,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .heightIn(min = 70.dp)
                .padding(horizontal = 10.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun LazyGridState.calculateDelayAndEasing(index: Int, columnCount: Int): Pair<Int, Easing> {
    val row = index / columnCount
    val column = index % columnCount
    val firstVisibleRow by remember { derivedStateOf { firstVisibleItemIndex } }
    val visibleRows = layoutInfo.visibleItemsInfo.count()
    val scrollingToBottom = firstVisibleRow < row
    val isFirstLoad = visibleRows == 0
    val rowDelay = 200 * when {
        isFirstLoad -> row // initial load
        scrollingToBottom -> visibleRows + firstVisibleRow - row // scrolling to bottom
        else -> 1 // scrolling to top
    }
    val scrollDirectionMultiplier = if (scrollingToBottom || isFirstLoad) 1 else -1
    val columnDelay = column * 150 * scrollDirectionMultiplier
    val easing = if (scrollingToBottom || isFirstLoad) LinearOutSlowInEasing else FastOutSlowInEasing
    return rowDelay + columnDelay to easing
}


private enum class State { PLACING, PLACED }
data class ScaleAndAlphaArgs(
    val fromScale: Float,
    val toScale: Float,
    val fromAlpha: Float,
    val toAlpha: Float
)

@Composable
fun scaleAndAlpha(
    args: ScaleAndAlphaArgs,
    animation: FiniteAnimationSpec<Float>
): Pair<Float, Float> {
    val transitionState = remember { MutableTransitionState(State.PLACING).apply { targetState = State.PLACED } }
    val transition = updateTransition(transitionState, label = "")
    val alpha by transition.animateFloat(transitionSpec = { animation }, label = "") { state ->
        when (state) {
            State.PLACING -> args.fromAlpha
            State.PLACED -> args.toAlpha
        }
    }
    val scale by transition.animateFloat(transitionSpec = { animation }, label = "") { state ->
        when (state) {
            State.PLACING -> args.fromScale
            State.PLACED -> args.toScale
        }
    }
    return alpha to scale
}

@Preview
@Composable
fun HomeScreenPreview() {
    val product = Product(
        id = 1,
        title = "Hoodie Re",
        price = "100",
        description = "",
        images = emptyList(),
        creationAt = "",
        updatedAt = "",
        category = Category(id = 1, name = "", image = "", creationAt = "", updatedAt = "")
    )
    PlatziFakeStoreAPITheme {
        HomeScreen(productsList = flowOf(PagingData.from(listOf(product))).collectAsLazyPagingItems())
    }
}