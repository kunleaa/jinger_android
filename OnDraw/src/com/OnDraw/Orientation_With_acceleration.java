package com.OnDraw;
public class Orientation_With_acceleration
{
	
	static public float OrientWithTime(PeakFinder PF, StepDistCalculater SDCal, float[] Acc_X, float[] Acc_Y)
	{
		int index = 0;
		float value = -1;
		if(SDCal.isStep == 1)
		{
			if(SDCal.PreMinValueIndex > SDCal.PreMaxValueIndex)
				index = (SDCal.PreMinValueIndex+SDCal.PreMaxValueIndex)/2;
			else
				index = (SDCal.PreMinValueIndex+SDCal.PreMaxValueIndex + PF.bufflength3)/2%PF.bufflength3;
			
			if(Acc_Y[index] <= 0 && Acc_X[index] <= 0)
			{
				value = (float) ((-Acc_Y[index])/Math.sqrt((Math.pow(Acc_Y[index], 2) + Math.pow(Acc_X[index], 2))));
				value = (float)(Math.toDegrees(Math.asin(value)));
			}
			else if(Acc_X[index] >= 0)
			{
				value = (float)(Acc_Y[index]/Math.sqrt((Math.pow(Acc_Y[index], 2) + Math.pow(Acc_X[index], 2))));
				value = (float)(Math.toDegrees(Math.PI + Math.asin(value)));
			}
			else if(Acc_Y[index] >= 0 && Acc_X[index] <= 0)
			{
				value = (float)(Acc_Y[index]/Math.sqrt((Math.pow(Acc_Y[index], 2) + Math.pow(Acc_X[index], 2))));
				value = (float)(Math.toDegrees(Math.PI*3/2 + Math.asin(value)));
			}
			return (float) value;
		}
		return -1;
	}
	//interval积分间隔
	//PF提供长度
	//SDC提供积分下标
	//Xbuff Ybuff 提供被积分函数值 程序待实现
	public float Orient(PeakFinder PF, StepDistCalculater SDC, int interval)
	{
		float volecity_X;
		float volecity_Y;
		volecity_X = Integrate.intgt_circle(PF.fArray3, 1, 2, 0, interval, PF.bufflength3);
		volecity_Y = Integrate.intgt_circle(PF.fArray3, 1, 2, 0, interval, PF.bufflength3);
		
		//随便写的返回值
		return volecity_X/volecity_Y;
	}
}