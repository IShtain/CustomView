package com.shtainyky.customview.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.shtainyky.customview.R;

public class CustomCircleMenu extends View {

    private int backgroundColor;
    private int colorMainBorder;
    private int colorChosenSector;
    private int circleRadius;
    private int numberOfSectors;
    private float widthMainBorder;

    private float mCenterX, mCenterY;
    private Paint mBackgroundPaint;
    private Path mPath;
    private Matrix mMatrix;
    private RectF mRectF;

    public CustomCircleMenu(Context context) {
        super(context);
        init();
    }

    public CustomCircleMenu(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        init();


    }

    public CustomCircleMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        init();
    }

    private void init(Context context, @Nullable AttributeSet attrs){
        TypedArray attributes = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.CustomCircleMenu, 0, 0);
        try {
            backgroundColor = attributes.getColor(R.styleable.CustomCircleMenu_backgroundColor, 0);
            colorMainBorder = attributes.getColor(R.styleable.CustomCircleMenu_colorMainBorder, 0);
            colorChosenSector = attributes.getColor(R.styleable.CustomCircleMenu_colorChosenSector, 0);
            circleRadius = attributes.getInteger(R.styleable.CustomCircleMenu_circleRadius, 0);
            widthMainBorder = attributes.getFloat(R.styleable.CustomCircleMenu_widthMainBorder, 0);
            numberOfSectors = attributes.getInteger(R.styleable.CustomCircleMenu_numberOfSectors, 0);

        } finally {
            attributes.recycle();
        }
    }


    private void init(){
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPath = new Path();
        mMatrix = new Matrix();
        mRectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mCenterX = getWidth()/2;
        mCenterY = circleRadius + getPaddingTop()+ widthMainBorder;
        //fill background
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(backgroundColor);
        canvas.drawCircle(mCenterX, mCenterY, circleRadius, mBackgroundPaint);

        //draw sector's line
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setStrokeWidth(widthMainBorder/2);
        mBackgroundPaint.setColor(colorMainBorder);
        mPath.reset();
        mPath.moveTo(mCenterX, mCenterY);
        mPath.lineTo(mCenterX - circleRadius, mCenterY);
        if (numberOfSectors == 0)
            throw new IllegalArgumentException("Amount of sectors can not be equal zero");
        int angle = 360/numberOfSectors;
        mMatrix.setTranslate(mCenterX, mCenterY);
        for (int i = 0; i < numberOfSectors; i++) {
            mMatrix.setRotate(angle, mCenterX, mCenterY);
            mPath.transform(mMatrix);
            canvas.drawPath(mPath, mBackgroundPaint);
            mMatrix.reset();
        }
        //fill sector
        mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBackgroundPaint.setColor(colorChosenSector);
        mRectF.set(mCenterX - circleRadius, mCenterY - circleRadius,
                mCenterX + circleRadius, mCenterY+circleRadius);
        canvas.drawArc(mRectF, 180, angle, true, mBackgroundPaint);
        //draw middle circle
        mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBackgroundPaint.setColor(Color.RED);
        canvas.drawCircle(mCenterX, mCenterY, circleRadius/6, mBackgroundPaint);
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setColor(colorChosenSector);
        canvas.drawCircle(mCenterX, mCenterY, circleRadius/6, mBackgroundPaint);
        //draw stroke
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setStrokeWidth(widthMainBorder);
        mBackgroundPaint.setColor(colorMainBorder);
        canvas.drawCircle(mCenterX, mCenterY, circleRadius, mBackgroundPaint);
    }
}
