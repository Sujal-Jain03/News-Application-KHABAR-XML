import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Drawable

class ShimmerDrawable : Drawable() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val shimmerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var shimmerAnimator: ValueAnimator? = null
    private var shimmerOffset = 0f

    private val backgroundColor = Color.parseColor("#E0E0E0")
    private val shimmerColor = Color.parseColor("#F5F5F5")

    init {
        setupShimmerAnimation()
    }

    private fun setupShimmerAnimation() {
        shimmerAnimator = ValueAnimator.ofFloat(-1f, 1f).apply {
            duration = 1500
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART

            addUpdateListener { animation ->
                shimmerOffset = animation.animatedValue as Float
                invalidateSelf()
            }
            start()
        }
    }

    override fun draw(canvas: Canvas) {
        val bounds = bounds

        // Draw background
        paint.color = backgroundColor
        canvas.drawRoundRect(
            bounds.left.toFloat(),
            bounds.top.toFloat(),
            bounds.right.toFloat(),
            bounds.bottom.toFloat(),
            16f, 16f, paint
        )

        // Create shimmer gradient
        val shimmerWidth = bounds.width() * 0.3f
        val shimmerStart = bounds.left + shimmerOffset * bounds.width()

        val gradient = LinearGradient(
            shimmerStart, 0f,
            shimmerStart + shimmerWidth, 0f,
            intArrayOf(
                Color.TRANSPARENT,
                shimmerColor,
                Color.TRANSPARENT
            ),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )

        shimmerPaint.shader = gradient

        // Draw shimmer effect
        canvas.drawRoundRect(
            bounds.left.toFloat(),
            bounds.top.toFloat(),
            bounds.right.toFloat(),
            bounds.bottom.toFloat(),
            16f, 16f, shimmerPaint
        )
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
        shimmerPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
        shimmerPaint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    fun stopShimmer() {
        shimmerAnimator?.cancel()
    }

    fun startShimmer() {
        shimmerAnimator?.start()
    }
}
