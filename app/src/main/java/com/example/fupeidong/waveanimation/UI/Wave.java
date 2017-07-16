package com.example.fupeidong.waveanimation.UI;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.fupeidong.waveanimation.R;

/**
 * Created by fupeidong on 2017/7/16.
 */

public class Wave extends View {

    Context mContext;

    int mColor;
    String mText;

    Paint mPaint;
    Paint mTxtPanit;

    Path mPath;

    int mWidth;
    int mHeight;
    float currentPercent;
    int currentColor;

    ValueAnimator mAnimator;
    ValueAnimator mColorAnimator;
    public Wave(Context context) {
        this(context, null);
    }

    public Wave(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Wave(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.mContext = context;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Wave);
        mColor = array.getColor(R.styleable.Wave_color, Color.rgb(41, 163, 254));
        mText = array.getString(R.styleable.Wave_text);
        array.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mColor);
        mPaint.setDither(true);

        mTxtPanit = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTxtPanit.setColor(Color.WHITE);
        mTxtPanit.setTypeface(Typeface.DEFAULT_BOLD);

        mPath = new Path();
        initAnima();
        initColorAnima();
    }

    private void initAnima() {
        mAnimator = ValueAnimator.ofFloat(0,1);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentPercent = animation.getAnimatedFraction();
                postInvalidate();
            }
        });
        mAnimator.setDuration(1000);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setInterpolator(new LinearInterpolator());
    }

    private void initColorAnima() {
        if (Build.VERSION.SDK_INT >= 21) {
            mColorAnimator = ValueAnimator.ofArgb(0xFFEE3423, 0xFF149862);
            mColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    currentColor = (int) animation.getAnimatedValue();
                }
            });
            mColorAnimator.setDuration(1000);
            mColorAnimator.setRepeatMode(ValueAnimator.REVERSE);
            mColorAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mColorAnimator.setInterpolator(new LinearInterpolator());
        }
    }

    private Path getWavePath(float percent) {
        Path path = new Path();

        int x = -mWidth;
        x += percent * mWidth;

        path.moveTo(x, mHeight / 2);
        int quadWidth = mWidth / 4;
        int quadHeight = mHeight / 20 * 3;

        path.rQuadTo(quadWidth, quadHeight, quadWidth * 2, 0);
        path.rQuadTo(quadWidth, -quadHeight, quadWidth * 2, 0);

        path.rQuadTo(quadWidth, quadHeight, quadWidth * 2, 0);
        path.rQuadTo(quadWidth, -quadHeight, quadWidth * 2, 0);

        path.lineTo(x + mWidth * 2, mHeight);
        path.lineTo(x, mHeight);
        path.close();
        return  path;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mWidth = getWidth();
        mHeight = getHeight();

        mTxtPanit.setColor(currentColor);
        mTxtPanit.setTextSize(mWidth/2);
        drawCenterText(canvas, mTxtPanit, mText);

        mTxtPanit.setColor(Color.WHITE);
        canvas.save(Canvas.CLIP_SAVE_FLAG);

        Path o = new Path();
        o.addCircle(mWidth/2, mHeight/2, mWidth/2, Path.Direction.CCW);
        canvas.clipPath(o);

        mPath = getWavePath(currentPercent);
        mPaint.setColor(currentColor);
        canvas.drawPath(mPath, mPaint);
        canvas.clipPath(mPath);
        drawCenterText(canvas, mTxtPanit, mText);
        canvas.restore();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAnimator != null) {
            mAnimator.start();
        }
        if (mColorAnimator != null) {
            mColorAnimator.start();
        }
    }

    private void drawCenterText(Canvas canvas, Paint paint, String text) {
        Rect rect = new Rect(0, 0, mWidth, mHeight);
        paint.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;

        int centerY = (int) (rect.centerY() - top/2 - bottom/2);
        canvas.drawText(text, rect.centerX(), centerY, paint);
    }
}
