package com.OnDraw;

//导航类
public class Navigation extends RotateAndFilt
{
	PeakFinder PeFin = new PeakFinder();
	PeakFinder PeFin_X = new PeakFinder();
	PeakFinder PeFin_Y = new PeakFinder();
	StepDistCalculater SDCal = new StepDistCalculater();
	Statistic statistic = new Statistic();
	float angleTrans = 0;
	//计算导航坐标
	void run_navigate(Data_Sensor ds, DrawView dv, Config cfg)
	{
		float [] data_ori_tans = ds.use_ori_trans();
		float [] AbsCoodinate_filt;
		//由旋转矩阵计算的绝对坐标系下加速度的坐标值 
		AbsCoodinate_filt = rotate_filt(ds,dv);
		//取极大值和极小值，加负号去掉方向的影响
		PeFin.FindPeak(-AbsCoodinate_filt[2]);
		//存储X Y轴的值
		PeFin_X.StoreValue(AbsCoodinate_filt[0]);
		PeFin_Y.StoreValue(AbsCoodinate_filt[1]);
		//计算步长和步数，记步也是正确的
		SDCal.CalcuStepDist(PeFin,cfg.STEP_PARAM);
		if(SDCal.isStep == true)
		{
			//用大地坐标系下的加速度来确定方向
			angleTrans = Orientation_With_acceleration.OrientWithTime(PeFin, SDCal, PeFin_X.fArray3, PeFin_Y.fArray3, cfg.SFM1_4, cfg.EFM1_4);
			//GeneralTool.saveToSDcard(angleTrans);
			dv.ori_acc = angleTrans; 
			//存储传感器方向和计算的方向和两个方向增量
			dv.ori_increment = statistic.cal_sto_oriincre(angleTrans, data_ori_tans[0]);
			statistic.store_oriacc(angleTrans);
			statistic.store_orisen(data_ori_tans[0]);
			dv.mean_orisensor = statistic.mean_orisen();
			dv.mean_oriacc = statistic.mean_oriacc();
			/*GeneralTool.saveToSDcard(angleTrans,drawView.orientationA,"data_angel.txt");
			GeneralTool.saveToSDcard(PeFin.Circle*PeFin.bufflength3 + SDCal.PreMinValueIndex,
									 SDCal.PreMaxValueIndex < SDCal.PreMinValueIndex ? (PeFin.Circle*PeFin.bufflength3 + SDCal.PreMaxValueIndex):((PeFin.Circle-1)*PeFin.bufflength3 + SDCal.PreMaxValueIndex),
									"data_min_max_index.txt");*/
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
		super.cleanall();
        //统计数据清空
        statistic.cleanalldata();
        //PeakFinder清空
	  	PeFin.cleanall();
	  	PeFin_X.cleanall();
	  	PeFin_Y.cleanall();
	  	//StepDistCalculater清空
	  	SDCal.cleanall();
	  	angleTrans = 0;
	}
}