package org.usfirst.frc.team360.robot;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.usfirst.frc.team360.robot.AdbBridge;

public class RobotServer {
	static AdbBridge bridge;
	 String line;
	 String line2;
	 static Process DeviceShell;
	 static Process LogcatShell;
	 static BufferedReader bufferedReader_logcat;
	 static BufferedReader bufferedReader_device;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			bridge = new AdbBridge();
			bridge.start();
			(new Thread(new DeviceReader())).start();
			(new Thread(new LogcatReader())).start();
	}
	
	protected static class DeviceReader extends RobotServer implements Runnable{
		
		DeviceReader(){
			DeviceShell = bridge.getDevices();
			if(DeviceShell != null){
				bufferedReader_device = new BufferedReader(
		  		new InputStreamReader(DeviceShell.getInputStream()));
		  		
		  	}	 
		}
		
		public void run() {
			// TODO Auto-generated method stub
			String line1;
			while(true){
				try {
					if((line1 = bufferedReader_device.readLine()) != null){
						System.out.println(line1);
						}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println(e.toString());
				}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			}
		}
		
	
	}
		
	
	protected static class LogcatReader extends RobotServer implements Runnable{
		
		public LogcatReader(){
			LogcatShell = bridge.startLogcat("dsa");
			if(LogcatShell != null){
				bufferedReader_logcat = new BufferedReader(
		  		new InputStreamReader(LogcatShell.getInputStream()));
		  		
		  	}	 
		}
		
		public void run() {
			// TODO Auto-generated method stub
			String line1;
			while(true){
				try {
					if((line1 = bufferedReader_logcat.readLine()) != null){
						System.out.println(line1);
						}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println(e.toString());
				}
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			}
		}
	}
}
