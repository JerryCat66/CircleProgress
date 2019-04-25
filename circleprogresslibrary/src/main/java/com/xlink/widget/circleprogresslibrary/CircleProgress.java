package com.xlink.widget.circleprogresslibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * File Header
 * 空调面板温度调节控件
 *
 * @author sswukang on 2018/12/5 15:07
 */

public class CircleProgress extends View {
    /**
     * 进度条所占用的角度     
     */
    private static final int ARC_FULL_DEGREE = 300;
    /**
     * 弧线的宽度   
     */
    private int STROKE_WIDTH;
    /**
     * 组件的宽，高 
     */
    private int width, height;
    /**
     * 进度条最小值最大值和当前进度值   
     */
    private float min, max, progress;
    /**
     *  是否允许拖动进度条   
     */
    private boolean draggingEnabled = false;
    /**
     *   绘制弧线的矩形区域   
     */
    private RectF circleRectF;
    /**
     *   绘制弧线的画笔     
     */
    private Paint progressPaint;
    /**
     *   绘制文字的画笔摄氏度   
     */
    private Paint textPaint;
    /**
     * 绘制底部文字
     */
    private Paint bottomTextPaint;
    /**
     * 绘制内部圆形
     */
    private Paint innerCirclePaint;
    /**
     * 绘制外部圆形
     */
    private Paint outerCirclePaint;
    /**
     * 绘制当前进度值的画笔
     */
    private Paint thumbPaint;
    /**
     * 圆弧的半径   
     */
    private int circleRadius;
    /**
     * 圆弧圆心位置     
     */
    private int centerX, centerY;
    /**
     * 刻度画笔
     */
    private Paint scalePaint;

    /**
     * 弧线细线条的长度
     */
    private int ARC_LINE_LENGTH;
    /**
     * 弧线细线条的宽度
     */
    private int ARC_LINE_WIDTH;
    /**
     * 室内温度
     */
    private String roomTemp;
    /**
     * 是否打开，默认关闭
     */
    private boolean powerOn = false;

    private static final int scaleCount = 32;//绘制32个刻度

    private progressListener mListener;

    //每个进度条所占用角度
    private static final float ARC_EACH_PROGRESS = ARC_FULL_DEGREE * 1.0f / (scaleCount - 1);

    public CircleProgress(Context context) {
        super(context);
        init();
    }


    public CircleProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setOnProgressListener(progressListener progressListener) {
        this.mListener = progressListener;
    }

    private void init() {
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);

        bottomTextPaint = new Paint();
        bottomTextPaint.setAntiAlias(true);

        thumbPaint = new Paint();
        thumbPaint.setAntiAlias(true);

        innerCirclePaint = new Paint();
        innerCirclePaint.setAntiAlias(true);
        innerCirclePaint.setColor(Color.WHITE);

        outerCirclePaint = new Paint();
        outerCirclePaint.setAntiAlias(true);

