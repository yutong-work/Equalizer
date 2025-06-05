package com.yscoco.equalizer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 优化版自定义EQ均衡器View，结构更清晰，功能和样式保持不变
 */
public class EqualizerView extends View {
    private int mWidth, mHeight;
    private int mMinHeight = 100;
    private float circleTextSize;
    private int circleTextColor;
    private float circleRadius;
    private float xTextSize;
    private int xSelectColor, xUnSelectColor;
    private int marginLR;
    private int mDbSize;
    private int xAxialStep;
    private Paint mPaint;
    private int[] yAxialVal = new int[]{6, 0, -6};
    private int[] xAxialVal = new int[]{32, 62, 125, 250, 500, 1000, 2000, 4000, 8000, 16000};
    private ArrayList<PointBean> mPoints = new ArrayList<>();
    private int currentSelectBarIndex = -1;
    private int maxDbBarNum = 0;
    private Map<Integer, Float> dBAndHzMap = new HashMap<>();
    private float startY;
    private float touchRange;
    private float currentDb = 0;
    private Path mBezierPath;
    private boolean isMove = true;
    private Paint dashLinePaint;
    private OnEqualizerValueChange mOnEqualizerValueChange;
    private int gradientTopColor = 0x80000000; // 默认80%黑
    private int gradientBottomColor = 0x00000000; // 默认全透明
    private boolean showGradient = true;
    private int yMax = 6;
    private int yMin = -6;
    private int dbOffset = 0; // 默认不需要减
    private int lineColor = Color.BLACK; // 线颜色，默认黑色
    private int ballColor = Color.BLACK; // 球颜色，默认黑色
    private int inBallColor = Color.WHITE; // 球颜色，默认黑色
    private float lineWidth = 5f; // 线宽度，默认5f
    private float ballRadius = 5f; // 球半径，默认20f
    private boolean useBezier = true; // 默认使用贝塞尔曲线
    private boolean showBackgroundLine = true; // 是否显示背景水平线
    private boolean showInBall = true; // 是否显示内球
    private int topLineColor = Color.GRAY; // 顶部线颜色，默认灰色
    private int bottomLineColor = Color.GRAY; // 底部线颜色，默认灰色
    private float topLineWidth = 0.5f; // 顶部线宽度，默认0.5f
    private float bottomLineWidth = 0.5f; // 底部线宽度，默认0.5f
    private boolean topLineRound = false; // 顶部线是否显示圆角
    private boolean bottomLineRound = false; // 底部线是否显示圆角
    private boolean showConnectionLine = true; // 是否显示球之间的连接线
    private boolean useFloatValue = false; // 是否使用浮点数滑动
    private int pointShape = 0; // 0:圆形 1:方形
    private float pointWidth = 0; // 方形宽度
    private float pointHeight = 0; // 方形高度
    private boolean pointRound = false; // 方形是否圆角
    private float pointRoundRadius = 0; // 方形圆角半径
    private boolean useScale = false; // 新增属性，控制是否使用drawScale

    public EqualizerView(Context context) {
        this(context, null);
    }

