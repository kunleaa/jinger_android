package com.OnDraw;

import android.widget.EditText;

public class Config
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
    	MODE = parse_mode(md);
    	SFM1_4 = Integer.parseInt(es.getText().toString());  
    	EFM1_4 = Integer.parseInt(ee.getText().toString());
    	STEP_PARAM = Float.parseFloat(sp.getText().toString());
    	DISTANCE = Float.parseFloat(ed.getText().toString());
    	//��ʾ��Ϣ
    	md.setText("");
    	md.setHint(Selector_Model.name_mode[MODE]);

    	//�洢����
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
        cv.edit_stepparam.setText(""+GeneralTool.cut_decimal(STEP_PARAM,2));
        //��ȡС�����2��λ����ʾ
        cv.edit_distance.setText(""+DISTANCE);

    	//��ʾ��Ϣ
        cv.edit_mode.setText("");
        cv.edit_mode.setHint(Selector_Model.name_mode[MODE]);
	}
	//�ӿؼ��н���Ҫ���õ�ģʽ
	int parse_mode(EditText md)
	{
		int mode = Selector_Model.SIMPLE_NAVIGATE;
    	String str_mode = md.getText().toString();
		if(str_mode.equalsIgnoreCase("c") == true)
			mode = Selector_Model.CALIBRATE;
    	else if(str_mode.equalsIgnoreCase("n") == true)
    		mode = Selector_Model.NAVIGATE;
    	else if(str_mode.equalsIgnoreCase("s") == true)
    		mode = Selector_Model.SIMPLE_NAVIGATE;
		return mode;
	}
}