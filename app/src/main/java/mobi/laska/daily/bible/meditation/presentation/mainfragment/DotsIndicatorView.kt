package mobi.laska.daily.bible.meditation.presentation.mainfragment

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.abs

class DotsIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var totalDots: Int = 5
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var selectedIndex: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
    }

    private val inactiveColor = Color.parseColor("#66FFFFFF")

    private val baseRadius = dp(3f)
    private val maxRadius = dp(4f)
    private val spacing = dp(12f)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = ((totalDots - 1) * spacing + maxRadius * 2).toInt()
        val height = (maxRadius * 2).toInt()
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerY = height / 2f

        for (i in 0 until totalDots) {
            val cx = i * spacing + maxRadius

            val distance = abs(i - selectedIndex)

            val maxDistance = (totalDots - 1) / 2f

            val normalized = (distance / maxDistance).coerceIn(0f, 1f)
            val t = (1f - normalized).let { it * it * it} // ease-out
            val radius = baseRadius + (maxRadius - baseRadius) * t

            val activeColor = Color.parseColor("#fbeece") // или любой твой
            paint.color = blendColors(inactiveColor, activeColor, t)

            canvas.drawCircle(cx, centerY, radius, paint)
        }
    }

    fun animateTo(index: Int) {
        val animator = ValueAnimator.ofFloat(selectedIndex, index.toFloat())
        animator.duration = 250
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener {
            selectedIndex = it.animatedValue as Float
        }
        animator.start()
    }

    private fun dp(value: Float): Float {
        return value * resources.displayMetrics.density
    }

    private fun blendColors(from: Int, to: Int, ratio: Float): Int {
        val inverse = 1f - ratio
        val r = Color.red(to) * ratio + Color.red(from) * inverse
        val g = Color.green(to) * ratio + Color.green(from) * inverse
        val b = Color.blue(to) * ratio + Color.blue(from) * inverse
        val a = Color.alpha(to) * ratio + Color.alpha(from) * inverse
        return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
    }
}