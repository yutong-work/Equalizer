# EqualizerView 使用文档

![微信截图_20250606112434](https://github.com/user-attachments/assets/1ae30b0b-221b-46e1-8af7-3ce9ca77bb69)

## 简介
EqualizerView 是一个自定义的音频均衡器视图组件，用于显示和调节音频均衡器的各个频段。它提供了丰富的自定义选项，可以满足不同的UI需求。
implementation 'com.github.yutong-work:Equalizer:beta-ys-v1.0'
## 基本用法

### 1. 在布局文件中使用
```xml
<com.audio.mydemo.ui.weight.EqualizerView
    android:id="@+id/equalizerView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:evBallColor="@color/color_1ABA2D"
    app:evBottomLineColor="@color/color_1CE0DA"
    app:evBottomLineRound="true"
    app:evBottomLineWidth="3dp"
    app:evInBallColor="#1AEBEB"
    app:evPointShape="square"
    app:evShowConnectionLine="false"
    app:evShowGradient="false"
    app:evShowInBall="true"
    app:evTopLineColor="@color/color_999999"
    app:evTopLineRound="true"
    app:evTopLineWidth="3dp"
    app:evUseScale="true" />
```

### 2. 在代码中初始化
```java
EqualizerView equalizerView = findViewById(R.id.equalizerView);
equalizerView.setEqualizerChange(new EqualizerView.OnEqualizerValueChange() {
    @Override
    public void valueChange(int[] dbs) {
        // 处理均衡器值变化
    }
});
```

## 主要属性

### 基本属性
- `evLineColor`: 连接线颜色
- `evBallColor`: 控制点颜色
- `evInBallColor`: 内球颜色
- `evGradientTopColor`: 渐变背景顶部颜色
- `evGradientBottomColor`: 渐变背景底部颜色
- `evShowGradient`: 是否显示渐变背景
- `evShowInBall`: 是否显示内球
- `evCircleTextSize`: 圆圈文本大小
- `evCircleTextColor`: 圆圈文本颜色
- `svCircleRadius`: 圆圈半径大小
- `evLineWidth`: 连接线宽度
- `evBallRadius`: 控制点半径
- `evUseBezier`: 是否使用贝塞尔曲线
- `evShowConnectionLine`: 是否显示控制点之间的连接线

### 坐标轴属性
- `evXTextSize`: X轴文本大小
- `evXSelectColor`: X轴选中颜色
- `evXUnSelectColor`: X轴未选中颜色
- `evLRMargin`: 左右边距
- `evYMax`: Y轴最大值
- `evYMin`: Y轴最小值

### 背景线属性
- `evShowBackgroundLine`: 是否显示背景线
- `evTopLineColor`: 顶部线颜色
- `evBottomLineColor`: 底部线颜色
- `evTopLineWidth`: 顶部线宽度
- `evBottomLineWidth`: 底部线宽度
- `evTopLineRound`: 顶部线是否圆角
- `evBottomLineRound`: 底部线是否圆角

## 主要方法

### 数据设置
```java
// 设置X轴和Y轴数据
setXYData(int[] xVal, int[] yVal, int[] defaultYVal)

// 设置Y轴值
setYVal(int[] defaultYVal)

// 设置X轴值
setXAxialVal(int[] xAxialVal)

// 设置Y轴最大最小值
setYMaxMin(int max, int min)

// 设置dB偏移量
setDbOffset(int offset)
```

### 样式设置
```java
// 设置是否使用贝塞尔曲线
setUseBezier(boolean useBezier)

// 设置是否显示背景线
setShowBackgroundLine(boolean showBackgroundLine)

// 设置是否显示连接线
setShowConnectionLine(boolean showConnectionLine)

// 设置是否使用浮点数值
setUseFloatValue(boolean useFloatValue)

// 设置是否显示渐变
setShowGradient(boolean showGradient)

// 设置控制点形状（0:圆形 1:方形）
setPointShape(int shape)

// 设置控制点大小
setPointSize(float width, float height)

// 设置控制点是否圆角
setPointRound(boolean round)

// 设置控制点圆角半径
setPointRoundRadius(float radius)
```

### 颜色设置
```java
// 设置控制点颜色
setBallColor(int ballColor)

// 设置连接线颜色
setLineColor(int lineColor)

// 设置内球颜色
setInBallColor(int inBallColor)

// 设置顶部线颜色
setTopLineColor(int topLineColor)

// 设置底部线颜色
setBottomLineColor(int bottomLineColor)
```

### 尺寸设置
```java
// 设置控制点半径
setBallRadius(float ballRadius)

// 设置连接线宽度
setLineWidth(float lineWidth)

// 设置顶部线宽度
setTopLineWidth(float topLineWidth)

// 设置底部线宽度
setBottomLineWidth(float bottomLineWidth)
```

## 事件监听
```java
// 设置均衡器值变化监听器
setEqualizerChange(OnEqualizerValueChange onEqualizerValueChange)

// 监听器接口
public interface OnEqualizerValueChange {
    void valueChange(int[] dbs);
}
```

## 使用示例

### 1. 基本设置
```java
EqualizerView equalizerView = findViewById(R.id.equalizerView);

// 设置基本属性
equalizerView.setUseBezier(true);
equalizerView.setShowGradient(true);
equalizerView.setShowConnectionLine(true);

// 设置颜色
equalizerView.setBallColor(Color.GREEN);
equalizerView.setLineColor(Color.BLUE);
equalizerView.setInBallColor(Color.WHITE);

// 设置尺寸
equalizerView.setBallRadius(10f);
equalizerView.setLineWidth(2f);
```

### 2. 数据设置
```java
// 设置频率点
int[] frequencies = {32, 62, 125, 250, 500, 1000, 2000, 4000, 8000, 16000};
// 设置dB范围
int[] dbRange = {6, 0, -6};
// 设置初始值
int[] initialValues = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

equalizerView.setXYData(frequencies, dbRange, initialValues);
```

### 3. 事件处理
```java
equalizerView.setEqualizerChange(new EqualizerView.OnEqualizerValueChange() {
    @Override
    public void valueChange(int[] dbs) {
        // 处理均衡器值变化
        for (int i = 0; i < dbs.length; i++) {
            Log.d("Equalizer", "Frequency " + i + ": " + dbs[i] + "dB");
        }
    }
});
```

## 注意事项
1. 使用前请确保在布局文件中正确配置了所有必要的属性
2. 设置数据时，确保X轴和Y轴的数据长度匹配
3. 在使用浮点数值时，注意精度控制
4. 建议在初始化时设置所有必要的样式属性，以避免重复绘制
5. 注意内存使用，特别是在频繁更新数据时

## 常见问题
1. 如果视图不显示，检查布局文件中的宽高设置
2. 如果控制点无法拖动，检查 `isMove` 属性是否设置为 true
3. 如果颜色显示异常，检查颜色值格式是否正确
4. 如果数据更新不及时，确保在数据变化后调用 `invalidate()` 方法
![微信图片_20250605194825](https://github.com/user-attachments/assets/87b49de4-a663-42cd-8240-1a2e7bae57ec)
![微信图片_20250605194819](https://github.com/user-attachments/assets/5da08c7d-7559-40da-9a85-2a080f722ab5)
![微信图片_20250605194802](https://github.com/user-attachments/assets/fc9d543e-bb9d-46d9-bac8-15c2e2242ca7)
![微信图片_20250605194811](https://github.com/user-attachments/assets/a7a686d9-da6f-42e0-9f15-6ca8d283b034)
