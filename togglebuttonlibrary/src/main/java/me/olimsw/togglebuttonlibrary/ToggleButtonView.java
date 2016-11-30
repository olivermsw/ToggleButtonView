package me.olimsw.togglebuttonlibrary;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by musiwen on 2016/11/30.
 */

public class ToggleButtonView extends View implements View.OnClickListener {
    public static final int TOGGLEBUTTON_STATE_STOP = 0;
    public static final int TOGGLEBUTTON_STATE_PLAYING = 1;

    private final int DEFAULT_EDGE_COLOR = Color.GRAY;
    private final int DEFAULT_ON_COLOR = Color.GREEN;
    private final int DEFAULT_OFF_COLOR = Color.WHITE;
    private final int DEFAULT_BTN_COLOR = Color.WHITE;
    private final int DEFAULT_EDGE_WIDTH = 2;
    private int edgeColor;
    private int onColor;
    private int offColor;
    private int btnColor;
    private boolean isOn;
    private int edgeWidth;
    private int radius;
    private ValueAnimator animator;
    private ValueAnimator bgAnimator;

    private Paint fillPaint;
    private float animatedValue;
    private float bgAnimatedValue;
    private int state;


    private int startX;
    private int endX;
    private int circleY;
    private int center_distance_between;

    public ToggleButtonView(Context context) {
        super(context);
        init(null);
    }

    public ToggleButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }


    public ToggleButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ToggleButtonView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    /**
     * 数据初始化
     *
     * @param attrs 属性集合{@link AttributeSet}
     */
    private void init(AttributeSet attrs) {
        if (null == attrs) {
            edgeWidth = dp2px(DEFAULT_EDGE_WIDTH);
            edgeColor = DEFAULT_EDGE_COLOR;
            offColor = DEFAULT_OFF_COLOR;
            onColor = DEFAULT_ON_COLOR;
            btnColor = DEFAULT_BTN_COLOR;
            isOn = false;
        } else {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ToggleButtonView);
            edgeWidth = (int) typedArray.getDimension(R.styleable.ToggleButtonView_tbv_edgeWidth, dp2px(DEFAULT_EDGE_WIDTH));
            edgeColor = typedArray.getColor(R.styleable.ToggleButtonView_tbv_edgeColor, DEFAULT_EDGE_COLOR);
            onColor = typedArray.getColor(R.styleable.ToggleButtonView_tbv_onColor, DEFAULT_ON_COLOR);
            offColor = typedArray.getColor(R.styleable.ToggleButtonView_tbv_offColor, DEFAULT_OFF_COLOR);
            btnColor = typedArray.getColor(R.styleable.ToggleButtonView_tbv_btnColor, DEFAULT_BTN_COLOR);
            isOn = typedArray.getBoolean(R.styleable.ToggleButtonView_tbv_defaultToggle, false);
            typedArray.recycle();
        }
        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setAntiAlias(true);
        state = TOGGLEBUTTON_STATE_STOP;
        setOnClickListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width;
        int height;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int desiredWidth = dp2px(50);
        int desiredHeight = dp2px(30);

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }
        radius = (height - 2 * edgeWidth) / 2;
        startX = edgeWidth + radius;
        endX = width - edgeWidth - radius;
        center_distance_between = width - 2 * radius - 2 * edgeWidth;
        circleY = height / 2;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawOffRectCirle(canvas);
        drawOnRectCirle(canvas);
        drawToggleCircle(canvas);
    }

    /**
     * 画关闭按钮
     *
     * @param canvas 画布{@link Canvas}
     */
    private void drawOnRectCirle(Canvas canvas) {
        int smallRadius;
        if (isOn) {
            smallRadius= (int) (radius*(1-bgAnimatedValue));
        } else {
            smallRadius= (int) (radius*(1-bgAnimatedValue));


        }
        drawRectCircle(canvas, startX, endX, edgeWidth + radius, edgeColor);
        drawRectCircle(canvas, startX, endX, smallRadius, offColor);
    }

    /**
     * 画开启按钮
     *
     * @param canvas 画布{@link Canvas}
     */
    private void drawOffRectCirle(Canvas canvas) {
    }

    /**
     * 画两运动的按钮
     *
     * @param canvas 画布{@link Canvas}
     */
    private void drawToggleCircle(Canvas canvas) {
        int relationX;
        if (isOn) {
            relationX = (int) (startX + (1 - animatedValue) * center_distance_between);
        } else {
            relationX = (int) (startX + animatedValue * center_distance_between);
        }
        fillPaint.setColor(edgeColor);
        canvas.drawCircle(relationX, circleY, radius + 1, fillPaint);
        fillPaint.setColor(btnColor);
        canvas.drawCircle(relationX, circleY, radius, fillPaint);
    }

    /**
     * 画两个圆和矩形
     *
     * @param canvas 画布{@link Canvas}
     * @param startX 第一圆圆心x坐标
     * @param endX   第二圆圆心x坐标
     * @param radius 圆半径
     * @param color  画笔颜色
     */
    private void drawRectCircle(Canvas canvas, int startX, int endX, int radius, int color) {
        fillPaint.setColor(color);
        canvas.drawCircle(startX, circleY, radius, fillPaint);
        canvas.drawCircle(endX, circleY, radius, fillPaint);
        canvas.drawRect(startX, circleY - radius, endX, circleY + radius, fillPaint);
    }


    @Override
    public void onClick(View view) {
        if (state == TOGGLEBUTTON_STATE_STOP) {
            bgAnimator = ValueAnimator.ofFloat(0, 1f);
            bgAnimator.setDuration(800);
            bgAnimator.setInterpolator(new AccelerateInterpolator());
            bgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    bgAnimatedValue = (float) valueAnimator.getAnimatedValue();
                }
            });
            animator = ValueAnimator.ofFloat(0, 1f);
            animator.setDuration(800);
            animator.setInterpolator(new BackOutInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    animatedValue = (float) valueAnimator.getAnimatedValue();
                    invalidate();
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    state = TOGGLEBUTTON_STATE_PLAYING;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    state = TOGGLEBUTTON_STATE_STOP;
                    isOn = isOn ? false : true;
                    animatedValue = 0;
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    state = TOGGLEBUTTON_STATE_STOP;
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                    state = TOGGLEBUTTON_STATE_PLAYING;
                }
            });
            animator.start();
            bgAnimator.start();
        }
    }

    /**
     * dp转px
     *
     * @param dpValue dp值
     * @return px值
     */
    public int dp2px(float dpValue) {
        final float scale = this.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
