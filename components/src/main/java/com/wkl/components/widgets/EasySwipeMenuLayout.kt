package com.wkl.components.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Scroller
import androidx.core.content.res.use
import androidx.core.view.children
import androidx.core.view.isGone
import com.wkl.components.R
import kotlin.math.abs

/**
 * Copyright © 2025 All Rights Reserved By HDZK.
 * Author: wangkelei
 * Date: 2025/4/29
 * Description:
 */
class EasySwipeMenuLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attributeSet, defStyleAttr) {
    private val TAG = "EasySwipeMenuLayout"
    private var mScaledTouchSlop: Int
    private var mScroller: Scroller
    private var mLeftViewResID = -1
    private var mRightViewResID = -1
    private var mContentViewResID = -1
    private var mLeftView: View? = null
    private var mRightView: View? = null
    private var mContentView: View? = null
    private var mCanLeftSwipe = true
    private var mCanRightSwipe = true
    private var mFraction = 0.3F
    private val mMatchParentChildren = ArrayList<View>(1)
    private var mContentViewLp: MarginLayoutParams? = null
    private var isSwiping = false
    private var mLastP: PointF? = null
    private var mFirstP: PointF? = null
    private var finallyDistanceX = 0F
    private var result: State = State.CLOSE

    init {
        val viewConfiguration = ViewConfiguration.get(context)
        mScaledTouchSlop = viewConfiguration.scaledTouchSlop
        mScroller = Scroller(context)
        val typedArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.EasySwipeMenuLayout,
            defStyleAttr,
            0
        )
        typedArray.use {
            mLeftViewResID =
                typedArray.getResourceId(R.styleable.EasySwipeMenuLayout_leftMenuView, -1)
            mContentViewResID =
                typedArray.getResourceId(R.styleable.EasySwipeMenuLayout_contentView, -1)
            mRightViewResID =
                typedArray.getResourceId(R.styleable.EasySwipeMenuLayout_rightMenuView, -1)
            mCanLeftSwipe =
                typedArray.getBoolean(R.styleable.EasySwipeMenuLayout_canLeftSwipe, true)
            mCanRightSwipe =
                typedArray.getBoolean(R.styleable.EasySwipeMenuLayout_canRightSwipe, true)
            mFraction =
                typedArray.getFloat(R.styleable.EasySwipeMenuLayout_leftMenuView, 0.5F)
            result = State.entries.toTypedArray()[typedArray.getInt(
                R.styleable.EasySwipeMenuLayout_easy_layout_state,
                2
            )]
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        Log.d(TAG, "onFinishInflate()")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.d(TAG, "onMeasure()")
        isClickable = true
        val measureMatchParentChildren =
            MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY || MeasureSpec.getMode(
                heightMeasureSpec
            ) != MeasureSpec.EXACTLY
        mMatchParentChildren.clear()
        var maxHeight = 0
        var maxWidth = 0
        var childState = 0
        children.forEach { child ->
            if (!child.isGone) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
                val lp = child.layoutParams as MarginLayoutParams
                maxWidth =
                    maxWidth.coerceAtLeast(child.measuredWidth + lp.leftMargin + lp.rightMargin)
                maxHeight =
                    maxHeight.coerceAtLeast(child.measuredHeight + lp.topMargin + lp.bottomMargin)
                childState = combineMeasuredStates(childState, child.measuredState)
                if (measureMatchParentChildren) {
                    if (lp.width == LayoutParams.MATCH_PARENT || lp.height == LayoutParams.MATCH_PARENT) {
                        mMatchParentChildren.add(child)
                    }
                }
            }
        }

        maxHeight = Math.max(maxHeight, suggestedMinimumHeight)
        maxWidth = Math.max(maxWidth, suggestedMinimumWidth)
        setMeasuredDimension(
            resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
            resolveSizeAndState(
                maxHeight,
                heightMeasureSpec,
                childState shl MEASURED_HEIGHT_STATE_SHIFT
            )
        )

