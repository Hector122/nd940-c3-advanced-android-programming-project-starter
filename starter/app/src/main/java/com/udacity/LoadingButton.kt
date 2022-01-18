package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.min
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(context: Context,
                                              attrs: AttributeSet? = null,
                                              defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    
    private val valueAnimator = ValueAnimator()
    
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
            
            }
            
            ButtonState.Loading -> {
            
            }
            
            ButtonState.Completed -> {
            
            }
        }
    }
    
    //This is the circle that is drawn inside the button.
    // private val circleRadius = resources.getDimension(R.dimen.circleRadius)
    var radius = 0f
    
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }
    
    init {
        // isClickable = true
    }
    
    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        //calculate the size for the custom view's dial
        radius = (min(width, height) / 2.0 * 0.8).toFloat()
    }
    
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        
        // draw initial background
        paint.color = ContextCompat.getColor(context, R.color.colorPrimary)
        canvas?.drawPaint(paint)
        
        drawText(canvas, resources.getString(R.string.download))
        //drawFace(canvas)
        //drawRect(canvas)
        //drawCircle(canvas)
        
    }
    
    private fun drawText(canvas: Canvas?, text: String) {
        paint.color = Color.WHITE
        canvas?.drawText(text, widthSize / 2f, heightSize / 2f + paint.textSize / 2f, paint)
    }
    
    private fun drawRect(canvas: Canvas?) {
        paint.color = Color.BLUE
        canvas?.drawRect(0f, 0f, 200f, 2f, paint)
    }
    
    private fun drawCircle(canvas: Canvas?) {
        paint.color = Color.YELLOW
//        canvas?.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paint)
    }
    
    private fun drawFace(canvas: Canvas?) {
        // View size in pixels
        var size = 80
        
        //var borderWidth = 4.0f
        
        // 1
        paint.color = Color.CYAN
        paint.style = Paint.Style.FILL
        
        // 2
        val radius = size / 2f
        
        // 3
        canvas?.drawCircle(size / 2f, size / 2f, radius, paint)
        
        // 4
        paint.color = ContextCompat.getColor(context, R.color.colorAccent)
        
        // 5
        canvas?.drawCircle((size / 2).toFloat(), (size / 2).toFloat(), radius, paint)
    }
    
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0)
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
    
}