        scalePaint = new Paint();
        scalePaint.setAntiAlias(true);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (width == 0 || height == 0) {
            width = getWidth();
            height = getHeight();

            //计算圆弧半径和圆心点
            circleRadius = Math.min(width, height) / 2;
            STROKE_WIDTH = circleRadius / 12;
            circleRadius -= STROKE_WIDTH;

            ARC_LINE_LENGTH = circleRadius / 10;
            ARC_LINE_WIDTH = ARC_LINE_LENGTH / 12;

            centerX = width / 2;
            centerY = height / 2;


            //圆弧所在矩形区域
            circleRectF = new RectF();
            circleRectF.left = centerX - circleRadius;
            circleRectF.top = centerY - circleRadius;
            circleRectF.right = centerX + circleRadius;
            circleRectF.bottom = centerY + circleRadius;
        }
    }


    private Rect textBounds = new Rect();


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float start = 90 + ((360 - ARC_FULL_DEGREE) >> 1); //进度条起始点
        float sweep1 = ARC_FULL_DEGREE * ((progress - min) / (max - min)); //进度划过的角度
        float sweep2 = ARC_FULL_DEGREE - sweep1; //剩余的角度


        //绘制起始位置小圆形
        progressPaint.setColor(powerOn ? Color.parseColor("#4674F6") : Color.parseColor("#F4F9FF"));
        progressPaint.setStrokeWidth(0);
        progressPaint.setStyle(Paint.Style.FILL);
        float radians = (float) (((360.0f - ARC_FULL_DEGREE) / 2) / 180 * Math.PI);
        float startX = centerX - circleRadius * (float) Math.sin(radians);
        float startY = centerY + circleRadius * (float) Math.cos(radians);
        canvas.drawCircle(startX, startY, STROKE_WIDTH / 2, progressPaint);


        //绘制进度条
        progressPaint.setStrokeWidth(STROKE_WIDTH);
        progressPaint.setStyle(Paint.Style.STROKE);//设置空心
        progressPaint.setColor(powerOn ? Color.parseColor("#4674F6") : Color.parseColor("#F4F9FF"));
        canvas.drawArc(circleRectF, start, sweep1, false, progressPaint);
        //绘制进度条背景
        progressPaint.setColor(Color.parseColor("#F4F9FF"));
        canvas.drawArc(circleRectF, start + sweep1, sweep2, false, progressPaint);

        // 绘制内部圆
        canvas.drawCircle(centerX, centerY, circleRadius - 15, innerCirclePaint);


        //绘制结束位置小圆形
       /* if (progress == max && powerOn) {
            progressPaint.setColor(Color.parseColor("#4674F6"));
        } else {
            progressPaint.setColor(Color.parseColor("#F4F9FF"));
        }*/
        progressPaint.setStrokeWidth(0);
        progressPaint.setStyle(Paint.Style.FILL);
        float endX = centerX + circleRadius * (float) Math.sin(radians);
        float endY = centerY + circleRadius * (float) Math.cos(radians);
        canvas.drawCircle(endX, endY, STROKE_WIDTH / 2, progressPaint);

        //上一行文字
        textPaint.setTextSize(circleRadius >> 1);
        String text = (int) (30 * progress / max) + "";
        float textLen = textPaint.measureText(text);
        //计算文字高度
        textPaint.getTextBounds("8", 0, 1, textBounds);
        float h1 = textBounds.height();

        //℃ 前面的数字水平居中，适当调整
        float extra = text.startsWith("1") ? -textPaint.measureText("1") / 2 : 0;
        canvas.drawText(text, centerX - textLen / 2 + extra, centerY - 30 + h1 / 2, textPaint);


        //摄氏度
        textPaint.setTextSize(circleRadius >> 2);
        canvas.drawText("℃", centerX + textLen / 2 + extra + 5, centerY - 30 + h1 / 2, textPaint);


        //下一行文字
        bottomTextPaint.setTextSize(circleRadius / 7);
        text = "室温" + roomTemp + "℃";
        textLen = bottomTextPaint.measureText(text);
        bottomTextPaint.setColor(Color.parseColor("#BABABA"));
        bottomTextPaint.getTextBounds(text, 0, text.length(), textBounds);
        float h2 = textBounds.height();
        canvas.drawText(text, centerX - textLen / 2, centerY + h1 / 2 + centerY / 2, bottomTextPaint);

        if (powerOn) {
            //绘制进度位置，也可以直接替换成一张图片
            float progressRadians = (float) (((360.0f - ARC_FULL_DEGREE) / 2 + sweep1) / 180 * Math.PI);
            float thumbX = centerX - circleRadius * (float) Math.sin(progressRadians);
            float thumbY = centerY + circleRadius * (float) Math.cos(progressRadians);
            thumbPaint.setColor(Color.parseColor("#4674F6"));
            canvas.drawCircle(thumbX, thumbY, STROKE_WIDTH * 1.0f, thumbPaint);
            thumbPaint.setColor(Color.WHITE);
            canvas.drawCircle(thumbX, thumbY, STROKE_WIDTH * 0.6f, thumbPaint);
        }

        drawScale(canvas);
    }

    //画刻度
    private void drawScale(Canvas canvas) {
        float start = (360 - ARC_FULL_DEGREE) >> 1; //进度条起始角度
        float sweep1 = ARC_FULL_DEGREE * (progress / max); //进度划过的角度

        float drawDegree = 1.6f;
        while (drawDegree <= ARC_FULL_DEGREE) {
            double a = (start + drawDegree) / 180 * Math.PI;
            float lineStartX = centerX - (circleRadius - 30) * (float) Math.sin(a);
            float lineStartY = centerY + (circleRadius - 30) * (float) Math.cos(a);
            float lineStopX = lineStartX + ARC_LINE_LENGTH * (float) Math.sin(a);
            float lineStopY = lineStartY - ARC_LINE_LENGTH * (float) Math.cos(a);


            if (drawDegree > sweep1) {
                //绘制进度条背景
                scalePaint.setColor(Color.parseColor("#E4E4E4"));
                scalePaint.setStrokeWidth(ARC_LINE_WIDTH >> 1);
            }
            canvas.drawLine(lineStartX, lineStartY, lineStopX, lineStopY, scalePaint);


            drawDegree += ARC_EACH_PROGRESS;
        }


    }

    private boolean isDragging = false;

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (!draggingEnabled) {
            return super.onTouchEvent(event);
        }
        //处理拖动事件
        float currentX = event.getX();
        float currentY = event.getY();
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN://手指压下去的时候
                //判断是否在进度条thumb位置
                if (checkOnArc(currentX, currentY)) {
                    float newProgress = calDegreeByPosition(currentX, currentY) / ARC_FULL_DEGREE * (max - min);
                    setProgressSync(newProgress);
                    isDragging = true;
                }
                break;
            case MotionEvent.ACTION_MOVE://手指移动的时候
                if (isDragging) {
                    //判断拖动时是否移出去了
                    if (checkOnArc(currentX, currentY)) {
                        setProgressSync(calDegreeByPosition(currentX, currentY) / ARC_FULL_DEGREE * (max - min));
                    } else {
                        isDragging = false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP://手指抬起来的时候将进度返回给用户
                if (mListener != null && checkOnArc(currentX, currentY)) {
                    float newProgress = calDegreeByPosition(currentX, currentY) / ARC_FULL_DEGREE * (max - min);
                    float customerProgress = checkProgress(newProgress);
                    mListener.getProgress(customerProgress);
                }
                isDragging = false;
                break;
        }
        return true;
    }


    private float calDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    /**
     * 判断该点是否在弧线上（附近）   
     */
    private boolean checkOnArc(float currentX, float currentY) {
        float distance = calDistance(currentX, currentY, centerX, centerY);
        float degree = calDegreeByPosition(currentX, currentY);

        return distance > circleRadius - STROKE_WIDTH * 4 && distance < circleRadius + STROKE_WIDTH * 4
                && (degree >= -8 && degree <= ARC_FULL_DEGREE + 10);
    }


    /**
     *   根据当前位置，计算出进度条已经转过的角度。     
     */
    private float calDegreeByPosition(float currentX, float currentY) {
        float a1 = (float) (Math.atan(1.0f * (centerX - currentX) / (currentY - centerY)) / Math.PI * 180);
        if (currentY < centerY) {
            a1 += 180;
        } else if (currentY > centerY && currentX > centerX) {
            a1 += 360;
        }
        return a1 - (360 - ARC_FULL_DEGREE) / 2;
    }

    /**
     * 设置进度条的最大值
     *
     * @param max 进度条最大值
     */
    public void setMax(int max) {
        this.max = max;
        invalidate();
    }

    /**
     * 设置进度条的最小值
     *
     * @param min
     */
    public void setMin(int min) {
        this.min = min;
        invalidate();
    }

    /**
     * 设置当前进度
     *
     * @param progress 当前进度
     */
    public void setProgress(final float progress) {
        //切换进度值
        new Thread(new Runnable() {
            @Override
            public void run() {
                CircleProgress.this.progress = checkProgress(progress);
                postInvalidate();
            }

        }).start();
    }

    public void setProgressSync(float progress) {
        this.progress = checkProgress(progress);
        invalidate();
    }

    /**
     * 温度增加
     */
    public void addTemp() {
        if (progress < max) {
            progress += 1;
            mListener.getProgress(progress);
            invalidate();
        }
    }

    /**
     * 温度减少
     */
    public void reduceTemp() {
        if (progress >= (min + 1)) {
            progress -= 1;
            mListener.getProgress(progress);
            invalidate();
        }
    }

    /**
     * 设置室内温度
     */
    public void setRoomTemp(String temp) {
        this.roomTemp = temp;
        invalidate();
    }

    /**
     * 设置开关
     *
     * @param onOff
     */
    public void setPowerSwitch(boolean onOff) {
        if (this.powerOn != onOff) {
            this.powerOn = onOff;
            invalidate();
        }
        setDraggingEnabled(onOff);
    }

    //保证progress的值位于[min,max]
    private float checkProgress(float targetProgress) {
        float myProgress = (float) Math.floor(targetProgress);
        if (myProgress < 0) myProgress = 0;
        if (myProgress < min) {
            float mProgress = myProgress + min;
            return mProgress > max ? max : mProgress;
        }
        return myProgress > max ? max : myProgress;
    }


    public void setDraggingEnabled(boolean draggingEnabled) {
        this.draggingEnabled = draggingEnabled;
    }

    public interface progressListener {
        void getProgress(float progress);
    }
}
