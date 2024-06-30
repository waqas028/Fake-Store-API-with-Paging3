package com.waqas028.platzifakestoreapi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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
    val productsList = homeViewModel.getProducts().collectAsLazyPagingItems()
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(productsList.itemCount) { item ->
                productsList[item]?.let { notificationDetail ->
                    ProductCard(modifier = Modifier, notificationDetail)
                }
            }
        }
    }
}

@Composable
private fun ProductCard(modifier: Modifier, product: Product) {
    Column(modifier = modifier.background(color = Color.White, shape = RoundedCornerShape(12.dp))) {
        AsyncImage(
            model = product.images.getOrNull(0),
            contentDescription = "",
            placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
            error = painterResource(id = R.drawable.ic_launcher_foreground),
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .padding(5.dp)
                .heightIn(min = 200.dp)
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
            modifier = Modifier.heightIn(min = 70.dp).padding(horizontal = 10.dp, vertical = 10.dp)
        )
    }
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