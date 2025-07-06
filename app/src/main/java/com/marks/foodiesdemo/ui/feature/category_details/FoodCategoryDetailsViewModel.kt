package com.marks.foodiesdemo.ui.feature.category_details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marks.foodiesdemo.NavigationKeys
import com.marks.foodiesdemo.model.data.FoodMenuRemoteSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodCategoryDetailsViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle,
    private val repository: FoodMenuRemoteSource
) : ViewModel() {

    var state by mutableStateOf(
        FoodCategoryDetailsContract.State(
            null, listOf(
            )
        )
    )
        private set

    init {
        /**
         * 这段代码里的网络请求是串行执行的，也就是一个完成后才会执行下一个。 具体顺序如下：
         * 先请求 repository.getFoodCategories()，拿到分类列表。
         * 再用拿到的 categoryId 查找对应分类。
         * 然后请求 repository.getMealsByCategory(categoryId)，获取该分类下的食物列表。
         * 每一步都要等上一步完成后才会继续，不是并发或同时发生。
         */
        viewModelScope.launch {
            val categoryId = stateHandle.get<String>(NavigationKeys.Arg.FOOD_CATEGORY_ID)
                ?: throw IllegalStateException("No categoryId was passed to destination.")
            val categories = repository.getFoodCategories()
            val category = categories.first { it.id == categoryId }
            state = state.copy(category = category)
            val foodItems = repository.getMealsByCategory(categoryId)
            state = state.copy(categoryFoodItems = foodItems)
        }
    }

    /**
     * 并行
     */
//    init {
//        viewModelScope.launch {
//            val categoryId = stateHandle.get<String>(NavigationKeys.Arg.FOOD_CATEGORY_ID)
//                ?: throw IllegalStateException("No categoryId was passed to destination.")
//
//            // 并发请求
//            val categoriesDeferred = async { repository.getFoodCategories() }
//            val foodItemsDeferred = async { repository.getMealsByCategory(categoryId) }
//
//            val categories = categoriesDeferred.await()
//            val foodItems = foodItemsDeferred.await()
//
//              这下面的代码会等到2个返回之后在执行
//            val category = categories.first { it.id == categoryId }
//            state = state.copy(category = category, categoryFoodItems = foodItems)
//        }
//    }

}