package com.OnDraw;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class OnDrawActivity extends Activity {
    //��ͼ��ʾ����
	Parameter_Map para_map ;
    
    //��ʾ�ؼ����
	private LinearLayout layout;
	DrawView drawView;
    Button press;
    Button clean;
    EditText edit_mode;
	EditText edit_start;
	EditText edit_end;
	EditText edit_stepparam;
	EditText edit_distance;
	
	//�������豸���
	private SensorManager manager;
	private SensorListener listener = new SensorListener();
	Data_Sensor data_sensor= new Data_Sensor();
	
	//�������
	Navigation navigation = new Navigation();
	Calibration calibration = new Calibration();
	Function_app function_app = new Function_app(navigation, calibration);
	//·����Ϣ���
	Trajectory trajectory;
	
	//�������
	Config config = new Config();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        layout = (LinearLayout)  findViewById(R.id.layout);//�ҵ�����ռ�
        drawView = new DrawView(this);//�����Զ���Ŀؼ�
        //drawView.setMinimumHeight(300);
        //drawView.setMinimumWidth(500);
        //����layout�ĳ��� �ɱ��� 240/706
        layout.addView(drawView);//���Զ���Ŀؼ��������
        
        press = (Button)findViewById(R.id.btn_get_value);
        clean = (Button)findViewById(R.id.btn_clean);
        edit_mode = (EditText)findViewById(R.id.btn_mode);
        edit_start = (EditText)findViewById(R.id.edit_start);
        edit_end = (EditText)findViewById(R.id.edit_end);
        edit_stepparam = (EditText)findViewById(R.id.edit_stepparam);
        edit_distance = (EditText)findViewById(R.id.edit_distance);

        press.setOnClickListener(new OCL_Press());
        clean.setOnClickListener(new OCL_Clean());
        edit_distance.setOnEditorActionListener(new OCL_Distance());
        
        //��sd�������ò���ʾ������
        config.Read_SDtoView(edit_start,edit_end,edit_mode,edit_stepparam,edit_distance);
        //�������������
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        
        //����ֻ���Ļ�ĳ���
        DisplayMetrics  dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //�����ͼ�Ĳ���
        para_map = new Parameter_Map(dm.widthPixels,dm.heightPixels);
        //���ƹ켣
        trajectory = new Trajectory(para_map);
        
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

    	super.onResume();
	}

    protected void onStop(){
    	manager.unregisterListener(listener);
    	super.onStop();
    }
    
	private final class SensorListener implements SensorEventListener{
		
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}

		public void onSensorChanged(SensorEvent event) {
			if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
				data_sensor.set_accle(event.values);
				//������ʾ����Ļ��
				//�ֻ������µ���ֵ��values����ʾ�ܵ���A�ķ�������B��������ϵ��ʾA
				drawView.SetAcceleration_1(data_sensor.accelerometer[1], -data_sensor.accelerometer[0], -data_sensor.accelerometer[2]);
				
				//������У׼������
				function_app.selectfunction(config.MODE,data_sensor,trajectory,config,edit_start,edit_end,edit_stepparam);
				//����·��
				drawView.points1 = trajectory.getpath(para_map);
				
				drawView.Step = trajectory.stepcount;
				drawView.distance = trajectory.distance;
		        drawView.paintX=trajectory.StepTranslate[0];
				drawView.paintY=trajectory.StepTranslate[1];
				drawView.radius = para_map.every;
			}
			else if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){
				 data_sensor.set_orien(event.values);
				 //������ʾ����Ļ��
				 drawView.SetOrientation_1(data_sensor.orientation_trans[0], data_sensor.orientation_trans[1], data_sensor.orientation_trans[2]);
			}
			
			//�������»���
			drawView.IsInvalidate();
		}
	}
	
	//���Ĺ���ѡ��
	public static class Function_app
	{
		static final int NAVIGATE = 0;
		static final int CALIBRATE = 1;
		static final String [] name_mode = {"NAVIG","CALIB"};
		
		Navigation nvgt;
		Calibration clbrt;
		
		Function_app(Navigation ng,Calibration cl)
		{
			nvgt = ng;
			clbrt = cl;
		}
		
		//������У׼
		public void selectfunction(int fun, Data_Sensor ds, Trajectory tjctr,Config cf,EditText es, EditText ee, EditText estep)
		{
			if(fun == NAVIGATE) 
			{
				//navigation����
				nvgt.run_navigate(ds);
				nvgt.give_trajectinfo(tjctr);
			}
			else if(fun == CALIBRATE)
			{
				if(clbrt.state == true)
				{
					clbrt.run_calibrate(ds);
					clbrt.give_trajectinfo(tjctr);
				}
				else if(clbrt.state == false)
				{
					clbrt.end_calibrate(cf,es,ee,estep);
				}
			}
		}
	}
	
	public class RotateAndFilt
	{
		RotationMatrix RotaMatrix = new RotationMatrix();
		Filter FilterOfAccX = new Filter();
		Filter FilterOfAccY = new Filter();
		Filter FilterOfAccZ = new Filter();
		
		protected float [] rotate_filt(Data_Sensor ds)
		{
			float[] data_ori = ds.use_ori();
			float[] data_acc = ds.use_acc();
			
			//������ת����
			RotaMatrix.CalRotaMatrix(data_ori[0], data_ori[1], data_ori[2]);
			
			//����ת�������ľ�������ϵ�¼��ٶȵ�����ֵ 
			double[][] AbsCoodinate =  RotaMatrix.CalcuAbsCoodinate(data_acc[1], data_acc[0], data_acc[2]);
			float [] AbsCoodinate_filt = new float[3];
			if(AbsCoodinate != null)
			{
				//X��Y����5ֵ��ֵ�˲�
				AbsCoodinate_filt[0] = FilterOfAccX.AverageFiltering_manual((float)AbsCoodinate[0][0],5);
				AbsCoodinate_filt[1] = FilterOfAccY.AverageFiltering_manual((float)AbsCoodinate[1][0],5);
				//Z������36ֵ��ֵ�˲�������3ֵ��ֵ�˲�
				AbsCoodinate_filt[2] = FilterOfAccZ.AverageFiltering((float)AbsCoodinate[2][0]);
				drawView.SetAbsCoodinate_1(AbsCoodinate_filt[0], AbsCoodinate_filt[1], AbsCoodinate_filt[2]);
				/*GeneralTool.saveToSDcard((float)AbsCoodinate_filt[0],
										 (float)AbsCoodinate_filt[1],
										 (float)AbsCoodinate_filt[2],
										 "data_acc.txt");*/
			}
			return AbsCoodinate_filt;
		}
		void cleanall()
		{
        	//Filter���
        	FilterOfAccX.cleanall();
        	FilterOfAccY.cleanall();
        	FilterOfAccZ.cleanall();
		}
	}
	//������
	public class Navigation extends RotateAndFilt
	{
		PeakFinder PeFin = new PeakFinder();
		PeakFinder PeFin_X = new PeakFinder();
		PeakFinder PeFin_Y = new PeakFinder();
		StepDistCalculater SDCal = new StepDistCalculater();
		Statistic statistic = new Statistic();
		float angleTrans = 0;
		//���㵼������
		void run_navigate(Data_Sensor ds)
		{
			float[] data_ori_tans = ds.use_ori_trans();
			float [] AbsCoodinate_filt;
			//����ת�������ľ�������ϵ�¼��ٶȵ�����ֵ 
			AbsCoodinate_filt = rotate_filt(ds);
			//ȡ����ֵ�ͼ�Сֵ���Ӹ���ȥ�������Ӱ��
			PeFin.FindPeak(-AbsCoodinate_filt[2]);
			//�洢X Y���ֵ
			PeFin_X.StoreValue(AbsCoodinate_filt[0]);
			PeFin_Y.StoreValue(AbsCoodinate_filt[1]);
			//���㲽���Ͳ������ǲ�Ҳ����ȷ��
			SDCal.CalcuStepDist(PeFin,config.STEP_PARAM);
			if(SDCal.isStep == true)
			{
				//�ô������ϵ�µļ��ٶ���ȷ������
				angleTrans = Orientation_With_acceleration.OrientWithTime(PeFin, SDCal, PeFin_X.fArray3, PeFin_Y.fArray3, config.SFM1_4, config.EFM1_4);
				//GeneralTool.saveToSDcard(angleTrans);
				drawView.ori_acc = angleTrans; 
				//�洢����������ͼ���ķ����������������
				drawView.ori_increment = statistic.cal_sto_oriincre(angleTrans, data_ori_tans[0]);
				statistic.store_oriacc(angleTrans);
				statistic.store_orisen(data_ori_tans[0]);
				drawView.mean_orisensor = statistic.mean_orisen();
				drawView.mean_oriacc = statistic.mean_oriacc();
				/*GeneralTool.saveToSDcard(angleTrans,drawView.orientationA,"data_angel.txt");
				GeneralTool.saveToSDcard(PeFin.Circle*PeFin.bufflength3 + SDCal.PreMinValueIndex,
										 SDCal.PreMaxValueIndex < SDCal.PreMinValueIndex ? (PeFin.Circle*PeFin.bufflength3 + SDCal.PreMaxValueIndex):((PeFin.Circle-1)*PeFin.bufflength3 + SDCal.PreMaxValueIndex),
										"data_min_max_index.txt");*/
			}
		}
		
		void give_trajectinfo(Trajectory tjctr)
		{
			tjctr.isstep = SDCal.isStep;
			tjctr.distonestep = SDCal.DistanceOneStep;
			tjctr.angle = angleTrans;
			tjctr.stepcount = SDCal.StepCount;
		}
		
		void cleanall()
		{
			super.cleanall();
            //ͳ���������
            statistic.cleanalldata();
            //PeakFinder���
        	PeFin.cleanall();
        	PeFin_X.cleanall();
        	PeFin_Y.cleanall();
        	//StepDistCalculater���
        	SDCal.cleanall();
		}
	}
	
	public class Calibration extends RotateAndFilt
	{
		PeakFinder PeFin = new PeakFinder();
		PeakFinder PeFin_X = new PeakFinder();
		PeakFinder PeFin_Y = new PeakFinder();
		StepDistCalculater SDCal = new StepDistCalculater();
		Statistic statistic = new Statistic();
		float angleTrans = 0;
		
		int count_step = 0;
		int count_cali = 0;
		
		boolean state = false;
		
		public void run_calibrate(Data_Sensor ds)
		{
			float[] data_ori_tans = ds.use_ori_trans();
			float [] AbsCoodinate_filt;
			//����ת�������ľ�������ϵ�¼��ٶȵ�����ֵ 
			AbsCoodinate_filt = rotate_filt(ds);
			//ȡ����ֵ�ͼ�Сֵ���Ӹ���ȥ�������Ӱ��
			PeFin.FindPeak(-AbsCoodinate_filt[2]);
			//�洢X Y���ֵ
			PeFin_X.StoreValue(AbsCoodinate_filt[0]);
			PeFin_Y.StoreValue(AbsCoodinate_filt[1]);
			//���㲽���Ͳ������ǲ�Ҳ����ȷ��
			SDCal.CalcuStepDist(PeFin,config.STEP_PARAM);
			
			if(SDCal.isStep == true)
			{
				//�洢���岨��ֵ���±�
				statistic.store_crest(SDCal.PreMaxValue, SDCal.PreMaxValueIndex);
				statistic.store_valley(SDCal.PreMinValue, SDCal.PreMinValueIndex);
				//�洢����������
				statistic.store_orisen_calibrate(count_cali,count_step,data_ori_tans[0]);
				int start,end;
				for(int interal=0;interal<5;interal++)
				{
					for(int j=0;j<10;j++)
					{
						start = -5+j;
						end = start + interal;
						angleTrans = Orientation_With_acceleration.OrientWithTime(PeFin, SDCal, PeFin_X.fArray3, PeFin_Y.fArray3, start, end);
						statistic.store_oriacc_calibrate(count_cali,count_step,interal,j,angleTrans);
					}
				}
				count_step++;
				statistic.store_oriacc(angleTrans);
				/*GeneralTool.saveToSDcard(angleTrans,drawView.orientationA,"data_angel.txt");
				GeneralTool.saveToSDcard(PeFin.Circle*PeFin.bufflength3 + SDCal.PreMinValueIndex,
										 SDCal.PreMaxValueIndex < SDCal.PreMinValueIndex ? (PeFin.Circle*PeFin.bufflength3 + SDCal.PreMaxValueIndex):((PeFin.Circle-1)*PeFin.bufflength3 + SDCal.PreMaxValueIndex),
										"data_min_max_index.txt");*/
			}
		}
		
		//ȥ������
		void end_calibrate(Config cf,EditText es, EditText ee, EditText estep)
		{
			int temp[];
			if(count_step <= 0)
				return;
			//�������0ʱ�����㲽������
			if(cf.DISTANCE > 0)
			{
				cf.STEP_PARAM = statistic.calcu_stepdisparam_byWeinberg(cf.DISTANCE);
				estep.setText(""+cf.STEP_PARAM);
			}
			
			statistic.mean_orisen_calibrate(count_cali, count_step);
			statistic.mean_oriacc_calibrate(count_cali, count_step);
			//���������Ҫ������䲢����
			statistic.calcu_orientparam();
			//����ȷ�������䣨��ʼλ�ã����䳤�ȣ�
			temp = statistic.getoneparam();
			if(temp == null)
				temp = new int[] {0,-100};
			
			cf.SFM1_4 = temp[1]-5;
			cf.EFM1_4 = temp[1]-5+temp[0];
	        es.setText(""+cf.SFM1_4);
	        ee.setText(""+cf.EFM1_4);
	        count_step = 0;
			count_cali++;
			if(count_cali == 2)
			{
	            //ͳ���������
	            statistic.cleanalldata();
	            count_cali=0;
			}
			else
				statistic.cleanstepdata();
		}
		
		void give_trajectinfo(Trajectory tjctr)
		{
			tjctr.isstep = SDCal.isStep;
			tjctr.distonestep = SDCal.DistanceOneStep;
			tjctr.angle = angleTrans;
			tjctr.stepcount = SDCal.StepCount;
		}
		
		void cleanall()
		{
			super.cleanall();
            //ͳ���������
            statistic.cleanalldata();
            //PeakFinder���
        	PeFin.cleanall();
        	PeFin_X.cleanall();
        	PeFin_Y.cleanall();
        	//StepDistCalculater���
        	SDCal.cleanall();

    		count_step = 0;
    		count_cali = 0;
    		
    		state = false;
		}
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
		float[] StepTranslate = new float[]{0,0};
		float AngleSin = 0;
		float AngleCos = 0;
		//·����Ϣ
		int iLastIndex = 0;
		int bufflength = 1024; 
		float[] pointsLine = new float[32];
		//�ܳ���
		float distance = 0;
		
		float[] getpath(Parameter_Map pm)
		{
			if(true == isstep)
			{
				//����cos��sinֵ
				trajectory.calcu_sincos(angle);
				//������һ��֮������ڵ�ͼ��λ��
				trajectory.Trans(distonestep,pm.every);
				//����·��
				trajectory.GetPointsLine();
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
			//��ʼλ������
    		StepTranslate[0] = pm.screenWidth/2;
            StepTranslate[1] = 100*pm.every/2;
		}
		
		public void Trans(float DistOneStep,float unit){
				//��Ա�������ֻ���ȷ���ı仯
				StepTranslate[0] = StepTranslate[0] -	(float) ((DistOneStep*AngleCos)/1.5)*unit;
				//��Ա�������ֻ��߶ȷ���ı仯
				StepTranslate[1] = StepTranslate[1] + (float) ((DistOneStep*AngleSin)/1.5)*unit;
				//���ߵľ���
				distance =distance+DistOneStep;
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
    
	class Config
	{
		//startFromMiddle 1/4
		int SFM1_4;
		int EFM1_4;
		//��ǰ����ģʽ
		int MODE;
		//��������
		//STEP_PARAM 0.1737,0.2314,0.1489;
		float STEP_PARAM;
		float DISTANCE;
		
		String file_config = "config_ondraw.txt";
		
		//��es--SFM1_4��ee--EFM1_4��md--MODE��sp--STEP_PARAM���浽SD����
		public void Read_ViewtoSD(EditText es,EditText ee,EditText md, EditText sp, EditText ed)
		{
        	//�ӽ����ȡ����
        	MODE =  md.getText().toString().equalsIgnoreCase("c") == true ? Function_app.CALIBRATE:Function_app.NAVIGATE;
        	SFM1_4 = Integer.parseInt(es.getText().toString());  
        	EFM1_4 = Integer.parseInt(ee.getText().toString());
        	STEP_PARAM = Float.parseFloat(sp.getText().toString());
        	DISTANCE = Float.parseFloat(ed.getText().toString());
        	//��ʾ��Ϣ
        	md.setText("");
        	md.setHint(Function_app.name_mode[MODE]);

        	//�洢����
        	GeneralTool.removefile(file_config);
        	GeneralTool.saveToSDcard(SFM1_4,file_config);
        	GeneralTool.saveToSDcard(EFM1_4,file_config);
        	GeneralTool.saveToSDcard(MODE,file_config);
        	GeneralTool.saveToSDcard(STEP_PARAM,file_config);
        	GeneralTool.saveToSDcard(DISTANCE,file_config);
		}
		
		public void Read_SDtoView(EditText es,EditText ee, EditText md, EditText sp, EditText ed)
		{
			
	        float rfdata[] = {0,0,0,(float) 0.6,30};
	        GeneralTool.read2vFromSDcard_value(file_config, rfdata, 5);
	        SFM1_4 = (int)rfdata[0];
	        EFM1_4 = (int)rfdata[1];
	        MODE = (int)rfdata[2];
	        STEP_PARAM = rfdata[3];
	        DISTANCE = rfdata[4];
	        
	        es.setText(""+SFM1_4);
	        ee.setText(""+EFM1_4);
	        sp.setText(""+STEP_PARAM);
	        ed.setText(""+DISTANCE);
        	//��ʾ��Ϣ
        	md.setText("");
	        md.setHint(Function_app.name_mode[MODE]);
		}
	}
    
	class OCL_Press implements OnClickListener
	{
		@Override
        public void onClick(View v) {  
			if(config.MODE == function_app.NAVIGATE)
			{
	        	//���ô���ͼ�洢��sd��
	        	config.Read_ViewtoSD(edit_start,edit_end,edit_mode,edit_stepparam,edit_distance);
			}
			else
			{
				if(function_app.NAVIGATE == (edit_mode.getText().toString().equalsIgnoreCase("n") == true ? Function_app.NAVIGATE:Function_app.CALIBRATE))
				{
					//���ô���ͼ�洢��sd��
		        	config.Read_ViewtoSD(edit_start,edit_end,edit_mode,edit_stepparam,edit_distance);
		        	((Button)v).setText("PRESS");
				}
				else
				{
					calibration.state = !calibration.state;
					if(calibration.state == false)
						((Button)v).setText("ENDED");
					else
						((Button)v).setText("RUNNING");;
				}
			}
        	//���ؼ���
            final InputMethodManager imm = (InputMethodManager)getSystemService(
            	      Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }  
	}
	
	class OCL_Clean implements OnClickListener
	{
    	@Override
        public void onClick(View v) {
    		//��ͼ���
    		drawView.clean();
        	navigation.cleanall();
        	calibration.cleanall();
    		//�켣���,λ�ûع�
        	trajectory = new Trajectory(para_map);
        } 
	}
	
	class OCL_Distance implements OnEditorActionListener
	{
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			//���ؼ���
			final InputMethodManager imm = (InputMethodManager)getSystemService(
            	      Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            config.DISTANCE = Float.parseFloat(edit_distance.getText().toString());;
			return false;
		}  
	}
	
	public class Data_Sensor
	{
		//�ֻ�������
		private float [] accelerometer = {0,0,0};
		
		private float [] orientation = {0,0,0};
		// �������ķ���Ƕ�
		private float [] orientation_trans = {0,0,0};
		//��¼����һ�α�ʹ�ú��µ������ݸ���
		int count_orient = 0;
		int count_orient_trans = 0;
		int count_accle = 0;
		
		void set_accle(float acc[])
		{
			data_sensor.accelerometer = acc;
			count_accle++;
		}
		
		void set_orien(float ori[])
		{
			 orientation = ori;
			 TranslateOrient();
			 count_orient++;
			 count_orient_trans++;
		}
		//���ֻ����귽�򴫸�������ֵת�������������
		public void TranslateOrient()
		{
			 //��ʼ�ֻ�����ˮƽ��̬
			 //yaw��ƫ��˳ʱ������ ��0��360��
			 orientation_trans[0] = 360 - orientation[0];
			 //pitch��б��������ת��Ȧ  ��0��-180�� ������ת��Ȧ��180��0��
			 orientation_trans[1] = -orientation[1];
			 //roll���������泯�ϴ��� ˳ʱ��תһȦ ��0��-90����-90,0����0��90����90,0��
			 orientation_trans[2] = orientation[2];
		}
		
		float[] use_ori()
		{
			reset_count_ori();
			return orientation;
		}
		
		float[] use_ori_trans()
		{
			reset_count_ori_trans();
			return orientation_trans;
		}
		
		float[] use_acc()
		{
			reset_count_acc();
			return accelerometer;
		}
		
		void reset_count_ori()
		{
			count_orient = 0;
		}
		
		void reset_count_ori_trans()
		{
			count_orient_trans = 0;
		}
		
		void reset_count_acc()
		{
			count_accle = 0;
		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu){
		
		getMenuInflater().inflate(R.menu.main,menu);
		return true;
	}
}
