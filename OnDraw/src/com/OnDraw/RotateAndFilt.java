package com.OnDraw;

public class RotateAndFilt
{
	RotationMatrix RotaMatrix = new RotationMatrix();
	Filter FilterOfAccX = new Filter();
	Filter FilterOfAccY = new Filter();
	Filter FilterOfAccZ = new Filter();
	
	protected float [] rotate_filt(Data_Sensor ds, DrawView dv)
	{
		float[] data_ori = ds.use_ori();
		float[] data_acc = ds.use_acc();
		
		//������ת����
		RotaMatrix.CalRotaMatrix(data_ori[0], data_ori[1], data_ori[2]);
		
		//����ת�������ľ�������ϵ�¼��ٶȵ�����ֵ 
		double[][] AbsCoodinate =  RotaMatrix.CalcuAbsCoodinate(data_acc[1], data_acc[0], data_acc[2]);
		float [] AbsCoodinate_filt = new float[3];
		if(AbsCoodinate != null)
		{
			//X��Y����5ֵ��ֵ�˲�
			AbsCoodinate_filt[0] = FilterOfAccX.AverageFiltering_manual((float)AbsCoodinate[0][0],5);
			AbsCoodinate_filt[1] = FilterOfAccY.AverageFiltering_manual((float)AbsCoodinate[1][0],5);
			//Z������36ֵ��ֵ�˲�������3ֵ��ֵ�˲�
			AbsCoodinate_filt[2] = FilterOfAccZ.AverageFiltering((float)AbsCoodinate[2][0]);
			dv.SetAbsCoodinate_1(AbsCoodinate_filt[0], AbsCoodinate_filt[1], AbsCoodinate_filt[2]);
			/*GeneralTool.saveToSDcard((float)AbsCoodinate_filt[0],
									 (float)AbsCoodinate_filt[1],
									 (float)AbsCoodinate_filt[2],
									 "data_acc.txt");*/
		}
		return AbsCoodinate_filt;
	}
	
	void cleanall()
	{
    	//Filter���
    	FilterOfAccX.cleanall();
    	FilterOfAccY.cleanall();
    	FilterOfAccZ.cleanall();
	}
}