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
	Filter FilterOfAccX = new Filter();
	Filter FilterOfAccY = new Filter();
	Filter FilterOfAccZ = new Filter();
	PeakFinder PeFin = new PeakFinder();
	PeakFinder PeFin_X = new PeakFinder();
	PeakFinder PeFin_Y = new PeakFinder();
	StepDistCalculater SDCal = new StepDistCalculater();
	
	int iLastIndex = 0;
	int bufflength = 1024; 
	float[] pointsLine = new float[bufflength];
	float temp0=0;
	float temp1=0;
	
    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];
    private float[] orientation_acc = new float[3];
	
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
       StepTranslate[0] = screenWidth/2;
        StepTranslate[1] = 100*every/2;
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

		//调用重新绘制
		drawView.IsInvalidate();
    }
    
    protected void onResume() {
		//监听加速度传感器TYPE_ACCELEROMETER
    	Sensor accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    	manager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME);
	
    	//监听方向传感器
    	Sensor orientation = manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    	manager.registerListener(listener, orientation, SensorManager.SENSOR_DELAY_GAME);
    	
    	//监听陀螺仪传感器
    	Sensor gyroscope = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    	manager.registerListener(listener, gyroscope, SensorManager.SENSOR_DELAY_GAME);
    	
    	// 初始化地磁场传感器
    	Sensor magnetic = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    	manager.registerListener(listener, magnetic, SensorManager.SENSOR_DELAY_GAME);
    	
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
			float [] AcValues = new float[3];
			float [] AbsCoodinate_filt = new float[3];
			float [] orientation_1 = new float[3];
			float [] OrienValue = new float[3];

			if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
				//数据显示到屏幕上
				accelerometerValues = event.values;
				AcValues = event.values;
				//原坐标下的数值表示受到力A的反作用力B，新坐标系表示A  
				drawView.SetAcceleration_1(AcValues[1], -AcValues[0], -AcValues[2]);
				
				//由旋转矩阵计算的绝对坐标系下加速度的坐标值 
				double[][] AbsCoodinate =  RotaMatrix.CalcuAbsCoodinate(AcValues[1], AcValues[0], AcValues[2]);
				if(AbsCoodinate != null)
				{
					AbsCoodinate_filt[0] = FilterOfAccX.AverageFiltering((float)AbsCoodinate[0][0]);
					AbsCoodinate_filt[1] = FilterOfAccY.AverageFiltering((float)AbsCoodinate[1][0]);
					AbsCoodinate_filt[2] = FilterOfAccZ.AverageFiltering((float)AbsCoodinate[2][0]);
					drawView.SetAbsCoodinate_1(AbsCoodinate_filt[0], AbsCoodinate_filt[1], AbsCoodinate_filt[2]);
					/*GeneralTool.saveToSDcard(AbsCoodinate_filt[0],
											 AbsCoodinate_filt[1],
											 AbsCoodinate_filt[2],
										     "AbsoluteCoordinate.txt");*/
				}
				//取极大值和极小值，加负号去掉方向的影响
				PeFin.FindPeak(-AbsCoodinate_filt[2]);
				//存储X Y轴的值
				PeFin_X.StoreValue(AbsCoodinate_filt[0]);
				PeFin_Y.StoreValue(AbsCoodinate_filt[1]);
				//计算步长和步数，记步也是正确的
				SDCal.CalcuStepDist(PeFin);
				angleTrans = Orientation_With_acceleration.OrientWithTime(PeFin, SDCal, PeFin_X.fArray3, PeFin_Y.fArray3);
				//GeneralTool.saveToSDcard(angleTrans);
				if(angleTrans > -1)
				{
					drawView.ori_acc = angleTrans; 
					AngleSin = (float) Math.sin((angleTrans*PI)/180);
					AngleCos = (float) Math.cos((angleTrans*PI)/180);
					//GeneralTool.saveToSDcard(PeFin.Circle*PeFin.bufflength3 + SDCal.PreMinValueIndex);
				}
				drawView.Step = SDCal.StepCount;
			}
			else if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){
				 //数据显示到屏幕上
				 OrienValue = event.values;
				 //GeneralTool.saveToSDcard(OrienValue[0], OrienValue[1], OrienValue[2]);
				 
				 //初始手机保持水平姿态
				 //yaw航偏：顺时针增大 【0，360】
				 orientation_1[0] = 360 - OrienValue[0];
				 //pitch倾斜：向上旋转半圈  【0，-180】 继续旋转半圈【180，0】
				 orientation_1[1] = -OrienValue[1];
				 //roll翻滚：正面朝上垂线 顺时针转一圈 【0，-90】【-90,0】【0，90】【90,0】
				 orientation_1[2] = OrienValue[2];
				 
				 drawView.SetOrientation_1(orientation_1[0], orientation_1[1], orientation_1[2]);
				 
				 //计算旋转矩阵
				 RotaMatrix.CalRotaMatrix(OrienValue[0], OrienValue[1], OrienValue[2]);
				 //计算旋转矩阵
				 //RotaMatrix.CalRotaMatrix(360 - orientation_acc[0], -orientation_acc[1], orientation_acc[2]);
				 /*if(SDCal.StepCount==1)
						AngleTemp = OrienValue[0];
					
				angleTrans = AngleTrans(OrienValue[0]);
				AngleSin = (float) Math.sin((angleTrans*PI)/180);
				AngleCos = (float) Math.cos((angleTrans*PI)/180);*/
			}
			else if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE){
				float [] GyroValue = event.values;
				drawView.SetGyroscope_1(GyroValue[0], GyroValue[1], GyroValue[2]);
			}
			else if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD)
			{
				magneticFieldValues = event.values;
			}
			
			calculateOrientation();
			
			getdata = Trans(SDCal.StepCount);
			
	        drawView.paintX=getdata[0];
			drawView.paintY=getdata[1];
			drawView.radius = every;
			drawView.points1=GetPointsLine(getdata);
			
			//调用重新绘制
			drawView.IsInvalidate();
		}
	}
	
    // 计算方向,数据不稳定
    private void calculateOrientation() {
    	float[] orientationvalues = new float[3];
	    float[] R = new float[9];
	    SensorManager.getRotationMatrix(R, null, accelerometerValues,magneticFieldValues);
	    SensorManager.getOrientation(R, orientationvalues);
	    orientationvalues[0] = (float) Math.toDegrees(orientationvalues[0]);
	    orientationvalues[1] = (float) Math.toDegrees(orientationvalues[1]);
	    orientationvalues[2] = (float) Math.toDegrees(orientationvalues[2]);
	    orientation_acc[0] = (720 - orientationvalues[0])%360;
	    orientation_acc[1] = -orientationvalues[1];
	    orientation_acc[2] = -orientationvalues[2];
	    drawView.SetOrientationByAcc(orientation_acc[0], orientation_acc[1], orientation_acc[2]);
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
							(float) (((K*SDCal.DistanceOneStep*(mStep-showStep))*AngleCos)/0.33)*every;
			//人员行走在手机高度方向的变化
			StepTranslate[1] = StepTranslate[1] + 
							(float) (((K*SDCal.DistanceOneStep*(mStep-showStep))*AngleSin)/0.33)*every;
			
			//行走的距离
			distance =distance+(SDCal.StepCount-showStep)*K*SDCal.DistanceOneStep;
			
			showStep = mStep;
		}
		//saveToSDcard(mStep, SDCal.StepCount, mAngleSin, AngleSin);
		return StepTranslate;
	}
	
	public  float[] GetPointsLine(float[] fValues){
		if((fValues[0]!=temp0)&&(fValues[1]!=temp1)&&
				(fValues[0]!=0)&&(fValues[1]!=0)){
			if(iLastIndex <4)
			{
				pointsLine[0] = fValues[0];
				pointsLine[1] = fValues[1];
				pointsLine[2] = fValues[0];
				pointsLine[3] = fValues[1];
			}
			else
			{
				pointsLine[iLastIndex-2] = fValues[0];
				pointsLine[iLastIndex-1] = fValues[1];
				pointsLine[iLastIndex] = fValues[0];
				pointsLine[iLastIndex+1] = fValues[1];
				pointsLine[iLastIndex+2] = fValues[0];
				pointsLine[iLastIndex+3] = fValues[1];
			}
			iLastIndex = (iLastIndex+4)%bufflength;
			temp0 = fValues[0];
			temp1 = fValues[1];
		}
		
		//generalTool.saveToSDcard(drawView.points1);
		return pointsLine;
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
