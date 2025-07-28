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
        /**
         * Box 在 Jetpack Compose 中相当于 XML 布局里的 FrameLayout。
         * 它的作用是：可以让子元素堆叠（叠放）在一起，后添加的子项会覆盖在前面的上面，常用于实现重叠、居中等布局效果。
         */
        Box(modifier = Modifier.padding(innerPadding)) {
            FoodCategoriesList(foodItems = state.categories) { itemId ->
                onNavigationRequested(itemId) //item点击之后触发这里
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
    /**
     * 在 Jetpack Compose 中，XML 的 RecyclerView 通常对应的是 LazyColumn 或 LazyRow 组件。
     * LazyColumn：垂直列表，类似于竖直方向的 RecyclerView。
     * LazyRow：水平方向的列表。
     */
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
        /**
         * 在 Jetpack Compose 中，modifier 属性用于修饰和调整 Composable 的外观和行为。它可以链式调用多个修饰符，
         * 比如设置宽高、内外边距、点击事件、对齐方式等。通过 modifier，你可以灵活地控制 UI 组件的布局和交互效果。
         */
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .clickable { onItemClicked(item.id) }
    ) {
        var expanded by rememberSaveable { mutableStateOf(false) }
        /**
         * `Row` 在 Jetpack Compose 中相当于 XML 布局里的 `LinearLayout`，
         * 并且是 `android:orientation="horizontal"`（水平方向）的用法。
         * 它用于让子元素在水平方向上依次排列。
         */
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
    /**
     * `Row` 在 Jetpack Compose 中相当于 XML 布局里的 `LinearLayout`，
     * 并且是 `android:orientation="verital"`（水平方向）的用法。
     * 它用于让子元素在水平方向上依次排列。
     */
    Column(modifier = modifier) {
        Text(
            text = item?.name ?: "",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if (item?.description?.trim()?.isNotEmpty() == true)
        /**
         * 在 Jetpack Compose 中，
         * Text 组件的 color 属性默认值是 LocalContentColor.current。
         * 这是因为 Compose 通过 CompositionLocal 机制实现了主题和局部样式的传递，
         * LocalContentColor 就是用来描述当前内容颜色的 CompositionLocal。
         * 这样可以让 Text 组件自动适应父级环境的颜色设置，实现一致的主题风格和局部样式继承，无需每次都手动指定颜色。
         */
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
        /**
        rememberAsyncImagePainter 不是图片加载框架本身，
        而是 Coil 图片加载库在 Jetpack Compose 下的一个图片加载工具函数。它用于在 Compose 中异步加载图片，
        底层实际使用的是 Coil 框架。你需要在项目中引入 Coil 依赖，才能使用 rememberAsyncImagePainter。
         */
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
    /**
     * Box 在 Jetpack Compose 中相当于 XML 布局里的 FrameLayout。
     * 它的作用是：可以让子元素堆叠（叠放）在一起，后添加的子项会覆盖在前面的上面，常用于实现重叠、居中等布局效果。
     */
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