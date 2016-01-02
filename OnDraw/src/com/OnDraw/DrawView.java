package com.OnDraw;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.DisplayMetrics;
import android.view.View;

public class DrawView extends View {
    //��ͼ��ʾ����
	Parameter_Map para_map ;
	//·����Ϣ���
	public Trajectory trajectory;
	
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
	float distance = 0;
	
	float AbsCoodinateA = 0;
	float AbsCoodinateB = 0;
	float AbsCoodinateC = 0;
	
	float GyroscopeA = 0;
	float GyroscopeB = 0;
	float GyroscopeC = 0;
	
	float ori_acc = 0;
	//���Ƶķ���ֵ��ȥ�������ķ���ֵ
	float ori_increment = 0;
	
	float[] points1 = {0,0,0,0};
	
	float mean_orisensor = 0;
	float mean_oriacc = 0;
	
	public DrawView(Context context) {
	super(context);
    //����ֻ���Ļ�ĳ���
    DisplayMetrics  dm = new DisplayMetrics();
    ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
    //�����ͼ�Ĳ���
    para_map = new Parameter_Map(dm.widthPixels,dm.heightPixels);
    //���ƹ켣
    trajectory = new Trajectory(para_map);
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
		//canvas.drawLines(points, paint);
		
		paint.setColor(Color.RED);//���ñʵ���ɫ
		paint.setTextSize(50);
		
		canvas.drawLines(points1, paint);

		canvas.drawText(String.valueOf(ori_acc), 10, 50, paint);
		canvas.drawText(String.valueOf(orientationA), 300, 50, paint);
		canvas.drawText(String.valueOf(ori_increment), 600, 50, paint);
		
		canvas.drawText(String.valueOf(mean_oriacc), 10, 100, paint);
		canvas.drawText(String.valueOf(mean_orisensor), 300, 100, paint);
		
		canvas.drawText(String.valueOf(Step), 10, 150, paint);
		canvas.drawText(String.valueOf(distance), 300, 150, paint);
		
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
		distance = 0;
		
		AbsCoodinateA = 0;
		AbsCoodinateB = 0;
		AbsCoodinateC = 0;
		
		GyroscopeA = 0;
		GyroscopeB = 0;
		GyroscopeC = 0;
		
		ori_acc = 0;
		//���Ƶķ���ֵ��ȥ�������ķ���ֵ
		ori_increment = 0;
		
		points1 = new float[4];
		
		mean_orisensor = 0;
		mean_oriacc = 0;
	}
	
	//�켣�洢��Ϊ��ͼ׼��
	public class Trajectory{
		final double PI = 3.1415926;
		
		//��ͼ��Ҫ��һЩ��Ϣ
		boolean isstep;
		float distonestep;
		float angle;
		float stepcount;
		
		//������Ϣ
		float position_start_x = 0;
		float position_start_y = 0;
		float[] StepTranslate = new float[]{0,0};
		float AngleSin = 0;
		float AngleCos = 0;
		//·����Ϣ
		int iLastIndex = 0;
		int bufflength = 1024; 
		float[] pointsLine = new float[32];
		//�ܳ���
		float sumdistance = 0;
		
		float[] getpath(Parameter_Map pm)
		{
			if(true == isstep)
			{
				//����cos��sinֵ
				calcu_sincos(angle);
				//������һ��֮������ڵ�ͼ��λ��
				Trans(distonestep,pm.every);
				//����·��
				GetPointsLine();
			}
			return pointsLine;
		}
		
		void calcu_sincos(float angle)
		{
				AngleSin = (float) Math.sin((angle*PI)/180);
				AngleCos = (float) Math.cos((angle*PI)/180);
		}
		
		Trajectory(Parameter_Map pm)
		{
			position_start_x = pm.screenWidth/2;
			position_start_y = 100*pm.every/2;
			//��ʼλ������
    		StepTranslate[0] = position_start_x;
            StepTranslate[1] = position_start_y;
		}
		
		public void Trans(float DistOneStep,float unit){
				//��Ա�������ֻ���ȷ���ı仯
				StepTranslate[0] = StepTranslate[0] -	(float) ((DistOneStep*AngleCos)/1.5)*unit;
				//��Ա�������ֻ��߶ȷ���ı仯
				StepTranslate[1] = StepTranslate[1] + (float) ((DistOneStep*AngleSin)/1.5)*unit;
				//���ߵľ���
				sumdistance += DistOneStep;
		}
		
		public void GetPointsLine(){
				//��������2��
				if(pointsLine.length < bufflength)
				{
					pointsLine = GeneralTool.enlarge_float(pointsLine, iLastIndex);
				}
				if(iLastIndex <4)
				{
					pointsLine[0] = StepTranslate[0];
					pointsLine[1] = StepTranslate[1];
					pointsLine[2] = StepTranslate[0];
					pointsLine[3] = StepTranslate[1];
				}
				else
				{
					pointsLine[iLastIndex-2] = StepTranslate[0];
					pointsLine[iLastIndex-1] = StepTranslate[1];
					pointsLine[iLastIndex] = StepTranslate[0];
					pointsLine[iLastIndex+1] = StepTranslate[1];
					pointsLine[iLastIndex+2] = StepTranslate[0];
					pointsLine[iLastIndex+3] = StepTranslate[1];
				}
				iLastIndex = (iLastIndex+4)%bufflength;
			//generalTool.saveToSDcard(drawView.points1);
		}
		
		public void setpaintdata()
		{
			//����·��
			points1 = trajectory.getpath(para_map);
			
			Step = stepcount;
			distance = sumdistance;
	        paintX=StepTranslate[0];
			paintY=StepTranslate[1];
			radius = para_map.every;
		}
		public void cleanalldata()
		{
			//��ͼ��Ҫ��һЩ��Ϣ
			isstep = false;
			distonestep = 0;
			angle = 0;
			stepcount = 0;
			
			//������Ϣ
    		StepTranslate[0] = position_start_x;
            StepTranslate[1] = position_start_y;
			AngleSin = 0;
			AngleCos = 0;
			//·����Ϣ
			iLastIndex = 0;
			bufflength = 1024; 
			pointsLine = new float[32];
			//�ܳ���
			sumdistance = 0;
		}
	}
	
    public class Parameter_Map
    {
    	int screenWidth = 0;   
        int screenHeight = 0;
        float every = 0;
        Parameter_Map(int widthPixels,int heightPixels)
        {
            screenWidth = widthPixels;   
            screenHeight = heightPixels;
            every = screenHeight/108;
        }
    }
	
}

