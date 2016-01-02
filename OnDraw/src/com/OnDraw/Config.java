package com.OnDraw;

import android.widget.EditText;

public class Config
{
	//startFromMiddle 1/4
	int SFM1_4;
	int EFM1_4;
	//当前运行模式
	int MODE;
	//步长参数
	//STEP_PARAM 0.1737,0.2314,0.1489;
	float STEP_PARAM;
	float DISTANCE;
	
	String file_config = "config_ondraw.txt";
	
	//把es--SFM1_4，ee--EFM1_4，md--MODE，sp--STEP_PARAM保存到SD卡中
	public void Read_ViewtoSD(EditText es,EditText ee,EditText md, EditText sp, EditText ed)
	{
    	//从界面获取参数
    	MODE =  md.getText().toString().equalsIgnoreCase("c") == true ? Selector_Model.CALIBRATE:Selector_Model.NAVIGATE;
    	SFM1_4 = Integer.parseInt(es.getText().toString());  
    	EFM1_4 = Integer.parseInt(ee.getText().toString());
    	STEP_PARAM = Float.parseFloat(sp.getText().toString());
    	DISTANCE = Float.parseFloat(ed.getText().toString());
    	//提示信息
    	md.setText("");
    	md.setHint(Selector_Model.name_mode[MODE]);

    	//存储参数
    	GeneralTool.removefile(file_config);
    	GeneralTool.saveToSDcard(SFM1_4,file_config);
    	GeneralTool.saveToSDcard(EFM1_4,file_config);
    	GeneralTool.saveToSDcard(MODE,file_config);
    	GeneralTool.saveToSDcard(STEP_PARAM,file_config);
    	GeneralTool.saveToSDcard(DISTANCE,file_config);
	}
	
	public void Read_SDtoView(Controller_View cv)
	{
		
        float rfdata[] = {0,0,0,(float) 0.6,30};
        GeneralTool.read2vFromSDcard_value(file_config, rfdata, 5);
        SFM1_4 = (int)rfdata[0];
        EFM1_4 = (int)rfdata[1];
        MODE = (int)rfdata[2];
        STEP_PARAM = rfdata[3];
        DISTANCE = rfdata[4];
        
        cv.edit_start.setText(""+SFM1_4);
        cv.edit_end.setText(""+EFM1_4);
        cv.edit_stepparam.setText(""+STEP_PARAM);
        cv.edit_distance.setText(""+DISTANCE);

    	//提示信息
        cv.edit_mode.setText("");
        cv.edit_mode.setHint(Selector_Model.name_mode[MODE]);
	}
}