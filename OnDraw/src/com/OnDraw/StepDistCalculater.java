package com.OnDraw;

public class StepDistCalculater{
	//当前最大值
	float MaxValue = 0;
	//步数计数器
	int StepCount = 0;
	//当前最大值下标
	int MaxValueIndex = 0;
	//一步的距离
	float DistanceOneStep = 0;
	//前一个最大值
	float PreMaxValue = 0;
	//前一个最大值下标
	int PreMaxValueIndex = 0;
	//前一个最小值
	float PreMinValue = 0;
	//前一个最小值下标
	int PreMinValueIndex = 0;
	//此时刻是否走出一步
	int isStep = 0;
	
	public void CalcuStepDist(PeakFinder PeFin)
	{
		isStep = 0;
		//不是极值
		if(0 == (int)PeFin.isPeak)
		{
			return;
		}
		
		//是极大值
		if(1 == (int)PeFin.isPeak)
		{
		  //和旧最大值比较
		  //新的极大值大 记录值和下标
		  if(MaxValue < PeFin.MPeak)
		  {
		  	MaxValue = PeFin.MPeak;
		  	MaxValueIndex = PeFin.MPeakIndex;
		  }
		  return;
		}
		
		//是极小值
		if(2 == (int)PeFin.isPeak)
		{
			//旧的最大值和新的极小值比较 是否满足0.8的条件
			  //这个条件在需要再讨论if(MaxValue - LPeak > 0.8)
			if(MaxValue - PeFin.LPeak > 0.8)
			{
				//加入步频阈值 假设每秒不超过4步 采集的样点数 n = F/(4*2) = 50/8 =6
				if(6 < (PeFin.LPeakIndex - MaxValueIndex + PeFin.bufflength3)%PeFin.bufflength3)
				{
					//这个时刻跨出一步
					isStep = 1;
					//保存本次有效的极小值
					PreMinValue = PeFin.LPeak;
					PreMinValueIndex = PeFin.LPeakIndex;
					//计算上一步的距离
					//DistanceOneStep = calculatedistance(PeFin);
					DistanceOneStep = (float) Math.pow((MaxValue - PreMinValue), 1.0/4);
					//generalTool.saveToSDcard(DistanceOneStep);
					
					//保存本次的最大值，为下次计算距离做准备
					PreMaxValue = MaxValue;
					PreMaxValueIndex = MaxValueIndex;
					
					//满足的话记步加1
					++StepCount;
				}
				//本次最大值清零
				MaxValue = -100;
				MaxValueIndex = 0;
				return;
			}
		}
	}
	
	public float calculatedistance(PeakFinder PeFin)
	{
		  float temp = 0;
		  float buchang = 0;
  		  //第一次进入时直接退出
  		  if(1 >= PreMaxValue && 0 == (int)PreMaxValueIndex)
  		  {
  		  	buchang = 0;
  		  }
  		  //计算步长
    	 if((int)PreMaxValueIndex < (int)PeFin.LPeakIndex)
   	      {
			  //一个周期是从 PreMaxValueIndex 到 maxNum[2] 两个波峰
				for(int j = (int) PreMaxValueIndex; j != (int)PeFin.LPeakIndex + 1; j++)
					temp =  temp + PeFin.fArray3[j];
				//一个周期长度是 PreMaxValueIndex - maxNum[2]
				buchang = (float) Math.pow(temp/(PeFin.LPeakIndex - PreMaxValueIndex), 1.0/3);
   		  }
		return buchang;
	}
}