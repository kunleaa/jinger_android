package com.OnDraw;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import android.os.Environment;

public class GeneralTool{
	
	public static void saveToSDcard(float x){
		try {			
			//��׷�ӵķ�ʽ��������ٶȵ�ֵ
			File file = new File(Environment.getExternalStorageDirectory(),"OnDrawData1.txt");
		    //�ڶ�������������˵�Ƿ���append��ʽ�������  
		    BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));  
		    String info = x+"";
		    bw.write(info);
		    bw.write("\r\n"); 
		    bw.flush();  
		    System.out.println("д��ɹ�");  	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void saveToSDcard(float x, float d){
		try {			
			//��׷�ӵķ�ʽ��������ٶȵ�ֵ
			File file = new File(Environment.getExternalStorageDirectory(),"OnDrawData2.txt");
		    //�ڶ�������������˵�Ƿ���append��ʽ�������  
		    BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));  
		    String info = x+"  "+d;
		    bw.write(info);
		    bw.write("\r\n"); 
		    bw.flush();  
		    System.out.println("д��ɹ�");  	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void saveToSDcard(float x, float d, float g){
		try {			
			//��׷�ӵķ�ʽ��������ٶȵ�ֵ
			File file = new File(Environment.getExternalStorageDirectory(),"OnDrawData3.txt");
		    //�ڶ�������������˵�Ƿ���append��ʽ�������  
		    BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));  
		    String info = x+"  "+d+"  "+g;
		    bw.write(info);
		    bw.write("\r\n"); 
		    bw.flush();  
		    System.out.println("д��ɹ�");  	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void saveToSDcard(float x, float d, float g, String FileName){
		try {			
			//��׷�ӵķ�ʽ��������ٶȵ�ֵ
			File file = new File(Environment.getExternalStorageDirectory(),FileName);
		    //�ڶ�������������˵�Ƿ���append��ʽ�������  
		    BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));  
		    String info = x+"  "+d+"  "+g;
		    bw.write(info);
		    bw.write("\r\n"); 
		    bw.flush();  
		    System.out.println("д��ɹ�");  	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}