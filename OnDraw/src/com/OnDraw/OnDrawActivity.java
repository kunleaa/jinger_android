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
	
	//��¥��ͷ�ĸ�����ñ���
	int screenWidth = 0;   
    int screenHeight = 0;
    float every = 0; 
    
	private LinearLayout layout;
	DrawView drawView;
	
	private SensorManager manager;
	private SensorListener listener = new SensorListener();
	
	//����ı仯
	float angleTrans = 0;
	float Angle = 0;//ͨ��¥����
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
	Filter FilterOfAcc = new Filter();
	PeakFinder PeFin = new PeakFinder();
	StepDistCalculater SDCal = new StepDistCalculater();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        
        layout = (LinearLayout)  findViewById(R.id.layout);//�ҵ�����ռ�
        drawView = new DrawView(this);//�����Զ���Ŀؼ�
        
        drawView.setMinimumHeight(300);
        drawView.setMinimumWidth(500);
        layout.addView(drawView);//���Զ���Ŀؼ��������
        
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        
      //����ֻ���Ļ�ĳ���
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

		//�������»���
		drawView.invalidate();
    
    }
    
    protected void onResume() {
		//�������ٶȴ�����
    	Sensor accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    	manager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME);
	
    	//�������򴫸���
    	Sensor orientation = manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    	manager.registerListener(listener, orientation, SensorManager.SENSOR_DELAY_GAME);
    	
    	//���������Ǵ�����
    	Sensor gyroscope = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    	manager.registerListener(listener, gyroscope, SensorManager.SENSOR_DELAY_GAME);
    	
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
			 float MyAveAcc = 0;
			
			if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
				//������ʾ����Ļ��
				 float[] AcceleValue = event.values;

				//�ֻ���ʼˮƽ����
				//ǰΪ������Ϊ�� x��
				drawView.accelerationB = AcceleValue[1];
				//��Ϊ������Ϊ�� y��
				drawView.accelerationA = -AcceleValue[0];
				//��Ϊ������Ϊ�� z��
				drawView.accelerationC = -AcceleValue[2];
				
				//����ת�������ľ�������ϵ�¼��ٶȵ�����ֵ
				double[][] AbsCoodinate =  RotaMatrix.CalcuAbsCoodinate(AcceleValue[1], AcceleValue[0], AcceleValue[2]);
				if(AbsCoodinate != null)
				{
					drawView.AbsCoodinateB = (float)AbsCoodinate[0][0];
					drawView.AbsCoodinateA = (float)AbsCoodinate[1][0];
					drawView.AbsCoodinateC = (float)AbsCoodinate[2][0];
					//GeneralTool.saveToSDcard(drawView.AbsCoodinateB, FilterOfAcc.AverageFiltering(drawView.AbsCoodinateB), drawView.AbsCoodinateC, "AbsAccelerate.txt");
				}
				
				//�ϼ��ٶ�
				Accelerometer = (float) java.lang.StrictMath.pow((Math.pow(AcceleValue[0],2)
						+Math.pow(AcceleValue[1],2)+Math.pow(AcceleValue[2],2)),1.0/2);
				//������ƽ��ֵ�ǲ�
				MyAveAcc = filter.AverageFiltering(Accelerometer);
				//ȡ�ڶ����˲����ֵ�ļ���ֵ�ͼ�Сֵ
				PeFin.FindPeak(MyAveAcc);
				//���㲽���Ͳ���
				SDCal.CalcuStepDist(PeFin);
				
				drawView.Step = SDCal.StepCount;
			}
			else if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){
				//������ʾ����Ļ��
				 float [] OrienValue = event.values;
				 //GeneralTool.saveToSDcard(OrienValue[0], OrienValue[1], OrienValue[2]);
				 
				 //��ʼ�ֻ�����ˮƽ��̬
				 //yaw��ƫ��˳ʱ������ ��0��360��
				 drawView.orientationA = 360 - OrienValue[0];
				 //pitch��б��������ת��Ȧ  ��0��-180�� ������ת��Ȧ��180��0��
				 drawView.orientationB = -OrienValue[1];
				 //roll���������泯�ϴ��� ˳ʱ��תһȦ ��0��-90����-90,0����0��90����90,0��
				 drawView.orientationC = OrienValue[2];
				 
				 //������ת����
				 RotaMatrix.CalRotaMatrix(OrienValue[0], OrienValue[1], OrienValue[2]);
				 
				 if(SDCal.StepCount==1)
						AngleTemp = OrienValue[0];
					
				angleTrans = AngleTrans(OrienValue[0]);
				AngleSin = (float) Math.sin((angleTrans*PI)/180);
				AngleCos = (float) Math.cos((angleTrans*PI)/180);
			}
			else if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE){
				float [] GyroValue = event.values;
				drawView.GyroscopeA = GyroValue[0];
				drawView.GyroscopeB = GyroValue[1];
				drawView.GyroscopeC = GyroValue[2];
			}
			
			getdata = Trans(SDCal.StepCount);
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
			//�������»���
			drawView.invalidate();
		}
	}
	
	//��Ա�Ƕȵı仯
	public float AngleTrans(float fValue){
		
		if((Math.abs(fValue - Angle)) >1){
			AngleTrans = AngleTemp - fValue;
			Angle = fValue;
		}
		return AngleTrans;
	}
	
	public float[] Trans(float mStep){
		if(showStep != mStep){
			
			//��Ա�������ֻ���ȷ���ı仯
			StepTranslate[0] = StepTranslate[0] -
							(float) (((K*SDCal.DistanceOneStep*(mStep-showStep))*AngleCos)/0.33)*every;
			//��Ա�������ֻ��߶ȷ���ı仯
			StepTranslate[1] = StepTranslate[1] + 
							(float) (((K*SDCal.DistanceOneStep*(mStep-showStep))*AngleSin)/0.33)*every;
			
			//���ߵľ���
			distance =distance+(SDCal.StepCount-showStep)*K*SDCal.DistanceOneStep;
			
			showStep = mStep;
		}
		//saveToSDcard(mStep, SDCal.StepCount, mAngleSin, AngleSin);
		return StepTranslate;
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
