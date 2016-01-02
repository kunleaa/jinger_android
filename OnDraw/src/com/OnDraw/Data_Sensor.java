package com.OnDraw;
public class Data_Sensor
{
	//�ֻ�������
	private float [] accelerometer = {0,0,0};
	
	private float [] orientation = {0,0,0};
	// �������ķ���Ƕ�
	private float [] orientation_trans = {0,0,0};
	//��¼����һ�α�ʹ�ú��µ������ݸ���
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
	//���ֻ����귽�򴫸�������ֵת�������������
	public void TranslateOrient()
	{
		 //��ʼ�ֻ�����ˮƽ��̬
		 //yaw��ƫ��˳ʱ������ ��0��360��
		 orientation_trans[0] = 360 - orientation[0];
		 //pitch��б��������ת��Ȧ  ��0��-180�� ������ת��Ȧ��180��0��
		 orientation_trans[1] = -orientation[1];
		 //roll���������泯�ϴ��� ˳ʱ��תһȦ ��0��-90����-90,0����0��90����90,0��
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