package com.OnDraw;
public class Statistic{
	
	float [] ori_sensor = new float[32];
	int index_sensor = 0;
	float [] ori_acc  = new float[32];
	int index_acc = 0;
	float [] ori_incre  = new float[32];
	int index_incre = 0;
	
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
	}
}