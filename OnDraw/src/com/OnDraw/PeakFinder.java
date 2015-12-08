package com.OnDraw;
public class PeakFinder{
	
	float fArray3[] = new float[100];
	int iLastIndex3 = -1;
	int iIsHundred3 = 0;
	
	//缓冲区长度定位80，保证至少可以容纳下两个波峰波谷
	int bufflength3 = 80;

	//极大值极小值标志
	float isPeak = 0;
	//极大值角标
	float MPeak = 0;
	//极小值角标
	float LPeak = 0;
	//极大值
	int MPeakIndex = 0;
	//极小值
	int LPeakIndex = 0;
	//记录缓冲区溢出多少次
	int Circle = 0;
	//求极大值极小值及其角标
	public void FindPeak(float fValue){
		isPeak = 0;
		
		AddCircle(iLastIndex3,bufflength3);
		iLastIndex3 = (++iLastIndex3)%bufflength3 ;
		fArray3[iLastIndex3] = fValue;
		// 数组没有存满时的情况  
		if(iIsHundred3 == 0){
			if(iLastIndex3 == bufflength3 -1){
				iIsHundred3 = 1;
			}
			//如果是第一个数，结束
			if(iLastIndex3 == 1||iLastIndex3 == 0)
			{
			    return ;
			}
			//不是第一数据再处理
			else
			{
					//判断新数据是否构成极大值（倒数第二个数据是不是一个极大值）
		  		if(fArray3[iLastIndex3 - 1] > fArray3[iLastIndex3] && 
		  		   fArray3[iLastIndex3 - 1] > fArray3[iLastIndex3 - 2])
		  	    {
		  				isPeak = 1;
		  				MPeakIndex = iLastIndex3 - 1;
		  				MPeak = fArray3[iLastIndex3 - 1];
	  				
		  		}
		  		//判断新数据是否构成极小值（倒数第二个数据是不是一个极小值）
		  		if(fArray3[iLastIndex3 - 1]<fArray3[iLastIndex3] 
		  		    && fArray3[iLastIndex3 - 1]<fArray3[iLastIndex3 - 2]){
		  				isPeak = 2;
		  				LPeakIndex = iLastIndex3 - 1;
		  				LPeak = fArray3[iLastIndex3 - 1];
		  		}
			}	
		}
		
	  //缓冲区已满、最新数据会覆盖最早的数据 /
		else
		{
			//判断新数据是否构成极大值（倒数第二个数据是不是一个极大值）
			if(fArray3[(iLastIndex3 + bufflength3 - 1) % bufflength3] > fArray3[iLastIndex3] && 
			   fArray3[(iLastIndex3 + bufflength3 - 1) % bufflength3] > fArray3[(iLastIndex3 + bufflength3 - 2) % bufflength3])
			{
  				
				isPeak = 1;
  				MPeakIndex = (iLastIndex3 + bufflength3 - 1) % bufflength3;
  				MPeak = fArray3[(iLastIndex3 + bufflength3 - 1) % bufflength3];
			}
  		//判断新数据是否构成极小值（倒数第二个数据是不是一个极小值）
			if(fArray3[(iLastIndex3 + bufflength3 - 1) % bufflength3] < fArray3[iLastIndex3] && 
			   fArray3[(iLastIndex3 + bufflength3 - 1) % bufflength3] < fArray3[(iLastIndex3 + bufflength3 - 2) % bufflength3])
			{
				isPeak = 2;
  				LPeakIndex = (iLastIndex3 + bufflength3 - 1) % bufflength3;
  				LPeak = fArray3[(iLastIndex3 + bufflength3 - 1) % bufflength3];
			}
		}
		//saveToSDcard(fArray3[(int) MPeakIndex],MPeakIndex,fArray3[(int) LPeakIndex],LPeakIndex);
		return;
	}
	public void AddCircle(int index,int buflen)
	{
		if(index == (buflen - 1))
		{
			Circle++;
		}
	}
	public void StoreValue(float fValue){
		iLastIndex3 = (++iLastIndex3)%bufflength3 ;
		fArray3[iLastIndex3] = fValue;
	}
}