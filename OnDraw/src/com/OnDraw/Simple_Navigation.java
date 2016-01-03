package com.OnDraw;
public class Simple_Navigation{
	PeakFinder PeFin = new PeakFinder();
	StepDistCalculater SDCal = new StepDistCalculater();
	float angleTrans = 0;
	void run_simple_navigate(Data_Sensor ds, DrawView dv, Config cfg)
	{
		float[] data_ori_tans = ds.use_ori_trans();
		float[] data_acc = ds.use_acc();
		angleTrans = data_ori_tans[0];
		//取极大值和极小值，加负号去掉方向的影响
		PeFin.FindPeak(-data_acc[2]);
		SDCal.CalcuStepDist(PeFin,cfg.STEP_PARAM);
		if(SDCal.isStep == true)
		{
			dv.ori_acc = angleTrans; 
		}
	}
	
	void give_trajectinfo(DrawView dv)
	{
		dv.trajectory.isstep = SDCal.isStep;
		dv.trajectory.distonestep = SDCal.DistanceOneStep;
		dv.trajectory.angle = angleTrans;
		dv.trajectory.stepcount = SDCal.StepCount;
	}
	
	void cleanall()
	{
		PeFin.cleanall();
		SDCal.cleanall();
		angleTrans = 0;
	}
}