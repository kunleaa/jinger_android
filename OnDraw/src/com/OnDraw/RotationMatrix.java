package com.OnDraw;

import Jama.Matrix;
public class RotationMatrix{
	
	Matrix RotaMatrix;
	Matrix AbsCoodinate;
	final double PIper180 = Math.PI/180;
	boolean HaveRotaMat = false;
	
	RotationMatrix()
	{
		RotaMatrix = new Matrix(3,3);
		AbsCoodinate = new Matrix(3,1);
	}
	void ProcessRotaMatrix(double YawAngle, double PitchAngle, double RollAngle)
	{
		 //求旋转矩阵R = Rz(A)Ry(B)Rx(C); a' = Ra
		 double[][] ArrayRz = {{Math.cos(YawAngle*PIper180),-Math.sin(YawAngle*PIper180),0},
				          {Math.sin(YawAngle*PIper180),Math.cos(YawAngle*PIper180),0},
				          {0,0,1}};
		 double[][] ArrayRy = {{Math.cos(PitchAngle*PIper180),0,Math.sin(PitchAngle*PIper180)},
				          {0,1,0},
				          {-Math.sin(PitchAngle*PIper180),0,Math.cos(PitchAngle*PIper180)}};
		 double[][] ArrayRx = {{1,0,0},
		                  {0,Math.cos(RollAngle*PIper180),-Math.sin(RollAngle*PIper180)},
		                  {0,Math.sin(RollAngle*PIper180),Math.cos(RollAngle*PIper180)}};
		 
		 Matrix Rz = new Matrix(ArrayRz);
		 Matrix Ry = new Matrix(ArrayRy);
		 Matrix Rx = new Matrix(ArrayRx);
		 RotaMatrix = Rz.times(Ry.times(Rx));
	}
	
	void CalRotaMatrix(double OriYawAngle, double OriPitchAngle, double OriRollAngle)
	{
		 HaveRotaMat = true;
		//手机正面向下时
		 if(Math.abs(OriPitchAngle) > 90)
		 {
			 OriRollAngle = -OriRollAngle;
		 }
		 
		 ProcessRotaMatrix(360 - OriYawAngle, -OriPitchAngle, OriRollAngle);
	}
	
	double[][] ProcessAbsCoodinate(double x, double y, double z)
	{
		if(HaveRotaMat == false)
			return null;
		double[][] ArrayRelaCoodinate = {{x}, {y}, {z}};
		Matrix RelaCoodinate = new Matrix(ArrayRelaCoodinate);
		AbsCoodinate = RotaMatrix.times(RelaCoodinate);
		return AbsCoodinate.getArrayCopy();
	}
	double[][] CalcuAbsCoodinate(double x, double y, double z)
	{
		return ProcessAbsCoodinate(x, -y, -z);
	}
}