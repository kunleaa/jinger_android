package com.OnDraw;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import android.os.Environment;

public class GeneralTool{
	
	public static void saveToSDcard(float x){
		try {			
			//以追加的方式来保存加速度的值
			File file = new File(Environment.getExternalStorageDirectory(),"OnDrawData1.txt");
		    //第二个参数意义是说是否以append方式添加内容  
		    BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));  
		    String info = x+"";
		    bw.write(info);
		    bw.write("\r\n"); 
		    bw.flush();  
		    System.out.println("写入成功");  	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void saveToSDcard(float x, String filename){
		try {			
			//以追加的方式来保存加速度的值
			File file = new File(Environment.getExternalStorageDirectory(),filename);
		    //第二个参数意义是说是否以append方式添加内容  
		    BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));  
		    String info = x+"";
		    bw.write(info);
		    bw.write("\r\n"); 
		    bw.flush();  
		    System.out.println("写入成功");  	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void saveToSDcard(float x, float d){
		try {			
			//以追加的方式来保存加速度的值
			File file = new File(Environment.getExternalStorageDirectory(),"OnDrawData2.txt");
		    //第二个参数意义是说是否以append方式添加内容  
		    BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));  
		    String info = x+"  "+d;
		    bw.write(info);
		    bw.write("\r\n"); 
		    bw.flush();  
		    System.out.println("写入成功");  	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void saveToSDcard(float x, float d, float g){
		try {			
			//以追加的方式来保存加速度的值
			File file = new File(Environment.getExternalStorageDirectory(),"OnDrawData3.txt");
		    //第二个参数意义是说是否以append方式添加内容  
		    BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));  
		    String info = x+"  "+d+"  "+g;
		    bw.write(info);
		    bw.write("\r\n"); 
		    bw.flush();  
		    System.out.println("写入成功");  	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void saveToSDcard(float x, float d, float g, String FileName){
		try {			
			//以追加的方式来保存加速度的值
			File file = new File(Environment.getExternalStorageDirectory(),FileName);
		    //第二个参数意义是说是否以append方式添加内容  
		    BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));  
		    String info = x+"  "+d+"  "+g;
		    bw.write(info);
		    bw.write("\r\n"); 
		    bw.flush();  
		    System.out.println("写入成功");  	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void saveToSDcard(float x, float d, float g, float h){
		try {			
			//以追加的方式来保存加速度的值
			File file = new File(Environment.getExternalStorageDirectory(),"OnDrawData4.txt");
		    //第二个参数意义是说是否以append方式添加内容  
		    BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));  
		    String info = x+"  "+d+"  "+g+"  "+h;
		    bw.write(info);
		    bw.write("\r\n"); 
		    bw.flush();  
		    System.out.println("写入成功");  	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}