package com.shtainyky.customview.views;


import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.shtainyky.customview.R;

public class CustomProgressView extends View {

    private int backgroundFiguresColor;
    private int lengthSquareSide;
    private int distanceBetweenSquares;

    private int mCenterX, mCenterY;
    private int blinkAlpfa = 90;

    private Paint mBackgroundPaint;
    private Paint mInnerSquarePaint;
    private RectF mRectF;
    private Path mPath;

    private int leftTopX, leftTopY;
    private int rightTopX, rightTopY;
    private int rightBottomX, rightBottomY;
    private int leftBottomX, leftBottomY;

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
        startAnimation();
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

        leftTopX = mCenterX - distanceBetweenSquares / 2;
        leftTopY = mCenterY - distanceBetweenSquares / 2;
        rightTopX = mCenterX + distanceBetweenSquares / 2;
        rightTopY = mCenterY - distanceBetweenSquares / 2;
        rightBottomX = mCenterX + distanceBetweenSquares / 2;
        rightBottomY = mCenterY + distanceBetweenSquares / 2;
        leftBottomX = mCenterX - distanceBetweenSquares / 2;
        leftBottomY = mCenterY + distanceBetweenSquares / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("myLog", "mCenterX = " + mCenterX);
        Log.d("myLog", "mCenterY = " + mCenterY);

        mBackgroundPaint.setColor(backgroundFiguresColor);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        //left top square
        mRectF.set(leftTopX, leftTopY, leftTopX + lengthSquareSide, leftTopY + lengthSquareSide);
        canvas.drawRect(mRectF, mBackgroundPaint);
        //right top square
        mRectF.set(rightTopX, rightTopY, rightTopX + lengthSquareSide, rightTopY + lengthSquareSide);
        canvas.drawRect(mRectF, mBackgroundPaint);
        // right bottom square
        mRectF.set(rightBottomX, rightBottomY, rightBottomX + lengthSquareSide, rightBottomY + lengthSquareSide);
        canvas.drawRect(mRectF, mBackgroundPaint);
        // left bottom square
        mRectF.set(leftBottomX, leftBottomY, leftBottomX + lengthSquareSide, leftBottomY + lengthSquareSide);
        canvas.drawRect(mRectF, mBackgroundPaint);

        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setStrokeWidth(lengthSquareSide / 10);
        mPath.reset();
        mPath.moveTo(leftBottomX + lengthSquareSide / 2, leftBottomY + lengthSquareSide / 2); //move to left bottom square
        mPath.lineTo(rightBottomX + lengthSquareSide / 2, rightBottomY + lengthSquareSide / 2); //line to right bottom square
        mPath.lineTo(rightTopX + lengthSquareSide / 2, rightTopY + lengthSquareSide / 2); //line to right top square
        mPath.lineTo(leftTopX + lengthSquareSide / 2, leftTopY + lengthSquareSide / 2); //line to left top square
        mPath.lineTo(leftBottomX + lengthSquareSide / 2, leftBottomY + lengthSquareSide / 2); //line to left bottom square
        mPath.lineTo(rightTopX + lengthSquareSide / 2, rightTopY + lengthSquareSide / 2); //line to right top square
        mPath.moveTo(leftTopX + lengthSquareSide / 2, leftTopY + lengthSquareSide / 2); //move to left top square
        mPath.lineTo(rightBottomX + lengthSquareSide / 2, rightBottomY + lengthSquareSide / 2); //line to right bottom square
        canvas.drawPath(mPath, mBackgroundPaint);
        mPath.reset();

