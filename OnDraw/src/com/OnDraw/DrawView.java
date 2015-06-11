package com.OnDraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class DrawView extends View {
	//float paintX =558;
	//float paintY =44;
	float paintX =0;
	float paintY =0;
	float radius =10;
	float[] points = new float[32];
	public DrawView(Context context) {
	super(context);
	// TODO Auto-generated constructor stub
	}

	/**
	* 这个方法会在初始化后背调用一次,invaildate()的时候会被调用
	*/
	@Override
	protected void onDraw(Canvas canvas) {
	super.onDraw(canvas);
	canvas.drawColor(Color.WHITE);//底色是白色
	Paint paint=new Paint();//设置一个笔
	paint.setAntiAlias(true);//设置没有锯齿
	paint.setColor(Color.RED);//设置笔的颜色
	canvas.drawCircle(paintX, paintY, radius, paint);//距离画圆
	
	
	paint.setColor(Color.BLUE);
	paint.setStrokeWidth(4);
	paint.setStyle(Paint.Style.STROKE);
	Path path = new Path();
	canvas.drawLines(points, paint);

	}
	}

