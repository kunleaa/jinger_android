package com.OnDraw;

//���Ĺ���ѡ��
public class Selector_Model
{
	static final int NAVIGATE = 0;
	static final int CALIBRATE = 1;
	static final String [] name_mode = {"NAVIG","CALIB"};
	
	Navigation nvgt = new Navigation();
	Calibration clbrt = new Calibration();
	
	Selector_Model(){}
	
	//������У׼
	public void selectfunction(Config cf, Data_Sensor ds, Controller_View cv, DrawView dv)
	{
		if(cf.MODE == NAVIGATE) 
		{
			//navigation����
			nvgt.run_navigate(ds,dv,cf);
			nvgt.give_trajectinfo(dv);
		}
		else if(cf.MODE == CALIBRATE)
		{
			if(clbrt.state == true)
			{
				clbrt.run_calibrate(ds,dv,cf);
				clbrt.give_trajectinfo(dv);
			}
			else if(clbrt.state == false)
			{
				clbrt.end_calibrate(cf,cv);
			}
		}
	}
}