    public EqualizerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EqualizerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EqualizerView);
        circleTextSize = a.getDimension(R.styleable.EqualizerView_evCircleTextSize, dip2px(getContext(), 8));
        circleTextColor = a.getColor(R.styleable.EqualizerView_evCircleTextColor, Color.WHITE);
        circleRadius = a.getDimension(R.styleable.EqualizerView_svCircleRadius, 20);
        xTextSize = a.getDimension(R.styleable.EqualizerView_evXTextSize, dip2px(getContext(), 8));
        xSelectColor = a.getColor(R.styleable.EqualizerView_evXSelectColor, Color.parseColor("#1AEBEB"));
        xUnSelectColor = a.getColor(R.styleable.EqualizerView_evXUnSelectColor, Color.parseColor("#66EDEEF0"));
        marginLR = a.getInt(R.styleable.EqualizerView_evLRMargin, 50);
        gradientTopColor = a.getColor(R.styleable.EqualizerView_evGradientTopColor, 0x80000000);
        gradientBottomColor = a.getColor(R.styleable.EqualizerView_evGradientBottomColor, 0x00000000);
        showGradient = a.getBoolean(R.styleable.EqualizerView_evShowGradient, true);
        yMax = a.getInt(R.styleable.EqualizerView_evYMax, 6);
        yMin = a.getInt(R.styleable.EqualizerView_evYMin, -6);
        lineColor = a.getColor(R.styleable.EqualizerView_evLineColor, Color.BLACK);
        ballColor = a.getColor(R.styleable.EqualizerView_evBallColor, Color.BLACK);
        inBallColor = a.getColor(R.styleable.EqualizerView_evInBallColor, Color.WHITE);
        lineWidth = a.getDimension(R.styleable.EqualizerView_evLineWidth, dip2px(getContext(), 5));
        ballRadius = a.getDimension(R.styleable.EqualizerView_evBallRadius, dip2px(getContext(), 5));
        useBezier = a.getBoolean(R.styleable.EqualizerView_evUseBezier, true);
        showConnectionLine = a.getBoolean(R.styleable.EqualizerView_evShowConnectionLine, true);
        showBackgroundLine = a.getBoolean(R.styleable.EqualizerView_evShowBackgroundLine, true);
        showInBall = a.getBoolean(R.styleable.EqualizerView_evShowInBall, true);
        topLineColor = a.getColor(R.styleable.EqualizerView_evTopLineColor, Color.GRAY);
        bottomLineColor = a.getColor(R.styleable.EqualizerView_evBottomLineColor, Color.GRAY);
        topLineWidth = a.getDimension(R.styleable.EqualizerView_evTopLineWidth, dip2px(getContext(), 5));
        bottomLineWidth = a.getDimension(R.styleable.EqualizerView_evBottomLineWidth, dip2px(getContext(), 5));
        topLineRound = a.getBoolean(R.styleable.EqualizerView_evTopLineRound, false);
        bottomLineRound = a.getBoolean(R.styleable.EqualizerView_evBottomLineRound, false);
        useFloatValue = a.getBoolean(R.styleable.EqualizerView_evUseFloatValue, false);
        pointShape = a.getInt(R.styleable.EqualizerView_evPointShape, 0);
        pointWidth = a.getDimension(R.styleable.EqualizerView_evPointWidth, dip2px(getContext(), 10));
        pointHeight = a.getDimension(R.styleable.EqualizerView_evPointHeight, dip2px(getContext(), 10));
        pointRound = a.getBoolean(R.styleable.EqualizerView_evPointRound, false);
        pointRoundRadius = a.getDimension(R.styleable.EqualizerView_evPointRoundRadius, dip2px(getContext(), 1));
        useScale = a.getBoolean(R.styleable.EqualizerView_evUseScale, false);
        a.recycle();
        mPaint = new Paint();
        dashLinePaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        yAxialVal = new int[]{yMax, 0, yMin};
        initXY(xAxialVal, yAxialVal, new int[xAxialVal.length]);
        setFocusableInTouchMode(true);
        setFocusable(true);
        mBezierPath = new Path();
    }

    private void initXY(int[] xAxialVal, int[] yAxialVal, int[] defaultYVal) {
        if (xAxialVal.length != defaultYVal.length) {
            throw new IllegalArgumentException("Y坐标值个数不匹配X坐标个数");
        }
        int y = yAxialVal[0] - yAxialVal[yAxialVal.length - 1];
        maxDbBarNum = y + 1;
        for (int i = 0; i < xAxialVal.length; i++) {
            dBAndHzMap.put(i, (float) calculateDbBum(defaultYVal[i]));
        }
    }

    private int calculateDbBum(int dbValue) {
        dbValue = dbValue - dbOffset;
        int up = yAxialVal[0];
        int down = yAxialVal[yAxialVal.length - 1];
        int val;
        if (dbValue >= down && dbValue <= up) {
            val = dbValue - down;
        } else {
            val = 0;
        }
        return val;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int defaultWidth = dip2px(getContext(), 300);
        int yStep = 300;
        int yTop = 20 + marginLR;
        int yStart = (int) (circleRadius + yTop);
        int minus6Index = yAxialVal.length - 1;
        float minus6dBY = yStart + yStep * minus6Index;
        int contentHeight = (int) (minus6dBY + dip2px(getContext(), 20) + xTextSize + dip2px(getContext(), 16));

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = (widthMode == MeasureSpec.EXACTLY) ? widthSize : defaultWidth;
        int height = (heightMode == MeasureSpec.EXACTLY) ? heightSize : contentHeight;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();
        int size = xAxialVal.length + 1;
        mDbSize = mWidth / (size * 3);//dB块宽度
        xAxialStep = mWidth / size;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(xTextSize);
        mPaint.setStyle(Paint.Style.FILL);
        float dB = mPaint.measureText("dB");

        mPoints.clear();
        mBezierPath.reset();

        int yStep = 300;
        int xStart = 20 + marginLR;
        int yTop = 20 + marginLR;
        int yStart = (int) (circleRadius + yTop);
        int startY = yStart, lastY = 0;

        // 绘制Y轴标签
        for (int i = 0; i < yAxialVal.length; i++) {
            mPaint.setStyle(Paint.Style.STROKE);
            lastY = yStart;
            float v = mPaint.measureText(String.valueOf(yAxialVal[i]));
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(circleTextColor);
            mPaint.setTextSize(circleTextSize);
            mPaint.setColor(Color.GRAY);
            float labelX = marginLR / 2;
            float labelY = yStart + circleRadius;
            canvas.drawText(String.valueOf(yAxialVal[i]) + "dB", labelX, labelY, mPaint);
            yStart += yStep;
        }

        // 计算dB高度
        int dBTotalHeight = lastY - startY;
        int dBHeight = dBTotalHeight / (maxDbBarNum * 2 - 2);

        // 绘制X轴标签和点
        int xAxialStart = (int) (marginLR + 2 * circleRadius) + (xAxialStep / 2) - (mDbSize / 2);
        // 计算-6dB线的y坐标
        int minus6Index = yAxialVal.length - 1;
        float minus6dBY = (int) (circleRadius + yTop) + yStep * minus6Index;

        // 先收集所有点的位置
        for (int i = 0; i < xAxialVal.length; i++) {
            float value = dBAndHzMap.get(i);
            float v1 = xAxialStart;

            if (!showBackgroundLine) {
                if (useScale) {
                    drawScale(canvas, value, lastY, dBHeight, v1);
                } else {
                    canvasValue(canvas, value, lastY, dBHeight, v1);
                }
            }

            float bottom = lastY + dBHeight / 2 - 2 * value * dBHeight;
            mPoints.add(new PointBean((mDbSize / 2 + v1), bottom - (18 / 2)));

            // 频率大于等于1000时显示为K
            String freqLabel = xAxialVal[i] >= 1000 ? (xAxialVal[i] / 1000) + "k" : String.valueOf(xAxialVal[i]);
            float v = mPaint.measureText(freqLabel);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setTextSize(xTextSize);

            int offsetX;
            if (v / 2 > mDbSize / 2) {
                offsetX = (int) -Math.abs(v / 2 - mDbSize / 2);
            } else {
                offsetX = (int) Math.abs(v / 2 - mDbSize / 2);
            }
            mPaint.setColor(Color.GRAY);
            canvas.drawText(freqLabel, v1 + offsetX, minus6dBY + dip2px(getContext(), 20), mPaint);

            xAxialStart += xAxialStep;
        }

        // 绘制渐变背景
        if (showGradient) {
            canvasBackground(canvas);
        }

        // 绘制连接线
        if (showConnectionLine) {
            canvasLine(canvas);
        }

        // 最后绘制球和动态线
        canvasBall(canvas, lastY, dBHeight);
    }

    private void canvasValue(Canvas canvas, float value, int lastY, int dBHeight, float v1) {
        for (int j = 0; j < maxDbBarNum; j++) {
            mPaint.setColor(xUnSelectColor);
            int top = lastY + dBHeight / 2 - dBHeight - 2 * j * dBHeight;
            int bottom = lastY + dBHeight / 2 - 2 * j * dBHeight;

            if (j % 2 == 0) {
                canvas.drawCircle((v1 + (mDbSize + v1)) / 2, (float) (bottom + top) / 2, ballRadius, mPaint);
            }
        }
    }

    private void drawScale(Canvas canvas, float value, int lastY, int dBHeight, float v1) {
        for (int j = 0; j < maxDbBarNum; j++) {
            int top = lastY + dBHeight / 2 - dBHeight - 2 * j * dBHeight;
            int bottom = lastY + dBHeight / 2 - 2 * j * dBHeight;

            float centerX = (v1 + (mDbSize + v1)) / 2;
            float centerY = (float) (bottom + top) / 2;

            // 宽度6dp，高度2dp
            float rectWidth = dip2px(getContext(), 12);
            float rectHeight = dip2px(getContext(), 4);
            float left = centerX - rectWidth / 2;
            float top2 = centerY - rectHeight / 2;
            float right = centerX + rectWidth / 2;
            float bottom2 = centerY + rectHeight / 2;

            // 获取当前控制点的位置
            float controlPointY = lastY + dBHeight / 2 - 2 * value * dBHeight;

            // 根据控制点位置设置颜色：控制点下方使用#1AEBEB，控制点上方使用#BCEEF3
            if (centerY >= controlPointY) {
                mPaint.setColor(Color.parseColor("#1AEBEB"));
            } else {
                mPaint.setColor(Color.parseColor("#BCEEF3"));
            }
            canvas.drawRect(left, top2, right, bottom2, mPaint);
        }
    }

    private void canvasBall(Canvas canvas, int lastY, int dBHeight) {
        int xAxialStart;
        mPaint.setStyle(Paint.Style.FILL);
        xAxialStart = (int) (marginLR + 2 * circleRadius) + (xAxialStep / 2) - (mDbSize / 2);

        int yStep = 300;
        int yTop = 20 + marginLR;
        int yStart = (int) (circleRadius + yTop);
        float top6dBY = yStart;
        float bottom6dBY = yStart + yStep * 2;

        for (int i = 0; i < xAxialVal.length; i++) {
            float value = dBAndHzMap.get(i);
            float v1 = xAxialStart;

            float bottom = lastY + dBHeight / 2 - 2 * value * dBHeight;
            float ballX = mDbSize / 2 + v1;
            float ballY = bottom - (18 / 2);

            // 绘制背景进度条
            if (showBackgroundLine) {
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setAntiAlias(true);

                // 绘制顶部线
                mPaint.setColor(topLineColor);
                mPaint.setStrokeWidth(topLineWidth);
                if (topLineRound) {
                    mPaint.setStrokeCap(Paint.Cap.ROUND);
                } else {
                    mPaint.setStrokeCap(Paint.Cap.BUTT);
                }
                canvas.drawLine(ballX, ballY, ballX, top6dBY, mPaint);

                // 绘制底部线
                mPaint.setColor(bottomLineColor);
                mPaint.setStrokeWidth(bottomLineWidth);
                if (bottomLineRound) {
                    mPaint.setStrokeCap(Paint.Cap.ROUND);
                } else {
                    mPaint.setStrokeCap(Paint.Cap.BUTT);
                }
                canvas.drawLine(ballX, ballY, ballX, bottom6dBY, mPaint);
            }

            // 绘制点
            mPaint.setColor(ballColor);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setAntiAlias(true);

            if (pointShape == 0) {
                // 绘制圆形
                mPaint.setColor(ballColor);
                canvas.drawCircle(ballX, ballY, ballRadius, mPaint);
                if (showInBall) {
                    mPaint.setColor(inBallColor);
                    canvas.drawCircle(ballX, ballY, ballRadius - dip2px(getContext(), 2), mPaint);
                }
            } else {
                // 绘制方形
                float left = ballX - pointWidth / 2;
                float top = ballY - pointHeight / 2;
                if (pointRound) {
                    // 使用圆角矩形
                    canvas.drawRoundRect(left, top, left + pointWidth, top + pointHeight,
                            pointRoundRadius, pointRoundRadius, mPaint);

                    if (showInBall) {
                        mPaint.setColor(inBallColor);
                        canvas.drawRoundRect(left + 4, top + 4, left + pointWidth - 4, top + pointHeight - 4,
                                pointRoundRadius, pointRoundRadius, mPaint);
                    }
                } else {
                    // 使用普通矩形
                    canvas.drawRect(left, top, left + pointWidth, top + pointHeight, mPaint);
                    if (showInBall) {
                        mPaint.setColor(inBallColor);
                        canvas.drawRect(left + 4, top + 4, left + pointWidth - 4, top + pointHeight - 4, mPaint);
                    }
                }
            }

            xAxialStart += xAxialStep;
        }
    }

    private void canvasLine(Canvas canvas) {
        if (mPoints.isEmpty()) {
            return;
        }

        mPaint.setColor(lineColor);
        mPaint.setStrokeWidth(lineWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);

        if (useBezier) {
            mBezierPath.reset();
            mBezierPath.moveTo(mPoints.get(0).getX(), mPoints.get(0).getY());

            for (int i = 0; i < mPoints.size() - 1; i++) {
                float x1 = mPoints.get(i).getX();
                float y1 = mPoints.get(i).getY();
                float x2 = mPoints.get(i + 1).getX();
                float y2 = mPoints.get(i + 1).getY();

                float controlX1 = x1 + (x2 - x1) / 3;
                float controlY1 = y1 - Math.abs(y2 - y1) * 0.1f;
                float controlX2 = x1 + 2 * (x2 - x1) / 3;
                float controlY2 = y2 + Math.abs(y2 - y1) * 0.1f;

                mBezierPath.cubicTo(controlX1, controlY1, controlX2, controlY2, x2, y2);
            }

            canvas.drawPath(mBezierPath, mPaint);
        } else {
            for (int i = 0; i < mPoints.size() - 1; i++) {
                float x1 = mPoints.get(i).getX();
                float y1 = mPoints.get(i).getY();
                float x2 = mPoints.get(i + 1).getX();
                float y2 = mPoints.get(i + 1).getY();

                canvas.drawLine(x1, y1, x2, y2, mPaint);
            }
        }
    }

    private void canvasBackground(Canvas canvas) {
        if (mPoints.isEmpty()) {
            return;
        }

        Path curvePath = new Path();
        curvePath.moveTo(mPoints.get(0).getX(), mPoints.get(0).getY());

        if (useBezier) {
            for (int i = 0; i < mPoints.size() - 1; i++) {
                float x1 = mPoints.get(i).getX();
                float y1 = mPoints.get(i).getY();
                float x2 = mPoints.get(i + 1).getX();
                float y2 = mPoints.get(i + 1).getY();

                float controlX1 = x1 + (x2 - x1) / 3;
                float controlY1 = y1 - Math.abs(y2 - y1) * 0.1f;
                float controlX2 = x1 + 2 * (x2 - x1) / 3;
                float controlY2 = y2 + Math.abs(y2 - y1) * 0.1f;

                curvePath.cubicTo(controlX1, controlY1, controlX2, controlY2, x2, y2);
            }
        } else {
            for (int i = 1; i < mPoints.size(); i++) {
                curvePath.lineTo(mPoints.get(i).getX(), mPoints.get(i).getY());
            }
        }

        curvePath.lineTo(mPoints.get(mPoints.size() - 1).getX(), mHeight - 150);
        curvePath.lineTo(mPoints.get(0).getX(), mHeight - 150);
        curvePath.close();

        if (showGradient) {
            dashLinePaint.setShader(new LinearGradient(
                    0, 0,
                    0, mHeight - 150,
                    new int[]{gradientTopColor, gradientBottomColor},
                    null,
                    Shader.TileMode.CLAMP));
            canvas.drawPath(curvePath, dashLinePaint);
        }
    }

    public boolean isMove() {
        return isMove;
    }

    public void setMove(boolean move) {
        isMove = move;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isMove) {
            return true;
        }

        int x = (int) ev.getX();
        int y = (int) ev.getY();
        int left = (int) (marginLR + 2 * circleRadius);
        int top = 0;
        int length = xAxialVal.length;
        int right = left + xAxialStep;
        int bottom = mHeight;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = ev.getY();
                touchRange = Math.min(mWidth, mHeight) * 0.5f; // 减小滑动范围，使控制更精确
                for (int i = 0; i < length; i++) {
                    Rect rect = new Rect(left, top, right, bottom);
                    left += xAxialStep;
                    right = left + xAxialStep;
                    if (rect.contains(x, y)) {
                        currentSelectBarIndex = i;
                        currentDb = dBAndHzMap.get(currentSelectBarIndex);
                        break;
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                updateDb(ev);
                break;
            case MotionEvent.ACTION_UP:
                sendDb();
                break;
            default:
                break;
        }
        return true;
    }

    private void sendDb() {
        Map<Integer, Float> dbAndHzMap = getDbAndHzMap();
        int[] dbs = new int[mPoints.size()];
        for (int i = 0; i < mPoints.size(); i++) {
            Float db = dbAndHzMap.get(i);
            dbs[i] = (int) (db + dbOffset);
        }
        if (mOnEqualizerValueChange != null) {
            mOnEqualizerValueChange.valueChange(dbs);
        }
    }

    private void updateDb(MotionEvent ev) {
        float endY = ev.getY();
        float distance = startY - endY;
        float changeDb = (distance / touchRange) * (maxDbBarNum - 1) * 0.5f;
        float dB = Math.min(Math.max(currentDb + changeDb, 0), (maxDbBarNum - 1));
        if (changeDb != 0 && dB >= 0) {
            if (useFloatValue) {
                dBAndHzMap.put(currentSelectBarIndex, dB);
            } else {
                dBAndHzMap.put(currentSelectBarIndex, (float) Math.round(dB));
            }
            invalidate();
        }
    }

    public interface OnEqualizerValueChange {
        void valueChange(int[] dbs);
    }

    public void setEqualizerChange(OnEqualizerValueChange onEqualizerValueChange) {
        this.mOnEqualizerValueChange = onEqualizerValueChange;
    }

    public int getBaseLineY(int circleR) {
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float textTop = fontMetrics.top;
        float textBottom = fontMetrics.bottom;
        float contentBottom = circleR / 2;
        int baseLineY = (int) (contentBottom - textTop / 2 - textBottom / 2);
        return baseLineY;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void moveLeft() {
        if (currentSelectBarIndex == 0) {
            return;
        }
        currentSelectBarIndex--;
        invalidate();
    }

    public void moveRight() {
        if (currentSelectBarIndex == xAxialVal.length - 1) {
            return;
        }
        currentSelectBarIndex++;
        invalidate();
    }

    public void moveUp() {
        if (currentSelectBarIndex == -1) {
            return;
        }
        float defaultYVal = dBAndHzMap.get(currentSelectBarIndex);
        if (defaultYVal >= maxDbBarNum - 1) {
            return;
        }
        if (useFloatValue) {
            defaultYVal += 0.05f;
        } else {
            defaultYVal = Math.min(defaultYVal + 1, maxDbBarNum - 1);
        }
        dBAndHzMap.put(currentSelectBarIndex, defaultYVal);
        invalidate();
    }

    public void moveDown() {
        if (currentSelectBarIndex == -1) {
            return;
        }
        float defaultYVal = dBAndHzMap.get(currentSelectBarIndex);
        if (defaultYVal <= 0) {
            return;
        }
        if (useFloatValue) {
            defaultYVal -= 0.05f;
        } else {
            defaultYVal = Math.max(defaultYVal - 1, 0);
        }
        dBAndHzMap.put(currentSelectBarIndex, defaultYVal);
        invalidate();
    }

    public void setCurrentSelectBarIndex(int currentSelectBarIndex) {
        this.currentSelectBarIndex = currentSelectBarIndex;
        invalidate();
    }

    public void setDbVal(int dbValue) {
        int val = calculateDbBum(dbValue);
        for (int i = 0; i < xAxialVal.length; i++) {
            dBAndHzMap.put(i, (float) val);
        }
        invalidate();
    }

    public void reset() {
        setDbVal(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                moveUp();
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                moveDown();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                moveLeft();
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                moveRight();
                break;
            case KeyEvent.KEYCODE_ENTER:
                break;
            case KeyEvent.KEYCODE_BACK:
                break;
            case KeyEvent.KEYCODE_HOME:
                break;
            case KeyEvent.KEYCODE_MENU:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setXYData(int[] xVal, int[] yVal, int[] defaultYVal) {
        this.xAxialVal = xVal;
        this.yAxialVal = yVal;
        int size = xVal.length + 1;
        mDbSize = mWidth / (size * 3);//dB块宽度
        xAxialStep = mWidth / size;
        initXY(xVal, yVal, defaultYVal);
        invalidate();
    }

    public void setYVal(int[] defaultYVal) {
        initXY(this.xAxialVal, this.yAxialVal, defaultYVal);
        invalidate();
    }

    public Map<Integer, Float> getDbAndHzMap() {
        int down = yAxialVal[yAxialVal.length - 1];
        Map<Integer, Float> temp = new HashMap<>();
        for (Map.Entry<Integer, Float> entry : dBAndHzMap.entrySet()) {
            Integer key = entry.getKey();
            Float value = entry.getValue();
            float realVal = value + down;
            temp.put(key, realVal);
        }
        return temp;
    }

    public void setXAxialVal(int[] xAxialVal) {
        this.xAxialVal = xAxialVal;
        // 重新初始化相关数据，默认y轴和db值不变
        initXY(xAxialVal, yAxialVal, new int[xAxialVal.length]);
        invalidate();
    }

    public void setYMaxMin(int max, int min) {
        this.yMax = max;
        this.yMin = min;
        this.yAxialVal = new int[]{yMax, 0, yMin};
        initXY(xAxialVal, yAxialVal, new int[xAxialVal.length]);
        invalidate();
    }

    public void setDbOffset(int offset) {
        this.dbOffset = offset;
        invalidate();
    }

    public void setUseBezier(boolean useBezier) {
        this.useBezier = useBezier;
        invalidate();
    }


    public void setShowBackgroundLine(boolean showBackgroundLine) {
        this.showBackgroundLine = showBackgroundLine;
        invalidate();
    }


    public void setTopLineColor(int topLineColor) {
        this.topLineColor = topLineColor;
        invalidate();
    }

    public void setBottomLineColor(int bottomLineColor) {
        this.bottomLineColor = bottomLineColor;
        invalidate();
    }

    public void setTopLineWidth(float topLineWidth) {
        this.topLineWidth = topLineWidth;
        invalidate();
    }

    public void setBottomLineWidth(float bottomLineWidth) {
        this.bottomLineWidth = bottomLineWidth;
        invalidate();
    }

    public void setTopLineRound(boolean topLineRound) {
        this.topLineRound = topLineRound;
        invalidate();
    }

    public void setBottomLineRound(boolean bottomLineRound) {
        this.bottomLineRound = bottomLineRound;
        invalidate();
    }

    public void setShowConnectionLine(boolean showConnectionLine) {
        this.showConnectionLine = showConnectionLine;
        invalidate();
    }

    public void setUseFloatValue(boolean useFloatValue) {
        this.useFloatValue = useFloatValue;
        // 如果切换到整数模式，将所有值四舍五入
        if (!useFloatValue) {
            for (Map.Entry<Integer, Float> entry : dBAndHzMap.entrySet()) {
                entry.setValue((float) Math.round(entry.getValue()));
            }
        }
        invalidate();
    }

    public boolean isUseFloatValue() {
        return useFloatValue;
    }

    public boolean isUseBezier() {
        return useBezier;
    }

    public boolean isShowGradient() {
        return showGradient;
    }

    public boolean isShowConnectionLine() {
        return showConnectionLine;
    }

    public boolean isShowBackgroundLine() {
        return showBackgroundLine;
    }

    public boolean isTopLineRound() {
        return topLineRound;
    }

    public boolean isBottomLineRound() {
        return bottomLineRound;
    }

    public void setShowGradient(boolean showGradient) {
        this.showGradient = showGradient;
        invalidate();
    }

    public void setBallColor(int ballColor) {
        this.ballColor = ballColor;
        invalidate();
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
        invalidate();
    }

    public void setPointShape(int shape) {
        this.pointShape = shape;
        invalidate();
    }

    public void setPointSize(float width, float height) {
        this.pointWidth = width;
        this.pointHeight = height;
        invalidate();
    }

    public int getPointShape() {
        return pointShape;
    }

    public float getPointWidth() {
        return pointWidth;
    }

    public float getPointHeight() {
        return pointHeight;
    }

    public void setPointRound(boolean round) {
        this.pointRound = round;
        invalidate();
    }

    public void setPointRoundRadius(float radius) {
        this.pointRoundRadius = radius;
        invalidate();
    }

    public boolean isPointRound() {
        return pointRound;
    }

    public float getPointRoundRadius() {
        return pointRoundRadius;
    }

    public void setUseScale(boolean useScale) {
        this.useScale = useScale;
        invalidate();
    }

    public boolean isUseScale() {
        return useScale;
    }

    public boolean isShowInBall() {
        return showInBall;
    }

    public void setShowInBall(boolean showInBall) {
        this.showInBall = showInBall;
        invalidate();
    }

    public float getBallRadius() {
        return ballRadius;
    }

    public void setBallRadius(float ballRadius) {
        this.ballRadius = ballRadius;
        invalidate();
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
        invalidate();
    }

    public void setInBallColor(int inBallColor) {
        this.inBallColor = inBallColor;
        invalidate();
    }
}
