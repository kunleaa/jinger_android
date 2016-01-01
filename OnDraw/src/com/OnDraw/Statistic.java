package com.OnDraw;
public class Statistic{
	/***************************导航用到的方向数据***************************/
	float [] ori_sensor = new float[32];
	int index_sensor = 0;
	float [] ori_acc  = new float[32];
	int index_acc = 0;
	float [] ori_incre  = new float[32];
	int index_incre = 0;
	
	/***************************方向校准用到的数据***************************/
	//T(时间,30步) I(间隔0,5) S(起始位置,-5,5) 
	float [][][][] ori_acc_cali_T_I_S = new float[2][30][5][10];
	float [][] ori_sensor_cali =new float[2][30];
	float[][][] ori_meanacc_I_S = new float [2][5][10];
	float[] ori_meansensor_I_S = new float[2];
	int count_calibrate = 0;
	
	//记录数组中满足要求的下标
	int ori_param[][];
	float ori_increment_calibrate[][];
	int count_pa;
	
	/***************************步长校准用到的数据***************************/
	//每一步的波峰值和下标
	float [] crest_value = new float[32];
	int [] crest_index = new int [32];
	int crest_count = 0;
	//每一步的波谷值和下标
	float [] valley_value = new float [32];
	int [] valley_index = new int [32];
	int valley_count = 0;
	
	/***************************步长校准用到的接口***************************/
	public void store_crest(float cv, int ci)
	{
		//扩容
		crest_value = GeneralTool.enlarge_float(crest_value, crest_count);
		crest_index = GeneralTool.enlarge_int(crest_index, crest_count);
		
		crest_value[crest_count] = cv;
		crest_index[crest_count] = ci;
		crest_count++;
	}
	
	public void store_valley(float vv, int vi)
	{
		//扩容
		valley_value = GeneralTool.enlarge_float(valley_value, valley_count);
		valley_index = GeneralTool.enlarge_int(valley_index, valley_count);
		
		valley_value[valley_count] = vv;
		valley_index[valley_count] = vi;
		valley_count++;
	}
	
	public float calcu_stepdisparam_byWeinberg(float distance)
	{
		double result = 0;
		int i=0;
		
		if(distance < 0 || valley_count != crest_count)
			return 0;
		
		for(; i < valley_count; i++)
			result += Math.pow(crest_value[i] - valley_value[i], 1.0/4);
		
		result = distance/result;
		
		return (float)result; 
	}
	
	/***************************方向校准用到的接口***************************/
	public void store_orisen_calibrate(int c,int t, float value)
	{
		ori_sensor_cali[c%2][t%30] = value;
	}
	
	public void store_oriacc_calibrate(int c,int t,int i,int s, float value)
	{
		//步数大于30时循环存储
		ori_acc_cali_T_I_S[c%2][t%30][i][s]= value;
	}
	
	//传感器方向平均值
	public void mean_orisen_calibrate(int c, int t)
	{
		c = c%2;
		ori_meansensor_I_S[c] = average_orient(ori_sensor_cali[c],t>=30?30:t);
	}
	
	//加速度计算方向平均值
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
	//找出区间的起点及连续长度
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
	//最理想的是选取波动最稳定且误差最小的参数，后边要添加该功能
	//在找出的所有区间中选出合适的方向区间
	public int[] getoneparam()
	{
		int i;
		if(count_pa > 0)
		{
			//优先返回长度为4(间隔为3)的参数
			for(i=count_pa-1; i >= 0; i--)
			{
				if(ori_param[i][0] == 3)
					return ori_param[i];
			}
			//其次返回长度为3的参数
			for(i=count_pa-1; i >= 0; i--)
			{
				if(ori_param[i][0] == 2)
					return ori_param[i];
			}
			//否侧返回第一个
			for(i=count_pa-1; i >= 0; i--)
			{
				if(ori_param[i][0] != 0 && ori_param[i][1] != 0)
					return ori_param[i];
			}
		}
		return null;
	}
	
	/***************************导航用到的方向接口***************************/
	public void store_orisen(float value)
	{
		ori_sensor = GeneralTool.enlarge_float(ori_sensor,index_sensor);
		ori_sensor[index_sensor++] = value;
	}
	
	public void store_oriacc(float value)
	{
		ori_acc = GeneralTool.enlarge_float(ori_acc,index_acc);
		ori_acc[index_acc++] = value;
	}
	
	public void store_oriincre(float value)
	{
		ori_incre = GeneralTool.enlarge_float(ori_incre,index_incre);
		ori_incre[index_incre++] = value;
	}
	
	//计算并存储方向增量
	public float cal_sto_oriincre(float valoriacc, float valorisen)
	{
		float result =  calcuoriincre(valoriacc,valorisen);
		store_oriincre(result);
		return result;
	}

	
	//计算方向的增量 计算出来的方向-传感器的方向
	private float calcuoriincre(float test,float standard)
	{
		float increment = 0;
		increment = (test-standard+360)%360;
		if(increment > 180)
			increment = increment - 360;
		return increment;
	}
	
	
	
	//传感器方向平均值
	public float mean_orisen()
	{
		return average_orient(ori_sensor,index_sensor);
	}
	//加速度计算方向平均值
	public float mean_oriacc()
	{
		return average_orient(ori_acc,index_acc);
	}
	//增量平均值
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
		//弥补丢失的度数 比如 一个是1度一个是359度 正确的是0 但是上面计算下来是180
		//如果包含0方向，返回在0到90度之间的个数
		count_left = iscontainzero(array,count);
		sum = sum + count_left*360;
		sum = sum / count;
		sum = sum % 360;
		return sum;
	}
	
	//方向范围中是否包含0度 返回在0到90度范围内的方向个数
	int iscontainzero(float [] array,int count)
	{
		//359到270为右
		int count_right = 0;
		//0到90为左
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
	public void cleanstepdata()
	{
		//每一步的波峰值和下标
		crest_value = new float[30];
		crest_index = new int [30];
		crest_count = 0;
		//每一步的波谷值和下标
		valley_value = new float [30];
		valley_index = new int [30];
		valley_count = 0;
	}
	public void cleanalldata()
	{
		ori_sensor = new float[32];
		index_sensor = 0;
		ori_acc  = new float[32];
		index_acc = 0;
		ori_incre  = new float[32];
		index_incre = 0;
		
		//T(时间,30步) I(间隔0,5) S(起始位置,-5,5) 
		ori_acc_cali_T_I_S = new float[2][30][5][10];
		ori_sensor_cali =new float[2][30];
		
		ori_meanacc_I_S = new float [2][5][10];
		ori_meansensor_I_S = new float[2];
		count_calibrate = 0;
		
		//每一步的波峰值和下标
		crest_value = new float[30];
		crest_index = new int [30];
		crest_count = 0;
		//每一步的波谷值和下标
		valley_value = new float [30];
		valley_index = new int [30];
		valley_count = 0;
	}
}