        mInnerSquarePaint.setColor(backgroundFiguresColor);
        mInnerSquarePaint.setStyle(Paint.Style.FILL);
        mInnerSquarePaint.setAlpha(blinkAlpfa);
        mRectF.set(mCenterX - distanceBetweenSquares / 4, mCenterY - distanceBetweenSquares / 4,
                mCenterX + distanceBetweenSquares / 4 + lengthSquareSide, mCenterY + distanceBetweenSquares / 4 + lengthSquareSide);
        canvas.drawRect(mRectF, mInnerSquarePaint);


    }



    private void startAnimation() {
        ValueAnimator blinkBigSquareAnimator = getBlinkBigSquareAnimator();
        ValueAnimator moveRightBottomYAnimator = getMovedRightBottomYAnimator();
        ValueAnimator moveRightTopXAnimator = getMovedRightTopXAnimator();
        ValueAnimator moveLeftTopYAnimator = getMovedLeftTopYAnimator();
        ValueAnimator moveLeftBottomXAnimator = getMovedLeftBottomXAnimator();

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(blinkBigSquareAnimator, moveRightBottomYAnimator,
                moveLeftBottomXAnimator, moveLeftTopYAnimator, moveRightTopXAnimator);

        if (!animatorSet.isStarted()) {
            animatorSet.start();
        }
    }

    private ValueAnimator getMovedRightBottomYAnimator() {
        ValueAnimator moveRightBottomYAnimator = ValueAnimator.ofInt(0, distanceBetweenSquares / 2);
        moveRightBottomYAnimator.setDuration(500);
        moveRightBottomYAnimator.setRepeatMode(ValueAnimator.RESTART);
        moveRightBottomYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                rightBottomY = mCenterY + distanceBetweenSquares / 2 - (int) animator.getAnimatedValue();
                invalidate();
                Log.d("myLog", "(int) animator.getAnimatedValue() = " + (int) animator.getAnimatedValue());
            }
        });
        return moveRightBottomYAnimator;
    }

    private ValueAnimator getMovedRightTopXAnimator() {
        ValueAnimator moveRightTopXAnimator = ValueAnimator.ofInt(0, distanceBetweenSquares / 2);
        moveRightTopXAnimator.setDuration(500);
        moveRightTopXAnimator.setRepeatMode(ValueAnimator.RESTART);
        moveRightTopXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                rightTopX = mCenterX + distanceBetweenSquares / 2 - (int) animator.getAnimatedValue();
                invalidate();
                Log.d("myLog", "(int) animator.getAnimatedValue() = " + (int) animator.getAnimatedValue());
            }
        });
        return moveRightTopXAnimator;
    }

    private ValueAnimator getMovedLeftTopYAnimator() {
        ValueAnimator moveLeftTopYAnimator = ValueAnimator.ofInt(0, distanceBetweenSquares / 2);
        moveLeftTopYAnimator.setDuration(500);
        moveLeftTopYAnimator.setRepeatMode(ValueAnimator.RESTART);
        moveLeftTopYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                leftTopY = mCenterY - distanceBetweenSquares / 2 + (int) animator.getAnimatedValue();
                invalidate();
                Log.d("myLog", "(int) animator.getAnimatedValue() = " + (int) animator.getAnimatedValue());
            }
        });
        return moveLeftTopYAnimator;
    }

    private ValueAnimator getMovedLeftBottomXAnimator() {
        ValueAnimator moveLeftBottomXAnimator = ValueAnimator.ofInt(0, distanceBetweenSquares / 2);
        moveLeftBottomXAnimator.setDuration(500);
        moveLeftBottomXAnimator.setRepeatMode(ValueAnimator.RESTART);
        moveLeftBottomXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                leftBottomX = mCenterX - distanceBetweenSquares / 2 + (int) animator.getAnimatedValue();
                invalidate();
                Log.d("myLog", "(int) animator.getAnimatedValue() = " + (int) animator.getAnimatedValue());
            }
        });
        return moveLeftBottomXAnimator;
    }

    private ValueAnimator getBlinkBigSquareAnimator() {
        ValueAnimator blinkAnimator = ValueAnimator.ofInt(90, 0);
        blinkAnimator.setDuration(1000);
        blinkAnimator.setRepeatCount(2);
        blinkAnimator.setRepeatMode(ValueAnimator.RESTART);
        blinkAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                blinkAlpfa = (int) animator.getAnimatedValue();
                invalidate();
            }
        });
        return blinkAnimator;
    }
}
