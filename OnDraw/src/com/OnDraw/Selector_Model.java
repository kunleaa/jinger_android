package com.OnDraw;

//核心功能选择
public class Selector_Model
{
	static final int NAVIGATE = 0;
	static final int CALIBRATE = 1;
	static final int SIMPLE_NAVIGATE = 2;
	static final String [] name_mode = {"NAVIG","CALIB","SIM_NAVI"};
	
	Navigation nvgt = new Navigation();
	Calibration clbrt = new Calibration();
	Simple_Navigation spnvgt = new Simple_Navigation();
	
	//导航？校准
	public void selectfunction(Config cf, Data_Sensor ds, Controller_View cv, DrawView dv)
	{
		if(cf.MODE == NAVIGATE) 
		{
			//navigation功能
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
		else if(cf.MODE == SIMPLE_NAVIGATE) 
		{
			spnvgt.run_simple_navigate(ds, dv, cf);
			spnvgt.give_trajectinfo(dv);
		}
	}
}

