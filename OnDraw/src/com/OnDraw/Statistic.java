package com.OnDraw;
public class Statistic{
	
	float [] ori_sensor = new float[32];
	int index_sensor = 0;
	float [] ori_acc  = new float[32];
	int index_acc = 0;
	float [] ori_incre  = new float[32];
	int index_incre = 0;
	
	//T(ʱ��,30��) I(���0,5) S(��ʼλ��,-5,5) 
	float [][][][] ori_acc_cali_T_I_S = new float[2][30][5][10];
	float [][] ori_sensor_cali =new float[2][30];
	
	float[][][] ori_meanacc_I_S = new float [2][5][10];
	float[] ori_meansensor_I_S = new float[2];
	int count_calibrate = 0;
	//��¼����������Ҫ����±�
	int ori_param[][];
	float ori_increment_calibrate[][];
	int count_pa;
	
	public void store_orisen_calibrate(int c,int t, float value)
	{
		ori_sensor_cali[c%2][t%30] = value;
	}
	
	public void store_oriacc_calibrate(int c,int t,int i,int s, float value)
	{
		//��������30ʱѭ���洢
		ori_acc_cali_T_I_S[c%2][t%30][i][s]= value;
	}
	
	//����������ƽ��ֵ
	public void mean_orisen_calibrate(int c, int t)
	{
		c = c%2;
		ori_meansensor_I_S[c] = average_orient(ori_sensor_cali[c],t>=30?30:t);
	}
	
	//���ٶȼ��㷽��ƽ��ֵ
	public void mean_oriacc_calibrate(int c, int t)
	{
		c = c%2;
		int length = t>=30?30:t;
		float[] ori_acc_T = new float[length];
		
		for(int interal = 0; interal < 5; interal++)
		{
			for(int start=0; start < 10; start++)
			{
				for(int step = 0;step < length; step++)
				{
					ori_acc_T[step] = ori_acc_cali_T_I_S[c][step][interal][start];
				}
				ori_meanacc_I_S[c][interal][start] = average_orient(ori_acc_T,length);
			}
		}
	}
	
	public void calcu_orientparam()
	{
		float increment = ori_meansensor_I_S[0]-ori_meansensor_I_S[1];
		
		ori_increment_calibrate = new float[5][10];
		ori_param = new int[50][2];
		count_pa=0;
		for(int interal = 0; interal < 5; interal++)
		{
			for(int start=0; start < 10; start++)
			{
				ori_increment_calibrate[interal][start] = ori_meanacc_I_S[0][interal][start] - ori_meanacc_I_S[1][interal][start] - increment;
				if(Math.abs(ori_increment_calibrate[interal][start]) < 10)
				{
					ori_param[count_pa][0] =interal;
					ori_param[count_pa][1] =start;
					GeneralTool.saveToSDcard(start-5, start-5+interal, "AvaiParam.txt");
					count_pa++;
				}
			}
		}
		GeneralTool.saveToSDcard(111111, 111111, "AvaiParam.txt");
	}
	//���������ѡȡ�������ȶ��������С�Ĳ��������Ҫ��Ӹù���
	public int[] getoneparam()
	{
		int i;
		if(count_pa > 0)
		{
			//���ȷ��س���Ϊ4(���Ϊ3)�Ĳ���
			for(i=count_pa-1; i >= 0; i--)
			{
				if(ori_param[i][0] == 3)
					return ori_param[i];
			}
			//��η��س���Ϊ3�Ĳ���
			for(i=count_pa-1; i >= 0; i--)
			{
				if(ori_param[i][0] == 2)
					return ori_param[i];
			}
			//��෵�ص�һ��
			for(i=count_pa-1; i >= 0; i--)
			{
				if(ori_param[i][0] != 0 && ori_param[i][1] != 0)
					return ori_param[i];
			}
		}
		return null;
	}
	
	public void store_orisen(float value)
	{
		ori_sensor = GeneralTool.enlarge(ori_sensor,index_sensor);
		ori_sensor[index_sensor++] = value;
	}
	
	public void store_oriacc(float value)
	{
		ori_acc = GeneralTool.enlarge(ori_acc,index_acc);
		ori_acc[index_acc++] = value;
	}
	
	public void store_oriincre(float value)
	{
		ori_incre = GeneralTool.enlarge(ori_incre,index_incre);
		ori_incre[index_incre++] = value;
	}
	
	//���㲢�洢��������
	public float cal_sto_oriincre(float valoriacc, float valorisen)
	{
		float result =  calcuoriincre(valoriacc,valorisen);
		store_oriincre(result);
		return result;
	}

	
	//���㷽������� ��������ķ���-�������ķ���
	private float calcuoriincre(float test,float standard)
	{
		float increment = 0;
		increment = (test-standard+360)%360;
		if(increment > 180)
			increment = increment - 360;
		return increment;
	}
	
	
	
	//����������ƽ��ֵ
	public float mean_orisen()
	{
		return average_orient(ori_sensor,index_sensor);
	}
	//���ٶȼ��㷽��ƽ��ֵ
	public float mean_oriacc()
	{
		return average_orient(ori_acc,index_acc);
	}
	//����ƽ��ֵ
	public float mean_oriincre()
	{
		int i = 0;
		float sum = 0;
		for(;i < index_incre;i++)
		{
			sum = sum + ori_incre[i];
		}
		return sum/index_incre;
	}
	
	float average_orient(float [] array, int count)
	{
		int index = 0;
		float sum = 0;
		int count_left = 0;
		for(;index < count;index++)
		{
			sum = sum + array[index];
		}
		//�ֲ���ʧ�Ķ��� ���� һ����1��һ����359�� ��ȷ����0 �����������������180
		//�������0���򣬷�����0��90��֮��ĸ���
		count_left = iscontainzero(array,count);
		sum = sum + count_left*360;
		sum = sum / count;
		sum = sum % 360;
		return sum;
	}
	
	//����Χ���Ƿ����0�� ������0��90�ȷ�Χ�ڵķ������
	int iscontainzero(float [] array,int count)
	{
		//359��270Ϊ��
		int count_right = 0;
		//0��90Ϊ��
		int count_left = 0;
		int index = 0;
		for(;index < count;index++)
		{
			if(array[index] >= 0 && array[index] < 90)
			{
				count_left++;
			}
			else if(array[index] >= 270 && array[index] < 360)
			{
				count_right++;
			}
		}
		if(count_left != 0 && count_right != 0)
			return count_left;
		else
			return 0;
	}
	
	public void cleanalldata()
	{
		ori_sensor = new float[32];
		index_sensor = 0;
		ori_acc  = new float[32];
		index_acc = 0;
		ori_incre  = new float[32];
		index_incre = 0;
		
		//T(ʱ��,30��) I(���0,5) S(��ʼλ��,-5,5) 
		ori_acc_cali_T_I_S = new float[2][30][5][10];
		ori_sensor_cali =new float[2][30];
		
		ori_meanacc_I_S = new float [2][5][10];
		ori_meansensor_I_S = new float[2];
		count_calibrate = 0;
	}
}