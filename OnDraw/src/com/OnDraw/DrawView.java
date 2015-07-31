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
	float accelerationA =0;
	float accelerationB =0;
	float accelerationC =0;
	float orientationA = 0;
	float orientationB = 0;
	float orientationC = 0;
	float Step = 0;
	
	float AbsCoodinateA = 0;
	float AbsCoodinateB = 0;
	float AbsCoodinateC = 0;
	
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
	
	paint.setColor(Color.RED);//设置笔的颜色
	paint.setTextSize(50);
	
	//yaw
	canvas.drawText(String.valueOf(orientationA), 10, 50, paint);
	//pitch
	canvas.drawText(String.valueOf(orientationB), 10, 100, paint);
	//roll
	canvas.drawText(String.valueOf(orientationC), 10, 150, paint);
	
	//x
	canvas.drawText(String.valueOf(accelerationB), 10, 250, paint);
	//y
	canvas.drawText(String.valueOf(accelerationA), 10, 300, paint);
	//z
	canvas.drawText(String.valueOf(accelerationC), 10, 350, paint);
	
	//x
	canvas.drawText(String.valueOf(AbsCoodinateB), 10, 500, paint);
	//y
	canvas.drawText(String.valueOf(AbsCoodinateA), 10, 550, paint);
	//z
	canvas.drawText(String.valueOf(AbsCoodinateC), 10, 600, paint);
	
	/*canvas.drawText(String.valueOf(arraytest[0][0]), 10, 50, paint);
	canvas.drawText(String.valueOf(arraytest[0][1]), 10, 100, paint);
	canvas.drawText(String.valueOf(arraytest[0][2]), 10, 150, paint);
	canvas.drawText(String.valueOf(arraytest[1][0]), 10, 200, paint);
	canvas.drawText(String.valueOf(arraytest[1][1]), 10, 250, paint);
	canvas.drawText(String.valueOf(arraytest[1][2]), 10, 300, paint);
	canvas.drawText(String.valueOf(arraytest[2][0]), 10, 350, paint);
	canvas.drawText(String.valueOf(arraytest[2][1]), 10, 400, paint);
	canvas.drawText(String.valueOf(arraytest[2][2]), 10, 450, paint);*/
	
	//canvas.drawText(String.valueOf(Step), 10, 250, paint);

	}
	}

