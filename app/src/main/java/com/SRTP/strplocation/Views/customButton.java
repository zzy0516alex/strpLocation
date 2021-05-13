package com.SRTP.strplocation.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.SRTP.strplocation.R;


public class customButton extends View {

    private Bitmap icon;
    private Bitmap icon_select;
    private String text;
    private int text_size;
    private int theme_color;
    private int text_padding;
    private boolean is_enable_BottomLight;
    private int BottomLight_color;

    private int background_width;
    private int background_height;

    private Paint painter;
    private Shader shader;
    private Rect text_bound;
    private Rect icon_canvas;

    private boolean action_down;
    private boolean is_selected;

    private OnBtnSelectListener select_listener;
    private OnBtnClickListener click_listener;

    //获得接口对象的方法。
    public void setOnBtnSelectListener(OnBtnSelectListener listener) {
        this.select_listener = listener;
    }

    public void setClickListener(OnBtnClickListener click_listener) {
        this.click_listener = click_listener;
    }

    //定义一个接口
    public interface  OnBtnSelectListener{
        void onBtnSelect();
        void onBtnCancel();
    }
    public interface  OnBtnClickListener{
        void onClick();
    }

    public void setIs_selected(boolean is_selected) {
        this.is_selected = is_selected;
        invalidate();
    }

    public OnBtnSelectListener getSelect_listener() {
        return select_listener;
    }

    public boolean IsSelected() {
        return is_selected;
    }

    //使得所有调用都是用三参构造
    public customButton(Context context) {
        this(context,null);
    }

    public customButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public customButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray att_list = context.getTheme().obtainStyledAttributes(attrs, R.styleable.customButton, defStyleAttr, 0);
        icon= BitmapFactory.decodeResource(getResources(), att_list.getResourceId(R.styleable.customButton_icon, 0));
        icon_select= BitmapFactory.decodeResource(getResources(), att_list.getResourceId(R.styleable.customButton_icon_select, 0));
        text=att_list.getString(R.styleable.customButton_text);
        text_size=att_list.getDimensionPixelSize(R.styleable.customButton_text_size, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
        theme_color =att_list.getColor(R.styleable.customButton_theme_color, Color.BLACK);
        text_padding=att_list.getDimensionPixelSize(R.styleable.customButton_text_padding, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
        is_enable_BottomLight=att_list.getBoolean(R.styleable.customButton_enable_bottom_light,false);
        BottomLight_color=att_list.getColor(R.styleable.customButton_bottom_light_color,Color.BLACK);
        //回收
        att_list.recycle();

        //init params
        painter=new Paint();
        text_bound=new Rect();
        icon_canvas=new Rect();
        //pre measure
        painter.setTextSize(text_size);
        painter.getTextBounds(text,0,text.length(),text_bound);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        /**
         * 设置宽度
         */
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);

        if (specMode == MeasureSpec.EXACTLY)// match_parent , accurate
        {
            background_width = specSize;
        } else
        {
            // 由图片决定的宽
            int icon_width =icon.getWidth();
            // 由字体决定的宽
            int text_width = text_bound.width();

            //总宽度
            int total_width=icon_width+text_width+getPaddingLeft()+getPaddingRight()+text_padding+10;

            if (specMode == MeasureSpec.AT_MOST)// wrap_content
            {
                background_width=Math.min(total_width,specSize);
            }
        }

        /***
         * 设置高度
         */

        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY)// match_parent , accurate
        {
            background_height = specSize;
        } else
        {
            // 由图片决定的高
            int icon_height =icon.getHeight()+getPaddingTop()+getPaddingBottom();
            // 由字体决定的高
            int text_height = text_bound.height()+getPaddingTop()+getPaddingBottom();

            //总高度
            int total_height=Math.max(icon_height,text_height);
            if (specMode == MeasureSpec.AT_MOST)// wrap_content
            {
                background_height = Math.min(total_height, specSize);
            }
        }
        setMeasuredDimension(background_width,background_height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        painter.setShader(null);
        //画 background
        painter.setColor(Color.WHITE);
        canvas.drawRect(0,0,getMeasuredWidth(),getMeasuredHeight(),painter);
        //画 icon
        painter.setAntiAlias(true);
        painter.setFilterBitmap(true);
        painter.setStyle(Paint.Style.FILL);
        icon_canvas.top=background_height/2-icon.getHeight()/2;
        icon_canvas.bottom=background_height/2+icon.getHeight()/2;
        icon_canvas.left=background_width/2-(icon.getWidth()+text_padding+text_bound.width())/2;
        icon_canvas.right=icon_canvas.left+icon.getWidth();
        if (!is_selected)canvas.drawBitmap(icon,null,icon_canvas,painter);
        else canvas.drawBitmap(icon_select,null,icon_canvas,painter);
        //画文字
        painter.setTextSize(text_size);
        if (is_selected)painter.setColor(theme_color);
        else painter.setColor(Color.BLACK);
        Typeface font = Typeface.create(Typeface.SANS_SERIF,Typeface.BOLD);
        painter.setTypeface(font);
        Paint.FontMetrics fontMetrics = painter.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;
        int baseLineY = (int) (icon_canvas.centerY() - top/2 - bottom/2);//基线中间点的y轴计算公式
        canvas.drawText(text, icon_canvas.right+text_padding, baseLineY, painter);
        //画蒙版
        painter.setColor(Color.WHITE);
        painter.setAlpha(150);
        if (action_down)canvas.drawRect(0,0,getMeasuredWidth(),getMeasuredHeight(),painter);
        painter.setAlpha(255);
        //画蒙版2
        shader=new LinearGradient(0, background_height,
                0, background_height*0.75f, BottomLight_color, Color.TRANSPARENT, Shader.TileMode.CLAMP);
        if (is_selected && is_enable_BottomLight){
            painter.setShader(shader);
            canvas.drawRect(0,0,getMeasuredWidth(),getMeasuredHeight(),painter);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                action_down=true;
                invalidate();
                Log.d("button", "onTouchEvent: down");
                return true;
            case MotionEvent.ACTION_UP:
                if (click_listener!=null)click_listener.onClick();
                action_down=false;
                float x=event.getX();
                float y = event.getY();
                boolean x_inside=x>0 && x<background_width;
                boolean y_inside=y>0 && y<background_height;
                if (x_inside&&y_inside){
                    is_selected=!is_selected;
                    if (select_listener !=null){
                        if (is_selected) select_listener.onBtnSelect();
                        else select_listener.onBtnCancel();
                    }
                }else action_down=false;
                invalidate();
                return true;
            default:
        }
        return super.onTouchEvent(event);
    }
}
