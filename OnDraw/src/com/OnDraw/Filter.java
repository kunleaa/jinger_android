package com.OnDraw;
public class Filter{
	
	public class AverageData
	{
		float AveFilArr[] = new float[36];
		int AveLastIndex = -1;
		int AveIsFull = 0;
		float Average = 0;
	}
	AverageData AveFirstData = new AverageData();
	AverageData AveSecondData = new AverageData();
	
	public void AverageFilteringProcess(float fValue, int M, AverageData AveData)
	{
		int i=0;
		float fAverage = fValue;
		AveData.AveLastIndex = (++AveData.AveLastIndex)%M;//%36求余数
		AveData.AveFilArr[AveData.AveLastIndex] = fValue;
		if(AveData.AveIsFull == 0)
		{
			if((M-1) == AveData.AveLastIndex)
			{
				AveData.AveIsFull = 1;
			}
			for(i = 0; i != AveData.AveLastIndex ; i++)
			{
				fAverage = fAverage + AveData.AveFilArr[i];
			}
			fAverage = fAverage / (AveData.AveLastIndex + 1);
		}
		else
		{
			for(i = ((AveData.AveLastIndex + 1)%M); i != AveData.AveLastIndex ; i = ((i + 1)%M))
			{
				fAverage = fAverage + AveData.AveFilArr[i];
			}
			fAverage =fAverage / M;
		}
		AveData.Average = fAverage;
	}
	
	public float AverageFiltering(float fValue)
	{
		//平均值,即第一次滤波，取36个数的平均值
		AverageFilteringProcess(fValue, 36, AveFirstData );
		//平均值,即第二次滤波，取3个数的平均值
		AverageFilteringProcess(AveFirstData.Average, 3, AveSecondData);
		return AveSecondData.Average;
	}
}