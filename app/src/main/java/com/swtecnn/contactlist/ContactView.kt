package com.swtecnn.contactlist

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.doOnEnd
import kotlin.math.abs

class ContactView(var c: Context, var attrs: AttributeSet? = null): View(c, attrs) {

    private enum class Displaying {
        CALL,
        TEXT,
        SMS
    }

    var contact: String
    private var displaying: Displaying = Displaying.TEXT

    private val h = 150

    private val bgPaint = Paint()
    private val textPaint = Paint()
    private val callPaint = Paint()
    private val smsPaint = Paint()
    private val cmdPaint = Paint()
    private var currPaint = textPaint

    private var callWidth = 0
    private var smsWidth = 0

    private val callRect = Rect()
    private val smsRect = Rect()

    var parentCallback: DisplayStateCallback? = null // unused

    init {
        val array: TypedArray = c.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ContactView,
            0, 0
        )

        contact = array.getString(R.styleable.ContactView_text) ?: ""

        textPaint.color = Color.BLACK
        textPaint.isAntiAlias = true
        textPaint.textSize = 50.0f
        textPaint.strokeWidth = 2.0f
        textPaint.style = Paint.Style.FILL_AND_STROKE

        cmdPaint.color = Color.LTGRAY
        cmdPaint.isAntiAlias = true
        cmdPaint.textSize = 50.0f
        cmdPaint.strokeWidth = 1.0f
        cmdPaint.style = Paint.Style.FILL_AND_STROKE

        callPaint.color = resources.getColor(R.color.call_green)
        smsPaint.color = resources.getColor(R.color.purple_500)
        bgPaint.color = Color.WHITE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(widthMeasureSpec, h)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(canvas == null) {
            return
        }

        canvas.drawColor(Color.WHITE)

        // Draw call and sms rectangles
        callRect.set(0, 0, callWidth, height)
        smsRect.set(width - smsWidth, 0, width, height)
        canvas.drawRect(callRect, callPaint)
        canvas.drawRect(smsRect, smsPaint)

        var currText = ""

        // Handle command text drawing
        when(displaying) {
            Displaying.TEXT -> {
                currPaint = textPaint
                currText = contact
            }
            Displaying.CALL -> {
                currPaint = cmdPaint
                currText = "Call $contact"
            }
            Displaying.SMS -> {
                currPaint = cmdPaint
                currText = "Sms to $contact"
            }
        }
        val textBounds = Rect()
        currPaint.getTextBounds(currText, 0, currText.length, textBounds)
        val textHeight = textBounds.height()
        val textWidth = textBounds.width()
        canvas.drawText(currText, (width/2 - textWidth/2).toFloat(), (height/2 - textHeight/2).toFloat(), currPaint)
    }

    var touchX = 0.0f
    var touchY = 0.0f
    var releaseX = 0.0f
    var releaseY = 0.0f

    fun selectSwipeDirection() {
        val deltaX = releaseX - touchX
        val deltaY = releaseY - touchY
        if(abs(deltaY) > abs(deltaX)) {
            // Vertical swipe code here
            return
        } else if(abs(deltaX) < 100) {
            // Swipe too short, ignore
            return
        } else {
            if(deltaX > 0) {
                onSwipeRight()
            }
            if(deltaX < 0) {
                onSwipeLeft()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event != null) {
            when (event.action) { // TODO implement animation on ACTION_MOVE
                MotionEvent.ACTION_DOWN -> {
                    touchX = event.x
                    touchY = event.y
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    releaseX = event.x
                    releaseY = event.y
                    selectSwipeDirection()
                }
            }
        }
        return true
    }

    fun onSwipeLeft()  {
        when (displaying) {
            Displaying.SMS -> {/* no-op */
            }
            Displaying.TEXT -> displaySms()
            Displaying.CALL -> displayText()
        }
        Log.d("ContactView", "Swipe Left, displaying ${displaying.name}")
    }

    fun onSwipeRight() {
        when (displaying) {
            Displaying.CALL -> {/* no-op */
            }
            Displaying.TEXT -> displayCall()
            Displaying.SMS -> displayText()
        }
        Log.d("ContactView", "Swipe Right, displaying ${displaying.name}")
    }

    private fun displaySms() {

        // Call parent to make other children display text
        //parentCallback?.notifyDisplayStateChanged()

        val animator = ValueAnimator.ofInt(0, width)
        animator.duration = 200
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener {
            updateSms(animator.animatedValue as Int)
        }
        animator.doOnEnd {
            displaying = Displaying.SMS
        }
        animator.start()
    }

    fun displayText() {
        val smsAnimator = ValueAnimator.ofInt(smsWidth, 0)
        smsAnimator.duration = 200
        smsAnimator.interpolator = DecelerateInterpolator()
        smsAnimator.addUpdateListener {
            updateSms(smsAnimator.animatedValue as Int)
        }
        smsAnimator.doOnEnd {
            displaying = Displaying.TEXT
        }

        val callAnimator = ValueAnimator.ofInt(callWidth, 0)
        callAnimator.duration = 200
        callAnimator.interpolator = DecelerateInterpolator()
        callAnimator.addUpdateListener {
            updateCall(callAnimator.animatedValue as Int)
        }
        callAnimator.doOnEnd {
            displaying = Displaying.TEXT
        }

        smsAnimator.start(); callAnimator.start()
    }

    private fun displayCall() {

        // Call parent to make other children display text
        //parentCallback?.notifyDisplayStateChanged()

        val animator = ValueAnimator.ofInt(0, width)
        animator.duration = 200
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener {
            updateCall(animator.animatedValue as Int)
        }
        animator.doOnEnd {
            displaying = Displaying.CALL
        }
        animator.start()
    }

    private fun updateCall(width: Int) {
        callWidth = width
        invalidate()
    }

    private fun updateSms(width: Int) {
        smsWidth = width
        invalidate()
    }
}
