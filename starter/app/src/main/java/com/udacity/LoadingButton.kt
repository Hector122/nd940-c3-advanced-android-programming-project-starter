package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(context: Context,
                                              attrs: AttributeSet? = null,
                                              defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {
    
    private var widthSize = 0
    private var heightSize = 0
    
    private var progressValue = 0f
    
    //custom attributes
    private var buttonBackgroundColor = 0
    private var buttonTextColor = 0
    
    private var valueAnimator = ValueAnimator()
    
    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { property, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                buttonState = ButtonState.Loading
            }
            ButtonState.Loading -> {
                isClickable = false
                startLoadingAnimation()
            }
            ButtonState.Completed -> {
                isClickable = true
                stopLoadingAnimation()
            }
        }
    }
    
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }
    
    init {
        isClickable = true
        
        
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonBackgroundColor = getColor(R.styleable.LoadingButton_backgroundColor, 0)
            buttonTextColor = getColor(R.styleable.LoadingButton_textColor, 0)
        }
    }
    
    override fun performClick(): Boolean {
        super.performClick()
        
        buttonState = ButtonState.Clicked
        
        
        invalidate() // invalidates the entire view, forcing a call to onDraw() to redraw the view
        return true
    }
    
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        
        // draw initial background
        paint.color = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
        canvas?.drawPaint(paint)
        
        drawRect(canvas)
        drawText(canvas)
        drawArc(canvas)
    }
    
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0)
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
    
    private fun drawText(canvas: Canvas?) {
        val text =
            if (buttonState == ButtonState.Completed) resources.getString(R.string.button_download)
            else resources.getString(R.string.button_loading)
        
        paint.color = Color.WHITE
        canvas?.drawText(text, widthSize / 2f, heightSize / 2f + paint.textSize / 2f, paint)
    }
    
    private fun drawRect(canvas: Canvas?) {
        paint.color = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)
        canvas?.drawRect(0f, 0f, progressValue, heightSize.toFloat(), paint)
    }
    
    private fun drawArc(canvas: Canvas?) {
        paint.color = ResourcesCompat.getColor(resources, R.color.colorAccent, null)
        
        //saw how to draw arc from: https://github.com/Gabryan1995/LoadApp/blob/main/starter/app/src/main/java/com/udacity/LoadingButton.kt
        canvas?.drawArc(//left, top, right, bottom, start angle, sweep angle, use center, Paint
                (widthSize / 4 * 3 - 30).toFloat(),
                (heightSize / 2 - 30).toFloat(),
                (widthSize / 4 * 3 + 30).toFloat(),
                (heightSize / 2 + 30).toFloat(),
                0f,
                progressValue / 2.70f,
                true,
                paint)
    }
    
    private fun startLoadingAnimation() {
        // animate progressbar
        valueAnimator.setObjectValues(0f, widthSize.toFloat())
        valueAnimator.apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            addUpdateListener {
                progressValue = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }
    
    private fun stopLoadingAnimation() {
        valueAnimator.repeatCount = 0
        valueAnimator.addUpdateListener {
            valueAnimator.doOnEnd {
                progressValue = 0f
                invalidate()
            }
        }
    }
}