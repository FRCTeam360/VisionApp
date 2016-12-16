package org.usfirst.frc.team360.robot;
import java.io.*;
import java.net.*;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

class TCPClient
{
	 String line;
	 Process shell;
	 BufferedReader bufferedReader;
	 BufferedReader bufferedReader2;
	 AdbBridge g;
 public TCPClient()
 {
	 try{
		  SmartDashboard.putString("FROM d: ", "begnning");
		  	g = new AdbBridge();
		  	g.start();
		  	shell = g.startShell();
	    	Timer.delay(1);
		  	if(shell != null){
		  		 bufferedReader = new BufferedReader(
		  		new InputStreamReader(shell.getInputStream()));
		  		 bufferedReader2 = new BufferedReader(
		  		new InputStreamReader(shell.getErrorStream()));

		  		StringBuilder log=new StringBuilder();
		  		String line = "";
		  		
		  	}	 
	 }catch(Exception e){
		 
	 }
 }
 String line2 = "";
 	public void read(){
 		
 		String line1;
 		try {
			if((line1 = bufferedReader.readLine()) != null){
					line2 +=line1;
				}
			if((line1 = bufferedReader2.readLine()) != null){
				line2 +=line1;
				}
			SmartDashboard.putString("Shell: ", line2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			SmartDashboard.putString("ADB Error", "d");
		}
 	}
}