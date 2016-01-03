package com.OnDraw;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class Controller_View
{
    //显示控件相关
    public Button press;
    public Button clean;
    public EditText edit_mode;
    public EditText edit_start;
    public EditText edit_end;
    public EditText edit_stepparam;
    public EditText edit_distance;
    
    //方法中使用到的一些对象
    Activity activity;
    Selector_Model function_app;
    Config config;
    DrawView drawView;
	
	Controller_View(Activity actvt,Selector_Model fctap, Config cfg, DrawView dv)
	{
		activity = actvt;
		function_app = fctap;
		config = cfg;
		drawView = dv;
		
        press = (Button)actvt.findViewById(R.id.btn_get_value);
        clean = (Button)actvt.findViewById(R.id.btn_clean);
        edit_mode = (EditText)actvt.findViewById(R.id.btn_mode);
        edit_start = (EditText)actvt.findViewById(R.id.edit_start);
        edit_end = (EditText)actvt.findViewById(R.id.edit_end);
        edit_stepparam = (EditText)actvt.findViewById(R.id.edit_stepparam);
        edit_distance = (EditText)actvt.findViewById(R.id.edit_distance);

        press.setOnClickListener(new OCL_Press());
        clean.setOnClickListener(new OCL_Clean());
        edit_distance.setOnEditorActionListener(new OCL_Distance());
	}
	
	class OCL_Press implements OnClickListener
	{
		@Override
        public void onClick(View v) {
			if(config.MODE == Selector_Model.NAVIGATE)
			{
	        	//配置从视图存储到sd卡
	        	config.Read_ViewtoSD(edit_start,edit_end,edit_mode,edit_stepparam,edit_distance);
			}
			else if(config.MODE == Selector_Model.CALIBRATE)
			{
				if(edit_mode.getText().toString().equalsIgnoreCase("n") == true || edit_mode.getText().toString().equalsIgnoreCase("s") == true)
				{
					//配置从视图存储到sd卡
		        	config.Read_ViewtoSD(edit_start,edit_end,edit_mode,edit_stepparam,edit_distance);
		        	((Button)v).setText("PRESS");
				}
				else
				{
					function_app.clbrt.state = !function_app.clbrt.state;
					if(function_app.clbrt.state == false)
						((Button)v).setText("ENDED");
					else
						((Button)v).setText("RUNNING");
				}
			}
			else if(config.MODE == Selector_Model.SIMPLE_NAVIGATE)
			{
				//配置从视图存储到sd卡
	        	config.Read_ViewtoSD(edit_start,edit_end,edit_mode,edit_stepparam,edit_distance);
			}
        	//隐藏键盘
            final InputMethodManager imm = (InputMethodManager)activity.getSystemService(
            	      Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }  
	}
	
	class OCL_Clean implements OnClickListener
	{
    	@Override
        public void onClick(View v) {
    		//绘图清空
    		drawView.clean();
    		function_app.nvgt.cleanall();
        	function_app.clbrt.cleanall();
    		//轨迹清空,位置回归
        	drawView.trajectory.cleanalldata();
        } 
	}
	
	class OCL_Distance implements OnEditorActionListener
	{
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			//隐藏键盘
			final InputMethodManager imm = (InputMethodManager)activity.getSystemService(
            	      Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            config.DISTANCE = Float.parseFloat(edit_distance.getText().toString());;
			return false;
		}  
	}
}