package com.OnDraw;
public class Data_Sensor
{
	//手机坐标下
	private float [] accelerometer = {0,0,0};
	
	private float [] orientation = {0,0,0};
	// 大地坐标的方向角度
	private float [] orientation_trans = {0,0,0};
	//记录从上一次被使用后，新到的数据个数
	int count_orient = 0;
	int count_orient_trans = 0;
	int count_accle = 0;
	
	void set_accle(float acc[])
	{
		accelerometer = acc;
		count_accle++;
	}
	
	void set_orien(float ori[])
	{
		 orientation = ori;
		 TranslateOrient();
		 count_orient++;
		 count_orient_trans++;
	}
	//将手机坐标方向传感器的数值转换到大地坐标下
	public void TranslateOrient()
	{
		 //初始手机保持水平姿态
		 //yaw航偏：顺时针增大 【0，360】
		 orientation_trans[0] = 360 - orientation[0];
		 //pitch倾斜：向上旋转半圈  【0，-180】 继续旋转半圈【180，0】
		 orientation_trans[1] = -orientation[1];
		 //roll翻滚：正面朝上垂线 顺时针转一圈 【0，-90】【-90,0】【0，90】【90,0】
		 orientation_trans[2] = orientation[2];
	}
	
	float[] use_ori()
	{
		reset_count_ori();
		return orientation;
	}
	
	float[] use_ori_trans()
	{
		reset_count_ori_trans();
		return orientation_trans;
	}
	
	float[] use_acc()
	{
		reset_count_acc();
		return accelerometer;
	}
	
	void reset_count_ori()
	{
		count_orient = 0;
	}
	
	void reset_count_ori_trans()
	{
		count_orient_trans = 0;
	}
	
	void reset_count_acc()
	{
		count_accle = 0;
	}
}