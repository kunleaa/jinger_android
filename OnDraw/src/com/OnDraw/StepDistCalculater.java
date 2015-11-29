package com.OnDraw;

public class StepDistCalculater{
	//��ǰ���ֵ
	float MaxValue = 0;
	//����������
	int StepCount = 0;
	//��ǰ���ֵ�±�
	int MaxValueIndex = 0;
	//һ���ľ���
	float DistanceOneStep = 0;
	//ǰһ�����ֵ
	float PreMaxValue = 0;
	//ǰһ�����ֵ�±�
	int PreMaxValueIndex = 0;
	//ǰһ����Сֵ
	float PreMinValue = 0;
	//ǰһ����Сֵ�±�
	int PreMinValueIndex = 0;
	//��ʱ���Ƿ��߳�һ��
	int isStep = 0;
	
	public void CalcuStepDist(PeakFinder PeFin)
	{
		isStep = 0;
		//���Ǽ�ֵ
		if(0 == (int)PeFin.isPeak)
		{
			return;
		}
		
		//�Ǽ���ֵ
		if(1 == (int)PeFin.isPeak)
		{
		  //�;����ֵ�Ƚ�
		  //�µļ���ֵ�� ��¼ֵ���±�
		  if(MaxValue < PeFin.MPeak)
		  {
		  	MaxValue = PeFin.MPeak;
		  	MaxValueIndex = PeFin.MPeakIndex;
		  }
		  return;
		}
		
		//�Ǽ�Сֵ
		if(2 == (int)PeFin.isPeak)
		{
			//�ɵ����ֵ���µļ�Сֵ�Ƚ� �Ƿ�����0.8������
			  //�����������Ҫ������if(MaxValue - LPeak > 0.8)
			if(MaxValue - PeFin.LPeak > 0.6 )
			{
				//���ʱ�̿��һ��
				isStep = 1;
				//���汾����Ч�ļ�Сֵ
				PreMinValue = PeFin.LPeak;
				PreMinValueIndex = PeFin.LPeakIndex;
				//������һ���ľ���
				DistanceOneStep = calculatedistance(PeFin);
				
				//���汾�ε����ֵ��Ϊ�´μ��������׼��
				PreMaxValue = MaxValue;
				PreMaxValueIndex = MaxValueIndex;
				
				//����Ļ��ǲ���1
				++StepCount;
				
				//�������ֵ����
				MaxValue = 0;
				MaxValueIndex = 0;
				
				return;
			}
		}
		return;
	}
	
	public float calculatedistance(PeakFinder PeFin)
	{
		  float temp = 0;
		  float buchang = 0;
  		  //��һ�ν���ʱֱ���˳�
  		  if(1 >= PreMaxValue && 0 == (int)PreMaxValueIndex)
  		  {
  		  	buchang = 0;
  		  }
  		  //���㲽��
    	 if((int)PreMaxValueIndex < (int)PeFin.LPeakIndex)
   	      {
			  //һ�������Ǵ� PreMaxValueIndex �� maxNum[2] ��������
				for(int j = (int) PreMaxValueIndex; j != (int)PeFin.LPeakIndex + 1; j++)
					temp =  temp + PeFin.fArray3[j];
				//һ�����ڳ����� PreMaxValueIndex - maxNum[2]
				buchang = (float) Math.pow(temp/(PeFin.LPeakIndex - PreMaxValueIndex), 1.0/3);
   		  }
		return buchang;
	}
}