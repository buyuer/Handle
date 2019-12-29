package com.upre.Handle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class RockerView extends View {

    private float x,y;
    private float ox,oy;
    private float inRadius,outRadius;
    private boolean isStart = true;

    private Paint paint;

    private boolean isDown;

    private CallBack callBack = new CallBack() {
        @Override
        public void onDown(float x, float y) {

        }

        @Override
        public void onMove(float x, float y) {

        }

        @Override
        public void onUp(float x, float y) {

        }
    };

    public interface CallBack{
        void onDown(float x,float y);
        void onMove(float x,float y);
        void onUp(float x,float y);
    }

    public RockerView(Context context){
        this(context,null);
    }

    public RockerView(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }

    public RockerView(Context context, AttributeSet attrs, int defStyleAttr){
        this(context,attrs,0,0);
    }

    public RockerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super(context,attrs,defStyleAttr,defStyleRes);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        ox = getWidth() / 2;
        oy = getHeight() / 2;
        outRadius = ox <= oy ? ox:oy;
        float ratio = 0.35f;
        inRadius = outRadius * ratio;
        paint.setColor(Color.RED);
        canvas.drawCircle(ox,oy,outRadius,paint);
        paint.setColor(Color.YELLOW);
        if(isStart){
            canvas.drawCircle(ox,oy,inRadius,paint);
            isStart = false;
        }else{
            canvas.drawCircle(x,y,inRadius,paint);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                float tx1 = event.getX();
                float ty1 = event.getY();
                double l1 = Math.sqrt(Math.pow(tx1-ox,2) + Math.pow(ty1-oy,2));
                if( l1 <= inRadius ){
                    isDown = true;
                    x = getXRatio();
                    y = getYRatio();
                    callBack.onDown(getXRatio(),getYRatio());
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(isDown){
                    float tx = event.getX();
                    float ty = event.getY();
                    double l = Math.sqrt(Math.pow(tx-ox,2) + Math.pow(ty-oy,2));
                    if(l <= (outRadius - inRadius) ){
                        x = tx;
                        y = ty;
                    }else{
                        double k = (outRadius - inRadius) / l;
                        x = ox + (tx - ox) * (float)k;
                        y = oy + (ty - oy) * (float)k;
                    }
                    invalidate();
                    callBack.onMove(getXRatio(),getYRatio());
                }
                break;
            case MotionEvent.ACTION_UP:
                isDown = false;
                x = ox;
                y = oy;
                invalidate();
                callBack.onUp(getXRatio(),getYRatio());
                break;
        }
        return true;
    }

    public void setCallBack(CallBack c){
        callBack = c;
    }

    public float getXRatio(){
        return (x - ox) / (outRadius - inRadius);
    }

    public float getYRatio(){
        return (oy - y) / (outRadius - inRadius);
    }
}
