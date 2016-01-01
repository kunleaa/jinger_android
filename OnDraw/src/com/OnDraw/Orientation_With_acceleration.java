package com.OnDraw;
public class Orientation_With_acceleration
{
	final static int X = 0;
	final static int Y = 1;
	static public float OrientWithTime(PeakFinder PF, StepDistCalculater SDCal, float[] Acc_X, float[] Acc_Y, int sfm1_4, int efm1_4)
	{
		float value = -1;
		float acc[] = new float[2];
		if(SDCal.isStep == true)
		{
			//取波峰波谷中点的加速度
			//acc = acc_middle(PF, SDCal, Acc_X, Acc_Y);
			//取波峰波谷指定点的加速度
			//acc = acc_onepoint(midindex(PF, SDCal), Acc_X, Acc_Y);
			//取波峰与波谷之间加速的平均值
			//acc[X] = acc_average(Acc_X,SDCal.PreMaxValueIndex, SDCal.PreMinValueIndex, PF.bufflength3);
			//acc[Y] = acc_average(Acc_Y,SDCal.PreMaxValueIndex, SDCal.PreMinValueIndex, PF.bufflength3);
			//取波谷和波峰四分之一（因为在此处的数据比较稳定）处，前两个到后两个之间的平均值
			int index1p4 = midindex(SDCal.PreMaxValueIndex, midindex(PF, SDCal), PF.bufflength3);
			
			//下标减2之后有可能越界，所以要加上bufflength3，再取余
			//找出能代表行人方向的区间（加速度坐标系中）
			int start = (index1p4+sfm1_4+PF.bufflength3)%PF.bufflength3;
			int end = (index1p4+efm1_4+PF.bufflength3)%PF.bufflength3;
			acc[X] = acc_average(Acc_X, start, end, PF.bufflength3);
			acc[Y] = acc_average(Acc_Y, start, end, PF.bufflength3);
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
		index = midindex(PF, SDCal);
		acc[X] = Acc_X[index];
		acc[Y] = Acc_Y[index];
		return acc;
	}
	
	//指定某一时刻点，确定加速度
	private static float[] acc_onepoint(int index, float[] Acc_X, float[] Acc_Y)
	{
		float acc[] = new float[2];

		acc[X] = Acc_X[index];
		acc[Y] = Acc_Y[index];
		return acc;
	}
	
	private static float acc_average(float [] array, int start, int end, int buflen)
	{
		int count = 1;
		float sum = array[end];
		//判断是否越界
		if(start >= buflen || end >= buflen || start < 0 || end < 0)
			return -1;
		for(;start != end;start = (++start)%buflen,++count)
		{
			sum = sum + array[start];
		}
		return sum/count;
	}
	
	private static int midindex(PeakFinder PF, StepDistCalculater SDCal)
	{
		return midindex(SDCal.PreMaxValueIndex,SDCal.PreMinValueIndex,PF.bufflength3);
	}
	
	private static int midindex(int start, int end, int buflen)
	{
		int index = 0;
		
		if(end > start)
			index = (end + start)/2;
		else
			index = (end + start + buflen)/2%buflen;
		
		return index;
	}
}