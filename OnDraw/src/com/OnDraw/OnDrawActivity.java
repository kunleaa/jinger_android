package com.OnDraw;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

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
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
	Filter FilterOfAccX = new Filter();
	Filter FilterOfAccY = new Filter();
	Filter FilterOfAccZ = new Filter();
	PeakFinder PeFin = new PeakFinder();
	PeakFinder PeFin_X = new PeakFinder();
	PeakFinder PeFin_Y = new PeakFinder();
	StepDistCalculater SDCal = new StepDistCalculater();
	
	int iLastIndex = 0;
	int bufflength = 1024; 
	float[] pointsLine = new float[32];
	
	float temp0=0;
	float temp1=0;
	
    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];
    private float[] orientation_acc = new float[3];
    
    Button press;
    Button clean;
	EditText edit_start;
	EditText edit_end;
	
	//startFromMiddle1p4
	int SFM1_4 = 1;
	int EFM1_4 = 1;
	
	Statistic statistic = new Statistic();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        final InputMethodManager imm = (InputMethodManager)getSystemService(
        	      Context.INPUT_METHOD_SERVICE);
        
        press = (Button)findViewById(R.id.btn_get_value);
        clean = (Button)findViewById(R.id.btn_clean);
        edit_start = (EditText)findViewById(R.id.edit_start);
        edit_end = (EditText)findViewById(R.id.edit_end);
        press.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
            	//�ӽ����ȡ����
            	SFM1_4 = Integer.parseInt(edit_start.getText().toString());  
            	EFM1_4 = Integer.parseInt(edit_end.getText().toString());
            	//�洢����
            	GeneralTool.removefile("config_ondraw.txt");
            	GeneralTool.saveToSDcard(SFM1_4,"config_ondraw.txt");
            	GeneralTool.saveToSDcard(EFM1_4,"config_ondraw.txt");
            	//���ؼ���
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }  
        });
        clean.setOnClickListener(new OnClickListener(){
        	@Override  
            public void onClick(View v) {
        		//��ͼ���
        		drawView.clean();
                //ͳ���������
                statistic.cleanalldata();
                //PeakFinder���
            	PeFin.cleanall();
            	PeFin_X.cleanall();
            	PeFin_Y.cleanall();
            	//Filter���
            	FilterOfAccX.cleanall();
            	FilterOfAccY.cleanall();
            	FilterOfAccZ.cleanall();
            	//StepDistCalculater���
            	SDCal.cleanall();
        		//�켣���
        		Arrays.fill(pointsLine, 0);
        		iLastIndex = 0;
        		//���ع�
        		StepTranslate[0] = screenWidth/2;
                StepTranslate[1] = 100*every/2;
                getdata[0] = screenWidth/2;
                getdata[1] = 100*every/2;
                //��������
                SDCal.StepCount = 0;
            }  
        });
        
        int rfdata[] = {0,0};
        GeneralTool.read2vFromSDcard("config_ondraw.txt", rfdata);
        SFM1_4 = rfdata[0];
        EFM1_4 = rfdata[1];
        edit_start.setText(""+rfdata[0]);
        edit_end.setText(""+rfdata[1]);
        
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
        StepTranslate[0] = screenWidth/2;
        StepTranslate[1] = 100*every/2;
        //StepTranslate[0] = screenWidth/2;
         //StepTranslate[1] = 8*every;
        drawView.points = somefigure(screenHeight,screenWidth,every);
		//�������»���
		drawView.IsInvalidate();
    }
    
    protected void onResume() {
		//�������ٶȴ�����TYPE_ACCELEROMETER
    	Sensor accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    	manager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME);
	
    	//�������򴫸���
    	Sensor orientation = manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    	manager.registerListener(listener, orientation, SensorManager.SENSOR_DELAY_GAME);
    	
    	//���������Ǵ�����
    	//Sensor gyroscope = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    	//manager.registerListener(listener, gyroscope, SensorManager.SENSOR_DELAY_GAME);
    	
    	// ��ʼ���شų�������
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
			float [] AcValues = new float[3];
			float [] AbsCoodinate_filt = new float[3];
			float [] orientation_1 = new float[3];
			float [] OrienValue = new float[3];

			if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
				//������ʾ����Ļ��
				accelerometerValues = event.values;
				AcValues = event.values;
				//ԭ�����µ���ֵ��ʾ�ܵ���A�ķ�������B��������ϵ��ʾA  
				drawView.SetAcceleration_1(AcValues[1], -AcValues[0], -AcValues[2]);
				
				//����ת�������ľ�������ϵ�¼��ٶȵ�����ֵ 
				double[][] AbsCoodinate =  RotaMatrix.CalcuAbsCoodinate(AcValues[1], AcValues[0], AcValues[2]);
				if(AbsCoodinate != null)
				{
					AbsCoodinate_filt[0] = FilterOfAccX.AverageFiltering_manual((float)AbsCoodinate[0][0],5);
					AbsCoodinate_filt[1] = FilterOfAccY.AverageFiltering_manual((float)AbsCoodinate[1][0],5);
					AbsCoodinate_filt[2] = FilterOfAccZ.AverageFiltering((float)AbsCoodinate[2][0]);
					drawView.SetAbsCoodinate_1(AbsCoodinate_filt[0], AbsCoodinate_filt[1], AbsCoodinate_filt[2]);
					/*GeneralTool.saveToSDcard((float)AbsCoodinate_filt[0],
											 (float)AbsCoodinate_filt[1],
											 (float)AbsCoodinate_filt[2],
											 "data_acc.txt");*/
				}
				//ȡ����ֵ�ͼ�Сֵ���Ӹ���ȥ�������Ӱ��
				PeFin.FindPeak(-AbsCoodinate_filt[2]);
				//�洢X Y���ֵ
				PeFin_X.StoreValue(AbsCoodinate_filt[0]);
				PeFin_Y.StoreValue(AbsCoodinate_filt[1]);
				//���㲽���Ͳ������ǲ�Ҳ����ȷ��
				SDCal.CalcuStepDist(PeFin);
				angleTrans = Orientation_With_acceleration.OrientWithTime(PeFin, SDCal, PeFin_X.fArray3, PeFin_Y.fArray3, SFM1_4, EFM1_4);
				//GeneralTool.saveToSDcard(angleTrans);
				if(angleTrans > -1)
				{
					drawView.ori_acc = angleTrans; 
					//�洢����������ͼ���ķ����������������
					drawView.ori_increment = statistic.cal_sto_oriincre(angleTrans, drawView.orientationA);
					statistic.store_oriacc(angleTrans);
					statistic.store_orisen(drawView.orientationA);
					drawView.mean_orisensor = statistic.mean_orisen();
					drawView.mean_oriacc = statistic.mean_oriacc();
					
					AngleSin = (float) Math.sin((angleTrans*PI)/180);
					AngleCos = (float) Math.cos((angleTrans*PI)/180);
					/*GeneralTool.saveToSDcard(angleTrans,drawView.orientationA,"data_angel.txt");
					GeneralTool.saveToSDcard(PeFin.Circle*PeFin.bufflength3 + SDCal.PreMinValueIndex,
											 SDCal.PreMaxValueIndex < SDCal.PreMinValueIndex ? (PeFin.Circle*PeFin.bufflength3 + SDCal.PreMaxValueIndex):((PeFin.Circle-1)*PeFin.bufflength3 + SDCal.PreMaxValueIndex),
											"data_min_max_index.txt");*/
				}
				drawView.Step = SDCal.StepCount;
			}
			else if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){
				 //������ʾ����Ļ��
				 OrienValue = event.values;
				 //GeneralTool.saveToSDcard(OrienValue[0], OrienValue[1], OrienValue[2]);
				 
				 //��ʼ�ֻ�����ˮƽ��̬
				 //yaw��ƫ��˳ʱ������ ��0��360��
				 orientation_1[0] = 360 - OrienValue[0];
				 //pitch��б��������ת��Ȧ  ��0��-180�� ������ת��Ȧ��180��0��
				 orientation_1[1] = -OrienValue[1];
				 //roll���������泯�ϴ��� ˳ʱ��תһȦ ��0��-90����-90,0����0��90����90,0��
				 orientation_1[2] = OrienValue[2];
				 
				 drawView.SetOrientation_1(orientation_1[0], orientation_1[1], orientation_1[2]);
				 
				 //������ת����
				 RotaMatrix.CalRotaMatrix(OrienValue[0], OrienValue[1], OrienValue[2]);
				 //������ת����
				 //RotaMatrix.CalRotaMatrix(360 - orientation_acc[0], -orientation_acc[1], orientation_acc[2]);
				 /*if(SDCal.StepCount==1)
						AngleTemp = OrienValue[0];
					
				angleTrans = AngleTrans(OrienValue[0]);
				AngleSin = (float) Math.sin((angleTrans*PI)/180);
				AngleCos = (float) Math.cos((angleTrans*PI)/180);*/
			}
			/*else if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE){
				float [] GyroValue = event.values;
				//drawView.SetGyroscope_1(GyroValue[0], GyroValue[1], GyroValue[2]);
			}*/
			else if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD)
			{
				magneticFieldValues = event.values;
			}
			
			//calculateOrientation();
			
			getdata = Trans(SDCal.StepCount);
			
	        drawView.paintX=getdata[0];
			drawView.paintY=getdata[1];
			drawView.radius = every;
			drawView.points1=GetPointsLine(getdata);
			
			//�������»���
			drawView.IsInvalidate();
		}
	}
	
    // ���㷽��,���ݲ��ȶ�
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
	
	public  float[] GetPointsLine(float[] fValues){
		if((fValues[0]!=temp0)&&(fValues[1]!=temp1)&&
				(fValues[0]!=0)&&(fValues[1]!=0)){
			//��������2��
			if(pointsLine.length < bufflength)
			{
				pointsLine = GeneralTool.enlarge(pointsLine, iLastIndex);
			}
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
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	float [] somefigure(int Height, int Width, float ev2)
	{
		float[] points2 = new float[]
			       {Width/2-3*ev2,0,Width/2-3*ev2,Height,
			  		Width/2+3*ev2,0,Width/2+3*ev2,Height,
			  		Width/2-18*ev2,0,Width/2-18*ev2,Height,
			  		Width/2+18*ev2,0,Width/2+18*ev2,Height,
			  		Width/2-18*ev2,0,Width/2+18*ev2,0,
			  		Width/2-18*ev2,Height-1,Width/2+18*ev2,Height-1,
			  		Width/2-3*ev2,18*ev2,Width/2-18*ev2,18*ev2,
			  		Width/2-3*ev2,36*ev2,Width/2-18*ev2,36*ev2,
			  		Width/2-3*ev2,61*ev2,Width/2-18*ev2,61*ev2,
			  		Width/2-3*ev2,71*ev2,Width/2-18*ev2,71*ev2,
			  		Width/2-3*ev2,88*ev2,Width/2-18*ev2,88*ev2,
			  		Width/2-3*ev2,99*ev2,Width/2-18*ev2,99*ev2,
			  		Width/2+3*ev2,8*ev2,Width/2+18*ev2,8*ev2,
			  		Width/2+3*ev2,24*ev2,Width/2+18*ev2,24*ev2,
			  		Width/2+3*ev2,37*ev2,Width/2+18*ev2,37*ev2,
			  		Width/2+3*ev2,64*ev2,Width/2+18*ev2,64*ev2,
			  		Width/2+3*ev2,90*ev2,Width/2+18*ev2,90*ev2,
			  		Width/2+3*ev2,99*ev2,Width/2+18*ev2,99*ev2};
		 return points2;
	}
}
