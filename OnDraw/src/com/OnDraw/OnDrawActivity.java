package com.OnDraw;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OnDrawActivity extends Activity {
	
	//画楼里头的格局所用变量
	int screenWidth = 0;   
    int screenHeight = 0;
    float every = 0; 
    
	private LinearLayout layout;
	DrawView drawView;
	
	private SensorManager manager;
	private SensorListener listener = new SensorListener();
	
	//max,min
	float fArray3[] = new float[100];
	int iLastIndex3 = -1;
	int iIsHundred3 = 0;
	//缓冲区长度定位80，保证至少可以容纳下两个波峰波谷
	int bufflength3 = 80;
	float[] maxNum = new float[]{0,0,0,0,0,0};
	float temp2 = 0;
	float[] Step =new float[]{0,0,0,0,0,0,0,0};
	//计步
	float[] maxNum2 = new float[]{0,0,0,0,0,0,0,0};
	//计算step
	float temp = 0;
	float buchang = 0;
	
	//方向的变化
	float angleTrans = 0;
	float Angle = 0;//通信楼方向
	float AngleTemp =0;
	float AngleTrans = 0;
	float AngleSin = 0;
	float AngleCos = 0;
	double PI = 3.1415926;
	float[] StepTranslate = new float[]{0,0};
	double distance = 0;
	float showStep = 0;
	//double K = 0.2314;
	//double K = 0.1489;
	double K = 0.1737;
	float[] getdata = new float[]{0,0};
	
	float[] points1 = new float[32];
	
	RotationMatrix RotaMatrix = new RotationMatrix();
	Filter filter = new Filter();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        
        layout = (LinearLayout)  findViewById(R.id.layout);//找到这个空间
        drawView = new DrawView(this);//创建自定义的控件
        
        drawView.setMinimumHeight(300);
        drawView.setMinimumWidth(500);
        layout.addView(drawView);//将自定义的控件进行添加
        
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        
      //获得手机屏幕的长宽
        DisplayMetrics  dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);    
        screenWidth = dm.widthPixels;   
        screenHeight = dm.heightPixels;
        every = screenHeight/108;
       StepTranslate[0] = screenWidth/2+18*every;
        StepTranslate[1] = 8*every/2;
        //StepTranslate[0] = screenWidth/2;
         //StepTranslate[1] = 8*every;
        float[] points2 = new float[]
       {screenWidth/2-3*every,0,screenWidth/2-3*every,screenHeight,
  		screenWidth/2+3*every,0,screenWidth/2+3*every,screenHeight,
  		screenWidth/2-18*every,0,screenWidth/2-18*every,screenHeight,
  		screenWidth/2+18*every,0,screenWidth/2+18*every,screenHeight,
  		screenWidth/2-18*every,0,screenWidth/2+18*every,0,
  		screenWidth/2-18*every,screenHeight-1,screenWidth/2+18*every,screenHeight-1,
  		screenWidth/2-3*every,18*every,screenWidth/2-18*every,18*every,
  		screenWidth/2-3*every,36*every,screenWidth/2-18*every,36*every,
  		screenWidth/2-3*every,61*every,screenWidth/2-18*every,61*every,
  		screenWidth/2-3*every,71*every,screenWidth/2-18*every,71*every,
  		screenWidth/2-3*every,88*every,screenWidth/2-18*every,88*every,
  		screenWidth/2-3*every,99*every,screenWidth/2-18*every,99*every,
  		screenWidth/2+3*every,8*every,screenWidth/2+18*every,8*every,
  		screenWidth/2+3*every,24*every,screenWidth/2+18*every,24*every,
  		screenWidth/2+3*every,37*every,screenWidth/2+18*every,37*every,
  		screenWidth/2+3*every,64*every,screenWidth/2+18*every,64*every,
  		screenWidth/2+3*every,90*every,screenWidth/2+18*every,90*every,
  		screenWidth/2+3*every,99*every,screenWidth/2+18*every,99*every};
        drawView.points = points2;
        //for(int i = 0;i<32;i++){
       // 	points1[i]=points2[i];
      // }
        
        
		//调用重新绘制
		drawView.invalidate();
    
    }
    
    protected void onResume() {
		//监听加速度传感器
    	Sensor accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    	manager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME);
	
    	//监听方向传感器
    	Sensor orientation = manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    	manager.registerListener(listener, orientation, SensorManager.SENSOR_DELAY_GAME);
    	
		super.onResume();
	}

    protected void onStop(){
    	manager.unregisterListener(listener);
    	super.onStop();
    }
	private final class SensorListener implements SensorEventListener{
		
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		public void onSensorChanged(SensorEvent event) {
			float Accelerometer = 0;
			float[] maxAccelerometer = new float[]{0,0,0,0,0,0};
			 
			 float MyAveAcc = 0;
			
			if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
				//数据显示到屏幕上
				 float[] AcceleValue = event.values;

				//手机初始水平放置
				//前为负，后为正 x轴
				drawView.accelerationB = AcceleValue[1];
				//左为正，右为负 y轴
				drawView.accelerationA = -AcceleValue[0];
				//上为负，下为正 z轴
				drawView.accelerationC = -AcceleValue[2];
				
				double[][] AbsCoodinate =  RotaMatrix.CalcuAbsCoodinate(AcceleValue[1], AcceleValue[0], AcceleValue[2]);
				
				if(AbsCoodinate != null)
				{
					drawView.AbsCoodinateB = (float)AbsCoodinate[0][0];
					drawView.AbsCoodinateA = (float)AbsCoodinate[1][0];
					drawView.AbsCoodinateC = (float)AbsCoodinate[2][0];
				}
				
				//合加速度
				Accelerometer = (float) java.lang.StrictMath.pow((Math.pow(AcceleValue[0],2)
						+Math.pow(AcceleValue[1],2)+Math.pow(AcceleValue[2],2)),1.0/2);
				
				//做两次平均值虑波
				MyAveAcc = filter.AverageFiltering(Accelerometer);
				
				//取第二次滤波后的值的极大值和极小值,
				maxAccelerometer = max(MyAveAcc);
				
				//计步Step[1]，步长Step[3]
				Step = countstep(maxAccelerometer[0],maxAccelerometer[4],maxAccelerometer[5],maxAccelerometer[1],maxAccelerometer[2]);
				
				drawView.Step = Step[1];
			}
			else if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){
				//数据显示到屏幕上
				 float [] OrienValue = event.values;
				 
				 GeneralTool.saveToSDcard(OrienValue[0], OrienValue[1], OrienValue[2]);
				 
				 //初始手机保持水平姿态
				 //yaw航偏：顺时针增大 【0，360】
				 drawView.orientationA = 360 - OrienValue[0];
				 //pitch倾斜：向上旋转半圈  【0，-180】 继续旋转半圈【180，0】
				 drawView.orientationB = -OrienValue[1];
				 //roll翻滚：正面朝上垂线 顺时针转一圈 【0，-90】【-90,0】【0，90】【90,0】
				 drawView.orientationC = OrienValue[2];
				 
				 RotaMatrix.CalRotaMatrix(OrienValue[0], OrienValue[1], OrienValue[2]);
				 
				 if(Step[1]==1)
						AngleTemp = OrienValue[0];
					
				angleTrans = AngleTrans(OrienValue[0]);
				AngleSin = (float) Math.sin((angleTrans*PI)/180);
				AngleCos = (float) Math.cos((angleTrans*PI)/180);
				 
				 //saveToSDcard(OrienValue[0],angleTrans,AngleSin,AngleCos,0,0);
			}
			
			getdata = Trans(Step[1]);
			if(getdata[0]>(screenWidth/2+18*every))
				getdata[0]=screenWidth/2+18*every;
			else if(getdata[0]<(screenWidth/2-18*every))
				getdata[0]=screenWidth/2-18*every;
			else if(getdata[1]>screenHeight)
					getdata[1]=screenHeight;
			else if(getdata[1]<0)
				getdata[1]=0;
			
	        drawView.paintX=getdata[0];
			drawView.paintY=getdata[1];
			
			drawView.radius = every;
	        
			//调用重新绘制
			drawView.invalidate();
			
		}
	}
	
	//人员角度的变化
	public float AngleTrans(float fValue){
		
		if((Math.abs(fValue - Angle)) >1){
			
			AngleTrans = AngleTemp - fValue;
			
			Angle = fValue;
			}

		return AngleTrans;
	}
	
	public float[] Trans(float mStep){
		if(showStep != mStep){
			
			//人员行走在手机宽度方向的变化
			StepTranslate[0] = StepTranslate[0] -
							(float) (((K*Step[3]*(mStep-showStep))*AngleCos)/0.33)*every;
			//人员行走在手机高度方向的变化
			StepTranslate[1] = StepTranslate[1] + 
							(float) (((K*Step[3]*(mStep-showStep))*AngleSin)/0.33)*every;
			
			//行走的距离
			distance =distance+(Step[1]-showStep)*K*Step[3];
			
			showStep = mStep;
			}
		//saveToSDcard(mStep, Step[1], mAngleSin, AngleSin);
		return StepTranslate;

		}
	
	//极大值极小值及其角标
	//[0]极大值极小值标志，[1]极大值角标，[2]极小值角标，[4]极大值，[5]极小值
	public float[] max(float fValue){
		maxNum[0] = 0;
		
		iLastIndex3 = (++iLastIndex3)%bufflength3 ;
		fArray3[iLastIndex3] = fValue;
		// 数组没有存满时的情况  
		if(iIsHundred3 == 0){
			if(iLastIndex3 == bufflength3 -1){
				iIsHundred3 = 1;
			}
			//如果是第一个数，结束
			if(iLastIndex3 == 1||iLastIndex3 == 0)
			{
			    return maxNum;
			}
			//不是第一数据再处理
			else
			{
					//判断新数据是否构成极大值（倒数第二个数据是不是一个极大值）
		  		if(fArray3[iLastIndex3 - 1] > fArray3[iLastIndex3] && 
		  		   fArray3[iLastIndex3 - 1] > fArray3[iLastIndex3 - 2])
		  	    {
		  				maxNum[0] = 1;
		  				maxNum[1] = iLastIndex3 - 1;
		  				maxNum[4] = fArray3[iLastIndex3 - 1];
	  				
		  		}
		  		//判断新数据是否构成极小值（倒数第二个数据是不是一个极小值）
		  		if(fArray3[iLastIndex3 - 1]<fArray3[iLastIndex3] 
		  		    && fArray3[iLastIndex3 - 1]<fArray3[iLastIndex3 - 2]){
		  				maxNum[0] = 2;
		  				maxNum[2] = iLastIndex3 - 1;
		  				maxNum[5] = fArray3[iLastIndex3 - 1];
		  		}
			}	
		}
		
	  //缓冲区已满、最新数据会覆盖最早的数据 /
		else
		{
			//判断新数据是否构成极大值（倒数第二个数据是不是一个极大值）
			if(fArray3[(iLastIndex3 + bufflength3 - 1) % bufflength3] > fArray3[iLastIndex3] && 
			   fArray3[(iLastIndex3 + bufflength3 - 1) % bufflength3] > fArray3[(iLastIndex3 + bufflength3 - 2) % bufflength3])
			{
  				
				maxNum[0] = 1;
  				maxNum[1] = (iLastIndex3 + bufflength3 - 1) % bufflength3;
  				maxNum[4] = fArray3[(iLastIndex3 + bufflength3 - 1) % bufflength3];
			}
  		//判断新数据是否构成极小值（倒数第二个数据是不是一个极小值）
			if(fArray3[(iLastIndex3 + bufflength3 - 1) % bufflength3] < fArray3[iLastIndex3] && 
			   fArray3[(iLastIndex3 + bufflength3 - 1) % bufflength3] < fArray3[(iLastIndex3 + bufflength3 - 2) % bufflength3])
			{
				
				maxNum[0] = 2;
  				maxNum[2] = (iLastIndex3 + bufflength3 - 1) % bufflength3;
  				maxNum[5] = fArray3[(iLastIndex3 + bufflength3 - 1) % bufflength3];
			}
		}
		//saveToSDcard(fArray3[(int) maxNum[1]],maxNum[1],fArray3[(int) maxNum[2]],maxNum[2]);
		return maxNum;
  }	
	//计步
	public float[] countstep(float isPeak,float MPeak,float LPeak,float MPeakIndex,float LPeakIndex){
		
		//不是极值
		if(0 == (int)isPeak)
		{
			return maxNum2;
		}
		
		//是极大值
		if(1 == (int)isPeak)
		{
		  //和旧最大值比较
		  //新的极大值大 记录值和下标
		  if(maxNum2[0] < MPeak)
		  {
		  	maxNum2[0] = MPeak;
		  	maxNum2[2] = MPeakIndex;
		  }
		  return maxNum2;
		}
		
		//是极小值
		if(2 == (int)isPeak)
		{
			//旧的最大值和新的极小值比较 是否满足0.8的条件
			  //这个条件在需要再讨论if(maxNum2[0] - LPeak > 0.8)
			if(maxNum2[0] - LPeak > 0.6 )
			{
				
				maxNum2[6] =LPeak;
				//极小值角标
				maxNum2[7] = LPeakIndex;
				//计算上一步的距离
				maxNum2[3] = calculatedistance();
				
				//保存本次的最大值，为下次计算距离做准备
				maxNum2[4] = maxNum2[0];
				maxNum2[5] = maxNum2[2]; 
				
				//满足的话记步加1
				++maxNum2[1];
				
				
				//本次最大值清零
				maxNum2[0] = 0;
				maxNum2[2] = 0;
				
				return maxNum2;
			}
		}
		//[0]最大值（=0），[1]计步，[2]最大值角标（=0），[3]步长，[4]最大值，[5]最大值角标
		return maxNum2;
	}
	//maxNum2[0] 最大值, [2] 最大值下标, [4] 上一个最大值, [5]上一个最大值的下标
	//步长
	public float calculatedistance()
	{
		//temp清零  
  		  temp = 0;
  		  //第一次进入时直接退出
  		  if(1 >= maxNum2[4] && 0 == (int)maxNum2[5])
  		  {
  		  	buchang = 0;
  		  }
  		  //计算步长
    	 if((int)maxNum2[5] < (int)maxNum[2])
   	      {
			  //一个周期是从 maxNum2[5] 到 maxNum[2] 两个波峰
				for(int j = (int) maxNum2[5]; j != (int)maxNum[2] + 1; j++)
					temp =  temp + fArray3[j];
				//一个周期长度是 maxNum2[5] - maxNum[2]
				buchang = (float) Math.pow(temp/(maxNum[2] - maxNum2[5]), 1.0/3);
   		  }
		return buchang;
	}
	
public boolean onCreateOptionsMenu(Menu menu){
		
		getMenuInflater().inflate(R.menu.main,menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.set:
		Intent intent = new Intent();
		intent.setClass(OnDrawActivity.this, NewActivityActivity.class);
		startActivity(intent);
			
			break;
		
		default:
			break;
		
		}
		return super.onOptionsItemSelected(item);
	}
	
}
