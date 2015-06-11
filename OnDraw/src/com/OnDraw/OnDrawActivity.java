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
	
	//����ƽ��ֵ��������ݣ�
	float fArray[] = new float[36];
	int iLastIndex = -1;
	int iIsHundred = 0;
	
	float fArray2[] = new float[36];
	int iLastIndex2 = -1;
	int iIsHundred2 = 0;
	
	//max,min
	float fArray3[] = new float[100];
	int iLastIndex3 = -1;
	int iIsHundred3 = 0;
	//���������ȶ�λ80����֤���ٿ����������������岨��
	int bufflength3 = 80;
	float[] maxNum = new float[]{0,0,0,0,0,0};
	float temp2 = 0;
	float[] Step =new float[]{0,0,0,0,0,0,0,0};
	//�Ʋ�
	float[] maxNum2 = new float[]{0,0,0,0,0,0,0,0};
	//����step
	float temp = 0;
	float buchang = 0;
	
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
        //for(int i = 0;i<32;i++){
       // 	points1[i]=points2[i];
      // }
        
        
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
			float AverageAccelerometer = 0;
			float AverageAccelerometer2 = 0;
			float[] maxAccelerometer = new float[]{0,0,0,0,0,0};
			 float[] values2 = null ;
			
			
			if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
				//������ʾ����Ļ��
				 float[] values = event.values;
				//�ϼ��ٶ�
				Accelerometer = (float) java.lang.StrictMath.pow((Math.pow(values[0],2)
						+Math.pow(values[2],2)+Math.pow(values[2],2)),1.0/2);
				
				
				//ƽ��ֵ,����һ���˲���ȡ36������ƽ��ֵ
				AverageAccelerometer = CalculateAverageOfHundred(Accelerometer,36);
				//ƽ��ֵ,���ڶ����˲���ȡ3������ƽ��ֵ
				AverageAccelerometer2 = CalculateAverageOfHundred2(AverageAccelerometer,3);
				
				//ȡ�ڶ����˲����ֵ�ļ���ֵ�ͼ�Сֵ,
				maxAccelerometer = max(AverageAccelerometer2);
				
				
				//saveToSDcard(maxAccelerometer[4],maxAccelerometer[1],maxAccelerometer[5],maxAccelerometer[2]);
				//�Ʋ�Step[1]������Step[3]
				Step = countstep(maxAccelerometer[0],maxAccelerometer[4],maxAccelerometer[5],maxAccelerometer[1],maxAccelerometer[2]);
				
				
			}
			else if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){
				//������ʾ����Ļ��
				 values2 = event.values;
				 
				//AngleSin = (float) Math.sin((values2[0]*PI)/180);
				//AngleCos = (float) Math.cos((values2[0]*PI)/180);
				 if(Step[1]==1)
						AngleTemp = values2[0];
					
					 angleTrans = AngleTrans(values2[0]);
					 AngleSin = (float) Math.sin((angleTrans*PI)/180);
					 AngleCos = (float) Math.cos((angleTrans*PI)/180);
				 
				 //saveToSDcard(values2[0],angleTrans,AngleSin,AngleCos,0,0);
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
							(float) (((K*Step[3]*(mStep-showStep))*AngleCos)/0.33)*every;
			//��Ա�������ֻ��߶ȷ���ı仯
			StepTranslate[1] = StepTranslate[1] + 
							(float) (((K*Step[3]*(mStep-showStep))*AngleSin)/0.33)*every;
			
			//���ߵľ���
			distance =distance+(Step[1]-showStep)*K*Step[3];
			
			showStep = mStep;
			}
		//saveToSDcard(mStep, Step[1], mAngleSin, AngleSin);
		return StepTranslate;

		}
	
	//ƽ��ֵ,����һ���˲���ȡ36������ƽ��ֵ
	public float CalculateAverageOfHundred(float fValue,int M){
		
		int i=0;
		float fAverage = fValue;
		iLastIndex = (++iLastIndex)%M;//%36������
		fArray[iLastIndex] = fValue;
		if(iIsHundred == 0)
		{
			if((M-1) == iLastIndex)
			{
				iIsHundred = 1;
			}
			for(i = 0; i != iLastIndex ; i++)
	  		{
	  			fAverage = fAverage + fArray[i];
	  		}
			fAverage = fAverage / (iLastIndex + 1);
	  }
	  else
	  {
	  	for(i = ((iLastIndex + 1)%M); i != iLastIndex ; i = ((i + 1)%M))
	  	{
	  		fAverage = fAverage + fArray[i];
	  	}
	  	fAverage =fAverage / M;
	  }
		
		return fAverage;
  }
	
	//ƽ��ֵ,���ڶ����˲���ȡ3������ƽ��ֵ	
	public float CalculateAverageOfHundred2(float fValue,int M){
			int i=0;
			float fAverage = fValue;
			iLastIndex2 = (++iLastIndex2)%M;//%36������
			fArray2[iLastIndex2] = fValue;
			if(iIsHundred2 == 0)
			{
				if((M-1) == iLastIndex2)
				{
					iIsHundred2 = 1;
				}
				for(i = 0; i != iLastIndex2 ; i++)
		  		{
		  			fAverage = fAverage + fArray2[i];
		  		}
				fAverage = fAverage / (iLastIndex2 + 1);
		  }
		  else
		  {
		  	for(i = ((iLastIndex2 + 1)%M); i != iLastIndex2 ; i = ((i + 1)%M))
		  	{
		  		fAverage = fAverage + fArray2[i];
		  	}
		  	fAverage =fAverage / M;
		  }
			return fAverage;
	  }
	//����ֵ��Сֵ����Ǳ�
	//[0]����ֵ��Сֵ��־��[1]����ֵ�Ǳ꣬[2]��Сֵ�Ǳ꣬[4]����ֵ��[5]��Сֵ
	public float[] max(float fValue){
		maxNum[0] = 0;
		
		iLastIndex3 = (++iLastIndex3)%bufflength3 ;
		fArray3[iLastIndex3] = fValue;
		// ����û�д���ʱ�����  
		if(iIsHundred3 == 0){
			if(iLastIndex3 == bufflength3 -1){
				iIsHundred3 = 1;
			}
			//����ǵ�һ����������
			if(iLastIndex3 == 1||iLastIndex3 == 0)
			{
			    return maxNum;
			}
			//���ǵ�һ�����ٴ���
			else
			{
					//�ж��������Ƿ񹹳ɼ���ֵ�������ڶ��������ǲ���һ������ֵ��
		  		if(fArray3[iLastIndex3 - 1] > fArray3[iLastIndex3] && 
		  		   fArray3[iLastIndex3 - 1] > fArray3[iLastIndex3 - 2])
		  	    {
		  				maxNum[0] = 1;
		  				maxNum[1] = iLastIndex3 - 1;
		  				maxNum[4] = fArray3[iLastIndex3 - 1];
	  				
		  		}
		  		//�ж��������Ƿ񹹳ɼ�Сֵ�������ڶ��������ǲ���һ����Сֵ��
		  		if(fArray3[iLastIndex3 - 1]<fArray3[iLastIndex3] 
		  		    && fArray3[iLastIndex3 - 1]<fArray3[iLastIndex3 - 2]){
		  				maxNum[0] = 2;
		  				maxNum[2] = iLastIndex3 - 1;
		  				maxNum[5] = fArray3[iLastIndex3 - 1];
		  		}
			}	
		}
		
	  //�������������������ݻḲ����������� /
		else
		{
			//�ж��������Ƿ񹹳ɼ���ֵ�������ڶ��������ǲ���һ������ֵ��
			if(fArray3[(iLastIndex3 + bufflength3 - 1) % bufflength3] > fArray3[iLastIndex3] && 
			   fArray3[(iLastIndex3 + bufflength3 - 1) % bufflength3] > fArray3[(iLastIndex3 + bufflength3 - 2) % bufflength3])
			{
  				
				maxNum[0] = 1;
  				maxNum[1] = (iLastIndex3 + bufflength3 - 1) % bufflength3;
  				maxNum[4] = fArray3[(iLastIndex3 + bufflength3 - 1) % bufflength3];
			}
  		//�ж��������Ƿ񹹳ɼ�Сֵ�������ڶ��������ǲ���һ����Сֵ��
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
	//�Ʋ�
	public float[] countstep(float isPeak,float MPeak,float LPeak,float MPeakIndex,float LPeakIndex){
		
		//���Ǽ�ֵ
		if(0 == (int)isPeak)
		{
			return maxNum2;
		}
		
		//�Ǽ���ֵ
		if(1 == (int)isPeak)
		{
		  //�;����ֵ�Ƚ�
		  //�µļ���ֵ�� ��¼ֵ���±�
		  if(maxNum2[0] < MPeak)
		  {
		  	maxNum2[0] = MPeak;
		  	maxNum2[2] = MPeakIndex;
		  }
		  return maxNum2;
		}
		
		//�Ǽ�Сֵ
		if(2 == (int)isPeak)
		{
			//�ɵ����ֵ���µļ�Сֵ�Ƚ� �Ƿ�����0.8������
			  //�����������Ҫ������if(maxNum2[0] - LPeak > 0.8)
			if(maxNum2[0] - LPeak > 0.6 )
			{
				
				maxNum2[6] =LPeak;
				//��Сֵ�Ǳ�
				maxNum2[7] = LPeakIndex;
				//������һ���ľ���
				maxNum2[3] = calculatedistance();
				
				//���汾�ε����ֵ��Ϊ�´μ��������׼��
				maxNum2[4] = maxNum2[0];
				maxNum2[5] = maxNum2[2]; 
				
				//����Ļ��ǲ���1
				++maxNum2[1];
				
				
				//�������ֵ����
				maxNum2[0] = 0;
				maxNum2[2] = 0;
				
				return maxNum2;
			}
		}
		//[0]���ֵ��=0����[1]�Ʋ���[2]���ֵ�Ǳ꣨=0����[3]������[4]���ֵ��[5]���ֵ�Ǳ�
		return maxNum2;
	}
	//maxNum2[0] ���ֵ, [2] ���ֵ�±�, [4] ��һ�����ֵ, [5]��һ�����ֵ���±�
	//����
	public float calculatedistance()
	{
		//temp����  
  		  temp = 0;
  		  //��һ�ν���ʱֱ���˳�
  		  if(1 >= maxNum2[4] && 0 == (int)maxNum2[5])
  		  {
  		  	buchang = 0;
  		  }
  		  //���㲽��
    	 if((int)maxNum2[5] < (int)maxNum[2])
   	      {
			  //һ�������Ǵ� maxNum2[5] �� maxNum[2] ��������
				for(int j = (int) maxNum2[5]; j != (int)maxNum[2] + 1; j++)
					temp =  temp + fArray3[j];
				//һ�����ڳ����� maxNum2[5] - maxNum[2]
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
