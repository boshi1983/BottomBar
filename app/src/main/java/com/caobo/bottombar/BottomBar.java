package com.caobo.bottombar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BottomBar extends View {

    public static int dp2px(Context context, float dp) {
        return (int)(context.getResources().getDisplayMetrics().density * dp + 0.5F);
    }

    private Context context;

    public BottomBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    //////////////////////////////////////////////////
    //提供的api 并且根据api做一定的物理基础准备
    //////////////////////////////////////////////////

    private List<BarUnit> barList = new ArrayList<>();

    private int itemCount;

    private Paint mPaint = new Paint();

    private int currentCheckedIndex;

    private int titleColorBefore = Color.parseColor("#999999");
    private int titleColorAfter = Color.parseColor("#ff5d5e");
    private int numberBgColor = Color.parseColor("#FF5EA8");
    private int numberTextColor = Color.parseColor("#ffffff");

    private int titleSize;
    private int numberSize;
    private int iconWidth = 30;
    private int iconHeight = 30;
    private int titleIconMargin = 5;
    private int titleBaseLine;
    private int parentItemWidth;
    private int target = -1;

    //设置按钮标题点击前后的颜色
    public BottomBar setTitleBeforeAndAfterColor(String beforeResCode, String afterResCode) {//支持"#333333"这种形式
        titleColorBefore = Color.parseColor(beforeResCode);
        titleColorAfter = Color.parseColor(afterResCode);
        return this;
    }

    //设置标题字号
    public BottomBar setTitleSize(int dp) {
        this.titleSize = dp2px(context, dp);
        return this;
    }

    //设置角标字号
    public BottomBar setNumberSize(int dp) {
        this.numberSize = dp2px(context, dp);
        return this;
    }

    //设置icon宽度
    public BottomBar setIconWidth(int iconWidth) {
        this.iconWidth = iconWidth;
        return this;
    }

    //设置icon Margin
    public BottomBar setTitleIconMargin(int titleIconMargin) {
        this.titleIconMargin = titleIconMargin;
        return this;
    }

    //设置icon 高度
    public BottomBar setIconHeight(int iconHeight) {
        this.iconHeight = iconHeight;
        return this;
    }

    /**
     * 添加按钮
     * @param title 标题
     * @param iconResBefore icon默认状态
     * @param iconResAfter icon选中状态
     * @param runnable 点击回调函数
     * @return
     */
    public BottomBar addItem(String title, int iconResBefore, int iconResAfter, Runnable runnable) {
        barList.add(new BarUnit(title, iconResBefore, iconResAfter, runnable));
        return this;
    }

    /**
     * 设置角标数字
     * @param index 按钮下标，按照实际addtime顺序
     * @param number
     */
    public void setNumber(int index, int number)
    {
        BarUnit unit = getUnit(index);
        if (null != unit) {
            unit.setNumber(number);
            invalidate();
        }
    }

    /**
     * 动态设置按钮标题
     * @param index
     * @param title
     */
    public void setTitle(int index, String title)
    {
        BarUnit unit = getUnit(index);
        if (null != unit) {
            unit.setTitle(title);
            invalidate();
        }
    }

    /**
     * 创建导航栏
     * @param bswitch 是否回调函数
     * @param currentindex 默认选中按钮下标
     */
    public void build(boolean bswitch, int currentindex) {
        itemCount = barList.size();
        for (BarUnit unit: barList) {
            unit.build();
        }

        currentCheckedIndex = currentindex;
        if (bswitch) {
            switchFragment(currentCheckedIndex);
        }

        invalidate();
    }

    /**
     * 指定选中按钮
     * @param currentindex
     */
    public void setCurrent(int currentindex)
    {
        currentCheckedIndex = currentindex;
        switchFragment(currentCheckedIndex);

        invalidate();
    }

    //////////////////////////////////////////////////
    //初始化数据基础
    //////////////////////////////////////////////////
    private BarUnit getUnit(int index)
    {
        if (index >= 0 && index < barList.size()) {
            return barList.get(index);
        }

        return null;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initParam();
    }

    /**
     * 初始化按钮布局
     */
    private void initParam() {
        if (itemCount > 0) {
            //单个item宽高
            parentItemWidth = getWidth() / itemCount;
            int parentItemHeight = getHeight();

            //图标边长
            int iconWidth = dp2px(context, this.iconWidth);//先指定20dp
            int iconHeight = dp2px(context, this.iconHeight);

            //图标文字margin
            int textIconMargin = dp2px(context, ((float)titleIconMargin)/2);//先指定5dp，这里除以一半才是正常的margin，不知道为啥，可能是图片的原因

            //标题高度
            mPaint.setTextSize(titleSize);
            Rect rect = new Rect();

            BarUnit unit = getUnit(0);

            mPaint.getTextBounds(unit.getTitle(), 0, unit.getTitle().length(), rect);
            int titleHeight = rect.height();

            //从而计算得出图标的起始top坐标、文本的baseLine
            int iconTop = (parentItemHeight - iconHeight - textIconMargin - titleHeight)/2;
            titleBaseLine = parentItemHeight - iconTop;

            //对icon的rect的参数进行赋值
            int firstRectX = (parentItemWidth - iconWidth) / 2;//第一个icon的左
            for (int i = 0; i < itemCount; i++) {
                unit = getUnit(i);

                int rectX = i * parentItemWidth + firstRectX;

                Rect temp = unit.getRect();

                temp.left = rectX;
                temp.top = iconTop ;
                temp.right = rectX + iconWidth;
                temp.bottom = iconTop + iconHeight;

                String title = unit.getTitle();
                mPaint.getTextBounds(title, 0, title.length(), rect);
                unit.setTitleX((parentItemWidth - rect.width()) / 2 + parentItemWidth * i);

                mPaint.setTextSize(numberSize);
                unit.setNumberRect(3f*iconWidth/4f);
            }
        }
    }

    //////////////////////////////////////////////////
    //根据得到的参数绘制
    //////////////////////////////////////////////////
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);//这里让view自身替我们画背景 如果指定的话

        if (itemCount > 0) {
            //画背景 和 角标

            mPaint.setAntiAlias(false);
            for (int i = 0; i < itemCount; i++) {
                BarUnit unit = getUnit(i);
                Bitmap bitmap;
                if (i == currentCheckedIndex) {
                    bitmap = unit.getAfterBitmap();
                } else {
                    bitmap = unit.getBeforeBitmap();
                }
                if (bitmap != null) {
                    Rect rect = unit.getRect();
                    canvas.drawBitmap(bitmap, null, rect, mPaint); //null代表bitmap全部画出
                }

                //画角标背景
                if (unit.getNumber() > 0) {
                    int radius = dp2px(context, 10);
                    Rect rect = unit.getNumberBgRect();
                    RectF rectF = new RectF(rect.left, rect.top, rect.right, rect.bottom);

                    mPaint.setColor(numberBgColor);
                    mPaint.setTextSize(numberSize);
                    canvas.drawRoundRect(rectF, radius, radius, mPaint);
                }
            }

            //画文字
            mPaint.setAntiAlias(true);
            for (int i = 0; i < itemCount; i ++) {
                BarUnit unit = getUnit(i);
                String title = unit.getTitle();
                if (i == currentCheckedIndex) {
                    mPaint.setColor(titleColorAfter);
                } else {
                    mPaint.setColor(titleColorBefore);
                }
                mPaint.setTextSize(titleSize);
                canvas.drawText(title, unit.getTitleX(), titleBaseLine, mPaint);

                //画角标文字
                if (unit.getNumber() > 0) {
                    mPaint.setTextSize(numberSize);
                    mPaint.setColor(numberTextColor);
                    Rect rect = unit.getNumberTextRect();
                    canvas.drawText("" + unit.getNumber(), rect.left, rect.top, mPaint);
                }
            }
        }
    }

    //////////////////////////////////////////////////
    //点击事件:我观察了微博和掌盟，发现down和up都在该区域内才响应
    //////////////////////////////////////////////////
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                target = withinWhichArea((int)event.getX());
                break;
            case MotionEvent.ACTION_UP :
                if (event.getY() < 0) {
                    break;
                }
                if (target == withinWhichArea((int)event.getX())) {
                    //这里触发点击事件
                    switchFragment(target);
                    currentCheckedIndex = target;
                    invalidate();
                }
                target = -1;
                break;
        }
        return true;
        //这里return super为什么up执行不到？是因为return super的值，全部取决于你是否
        //clickable，当你down事件来临，不可点击，所以return false，也就是说，而且你没
        //有设置onTouchListener，并且控件是ENABLE的，所以dispatchTouchEvent的返回值
        //也是false，所以在view group的dispatchTransformedTouchEvent也是返回false，
        //这样一来，view group中的first touch target就是空的，所以intercept标记位
        //果断为false，然后就再也进不到循环取孩子的步骤了，直接调用dispatch-
        // TransformedTouchEvent并传孩子为null，所以直接调用view group自身的dispatch-
        // TouchEvent了
    }

    private int withinWhichArea(int x) { return x/parentItemWidth; }//从0开始

    //注意 这里是只支持AppCompatActivity 需要支持其他老版的 自行修改
    protected void switchFragment(int whichFragment) {
        BarUnit unit = getUnit(whichFragment);
        if (unit != null) {
            unit.run();
        }
    }

    /**
     * 按钮数据类
     */
    private class BarUnit {
        private String title;
        private int titleX;
        private int iconResBefore;
        private int iconResAfter;
        private Runnable runnable;
        private int number = 0;
        private Rect numberBgRect = new Rect();
        private Rect numberTextRect = new Rect();
        private float offsetX;

        Bitmap beforeBitmap;
        Bitmap afterBitmap;
        Rect rect = new Rect();

        public BarUnit(String title, int iconResBefore, int iconResAfter, Runnable runnable) {
            this.title = title;
            this.iconResBefore = iconResBefore;
            this.iconResAfter = iconResAfter;
            this.runnable = runnable;
        }

        private Bitmap getBitmap(int resId) {
            if (resId != 0) {
                return BitmapFactory.decodeResource(getResources(), resId);
            }
            return null;
        }

        String getTitle() {
            return title;
        }

        public BarUnit setTitle(String title) {
            this.title = title;
            return this;
        }

        public int getTitleX() {
            return titleX;
        }

        public BarUnit setTitleX(int titleX) {
            this.titleX = titleX;
            return this;
        }

        public int getIconResBefore() {
            return iconResBefore;
        }

        public BarUnit setIconResBefore(int iconResBefore) {
            this.iconResBefore = iconResBefore;
            return this;
        }

        public int getIconResAfter() {
            return iconResAfter;
        }

        public BarUnit setIconResAfter(int iconResAfter) {
            this.iconResAfter = iconResAfter;
            return this;
        }

        public Runnable getRunnable() {
            return runnable;
        }

        public BarUnit setRunnable(Runnable runnable) {
            this.runnable = runnable;
            return this;
        }

        public Rect getRect() {
            return rect;
        }

        public BarUnit setRect(Rect rect) {
            this.rect = rect;
            return this;
        }

        Bitmap getBeforeBitmap() {
            return beforeBitmap;
        }

        Bitmap getAfterBitmap() {
            return afterBitmap;
        }

        public int getNumber() {
            return number;
        }

        public BarUnit setNumber(int number) {
            this.number = number;
            setNumberRect(this.offsetX);
            return this;
        }

        /**
         * 设置角标背景数据
         * @param offsetX
         */
        public void setNumberRect(float offsetX)
        {
            this.offsetX = offsetX;
            if (this.number <= 0) {
                return;
            }

            int padding = dp2px(context, 5);

            Rect rect = new Rect();
            mPaint.getTextBounds("" + this.number, 0, ("" + this.number).length(), rect);
            numberBgRect.set(0, 0, rect.width() + padding * 2, rect.height() + padding * 2);

            offsetX += (this.rect.left - numberBgRect.width()/2f);

            numberBgRect.offset((int)(offsetX), 0);

            Paint.FontMetrics metrics = mPaint.getFontMetrics();
            //numberTextRect.set(numberBgRect.left + padding, (int)(numberBgRect.top - metrics.top), numberBgRect.right, numberBgRect.bottom);
            numberTextRect.set((int)(offsetX + padding), numberBgRect.height() - padding, numberBgRect.right, numberBgRect.bottom);
        }

        public Rect getNumberBgRect() {
            return numberBgRect;
        }

        public Rect getNumberTextRect() {
            return numberTextRect;
        }

        /**
         * 生成icon资源
         */
        public void build()
        {
            beforeBitmap = getBitmap(iconResBefore);
            afterBitmap = getBitmap(iconResAfter);
        }

        public void run()
        {
            if (runnable != null) {
                runnable.run();
            }
        }
    }
}
