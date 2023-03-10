package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0

    private var loadingRectWidth = 0f
    private var loadingArcAngle = 0f
    private val loadingArcPadding = 60f
    private val loadingArcOffset = 200f
    private val animationDuration = 2000L

    private var buttonText = ""
    private val buttonTextSize = resources.getDimensionPixelSize(R.dimen.button_text_size).toFloat()

    private var buttonBackgroundColor = 0
    private var loadingProgressColor = 0
    private var loadingArcColor = 0
    private var textColor = 0

    private val valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {

        duration = animationDuration

        addUpdateListener {
            loadingRectWidth = widthSize * (it.animatedValue as Float)
            loadingArcAngle = 360 * (it.animatedValue as Float)
            invalidate()
        }

        addListener(
            object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator?) {
                    if (buttonState == ButtonState.Loading) animation?.start()
                    if (buttonState == ButtonState.Completed) {
                        loadingArcAngle = 0f
                        loadingRectWidth = 0f
                        buttonText = context.getString(R.string.button_completed)
                        animation?.cancel()
                        invalidate()
                    }
                }
            }
        )

    }


    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

        when (new) {
            ButtonState.Clicked -> {
                isEnabled = false

            }
            ButtonState.Loading -> {
                buttonText = context.getString(R.string.button_loading)
                valueAnimator.start()
            }
            ButtonState.Completed -> {
                isEnabled = true
            }
        }

    }

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = buttonTextSize
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create( "", Typeface.BOLD)
    }

    init {
        buttonText = context.getString(R.string.button_completed)
        isEnabled = true

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonBackgroundColor = getColor(R.styleable.LoadingButton_backgroundColor, 0)
            loadingProgressColor = getColor(R.styleable.LoadingButton_loadingProgressColor, 0)
            loadingArcColor = getColor(R.styleable.LoadingButton_loadingCircleColor, 0)
            textColor = getColor(R.styleable.LoadingButton_labelTextColor, 0)
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            paint.color = buttonBackgroundColor
            it.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

            paint.color = loadingProgressColor
            it.drawRect(0f, 0f, loadingRectWidth, heightSize.toFloat(), paint)

            paint.color = loadingArcColor
            it.drawArc(
                widthSize - heightSize + 2 * loadingArcPadding - loadingArcOffset,
                loadingArcPadding,
                widthSize - loadingArcOffset,
                heightSize - loadingArcPadding,
                0f,
                loadingArcAngle,
                true,
                paint
            )

            paint.color = textColor
            it.drawText(
                buttonText,
                widthSize.toFloat() / 2,
                (heightSize.toFloat() + buttonTextSize) / 2,
                paint
            )
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
}

