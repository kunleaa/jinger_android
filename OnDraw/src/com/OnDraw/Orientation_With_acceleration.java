package com.OnDraw;
public class Orientation_With_acceleration
{
	final static int X = 0;
	final static int Y = 1;
	static public float OrientWithTime(PeakFinder PF, StepDistCalculater SDCal, float[] Acc_X, float[] Acc_Y)
	{
		float value = -1;
		float acc[] = new float[2];
		if(SDCal.isStep == 1)
		{
			//acc = acc_middle(PF, SDCal, Acc_X, Acc_Y);
			acc[X] = acc_average(Acc_X,SDCal.PreMaxValueIndex, SDCal.PreMinValueIndex, PF.bufflength3);
			acc[Y] = acc_average(Acc_Y,SDCal.PreMaxValueIndex, SDCal.PreMinValueIndex, PF.bufflength3);
			//方向的计算是正确的
			if(acc[Y] <= 0 && acc[X] <= 0)
			{
				value = (float) ((-acc[Y])/Math.sqrt((Math.pow(acc[Y], 2) + Math.pow(acc[X], 2))));
				value = (float)(Math.toDegrees(Math.asin(value)));
			}
			else if(acc[X] >= 0)
			{
				value = (float)(acc[Y]/Math.sqrt((Math.pow(acc[Y], 2) + Math.pow(acc[X], 2))));
				value = (float)(Math.toDegrees(Math.PI + Math.asin(value)));
			}
			else if(acc[Y] >= 0 && acc[X] <= 0)
			{
				value = (float)(acc[Y]/Math.sqrt((Math.pow(acc[Y], 2) + Math.pow(acc[X], 2))));
				value = (float)(Math.toDegrees(Math.PI*3/2 + Math.asin(value)));
			}
			return (float) value;
		}
		return -1;
	}
	private static float[] acc_middle(PeakFinder PF, StepDistCalculater SDCal, float[] Acc_X, float[] Acc_Y)
	{
		float acc[] = new float[2];
		int index = 0;
		if(SDCal.PreMinValueIndex > SDCal.PreMaxValueIndex)
			index = (SDCal.PreMinValueIndex+SDCal.PreMaxValueIndex)/2;
		else
			index = (SDCal.PreMinValueIndex+SDCal.PreMaxValueIndex + PF.bufflength3)/2%PF.bufflength3;
		
		acc[X] = Acc_X[index];
		acc[Y] = Acc_Y[index];
		return acc;
	}
	
	private static float acc_average(float [] array, int start, int end, int buflen)
	{
		int count = 1;
		float sum = array[end];
		//判断是否越界
		if(start >= buflen-1 || end >= buflen-1 || start < 0 || end < 0)
			return -1;
		for(;start != end;start = (++start)%buflen,++count)
		{
			sum = sum + array[start];
		}
		return sum/count;
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