/*
 * Copyright (c) 2016 Kaku咖枯 <kaku201313@163.com | 3772304@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.kaku.sldv;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * @author Kaku咖枯
 * @version 1.0 2015/01/05
 */
public class SlideUnlockView extends RelativeLayout {

    private static final String LOG_TAG = "MySlidingView";

    /**
     * smooth scroll time
     */
    private static final int DURATION = 600;

    private static final int MIN_FLING_VELOCITY = 400;
    private Context mContext;
    private Scroller mScroller;
    private int mMinimumVelocity;
    private int mScreenWidth = 0;
    private int mLastX = 0;
    private int mLastDownX = 0;
    private boolean mCloseFlag = false;
    private VelocityTracker mVelocityTracker;
    private SlidingTipListener mSlidingTipListener;

    public void setSlidingTipListener(SlidingTipListener slidingTipListener) {
        mSlidingTipListener = slidingTipListener;
    }

    public SlideUnlockView(Context context) {
        super(context);
    }

    public SlideUnlockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public SlideUnlockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init() {
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;
        mScroller = new Scroller(mContext);
        mMinimumVelocity = (int) (MIN_FLING_VELOCITY * getResources().getDisplayMetrics().density);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercepted = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                intercepted = false;
                mLastDownX = (int) event.getX();
//                LogUtil.d(LOG_TAG, "mLastDownX= " + mLastDownX);
                break;
            case MotionEvent.ACTION_MOVE:
                // <!-- Base "touch slop" value used by ViewConfiguration as a
                // <dimen name="config_viewConfigurationTouchSlop">8dp</dimen>
                intercepted = Math.abs(event.getX() - mLastDownX) >= ViewConfiguration.get(
                        getContext()).getScaledTouchSlop();
                break;
            case MotionEvent.ACTION_UP:
                intercepted = false;
                break;
        }
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        acquireVelocityTracker(event);
        int x = (int) event.getX();
//        LogUtil.d(LOG_TAG, "x= " + x);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
//                mLastX = x;
//                LogUtil.d(LOG_TAG, "mLastX(ACTION_DOWN)= " + mLastX);
                break;
            case MotionEvent.ACTION_MOVE:
                int scrollX = x - mLastX;
                int deltaX = x - mLastDownX;
                if (deltaX > 0) {
                    scrollBy(-scrollX, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                int xVelocity = (int) mVelocityTracker.getXVelocity();
//                LogUtil.d(LOG_TAG, "xVelocity= " + xVelocity);
                releaseVelocityTracker();
                deltaX = x - mLastDownX;
                if (deltaX > 0) {
                    if ((Math.abs(deltaX) > mScreenWidth / 2) || xVelocity > mMinimumVelocity) {
                        smoothScrollTo(getScrollX(), -mScreenWidth, DURATION);
                        mCloseFlag = true;

                    } else {
                        smoothScrollTo(getScrollX(), -getScrollX(), DURATION);
                    }
                } else {
                    smoothScrollTo(getScrollX(), -getScrollX(), DURATION);
                }
                break;
        }
        mLastX = x;
//        LogUtil.d(LOG_TAG, "mLastX= " + mLastX);
        return true;
    }

    private void acquireVelocityTracker(MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void smoothScrollTo(int startX, int deltaX, int duration) {
        mScroller.startScroll(startX, 0, deltaX, 0, duration);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            // 更新界面
            postInvalidate();
        } else if (mCloseFlag) {
            mSlidingTipListener.onSlidFinish();
            mCloseFlag = false;
        }
    }

    public interface SlidingTipListener {
        void onSlidFinish();
    }
}
