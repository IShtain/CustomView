package com.shtainyky.customview.views;


import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

import com.shtainyky.customview.R;

public class CustomProgressView extends View {

    private int backgroundFiguresColor;
    private int lengthSquareSide;
    private int distanceBetweenSquares;

    private int mCenterX, mCenterY;

    private Paint mBackgroundPaint;
    private Paint mInnerSquarePaint;
    private RectF mRectF;
    private Path mPath;

    private ValueAnimator blinkAnimator;

    public CustomProgressView(Context context) {
        super(context);
        init();
    }

    public CustomProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        init();
    }

    public CustomProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        init();
    }

    private void init() {
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerSquarePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectF = new RectF();
        mPath = new Path();
       // startInnerSquareBlinkingAnimation();
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray attributes = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.CustomProgressView, 0, 0);
        try {
            backgroundFiguresColor = attributes.getColor(R.styleable.CustomProgressView_backgroundFiguresColor, 0);
            lengthSquareSide = attributes.getColor(R.styleable.CustomProgressView_lengthSquareSide, 0);
            distanceBetweenSquares = attributes.getColor(R.styleable.CustomProgressView_distanceBetweenSquares, 0);

        } finally {
            attributes.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = getWidth() / 2;
        mCenterY = getHeight() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("myLog", "mCenterX = " + mCenterX);
        Log.d("myLog", "mCenterY = " + mCenterY);

        mBackgroundPaint.setColor(backgroundFiguresColor);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mRectF.set(mCenterX - distanceBetweenSquares / 2, mCenterY - distanceBetweenSquares / 2,
                mCenterX - distanceBetweenSquares / 2 + lengthSquareSide, mCenterY - distanceBetweenSquares / 2 + lengthSquareSide);
        canvas.drawRect(mRectF, mBackgroundPaint);

        mRectF.set(mCenterX + distanceBetweenSquares / 2, mCenterY - distanceBetweenSquares / 2,
                mCenterX + distanceBetweenSquares / 2 + lengthSquareSide, mCenterY - distanceBetweenSquares / 2 + lengthSquareSide);
        canvas.drawRect(mRectF, mBackgroundPaint);

        mRectF.set(mCenterX + distanceBetweenSquares / 2, mCenterY + distanceBetweenSquares / 2,
                mCenterX + distanceBetweenSquares / 2 + lengthSquareSide, mCenterY + distanceBetweenSquares / 2 + lengthSquareSide);
        canvas.drawRect(mRectF, mBackgroundPaint);

        mRectF.set(mCenterX - distanceBetweenSquares / 2, mCenterY + distanceBetweenSquares / 2,
                mCenterX - distanceBetweenSquares / 2 + lengthSquareSide, mCenterY + distanceBetweenSquares / 2 + lengthSquareSide);
        canvas.drawRect(mRectF, mBackgroundPaint);

        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setStrokeWidth(lengthSquareSide / 10);
        mPath.moveTo(mCenterX - distanceBetweenSquares / 2 + lengthSquareSide / 2,//move to left bottom square
                mCenterY + distanceBetweenSquares / 2 + lengthSquareSide / 2);
        mPath.lineTo(mCenterX + distanceBetweenSquares / 2 + lengthSquareSide / 2,//line to right bottom square
                mCenterY + distanceBetweenSquares / 2 + lengthSquareSide / 2);
        mPath.lineTo(mCenterX + distanceBetweenSquares / 2 + lengthSquareSide / 2,//line to right top square
                mCenterY - distanceBetweenSquares / 2 + lengthSquareSide / 2);
        mPath.lineTo(mCenterX - distanceBetweenSquares / 2 + lengthSquareSide / 2,//line to left top square
                mCenterY - distanceBetweenSquares / 2 + lengthSquareSide / 2);
        mPath.lineTo(mCenterX - distanceBetweenSquares / 2 + lengthSquareSide / 2,//line to left bottom square
                mCenterY + distanceBetweenSquares / 2 + lengthSquareSide / 2);
        mPath.lineTo(mCenterX + distanceBetweenSquares / 2 + lengthSquareSide / 2,//line to right top square
                mCenterY - distanceBetweenSquares / 2 + lengthSquareSide / 2);
        mPath.moveTo(mCenterX - distanceBetweenSquares / 2 + lengthSquareSide / 2,//move to left top square
                mCenterY - distanceBetweenSquares / 2 + lengthSquareSide / 2);
        mPath.lineTo(mCenterX + distanceBetweenSquares / 2 + lengthSquareSide / 2,//line to right bottom square
                mCenterY + distanceBetweenSquares / 2 + lengthSquareSide / 2);
        canvas.drawPath(mPath, mBackgroundPaint);

        mInnerSquarePaint.setColor(backgroundFiguresColor);
        mInnerSquarePaint.setStyle(Paint.Style.FILL);
        mInnerSquarePaint.setAlpha(90);
        mRectF.set(mCenterX - distanceBetweenSquares / 4, mCenterY - distanceBetweenSquares / 4,
                mCenterX + distanceBetweenSquares / 4 + lengthSquareSide, mCenterY + distanceBetweenSquares / 4 + lengthSquareSide);
        canvas.drawRect(mRectF, mInnerSquarePaint);
        startInnerSquareBlinkingAnimation();

    }

    private void startInnerSquareBlinkingAnimation() {
        blinkAnimator = ValueAnimator.ofInt(100, 0);
        blinkAnimator.setDuration(3000);
      //  blinkAnimator.setRepeatCount(ValueAnimator.INFINITE);
        blinkAnimator.setRepeatMode(ValueAnimator.RESTART);
        blinkAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mInnerSquarePaint.setAlpha((int) animator.getAnimatedValue());

                Log.d("myLog", "(int) animator.getAnimatedValue() = " + (int) animator.getAnimatedValue());
            }
        });

        if (!blinkAnimator.isStarted()) {
            blinkAnimator.start();
        }



    }
}
