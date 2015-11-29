package com.OnDraw;

public class Integrate
{
	static public float intgt(float [] fun, int start, int end, float basic ,float interval)
	{
		float sum = 0;
		int index = start;
		for(;index <= end;index++)
		{
			sum = sum + interval * fun[index];
		}
		sum = basic + sum;
		return sum;
	}
	//循环队列积分
	static public float intgt_circle(float [] fun, int start, int end, float basic ,float interval, int length)
	{
		float sum = 0;
		int index = start;
		for(;index == end;index = (++index)%length)
		{
			sum = sum + interval * fun[index];
		}
		sum = basic + sum;
		return sum;
	}
}