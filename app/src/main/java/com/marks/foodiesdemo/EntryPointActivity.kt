package com.marks.foodiesdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.codingtroops.foodies.ui.feature.category_details.FoodCategoryDetailsScreen
import com.marks.foodiesdemo.NavigationKeys.Arg.FOOD_CATEGORY_ID
import com.marks.foodiesdemo.ui.feature.categories.FoodCategoriesScreen
import com.marks.foodiesdemo.ui.feature.categories.FoodCategoriesViewModel
import com.marks.foodiesdemo.ui.feature.category_details.FoodCategoryDetailsViewModel
import com.marks.foodiesdemo.ui.theme.FoodiesDemoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.receiveAsFlow

// Single Activity per app
/**
 * Hilt 是 Google 推出的 Android 依赖注入（DI）框架，基于 Dagger。它的主要作用是：
 * 简化依赖注入的配置和使用，减少样板代码。
 * 自动管理依赖的生命周期，提升代码的可维护性和可测试性。
 * 支持在 Android 组件（如 Activity、Fragment、ViewModel 等）中自动注入依赖。
 * 提供作用域（如 Singleton、ActivityScoped 等）来控制依赖对象的生命周期。
 * 与 Jetpack 组件（如 ViewModel、Navigation、Compose）无缝集成。
 *
 * AndroidEntryPoint 是 Hilt 提供的注解，用于标记 Android 组件（如 Activity、Fragment、Service 等），
 * 让它们支持依赖注入。被标记的类会自动生成一个以 Hilt_ 前缀命名的基类，负责依赖注入的初始化和生命周期管理。
 */
@AndroidEntryPoint
class EntryPointActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**主要作用：
         * 让 Activity 的内容延伸到状态栏和导航栏下方，实现沉浸式体验。
         */
        enableEdgeToEdge()
        setContent {
            // 布局的主题风格
            FoodiesDemoTheme {
                FoodApp()
            }
        }
    }
}

/**
 * 用于标记一个函数为可组合函数（Composable Function）。
 *  用于声明 UI 组件，并且可以在其他可组合函数中调用，实现响应式 UI 构建。  核心作用：
 * 告诉 Compose 编译器该函数会参与 UI 组合和重组。
 * 只能在其他 @Composable 函数内部调用。
 */
@Composable
private fun FoodCategoriesDestination(navController: NavHostController) {
    val viewModel: FoodCategoriesViewModel = hiltViewModel()
    FoodCategoriesScreen(
        state = viewModel.state,
        effectFlow = viewModel.effects.receiveAsFlow(),
        onNavigationRequested = { itemId ->
            navController.navigate("${NavigationKeys.Route.FOOD_CATEGORIES_LIST}/${itemId}")
        })
}

@Composable
private fun FoodApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = NavigationKeys.Route.FOOD_CATEGORIES_LIST) {
        composable(route = NavigationKeys.Route.FOOD_CATEGORIES_LIST) {
            FoodCategoriesDestination(navController)
        }
        composable(
            route = NavigationKeys.Route.FOOD_CATEGORY_DETAILS,
            arguments = listOf(navArgument(NavigationKeys.Arg.FOOD_CATEGORY_ID) {
                type = NavType.StringType
            })
        ) {
            FoodCategoryDetailsDestination()
        }
    }
}

@Composable
private fun FoodCategoryDetailsDestination() {
    val viewModel: FoodCategoryDetailsViewModel = hiltViewModel()
    FoodCategoryDetailsScreen(viewModel.state)
}

object NavigationKeys {

    object Arg {
        const val FOOD_CATEGORY_ID = "foodCategoryName"
    }

    object Route {
        const val FOOD_CATEGORIES_LIST = "food_categories_list"
        const val FOOD_CATEGORY_DETAILS = "$FOOD_CATEGORIES_LIST/{$FOOD_CATEGORY_ID}"
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FoodiesDemoTheme {
        Greeting("Android")
    }
}