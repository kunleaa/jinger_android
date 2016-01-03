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
    Selector_Model selector_model;
    Config config;
    DrawView drawView;
	
	Controller_View(Activity actvt,Selector_Model fctap, Config cfg, DrawView dv)
	{
		activity = actvt;
		selector_model = fctap;
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
        edit_mode.setOnEditorActionListener(new OCL_Mode());
        edit_start.setOnEditorActionListener(new OCL_Start());
        edit_end.setOnEditorActionListener(new OCL_End());
        edit_stepparam.setOnEditorActionListener(new OCL_StepParam());
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
				selector_model.clbrt.state = !selector_model.clbrt.state;
				if(selector_model.clbrt.state == false)
					((Button)v).setText("ENDED");
				else
					((Button)v).setText("RUNNING");
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
    		selector_model.nvgt.cleanall();
        	selector_model.clbrt.cleanall();
        	press.setText("ENDED");
    		//轨迹清空,位置回归
        	drawView.trajectory.cleanalldata();
        } 
	}
	class OCL_Mode implements OnEditorActionListener
	{
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			//隐藏键盘
			final InputMethodManager imm = (InputMethodManager)activity.getSystemService(
            	      Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            config.MODE = config.parse_mode((EditText)v);
        	//提示信息
        	v.setText("");
        	v.setHint(Selector_Model.name_mode[config.MODE]);
        	
        	//适配修改button的提示信息
        	if(config.MODE == Selector_Model.NAVIGATE)
        		press.setText("STORE");
        	else if(config.MODE == Selector_Model.CALIBRATE)
        	{
        		if(selector_model.clbrt.state == false)
        			press.setText("ENDED");
				else
					press.setText("RUNNING");
        	}
        	else if(config.MODE == Selector_Model.SIMPLE_NAVIGATE)
        		press.setText("STORE");
        	
			return false;
		}  
	}
	class OCL_Start implements OnEditorActionListener
	{
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			//隐藏键盘
			final InputMethodManager imm = (InputMethodManager)activity.getSystemService(
            	      Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            config.SFM1_4 = Integer.parseInt(v.getText().toString());
			return false;
		}  
	}
	class OCL_End implements OnEditorActionListener
	{
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			//隐藏键盘
			final InputMethodManager imm = (InputMethodManager)activity.getSystemService(
            	      Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            config.EFM1_4 = Integer.parseInt(v.getText().toString());;
			return false;
		}  
	}
	class OCL_StepParam implements OnEditorActionListener
	{
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			//隐藏键盘
			final InputMethodManager imm = (InputMethodManager)activity.getSystemService(
            	      Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            config.STEP_PARAM = Float.parseFloat(v.getText().toString());;
			return false;
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
            config.DISTANCE = Float.parseFloat(v.getText().toString());;
			return false;
		}  
	}
}