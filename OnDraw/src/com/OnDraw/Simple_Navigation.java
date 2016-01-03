package com.OnDraw;
public class Simple_Navigation extends RotateAndFilt{
	PeakFinder PeFin = new PeakFinder();
	StepDistCalculater SDCal = new StepDistCalculater();
	float angleTrans = 0;
	void run_simple_navigate(Data_Sensor ds, DrawView dv, Config cfg)
	{
		float [] data_ori_tans = ds.use_ori_trans();
		float [] AbsCoodinate_filt;
		//����ת�������ľ�������ϵ�¼��ٶȵ�����ֵ 
		AbsCoodinate_filt = rotate_filt(ds,dv);
		angleTrans = data_ori_tans[0];
		//ȡ����ֵ�ͼ�Сֵ���Ӹ���ȥ�������Ӱ��
		PeFin.FindPeak(-AbsCoodinate_filt[2]);
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