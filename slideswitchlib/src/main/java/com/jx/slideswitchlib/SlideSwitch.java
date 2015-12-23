package com.jx.slideswitchlib;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by jiang.xu on 2015/12/19.
 */
public class SlideSwitch extends HorizontalScrollView {
    public final  String TAG="SlideSwitch";
    private boolean mIsChangeColor;
    private int mSelectTextColor;
    private int mUnSelectTextColor;
    private LinearLayout.LayoutParams mLayoutParams;
    private final PageListener mPageListener = new PageListener();
    public ViewPager.OnPageChangeListener mOnPageChangeListener;
    private LinearLayout mTabsContainer;
    private ViewPager mViewPager;
    private int mTabCount;
    private int mCurrentPosition = 0;
    private float mCurrentPositionOffset = 0f;
    private int mScrollOffset = 52;
    private int mLastScrollX = 0;
    private int mTabTextSize = 20;
    private Typeface mTypeface = null;
    private int mTabTypefaceStyle = Typeface.BOLD;
    private int transparentColorId = R.color.transparent;
    private int mTabPadding = 24;
    private RectF mRect;
    private Paint mPaint;
    public SlideSwitch(Context context) {
        this(context, null);
    }

    public SlideSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlideSwitch);
        mIsChangeColor = typedArray.getBoolean(R.styleable.SlideSwitch_isChangeColor, true);
        mSelectTextColor = typedArray.getColor(R.styleable.SlideSwitch_selectTextColor, Color.RED);
        mUnSelectTextColor = typedArray.getColor(R.styleable.SlideSwitch_unSelectTextColor, Color.WHITE);
        mTabTextSize = typedArray.getDimensionPixelSize(R.styleable.SlideSwitch_tabTextSize, 18);
        typedArray.recycle();
        init(context);
    }

    private void init(Context context){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mRect=new RectF();
        mLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mTabsContainer = new LinearLayout(context);
        mTabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        mTabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(mTabsContainer);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode()||mTabCount == 0) {
            return;
        }
        final int height = getHeight();
        View currentTab = mTabsContainer.getChildAt(mCurrentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();
        // if there is an offset, start interpolating left and right coordinates between current and next tab
        if (mCurrentPositionOffset > 0f && mCurrentPosition < mTabCount - 1) {
            View nextTab = mTabsContainer.getChildAt(mCurrentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();
            lineLeft = (mCurrentPositionOffset * nextTabLeft + (1f - mCurrentPositionOffset) * lineLeft);
            lineRight = (mCurrentPositionOffset * nextTabRight + (1f - mCurrentPositionOffset) * lineRight);
        }
        mPaint.setColor(Color.WHITE);
        mRect.left=lineLeft;
        mRect.top=0;
        mRect.right=lineRight;
        mRect.bottom=height;
        canvas.drawRoundRect(mRect, height / 2, height / 2, mPaint);
    }



    public void setViewPager(ViewPager pager) {
        this.mViewPager = pager;
        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        pager.addOnPageChangeListener(mPageListener);
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        mTabsContainer.removeAllViews();
        mTabCount = mViewPager.getAdapter().getCount();
        for (int i = 0; i < mTabCount; i++) {
            addTextTab(i, mViewPager.getAdapter().getPageTitle(i).toString());
        }
        updateTabStyles();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                mCurrentPosition = mViewPager.getCurrentItem();
                scrollToChild(mCurrentPosition, 0);
            }
        });
    }

    private void addTextTab(final int position, String title) {
        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setGravity(Gravity.CENTER);
        tab.setSingleLine();
        addTab(position, tab);
    }

    private void addTab(final int position, View tab) {
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(position);
            }
        });
        tab.setPadding(mTabPadding, 0, mTabPadding, 0);
        mTabsContainer.addView(tab, position, mLayoutParams);
    }


    private void updateTabStyles() {
        for (int i = 0; i < mTabCount; i++) {
            View v = mTabsContainer.getChildAt(i);
            v.setBackgroundResource(transparentColorId);
            TextView tab = (TextView) v;
            tab.setTextSize(TypedValue.COMPLEX_UNIT_PX,mTabTextSize);
            tab.setTypeface(mTypeface, mTabTypefaceStyle);
            tab.setTextColor( i != 0 ? mUnSelectTextColor : mSelectTextColor);
        }
    }

    private void updateSelectText(){
        Log.i(TAG,"mCurrentPosition  =" +mCurrentPosition);
        for (int i = 0; i < mTabCount; i++) {
            View v = mTabsContainer.getChildAt(i);
            TextView tab = (TextView) v;
            tab.setTextColor(i != mViewPager.getCurrentItem() ? mUnSelectTextColor : mSelectTextColor);
        }
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.mOnPageChangeListener = listener;
    }

    private void scrollToChild(int position, int offset) {
        if (mTabCount == 0) {
            return;
        }
        int newScrollX = mTabsContainer.getChildAt(position).getLeft() + offset;
        if (position > 0 || offset > 0) {
            newScrollX -= mScrollOffset;
        }
        if (newScrollX != mLastScrollX) {
            mLastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }
    }

    private class PageListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            mCurrentPosition = position;
            mCurrentPositionOffset = positionOffset;
            int normalPosition = 1;
            if (mCurrentPosition == 1) {
                normalPosition = 0;
            }
            scrollToChild(position, (int) (positionOffset * mTabsContainer.getChildAt(position).getWidth()));
            if (mIsChangeColor&&positionOffset > 0) {
                TextView v1 = (TextView) mTabsContainer.getChildAt(normalPosition);
                TextView v2 = (TextView) mTabsContainer.getChildAt(mCurrentPosition);
                Integer color = (Integer) new ArgbEvaluator().evaluate(positionOffset,mUnSelectTextColor, mSelectTextColor);
                Integer color2 = (Integer) new ArgbEvaluator().evaluate(positionOffset, mSelectTextColor, mUnSelectTextColor);
                v1.setTextColor(color);
                v2.setTextColor(color2);
            }
            invalidate();
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                scrollToChild(mViewPager.getCurrentItem(), 0);
            }
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrollStateChanged(state);
            }
        }
        @Override
        public void onPageSelected(int position) {
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageSelected(position);
            }
            if (!mIsChangeColor){
               updateSelectText();
            }
        }
    }
}
