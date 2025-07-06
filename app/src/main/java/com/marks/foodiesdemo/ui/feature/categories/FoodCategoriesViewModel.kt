package com.marks.foodiesdemo.ui.feature.categories

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marks.foodiesdemo.model.data.FoodMenuRemoteSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodCategoriesViewModel @Inject constructor(private val remoteSource: FoodMenuRemoteSource) :
    ViewModel() {

    /**
     * mutableStateOf 和 LiveData 都能用于状态管理和 UI 响应式刷新，但它们并不完全等价：
     * mutableStateOf 是 Jetpack Compose 的状态容器，专为 Compose 设计，值变化会自动触发 Composable 重组。
     * LiveData 是 Jetpack 的生命周期感知数据容器，适用于传统 View（如 Activity/Fragment）和 XML 布局，值变化会通知观察者。
     * 总结：两者作用类似，但 mutableStateOf 更适合 Compose，LiveData 更适合传统 UI
     */
    var state by mutableStateOf(
        FoodCategoriesContract.State(
            categories = listOf(),
            isLoading = true
        )
    )
        private set

    var effects = Channel<FoodCategoriesContract.Effect>(UNLIMITED)
        private set

    init {
        viewModelScope.launch { getFoodCategories() }
    }

    private suspend fun getFoodCategories() {
        val categories = remoteSource.getFoodCategories()
        viewModelScope.launch {
            state = state.copy(categories = categories, isLoading = false)
            effects.send(FoodCategoriesContract.Effect.DataWasLoaded)
        }
    }
}