package com.OnDraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class DrawView extends View {
	//参数信息显示开关
	boolean switch_info = false;
    //地图显示参数
	public Parameter_Map para_map;
	//路径信息相关
	public Trajectory trajectory;
	
	final int FREQUENT = 50; //目前传感器更新频率为一秒50次
	int refreshcount = 0;
	float paintX =0;
	float paintY =0;
	float radius =10;
	
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
	//估计的方向值减去传感器的方向值
	float ori_increment = 0;
	
	float[] points1 = {0,0,0,0};
	
	float mean_orisensor = 0;
	float mean_oriacc = 0;
	
	public DrawView(Context context) {
		super(context);
	    //定义地图的参数
	    para_map = new Parameter_Map();
	    //绘制轨迹
	    trajectory = new Trajectory(para_map);
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
		
		paint.setColor(Color.RED);//设置笔的颜色
		paint.setTextSize(50);
		
		canvas.drawLines(points1, paint);

		if(switch_info == true)
		{
			canvas.drawText(String.valueOf(ori_acc), 10, 50, paint);
			canvas.drawText(String.valueOf(orientationA), 300, 50, paint);
			canvas.drawText(String.valueOf(ori_increment), 600, 50, paint);
			
			canvas.drawText(String.valueOf(mean_oriacc), 10, 100, paint);
			canvas.drawText(String.valueOf(mean_orisensor), 300, 100, paint);
			
			canvas.drawText(String.valueOf(Step), 10, 150, paint);
			canvas.drawText(String.valueOf(distance), 300, 150, paint);
			
			canvas.drawText(String.valueOf(accelerationA), 10, 200, paint);
			canvas.drawText(String.valueOf(accelerationB), 300, 200, paint);
			canvas.drawText(String.valueOf(accelerationC), 600, 200, paint);
			
			canvas.drawText(String.valueOf(orientationA), 10, 250, paint);
			canvas.drawText(String.valueOf(orientationB), 300, 250, paint);
			canvas.drawText(String.valueOf(orientationC), 600, 250, paint);
		}
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
		//估计的方向值减去传感器的方向值
		ori_increment = 0;
		
		points1 = new float[4];
		
		mean_orisensor = 0;
		mean_oriacc = 0;
	}
	
	//轨迹存储，为绘图准备
	public class Trajectory{
		final double PI = 3.1415926;
		
		//绘图需要的一些信息
		boolean isstep;
		float distonestep;
		float angle;
		float stepcount;
		
		//单步信息
		Parameter_Map parmap;
		float[] StepTranslate = new float[]{0,0};
		float AngleSin = 0;
		float AngleCos = 0;
		//路径信息
		int iLastIndex = 0;
		int bufflength = 1024; 
		float[] pointsLine = new float[32];
		//总长度
		float sumdistance = 0;
		
		float[] getpath()
		{
			if(true == isstep)
			{
				//计算cos和sin值
				calcu_sincos(angle);
				//计算这一步之后的人在地图中位置
				Trans(distonestep);
				//构造路径
				GetPointsLine();
			}
			return pointsLine;
		}
		
		void calcu_sincos(float angle)
		{
				AngleSin = (float) Math.sin((angle*PI)/180);
				AngleCos = (float) Math.cos((angle*PI)/180);
				//改变绘图的方向适应地图
				AngleSin = -AngleSin;
				AngleCos = -AngleCos;
		}
		
		Trajectory(Parameter_Map pm)
		{
			parmap = pm;
		}
		
		public void Trans(float DistOneStep){
				//人员行走在手机宽度方向的变化 乘以100将单位m转化为mm
				StepTranslate[0] = StepTranslate[0] - parmap.convert_buildtoscreen(DistOneStep*AngleCos*100);
				//人员行走在手机高度方向的变化
				StepTranslate[1] = StepTranslate[1] + parmap.convert_buildtoscreen(DistOneStep*AngleSin*100);
				//行走的距离
				sumdistance += DistOneStep;
		}
		
		public void GetPointsLine(){
				//数组扩容2倍
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
		}
		
		public void setpaintdata()
		{
			//计算路径
			points1 = trajectory.getpath();
			Step = stepcount;
			distance = sumdistance;
	        paintX=StepTranslate[0];
			paintY=StepTranslate[1];
			radius = 10;
		}
		
		void init_position()
		{
			//起始位置坐标
    		StepTranslate[0] = para_map.convert_buildtoscreen(750);
            StepTranslate[1] = para_map.convert_buildtoscreen(50);
		}
		
		public void cleanalldata()
		{
			//绘图需要的一些信息
			isstep = false;
			distonestep = 0;
			angle = 0;
			stepcount = 0;
			
			//单步信息
			init_position();
			AngleSin = 0;
			AngleCos = 0;
			//路径信息
			iLastIndex = 0;
			bufflength = 1024; 
			pointsLine = new float[32];
			//总长度
			sumdistance = 0;
		}
	}
	
    public class Parameter_Map
    {
    	//实验楼的长和宽是分别是 4400mm 和 1500m
    	float WIDTH_BUILD = 1495;
    	float HEIGHT_BUILD = 4400;
    	float screenWidth = 0;
        float screenHeight = 0;
        //比例  画布的高（像素）/实际长度（毫米）
        float ratio = 0;
        
        void set_paramter_map(int heightPixels)
        {
        	screenHeight = heightPixels;
            screenWidth = (int) (screenHeight*0.3399);   
            ratio = screenHeight/HEIGHT_BUILD;
            //初始化位置
            trajectory.init_position();
        }
        float convert_buildtoscreen(float length)
        {
        	return length*ratio;
        }
    }
}

