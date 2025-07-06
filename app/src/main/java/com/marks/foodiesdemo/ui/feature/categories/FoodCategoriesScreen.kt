package com.marks.foodiesdemo.ui.feature.categories

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.marks.foodiesdemo.R
import com.marks.foodiesdemo.model.FoodItem
import com.marks.foodiesdemo.noRippleClickable
import com.marks.foodiesdemo.ui.theme.FoodiesDemoTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@ExperimentalCoilApi
@Composable
fun FoodCategoriesScreen(
    state: FoodCategoriesContract.State, //state的改变会带动compose的重组（更新一次）
    effectFlow: Flow<FoodCategoriesContract.Effect>?,
    onNavigationRequested: (itemId: String) -> Unit
) {
    val scaffoldState:SnackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    /**
     * LaunchedEffect 是 Jetpack Compose 中的一个组合函数，
     * 用于在组合（Compose）时启动一个协程，
     * 并在指定的 key 发生变化时重新执行其代码块。
     * 常用于执行副作用操作（如网络请求、监听 Flow、发送事件等），
     * 并能自动管理协程的生命周期，避免内存泄漏。
     */
    Log.i("TAG", "FoodCategoriesScreen----1111111111111111-------------: ")
    // Listen for side effects from the VM
    LaunchedEffect(effectFlow) {
        Log.i("TAG", "FoodCategoriesScreen------------2222222222222222222222-------------: ")
        // effectFlow?.onEach不是挂起函数，所以不能直接在这里使用 scaffoldState.showSnackbar
        effectFlow?.onEach { effect ->
            if (effect is FoodCategoriesContract.Effect.DataWasLoaded) {
                scope.launch {
                    Log.i("TAG", "FoodCategoriesScreen------------4444444444444444444444-------------: ")
                    scaffoldState.showSnackbar(
                        message = "Food categories are loaded.",
                    )
                }
            }
        }?.collect{}
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = scaffoldState) },
        topBar = {
            CategoriesAppBar()
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            FoodCategoriesList(foodItems = state.categories) { itemId ->
                onNavigationRequested(itemId)
            }
            if (state.isLoading)
                LoadingBar()
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoriesAppBar() {
    TopAppBar(
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.Home,
                modifier = Modifier.padding(horizontal = 12.dp),
                contentDescription = "Action icon"
            )
        },
        title = { Text(stringResource(R.string.app_name)) }
    )
}

@Composable
fun FoodCategoriesList(
    foodItems: List<FoodItem>,
    onItemClicked: (id: String) -> Unit = { }
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(foodItems) { item ->
            FoodItemRow(item = item, itemShouldExpand = true, onItemClicked = onItemClicked)
        }
    }
}

@Composable
fun FoodItemRow(
    item: FoodItem,
    itemShouldExpand: Boolean = false,
    iconTransformationBuilder: ImageRequest.Builder.() -> Unit = { },
    onItemClicked: (id: String) -> Unit = { }
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .clickable { onItemClicked(item.id) }
    ) {
        var expanded by rememberSaveable { mutableStateOf(false) }
        Row(modifier = Modifier.animateContentSize()) {
            Box(modifier = Modifier.align(alignment = Alignment.CenterVertically)) {
                FoodItemThumbnail(item.thumbnailUrl, iconTransformationBuilder)
            }
            FoodItemDetails(
                item = item,
                expandedLines = if (expanded) 10 else 2,
                modifier = Modifier
                    .padding(
                        start = 8.dp,
                        end = 8.dp,
                        top = 24.dp,
                        bottom = 24.dp
                    )
                    .fillMaxWidth(0.80f)
                    .align(Alignment.CenterVertically)
            )
            if (itemShouldExpand)
                Box(
                    modifier = Modifier
                        .align(if (expanded) Alignment.Bottom else Alignment.CenterVertically)
                        .noRippleClickable { expanded = !expanded }
                ) {
                    ExpandableContentIcon(expanded)
                }
        }
    }
}

@Composable
private fun ExpandableContentIcon(expanded: Boolean) {
    Icon(
        imageVector = if (expanded)
            Icons.Filled.KeyboardArrowUp
        else
            Icons.Filled.KeyboardArrowDown,
        contentDescription = "Expand row icon",
        modifier = Modifier
            .padding(all = 16.dp)
    )
}

@Composable
fun FoodItemDetails(
    item: FoodItem?,
    expandedLines: Int,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = item?.name ?: "",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if (item?.description?.trim()?.isNotEmpty() == true)
            CompositionLocalProvider(LocalContentColor provides LocalContentColor.current.copy(alpha = 0.4f)) {
                Text(
                    text = item.description.trim(),
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = expandedLines
                )
            }
    }
}

@Composable
fun FoodItemThumbnail(
    thumbnailUrl: String,
    iconTransformationBuilder: ImageRequest.Builder.() -> Unit
) {
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current).data(data = thumbnailUrl).apply(
                block = iconTransformationBuilder
            ).build()
        ),
        modifier = Modifier
            .size(88.dp)
            .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
        contentDescription = "Food item thumbnail picture",
    )
}

@Composable
fun LoadingBar() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FoodiesDemoTheme  {
        FoodCategoriesScreen(FoodCategoriesContract.State(), null) { }
    }
}