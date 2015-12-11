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
			//ȡ���岨���е�ļ��ٶ�
			//acc = acc_middle(PF, SDCal, Acc_X, Acc_Y);
			//ȡ���岨��ָ����ļ��ٶ�
			//acc = acc_onepoint(midindex(PF, SDCal), Acc_X, Acc_Y);
			//ȡ�����벨��֮����ٵ�ƽ��ֵ
			//acc[X] = acc_average(Acc_X,SDCal.PreMaxValueIndex, SDCal.PreMinValueIndex, PF.bufflength3);
			//acc[Y] = acc_average(Acc_Y,SDCal.PreMaxValueIndex, SDCal.PreMinValueIndex, PF.bufflength3);
			//ȡ���ȺͲ����ķ�֮һ����ǰ������������֮���ƽ��ֵ
			int index1p4 = midindex(SDCal.PreMaxValueIndex, midindex(PF, SDCal), PF.bufflength3);
			//�±��2֮���п���Խ�磬����Ҫ����bufflength3����ȡ��
			int start = (index1p4-2+PF.bufflength3)%PF.bufflength3;
			int end = index1p4;
			acc[X] = acc_average(Acc_X, start, end, PF.bufflength3);
			acc[Y] = acc_average(Acc_Y, start, end, PF.bufflength3);
			//����ļ�������ȷ��
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
	
	//ָ��ĳһʱ�̵㣬ȷ�����ٶ�
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
		//�ж��Ƿ�Խ��
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
	
	//interval���ּ��
	//PF�ṩ����
	//SDC�ṩ�����±�
	//Xbuff Ybuff �ṩ�����ֺ���ֵ �����ʵ��
	public float Orient(PeakFinder PF, StepDistCalculater SDC, int interval)
	{
		float volecity_X;
		float volecity_Y;
		volecity_X = Integrate.intgt_circle(PF.fArray3, 1, 2, 0, interval, PF.bufflength3);
		volecity_Y = Integrate.intgt_circle(PF.fArray3, 1, 2, 0, interval, PF.bufflength3);
		
		//���д�ķ���ֵ
		return volecity_X/volecity_Y;
	}
}