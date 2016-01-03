package com.OnDraw;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.LinearLayout;

public class OnDrawActivity extends Activity {
	
	private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                DisplayMetrics  dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
    			drawView.para_map.set_paramter_map(dm.heightPixels);
            }
            super.handleMessage(msg);
        }
    };
	
	//传感器设备相关
	private SensorManager manager;
	private SensorListener listener = new SensorListener();
	Data_Sensor data_sensor= new Data_Sensor();
	//视图显示
	private LinearLayout layout;
	DrawView drawView;
	//视图控件
	Controller_View controller_view;
	//配置相关
	Config config = new Config();
	//功能相关
	Selector_Model selector_model = new Selector_Model();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        layout = (LinearLayout)  findViewById(R.id.layout);//找到这个空间
        drawView = new DrawView(this);//创建自定义的控件
        layout.addView(drawView);//将自定义的控件进行添加
        
        controller_view = new Controller_View(this,selector_model,config,drawView);
        //从sd卡读配置并显示到界面
        config.Read_SDtoView(controller_view);
        //传感器管理服务
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		//调用重新绘制
		drawView.IsInvalidate();
		
        //设置地图长宽适当的值
        Message msg = new Message();
        msg.what = 0;
        handler.sendMessageDelayed(msg,1);
    }
    
    protected void onResume() {
		//监听加速度传感器TYPE_ACCELEROMETER
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
		
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}

		public void onSensorChanged(SensorEvent event) {
			if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
				data_sensor.set_accle(event.values);
				//数据显示到屏幕上
				//手机坐标下的数值（values）表示受到力A的反作用力B，新坐标系表示A
				drawView.SetAcceleration_1(data_sensor.use_acc()[1], -data_sensor.use_acc()[0], -data_sensor.use_acc()[2]);
				//导航？校准（方向）
				selector_model.selectfunction(config,data_sensor,controller_view,drawView);
				drawView.trajectory.setpaintdata();
			}
			else if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){
				 data_sensor.set_orien(event.values);
				 //数据显示到屏幕上
				 drawView.SetOrientation_1(data_sensor.use_ori_trans()[0], data_sensor.use_ori_trans()[1], data_sensor.use_ori_trans()[2]);
			}
			//调用重新绘制
			drawView.IsInvalidate();
		}
	}
}
