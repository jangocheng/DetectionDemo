package com.compilesense.liuyi.detectiondemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * TODO: document your custom view class.
 */
public class FaceRectangleView extends ImageView {

    private Rect sourceRect;
    private Rect sourceFaceRect;
    private Rect faceRect = new Rect();
    Paint rectPaint;

    public FaceRectangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FaceRectangleView(Context context) {
        super(context);
        init();
    }

    public void setRect(Rect sourceRect, Rect rect) {
        this.sourceFaceRect = rect;
        this.sourceRect = sourceRect;
        invalidate();
    }

    void init(){
        rectPaint = new Paint();
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(2);
        rectPaint.setColor(Color.GREEN);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        if (sourceFaceRect != null){
            int w = getWidth();
            int h = getHeight();
            int l = getLeft();
            int t = getTop();


            int dRLeft = (sourceFaceRect.left* w/sourceRect.width()) ;
            int dRWidth = (sourceFaceRect.width()* w/sourceRect.width()) ;
            int dRTop = (sourceFaceRect.top* h/sourceRect.height()) ;
            int dRHeight = (sourceFaceRect.height() * h/sourceRect.height());
            faceRect.left = dRLeft;
            faceRect.top = dRTop;
            faceRect.right = dRLeft + dRWidth;
            faceRect.bottom = dRTop + dRHeight;

            Rect viewRect = new Rect(l,t,l+w,t+t);
            Log.d("faceDraw","faceRect:"+faceRect.toString());
            Log.d("faceDraw","ViewRect:"+viewRect.toString());
            Log.d("faceDraw","sourceRect:"+sourceRect.toString());
            Log.d("faceDraw","sourceFaceRect:"+sourceFaceRect.toString());
            canvas.drawRect(faceRect,rectPaint);
        }
    }


}
