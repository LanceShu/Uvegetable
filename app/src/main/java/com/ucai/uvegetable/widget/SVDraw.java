package com.ucai.uvegetable.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/*定义一个画矩形框的类*/
public class SVDraw extends SurfaceView implements SurfaceHolder.Callback{

	protected SurfaceHolder sh;
	private int mWidth;
	private int mHeight;
	public SVDraw(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		sh = getHolder();
		sh.addCallback(this);
		sh.setFormat(PixelFormat.TRANSPARENT);
		setZOrderOnTop(true);
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int w, int h) {
		// TODO Auto-generated method stub
		mWidth = w;
		mHeight = h;
	}

	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}
	void clearDraw()
	{
		Canvas canvas = sh.lockCanvas();
		canvas.drawColor(Color.BLUE);
		sh.unlockCanvasAndPost(canvas);
	}
	
	/************摄像头预览后，进行绘制引导线**************/
	public void drawGuideLines()
	{
		Canvas canvas = sh.lockCanvas();
		//canvas.drawColor(Color.TRANSPARENT);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.RED);
		
		paint.setStyle(Style.FILL);
		/*右侧的引导线*/
		//canvas.drawLine(500,0, 500, 600, paint);
		int center = getWidth()/2;
		canvas.drawLine(450, 0, 450, 600, paint);
		paint.setAlpha(100);
		canvas.drawCircle(center-20, center-80, 30, paint);

		sh.unlockCanvasAndPost(canvas);
		
		
	}
	
	/************根据key.xml绘制掌纹roi区域**************/
	public String drawPalmRoi(Point p1, Point p2, Point p3)
	{
		
		String retStr = "";
		Canvas canvas = sh.lockCanvas();
		canvas.drawColor(Color.TRANSPARENT);
		Paint p = new Paint();
		/***************清屏******************/
		p.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		canvas.drawPaint(p);
		p.setXfermode(new PorterDuffXfermode(Mode.SRC));
		/********************开始绘图****************/
		p.setAntiAlias(true);
		p.setColor(Color.RED);
		p.setStyle(Style.FILL);
		
		/*绘制引导线和屏幕标定的圆形*/		
		p.setAlpha(100);
		int center = getWidth()/2;
		canvas.drawCircle(center-20, center-80, 30, p);
		canvas.drawLine(450, 0, 450, 600, p);
		
	
		//绘制举行roi和三个关键点
		p.setAlpha(0);
		p.setColor(Color.BLUE);
		//canvas.drawLine(p1.x, p1.y, p3.x, p3.y, p); //这跳线不绘制了
		canvas.drawCircle(p1.x, p1.y, 10.0f, p);
		canvas.drawCircle(p2.x, p2.y, 10.0f, p);
		canvas.drawCircle(p3.x, p3.y, 10.0f, p);
		double d = Math.sqrt((p3.x-p1.x)*(p3.x - p1.x)+(p3.y-p1.y)*(p3.y-p1.y));
		double d1 = d*0.4;			
		
		float k = Math.abs((p3.y - p1.y)/(p3.x - p1.x));
		double angle  = Math.atan(k);
		//Log.i(tag, "角度 "+ angle);
		double xd1 = Math.sin(angle)*d1;
		double yd1 = Math.cos(angle)*d1;
		/*******point1和point2是平移d/4后的两个点的坐标******/
		Point point1 = new Point();
		Point point2 = new Point();
		point1.set((int)(p1.x-xd1), (int)(p1.y-yd1));
		point2.set((int)(p3.x-xd1), (int)(p3.y-yd1));
		//canvas.drawLine(point1.x, point1.y, point2.x, point2.y, p);
		Point midPoint = new Point((point1.x+point2.x)/2, (point1.y+point2.y)/2);
		
		/**********绘制新的坐标系***********/
		canvas.save();
		canvas.translate(midPoint.x, midPoint.y); //将坐标中心平移到midPoint
		canvas.rotate((float) (90 - Math.toDegrees(angle))); //
		double dd = d;
		
		RectF roiRect = new RectF((float)0, (float)(dd/2), (float)(-dd), (float)(-d/2));
		Paint paint2 = new Paint();
		paint2.setAntiAlias(true);
		paint2.setStyle(Style.STROKE);
		paint2.setColor(Color.BLUE);

		canvas.drawRect(roiRect, paint2);
		paint2.setStyle(Style.FILL);
		canvas.drawCircle((float) (-d/2), 0, 10, paint2);
		canvas.restore();
		sh.unlockCanvasAndPost(canvas);
		
		
		retStr = String.valueOf((int)(d)) + "/" + String.valueOf(90 - (int)(Math.toDegrees(angle)));
		
		return retStr; //两个关键点之间的距离
		
		
	}
	
}