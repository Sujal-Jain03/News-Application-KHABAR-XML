package com.example.khabar.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import com.example.khabar.R

class ShimmerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val shimmerViews = mutableListOf<android.view.View>()

    init {
        LayoutInflater.from(context).inflate(R.layout.shimmer_news_item, this, true)

        // Find all shimmer views
        shimmerViews.apply {
            add(findViewById(R.id.shimmerImage))
            add(findViewById(R.id.shimmerTitle1))
            add(findViewById(R.id.shimmerTitle2))
            add(findViewById(R.id.shimmerDesc1))
            add(findViewById(R.id.shimmerDesc2))
            add(findViewById(R.id.shimmerAuthor))
            add(findViewById(R.id.shimmerDate))
        }
    }

    fun startShimmer() {
        val shimmerAnimation = AnimationUtils.loadAnimation(context, R.anim.shimmer_animation)
        shimmerViews.forEach { view ->
            view.startAnimation(shimmerAnimation)
        }
    }

    fun stopShimmer() {
        shimmerViews.forEach { view ->
            view.clearAnimation()
        }
    }
}
