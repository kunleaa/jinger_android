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
	Path path = new Path();
	canvas.drawLines(points, paint);

	}
	}

