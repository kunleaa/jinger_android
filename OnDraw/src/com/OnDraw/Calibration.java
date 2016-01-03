package com.OnDraw;

public class Calibration extends RotateAndFilt
{
	PeakFinder PeFin = new PeakFinder();
	PeakFinder PeFin_X = new PeakFinder();
	PeakFinder PeFin_Y = new PeakFinder();
	StepDistCalculater SDCal = new StepDistCalculater();
	Statistic statistic = new Statistic();
	float angleTrans = 0;
	
	int count_step = 0;
	int count_cali = 0;
	
	boolean state = false;
	
	public void run_calibrate(Data_Sensor ds, DrawView dv, Config cfg)
	{
		float[] data_ori_tans = ds.use_ori_trans();
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
			//存储波峰波谷值及下标
			statistic.store_crest(SDCal.PreMaxValue, SDCal.PreMaxValueIndex);
			statistic.store_valley(SDCal.PreMinValue, SDCal.PreMinValueIndex);
			//存储传感器方向
			statistic.store_orisen_calibrate(count_cali,count_step,data_ori_tans[0]);
			int start,end;
			for(int interal=0;interal<5;interal++)
			{
				for(int j=0;j<10;j++)
				{
					start = -5+j;
					end = start + interal;
					angleTrans = Orientation_With_acceleration.OrientWithTime(PeFin, SDCal, PeFin_X.fArray3, PeFin_Y.fArray3, start, end);
					statistic.store_oriacc_calibrate(count_cali,count_step,interal,j,angleTrans);
				}
			}
			count_step++;
			statistic.store_oriacc(angleTrans);
			/*GeneralTool.saveToSDcard(angleTrans,drawView.orientationA,"data_angel.txt");
			GeneralTool.saveToSDcard(PeFin.Circle*PeFin.bufflength3 + SDCal.PreMinValueIndex,
									 SDCal.PreMaxValueIndex < SDCal.PreMinValueIndex ? (PeFin.Circle*PeFin.bufflength3 + SDCal.PreMaxValueIndex):((PeFin.Circle-1)*PeFin.bufflength3 + SDCal.PreMaxValueIndex),
									"data_min_max_index.txt");*/
		}
	}
	
	//去顶区间
	void end_calibrate(Config cf, Controller_View cv)
	{
		int temp[];
		if(count_step <= 0)
			return;
		//距离大于0时，计算步长参数
		if(cf.DISTANCE > 0)
		{
			cf.STEP_PARAM = statistic.calcu_stepdisparam_byWeinberg(cf.DISTANCE);
			cv.edit_stepparam.setText(""+GeneralTool.cut_decimal(cf.STEP_PARAM,2));
		}
		
		statistic.mean_orisen_calibrate(count_cali, count_step);
		statistic.mean_oriacc_calibrate(count_cali, count_step);
		//计算出符合要求的区间并保存
		statistic.calcu_orientparam();
		//返回确定的区间（起始位置，区间长度）
		temp = statistic.getoneparam();
		if(temp == null)
			temp = new int[] {0,-100};
		
		cf.SFM1_4 = temp[1]-5;
		cf.EFM1_4 = temp[1]-5+temp[0];
        cv.edit_start.setText(""+cf.SFM1_4);
        cv.edit_end.setText(""+cf.EFM1_4);
        count_step = 0;
		count_cali++;
		if(count_cali == 2)
		{
            //统计数据清空
            statistic.cleanalldata();
            count_cali=0;
		}
		else
			statistic.cleanstepdata();
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

		count_step = 0;
		count_cali = 0;
		
		state = false;
	}
}

