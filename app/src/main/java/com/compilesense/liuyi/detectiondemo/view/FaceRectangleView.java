package com.compilesense.liuyi.detectiondemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import android.widget.ImageView;

import com.compilesense.liuyi.detectiondemo.model.bean.KeyPointBean;

import java.util.List;

/**
 * 给人脸画框
 */
public class FaceRectangleView extends ImageView {

    private Rect sourceRect;
    private Rect sourceFaceRect;
    private boolean isClear=false;
    private boolean isDrawRect=false;
    private boolean isDrawPoint=false;
    private List<KeyPointBean.FacesBean.PointsBean> sourceFacePoints;
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
        this.isClear = false;
        this.isDrawRect=true;
        invalidate();
    }

    public void cleartRectAndPoints() {
        this.isClear = true;
        invalidate();
    }

    public void setPoints(List<KeyPointBean.FacesBean.PointsBean> sourceFacePoints,Rect sourceRect) {
        this.sourceRect = sourceRect;
        this.sourceFacePoints = sourceFacePoints;
        this.isClear = false;
        this.isDrawPoint=true;
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
        //当用户执行cleartRectAndPoints方法，则直接return
        if (isClear){return;}

        int w = getWidth();
        int h = getHeight();
        int l = getLeft();
        int t = getTop();

        //人脸检测执行代码
        if (isDrawRect && sourceFaceRect != null ){
            int dRLeft = (sourceFaceRect.left * w / sourceRect.width()) ;
            int dRWidth = (sourceFaceRect.width() * w / sourceRect.width()) ;
            int dRTop = (sourceFaceRect.top * h / sourceRect.height()) ;
            int dRHeight = (sourceFaceRect.height() * h / sourceRect.height());
            faceRect.left = dRLeft;
            faceRect.top = dRTop;
            faceRect.right = dRLeft + dRWidth;
            faceRect.bottom = dRTop + dRHeight;
            canvas.drawRect(faceRect,rectPaint);
            isDrawRect=false;
        }
        //关键点检测执行代码
        if (isDrawPoint && sourceFacePoints!=null && sourceFacePoints.size() >0){
            for (KeyPointBean.FacesBean.PointsBean point : sourceFacePoints) {
                int pX = (w * Integer.parseInt(point.getX()) / sourceRect.width()) ;
                int pY = (h * Integer.parseInt(point.getY()) / sourceRect.height()) ;
                canvas.drawPoint(pX+0.5f,pY+0.5f,rectPaint);
            }
            isDrawPoint=false;
        }
    }


}
