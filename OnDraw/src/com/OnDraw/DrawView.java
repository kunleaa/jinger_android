package com.OnDraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class DrawView extends View {
	final int FREQUENT = 50; //目前传感器更新频率为一秒50次
	int refreshcount = 0;
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
	
	float orientationAA = 0;
	float orientationBB = 0;
	float orientationCC = 0;
	float Step = 0;
	
	float AbsCoodinateA = 0;
	float AbsCoodinateB = 0;
	float AbsCoodinateC = 0;
	
	float GyroscopeA = 0;
	float GyroscopeB = 0;
	float GyroscopeC = 0;
	
	float ori_acc = 0;
	//估计的方向值减去传感器的方向值
	float ori_increment = 0;
	
	float[] points1 = {0,0,0,0};
	
	float mean_orisensor = 0;
	float mean_oriacc = 0;
	
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
		//Path path = new Path();
		//canvas.drawLines(points, paint);
		
		paint.setColor(Color.RED);//设置笔的颜色
		paint.setTextSize(50);
		
		canvas.drawLines(points1, paint);

		canvas.drawText(String.valueOf(ori_acc), 10, 50, paint);
		canvas.drawText(String.valueOf(orientationA), 300, 50, paint);
		canvas.drawText(String.valueOf(ori_increment), 600, 50, paint);
		
		canvas.drawText(String.valueOf(mean_oriacc), 10, 100, paint);
		canvas.drawText(String.valueOf(mean_orisensor), 300, 100, paint);
		
		canvas.drawText(String.valueOf(Step), 10, 150, paint);
		
		/*
		//yaw
		canvas.drawText(String.valueOf(orientationA), 10, 50, paint);
		//pitch
		canvas.drawText(String.valueOf(orientationB), 10, 100, paint);
		//roll
		canvas.drawText(String.valueOf(orientationC), 10, 150, paint);
		*/
		
		/*//yaw
		canvas.drawText(String.valueOf(orientationAA), 400, 50, paint);
		//pitch
		canvas.drawText(String.valueOf(orientationBB), 400, 100, paint);
		//roll
		canvas.drawText(String.valueOf(orientationCC), 400, 150, paint);
		*/
		/*
		//x
		canvas.drawText(String.valueOf(accelerationA), 10, 250, paint);
		//y
		canvas.drawText(String.valueOf(accelerationB), 10, 300, paint);
		//z
		canvas.drawText(String.valueOf(accelerationC), 10, 350, paint);
		*/
		
		/*
		//x
		canvas.drawText(String.valueOf(Math.abs(AbsCoodinateA)), 10, 500, paint);
		//y
		canvas.drawText(String.valueOf(Math.abs(AbsCoodinateB)), 10, 550, paint);
		//z
		canvas.drawText(String.valueOf(Math.abs(AbsCoodinateC)), 10, 600, paint);
		*/
		
		/*
		if(AbsCoodinateA > 0)
		{
			canvas.drawText("正X", 10, 750, paint);
			canvas.drawText("--", 10, 850, paint);
		}
		else
		{
			canvas.drawText("--", 10, 750, paint);
			canvas.drawText("负X", 10, 850, paint);
		}
		if(AbsCoodinateB > 0)
		{
			canvas.drawText("正Y", 200, 750, paint);
			canvas.drawText("--", 200, 850, paint);
		}
		else
		{
			canvas.drawText("--", 200, 750, paint);
			canvas.drawText("负Y", 200, 850, paint);
		}
		if(AbsCoodinateC > 0)
		{
			canvas.drawText("正Z", 400, 750, paint);
			canvas.drawText("--", 400, 850, paint);
		}
		else
		{
			canvas.drawText("--", 400, 750, paint);
			canvas.drawText("负Z", 400, 850, paint);
		}
		*/
		/*
		canvas.drawText(String.valueOf(GyroscopeA), 10, 950, paint);
		canvas.drawText(String.valueOf(GyroscopeB), 10, 1000, paint);
		canvas.drawText(String.valueOf(GyroscopeC), 10, 1050, paint);
		*/
		
		/*canvas.drawText(String.valueOf(arraytest[0][0]), 10, 50, paint);
		canvas.drawText(String.valueOf(arraytest[0][1]), 10, 100, paint);
		canvas.drawText(String.valueOf(arraytest[0][2]), 10, 150, paint);
		canvas.drawText(String.valueOf(arraytest[1][0]), 10, 200, paint);
		canvas.drawText(String.valueOf(arraytest[1][1]), 10, 250, paint);
		canvas.drawText(String.valueOf(arraytest[1][2]), 10, 300, paint);
		canvas.drawText(String.valueOf(arraytest[2][0]), 10, 350, paint);
		canvas.drawText(String.valueOf(arraytest[2][1]), 10, 400, paint);
		canvas.drawText(String.valueOf(arraytest[2][2]), 10, 450, paint);*/
		
	}
	
	public void SetAcceleration_1(float A, float B, float C)
	{
		accelerationA = A;
		accelerationB = B;
		accelerationC = C;
		return;
	}
	
	public void SetOrientation_1(float A, float B, float C)
	{
		orientationA = A;
		orientationB = B;
		orientationC = C;
		return;
	}
	
	public void SetOrientationByAcc(float A, float B, float C)
	{
		orientationAA = A;
		orientationBB = B;
		orientationCC = C;
		return;
	}
	
	public void SetAbsCoodinate_1(float A, float B, float C)
	{
		AbsCoodinateA = A;
		AbsCoodinateB = B;
		AbsCoodinateC = C;
		return;
	}
	
	public void SetGyroscope_1(float A, float B, float C)
	{
		GyroscopeA = A;
		GyroscopeB = B;
		GyroscopeC = C;
		return;
	}
	
	public void IsInvalidate()
	{
		//控制一秒刷新一次
		if(refreshcount > 0.6*FREQUENT)
		{
			refreshcount = 0;
			this.invalidate();
		}
		else
		{
			refreshcount++;
			return ;
		}
	}
	void clean()
	{
		refreshcount = 0;
		paintX =0;
		paintY =0;
		radius =10;
		points = new float[32];
		
		accelerationA =0;
		accelerationB =0;
		accelerationC =0;
		
		orientationA = 0;
		orientationB = 0;
		orientationC = 0;
		
		orientationAA = 0;
		orientationBB = 0;
		orientationCC = 0;
		Step = 0;
		
		AbsCoodinateA = 0;
		AbsCoodinateB = 0;
		AbsCoodinateC = 0;
		
		GyroscopeA = 0;
		GyroscopeB = 0;
		GyroscopeC = 0;
		
		ori_acc = 0;
		//估计的方向值减去传感器的方向值
		ori_increment = 0;
		
		points1 = new float[4];
		
		mean_orisensor = 0;
		mean_oriacc = 0;
	}
}

