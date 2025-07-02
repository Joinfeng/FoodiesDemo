package com.marks.foodiesdemo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

//明显是一个扩展函数
inline fun Modifier.noRippleClickable(crossinline onClick: () -> Unit): Modifier = composed {
    clickable(indication = null,
        //remember:请记住由calculation生成的值。calculation仅在组合期间被计算。重组时始终返回组合阶段生成的值。
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}