        if (mMatchParentChildren.size > 1) {
            for (child in mMatchParentChildren) {
                val lp = child.layoutParams as MarginLayoutParams
                var childWidthMeasureSpec: Int
                if (lp.width == LayoutParams.MATCH_PARENT) {
                    val width = Math.max(0, measuredWidth - lp.leftMargin - lp.rightMargin)
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
                } else {
                    childWidthMeasureSpec = getChildMeasureSpec(
                        widthMeasureSpec,
                        lp.leftMargin + lp.rightMargin,
                        lp.width
                    )
                }

                var childHeightMeasureSpec: Int
                if (lp.height == FrameLayout.LayoutParams.MATCH_PARENT) {
                    val height = Math.max(0, measuredHeight - lp.topMargin - lp.bottomMargin)
                    childHeightMeasureSpec =
                        MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
                } else {
                    childHeightMeasureSpec = getChildMeasureSpec(
                        heightMeasureSpec,
                        lp.topMargin + lp.bottomMargin,
                        lp.height
                    )
                }
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
            }
        }
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val left = 0 + paddingLeft
        val top = 0 + paddingTop
        for (child in children) {
            if (mLeftView == null && child.id == mLeftViewResID) {
                mLeftView = child
                mLeftView?.isClickable = true
            }
            if (mContentView == null && child.id == mContentViewResID) {
                mContentView = child
                mContentView?.isClickable = true
            }
            if (mRightView == null && child.id == mRightViewResID) {
                mRightView = child
                mRightView?.isClickable = true
            }
        }
        var cRight: Int
        mContentView?.let {
            mContentViewLp = it.layoutParams as MarginLayoutParams
            val cTop = top + mContentViewLp!!.topMargin
            val cLeft = left + mContentViewLp!!.leftMargin
            cRight = left + mContentViewLp!!.leftMargin + it.measuredWidth
            val cBottom = cTop + it.measuredHeight
            it.layout(cLeft, cTop, cRight, cBottom)
        }

        mLeftView?.let {
            val lp = it.layoutParams as MarginLayoutParams
            val lTop = top + lp.topMargin
            val lLeft = 0 - it.measuredWidth + lp.leftMargin + lp.rightMargin
            val lRight = 0 - lp.rightMargin
            val lBottom = lTop + it.measuredHeight
            it.layout(lLeft, lTop, lRight, lBottom)
        }

        mRightView?.let {
            val lp = it.layoutParams as MarginLayoutParams
            val lTop = top + lp.topMargin
            val lLeft = mContentView!!.right + mContentViewLp!!.rightMargin + lp.leftMargin
            val lRight = lLeft + it.measuredWidth
            val lBottom = lTop + it.measuredHeight
            it.layout(lLeft, lTop, lRight, lBottom)
        }

        handleSwipeMenu(result, false)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                isSwiping = false
                if (mLastP == null) {
                    mLastP = PointF()
                }
                mLastP?.set(ev.rawX, ev.rawY)

