package com.marks.foodiesdemo.ui.feature.category_details

import com.marks.foodiesdemo.model.FoodItem

class FoodCategoryDetailsContract {
    data class State(
        val category: FoodItem?,
        val categoryFoodItems: List<FoodItem>
    )
}