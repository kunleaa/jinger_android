package com.OnDraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class DrawView extends View {
	final int FREQUENT = 50; //Ŀǰ����������Ƶ��Ϊһ��50��
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
	
	float[] points1 = {0,0,0,0};
	
	public DrawView(Context context) {
	super(context);
	
	// TODO Auto-generated constructor stub
	}

	/**
	* ����������ڳ�ʼ���󱳵���һ��,invaildate()��ʱ��ᱻ����
	*/
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.WHITE);//��ɫ�ǰ�ɫ
		
		Paint paint=new Paint();//����һ����
		paint.setAntiAlias(true);//����û�о��
		paint.setColor(Color.RED);//���ñʵ���ɫ
		canvas.drawCircle(paintX, paintY, radius, paint);//���뻭Բ
		
		paint.setColor(Color.BLUE);
		paint.setStrokeWidth(4);
		paint.setStyle(Paint.Style.STROKE);
		//Path path = new Path();
		//canvas.drawLines(points, paint);
		
		paint.setColor(Color.RED);//���ñʵ���ɫ
		paint.setTextSize(50);
		
		canvas.drawLines(points1, paint);
		
		//yaw
		canvas.drawText(String.valueOf(orientationA), 10, 50, paint);
		//pitch
		canvas.drawText(String.valueOf(orientationB), 10, 100, paint);
		//roll
		canvas.drawText(String.valueOf(orientationC), 10, 150, paint);
		
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
		
		canvas.drawText(String.valueOf(ori_acc), 400, 300, paint);
		
		//x
		canvas.drawText(String.valueOf(Math.abs(AbsCoodinateA)), 10, 500, paint);
		//y
		canvas.drawText(String.valueOf(Math.abs(AbsCoodinateB)), 10, 550, paint);
		//z
		canvas.drawText(String.valueOf(Math.abs(AbsCoodinateC)), 10, 600, paint);
		/*
		if(AbsCoodinateA > 0)
		{
			canvas.drawText("��X", 10, 750, paint);
			canvas.drawText("--", 10, 850, paint);
		}
		else
		{
			canvas.drawText("--", 10, 750, paint);
			canvas.drawText("��X", 10, 850, paint);
		}
		if(AbsCoodinateB > 0)
		{
			canvas.drawText("��Y", 200, 750, paint);
			canvas.drawText("--", 200, 850, paint);
		}
		else
		{
			canvas.drawText("--", 200, 750, paint);
			canvas.drawText("��Y", 200, 850, paint);
		}
		if(AbsCoodinateC > 0)
		{
			canvas.drawText("��Z", 400, 750, paint);
			canvas.drawText("--", 400, 850, paint);
		}
		else
		{
			canvas.drawText("--", 400, 750, paint);
			canvas.drawText("��Z", 400, 850, paint);
		}
		*/
		/*
		canvas.drawText(String.valueOf(GyroscopeA), 10, 950, paint);
		canvas.drawText(String.valueOf(GyroscopeB), 10, 1000, paint);
		canvas.drawText(String.valueOf(GyroscopeC), 10, 1050, paint);
		*/
		canvas.drawText(String.valueOf(Step), 400, 500, paint);
		
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
		//����һ��ˢ��һ��
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
}

