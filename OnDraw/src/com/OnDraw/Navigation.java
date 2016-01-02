package com.OnDraw;

//������
public class Navigation extends RotateAndFilt
{
	PeakFinder PeFin = new PeakFinder();
	PeakFinder PeFin_X = new PeakFinder();
	PeakFinder PeFin_Y = new PeakFinder();
	StepDistCalculater SDCal = new StepDistCalculater();
	Statistic statistic = new Statistic();
	float angleTrans = 0;
	//���㵼������
	void run_navigate(Data_Sensor ds, DrawView dv, Config cfg)
	{
		float[] data_ori_tans = ds.use_ori_trans();
		float [] AbsCoodinate_filt;
		//����ת�������ľ�������ϵ�¼��ٶȵ�����ֵ 
		AbsCoodinate_filt = rotate_filt(ds,dv);
		//ȡ����ֵ�ͼ�Сֵ���Ӹ���ȥ�������Ӱ��
		PeFin.FindPeak(-AbsCoodinate_filt[2]);
		//�洢X Y���ֵ
		PeFin_X.StoreValue(AbsCoodinate_filt[0]);
		PeFin_Y.StoreValue(AbsCoodinate_filt[1]);
		//���㲽���Ͳ������ǲ�Ҳ����ȷ��
		SDCal.CalcuStepDist(PeFin,cfg.STEP_PARAM);
		if(SDCal.isStep == true)
		{
			//�ô������ϵ�µļ��ٶ���ȷ������
			angleTrans = Orientation_With_acceleration.OrientWithTime(PeFin, SDCal, PeFin_X.fArray3, PeFin_Y.fArray3, cfg.SFM1_4, cfg.EFM1_4);
			//GeneralTool.saveToSDcard(angleTrans);
			dv.ori_acc = angleTrans; 
			//�洢����������ͼ���ķ����������������
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
        //ͳ���������
        statistic.cleanalldata();
        //PeakFinder���
	  	PeFin.cleanall();
	  	PeFin_X.cleanall();
	  	PeFin_Y.cleanall();
	  	//StepDistCalculater���
	  	SDCal.cleanall();
	}
}