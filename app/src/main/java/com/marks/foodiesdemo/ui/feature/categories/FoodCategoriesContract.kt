package com.marks.foodiesdemo.ui.feature.categories

import com.marks.foodiesdemo.model.FoodItem

class FoodCategoriesContract {

    data class State(
        val categories: List<FoodItem> = listOf(),
        val isLoading: Boolean = false
    )

    sealed class Effect {
        object DataWasLoaded : Effect()
    }


    /**
     * 枚举：适合表示一组无附加数据的固定常量（如星期、颜色）。
     * 密封类：适合表示有限但多样的状态，且每种状态可能有不同的数据（如网络请求的 Loading/Success/Error）。
     */
    // 密封类
//    sealed class LoginStatus {
//        object Idle : LoginStatus()
//        object Loading : LoginStatus()
//        data class Success(val user: User) : LoginStatus()
//        data class Error(val message: String) : LoginStatus()
//    }
}