                if (mFirstP == null) {
                    mFirstP = PointF()
                }
                mFirstP?.set(ev.rawX, ev.rawY)
                mViewCache?.let {
                    if (it != this) {
                        it.handleSwipeMenu(State.CLOSE)
                    }
                    // Log.i(TAG, ">>>有菜单被打开");
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                val distanceX = mLastP!!.x - ev.rawX
                val distanceY = mLastP!!.y - ev.rawY
                if (abs(distanceY) > mScaledTouchSlop && abs(distanceY) > abs(distanceX)) {
                    return super.dispatchTouchEvent(ev)
                }

                scrollBy(distanceX.toInt(), 0)//滑动使用scrollBy
                //越界修正
                if (scrollX < 0) {
                    if (!mCanRightSwipe || mLeftView == null) {
                        scrollTo(0, 0)
                    } else {
                        //左滑
                        if (scrollX < mLeftView!!.left) {
                            scrollTo(mLeftView!!.left, 0)
                        }
                    }
                } else if (scrollX > 0) {
                    if (!mCanLeftSwipe || mRightView == null) {
                        scrollTo(0, 0)
                    } else {
                        if (scrollX > mRightView!!.right - mContentView!!.right - mContentViewLp!!.rightMargin) {
                            scrollTo(
                                mRightView!!.right - mContentView!!.right - mContentViewLp!!.rightMargin,
                                0
                            )
                        }
                    }
                }
                //当处于水平滑动时，禁止父类拦截
                if (abs(distanceX) > mScaledTouchSlop) {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                mLastP?.set(ev.rawX, ev.rawY)
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                finallyDistanceX = mFirstP!!.x - ev.rawX
                if (abs(finallyDistanceX) > mScaledTouchSlop) {
                    isSwiping = true
                }
                result = isShouldOpen(scrollX)
                handleSwipeMenu(result)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_MOVE -> {
                if (abs(finallyDistanceX) > mScaledTouchSlop) {
                    return true
                }
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                if (isSwiping) {
                    isSwiping = false
                    finallyDistanceX = 0F
                    return true
                }
            }
        }

        return super.onInterceptTouchEvent(ev)
    }

    override fun computeScroll() {
        //判断Scroller是否执行完毕：
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.currX, mScroller.currY)
            //通知View重绘-invalidate()->onDraw()->computeScroll()
            invalidate()
        }
    }

    fun handleSwipeMenu(state: State, refresh: Boolean = true) {
        mViewCache?.let {
            if (mStateCache != state)
                resetStatus()
        }
        when (state) {
            State.LEFTOPEN -> {
                mScroller.startScroll(scrollX, 0, mLeftView!!.left - scrollX, 0)
                mViewCache = this
                mStateCache = state
            }

            State.RIGHTOPEN -> {
                mScroller.startScroll(
                    scrollX,
                    0,
                    mRightView!!.right - mContentView!!.right - mContentViewLp!!.rightMargin - scrollX,
                    0
                )
                mViewCache = this
                mStateCache = state
            }

            State.CLOSE -> {
                mScroller.startScroll(scrollX, 0, -scrollX, 0)
                mViewCache = null
                mStateCache = State.CLOSE
            }
        }
        if (refresh) invalidate()
    }

    private fun isShouldOpen(newScrollX: Int): State {
        if (!(mScaledTouchSlop < abs(finallyDistanceX)))
            return mStateCache
        if (finallyDistanceX < 0) {
            //➡️滑动
            //1、展开左边按钮
            //获得leftView的测量长度
            if (newScrollX < 0 && mLeftView != null) {
                if (abs(mLeftView!!.width * mFraction) < abs(newScrollX))
                    return State.LEFTOPEN
            }
            //2、关闭右边按钮
            if (newScrollX > 0 && mRightView != null)
                return State.CLOSE
        } else if (finallyDistanceX > 0) {
            //⬅️滑动
            //3、开启右边菜单按钮
            if (newScrollX > 0 && mRightView != null) {
                if (abs(mRightView!!.width * mFraction) < abs(newScrollX))
                    return State.RIGHTOPEN
            }
            //关闭左边
            if (newScrollX < 0 && mLeftView != null) {
                return State.CLOSE
            }
        }
        return State.CLOSE
    }

    fun resetStatus() {
        mViewCache?.let {
            if (mStateCache !== State.CLOSE) {
                mScroller.startScroll(mViewCache!!.scrollX, 0, -mViewCache!!.scrollX, 0)
                mViewCache!!.invalidate()
                mViewCache = null
                mStateCache = State.CLOSE
            }
        }
    }

    override fun onDetachedFromWindow() {
        if (this == mViewCache)
            mViewCache?.handleSwipeMenu(State.CLOSE)
        super.onDetachedFromWindow()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (this == mViewCache) {
            mViewCache?.handleSwipeMenu(mStateCache)
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var mViewCache: EasySwipeMenuLayout? = null
        var mStateCache: State = State.CLOSE
    }

    enum class State {
        LEFTOPEN,
        RIGHTOPEN,
        CLOSE
    }
}