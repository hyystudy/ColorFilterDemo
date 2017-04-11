package com.abclauncher.colorfilterdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;


/**
 * Created by sks on 2016/11/30.
 */
public class CircleProgress extends View {

    private static final String TAG = "CircleProgress";
    private Paint mBgCirclePaint, mProgressPaint, mPointPaint;
    private Context mContext;
    private float DEFAULT_BG_CIRCLE_WIDTH = 2, DEFAULT_PROGRESS_WIDTH = 2, DEFAULT_POINT_RADIUS = 4;
    private float MAX_SWEEP_ANGLE = 360;
    private float DEFAULT_PERCENT_TEXT_SIZE = 25;
    private float mBgCircleWidth, mProgressWidth;
    private int mBgCircleColor, mProgressColor, mPercentTextColor;
    private float mStartAngle, mSweepAngle;
    private float mProgress;
    private float mPercentTextSize;
    private RectF mCircleBounds, mProgressBounds;
    private boolean mShowPercentText, mShowPercent;
    private Paint mTextPaint;
    private float mDensity;
    private int mCenterX, mCenterY;
    private float mInnerCircleRadius;
    private float mPointRadius;
    private boolean reverse;

    public CircleProgress(Context context) {
        this(context, null);
        initView(null);
    }

    public CircleProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        mContext = getContext();
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        mDensity = displayMetrics.density;

        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.CircleProgress);
        mBgCircleWidth = a.getDimension(R.styleable.CircleProgress_bg_circular_width, DEFAULT_BG_CIRCLE_WIDTH * mDensity);
        mProgressWidth = a.getDimension(R.styleable.CircleProgress_progress_width, DEFAULT_PROGRESS_WIDTH * mDensity);
        mPointRadius = a.getDimension(R.styleable.CircleProgress_point_radius, DEFAULT_POINT_RADIUS * mDensity);
        mBgCircleColor = a.getColor(R.styleable.CircleProgress_bg_circular_color, Color.parseColor("#33ffffff"));
        mProgressColor = a.getColor(R.styleable.CircleProgress_circle_progress_color, Color.RED);
        mProgress = a.getInteger(R.styleable.CircleProgress_circle_progress, 0);
        mSweepAngle = a.getFloat(R.styleable.CircleProgress_sweep_angle, MAX_SWEEP_ANGLE * mProgress / 100);
        mStartAngle = a.getFloat(R.styleable.CircleProgress_start_angle, -90);

        mShowPercentText = a.getBoolean(R.styleable.CircleProgress_show_percent_text, false);
        mShowPercent = a.getBoolean(R.styleable.CircleProgress_show_percent, true);
        mPercentTextSize = a.getDimension(R.styleable.CircleProgress_percent_text_size, dp2px(DEFAULT_PERCENT_TEXT_SIZE));
        mPercentTextColor = a.getColor(R.styleable.CircleProgress_percent_text_color, Color.WHITE);
        a.recycle();

        //初始化背景圆弧 画笔
        mBgCirclePaint = new Paint();
        mBgCirclePaint.setAntiAlias(true);
        mBgCirclePaint.setStyle(Paint.Style.STROKE);
        mBgCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        mBgCirclePaint.setStrokeWidth(mBgCircleWidth);
        mBgCirclePaint.setColor(mBgCircleColor);

        //初始化progress 的画笔
        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeCap(Paint.Cap.BUTT);
        mProgressPaint.setStrokeWidth(mProgressWidth);
        mProgressPaint.setColor(mProgressColor);

        //初始化 text 的画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mPercentTextColor);
        mTextPaint.setTextSize(mPercentTextSize);

        mPointPaint = new Paint();
        mPointPaint.setAntiAlias(true);
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setColor(mProgressColor);

        //初始化 画圆弧 的矩形区域
        mCircleBounds = new RectF();

        //初始化 画圆弧 的矩形区域
        mProgressBounds = new RectF();
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    private int dp2px(float dp) {
        float density = getResources().getDisplayMetrics().density;

        return (int) (dp * density + .5f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h / 2;
        mCircleBounds.left = mPointRadius ;
        mCircleBounds.right = w - mPointRadius;
        mCircleBounds.top = mPointRadius;
        mCircleBounds.bottom = h - mPointRadius;

        mProgressBounds.left = mCircleBounds.left;
        mProgressBounds.right = mCircleBounds.right;
        mProgressBounds.top = mCircleBounds.top;
        mProgressBounds.bottom = mCircleBounds.bottom;

        mInnerCircleRadius = (mCircleBounds.right - mCircleBounds.left)/2f;
        Log.d(TAG, "onSizeChanged: mInnerCircleRadius-->" + mInnerCircleRadius );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mSweepAngle = MAX_SWEEP_ANGLE * mProgress / 100;
        //canvas.drawRect(mCircleBounds, mBgCirclePaint);
        if (reverse){
            canvas.drawArc(mCircleBounds, mStartAngle, -360, false, mBgCirclePaint);
        }

        if (reverse) {
            canvas.drawArc(mProgressBounds, mStartAngle, mSweepAngle, false, mProgressPaint);
        } else {
            canvas.drawArc(mProgressBounds, mStartAngle, mSweepAngle, false, mProgressPaint);
        }


        if (!reverse) {
            float mCurrentDegree = (float) (2.0f * Math.PI * mProgress/ 100);
            Log.d(TAG, "onDraw: " + mCircleBounds.top);
            float cX = (float) (mCenterX + Math.cos(mCurrentDegree) * mInnerCircleRadius);
            float cy = (float) (mCenterY + Math.sin(mCurrentDegree) * mInnerCircleRadius);
            Log.d(TAG, "onDraw: " + cX + " ," + cy);
            canvas.drawCircle(cX, cy, mPointRadius, mPointPaint);
        }

    }

    private float getTextHeight() {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        Log.d(TAG, "getTextHeight: ascent " + fontMetrics.ascent);
        Log.d(TAG, "getTextHeight: descent " + fontMetrics.descent);
        Log.d(TAG, "getTextHeight: leading " + fontMetrics.leading);
        Log.d(TAG, "getTextHeight: bottom " + fontMetrics.bottom);
        Log.d(TAG, "getTextHeight: top " + fontMetrics.top);
        return fontMetrics.descent - fontMetrics.ascent;
    }

    private float getTextLeading(){
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        return fontMetrics.leading - fontMetrics.ascent;
    }

    public void setStartAngle(float startAngle) {
        this.mStartAngle = startAngle;
        invalidate();
    }

    private String getProgressStringWithOutPercent() {
        //int progress = mProgress < 1f ? (int) (mProgress * 100) : 100;
        return String.valueOf(mProgress);
    }

    private String getProgressStringWithPercent() {
        String text;
        //int progress = mProgress < 1f ? (int) (mProgress * 100) : 100;
        text = mProgress + "%";
        return text;
    }

    public void setBgCircleColor(int bgCircleColor) {
        this.mBgCircleColor = bgCircleColor;
        mBgCirclePaint.setColor(mBgCircleColor);
        invalidate();
    }

    public void setProgressColor(int progressColor) {
        this.mProgressColor = progressColor;
        mProgressPaint.setColor(mProgressColor);
        invalidate();
    }

    public void setBgCircleWidth(float bgCircleWidth) {
        this.mBgCircleWidth = bgCircleWidth;
        mBgCirclePaint.setStrokeWidth(bgCircleWidth);
        invalidate();
    }

    public void setProgressWidth(int width) {
        this.mProgressWidth = width;
        mProgressPaint.setStrokeWidth(width);
        invalidate();
    }

    public void setProgress(float progress) {
        this.mProgress = progress;
        invalidate();
    }
}
