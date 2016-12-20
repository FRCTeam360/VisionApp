package org.usfirst.frc.team360.robot;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import org.usfirst.frc.team360.robot.*;

public class PhoneConnection {

	RobotServer robotServer;
	boolean shouldRun;
	int localPort;
	int phonePort;
	String m_device;
	BufferedReader bufferedReaderLogcat;
	//LogcatReader logcatReader;
	AndroidReader androidReader;
	public PhoneConnection(String device, int localSocketPort, int phoneSocketPort){
		System.out.println("started connection");
		robotServer = RobotServer.getInstance();
		shouldRun = true;
		m_device = device;
		localPort = localSocketPort;
		phonePort = phoneSocketPort;
		androidReader = new AndroidReader();
		androidReader.start();
		//logcatReader = new LogcatReader();
		//logcatReader.start();
	}
	
	public void shutDown(){
		shouldRun = false;
	}
	
	public String getDevice(){
		return m_device;
	}
	
	protected class AndroidReader implements Runnable{
		Socket m_ReadSocket;
		BufferedReader inFromServer;
		long timeSinceLastMessage;
		String inputLine;
		Thread m_Thread;
		DataOutputStream outToServer;
		public AndroidReader(){
			try {
				robotServer.bridge.portForward(m_device, localPort, phonePort);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		@Override
		public void run() {
			try{
				m_ReadSocket = new Socket("localhost", localPort);
				inFromServer = new BufferedReader(new InputStreamReader(m_ReadSocket.getInputStream()));
				outToServer = new DataOutputStream(m_ReadSocket.getOutputStream());
				send("Knock Knock");
				timeSinceLastMessage = System.currentTimeMillis();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while(m_ReadSocket != null && shouldRun && !m_ReadSocket.isClosed() && m_ReadSocket.isConnected() && 
				System.currentTimeMillis() - timeSinceLastMessage < 333){
				try {
					while(inFromServer.ready() && (inputLine = inFromServer.readLine()) != null){
						System.out.println("received Message: " + inputLine);
						if(inputLine.equals("Whos There")){
							System.out.println("Recived Whos There");
							timeSinceLastMessage = System.currentTimeMillis();
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println(e.toString());
				}
				if(System.currentTimeMillis() - timeSinceLastMessage > 100){
					send("Knock Knock");
				}
				try {
					Thread.sleep(20);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("connection done");
			if(shouldRun){
				try{
					Thread.sleep(333);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(shouldRun){
					run();
				}
			}
		}
		public void send(String message){			
			try {
				outToServer.writeBytes(message + '\n');
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		public void start() {
		      System.out.println("Starting " );
		      if (m_Thread == null) {
		    	  m_Thread = new Thread (this);
		         m_Thread.start ();
		      }
		}
	}
	protected class LogcatReader implements Runnable{
		Process logcatShell;
		Thread m_Thread;
		String m_Name;
		public LogcatReader(String device){
			m_Name = device;
			logcatShell = robotServer.bridge.startLogcatAtDevice(device);
			if(logcatShell != null){
				bufferedReaderLogcat = new BufferedReader(
		  		new InputStreamReader(logcatShell.getInputStream()));
		  		
		  	}	 
		}
		public LogcatReader(){
			logcatShell = robotServer.bridge.startLogcat();
			if(logcatShell != null){
				bufferedReaderLogcat = new BufferedReader(
		  		new InputStreamReader(logcatShell.getInputStream()));
		  		
		  	}	 
		}
		public void setThread(Thread thread){
			m_Thread = thread;
		}
		public void run() {
			// TODO Auto-generated method stub
			String line1;
			while(shouldRun && logcatShell.isAlive()){
				try {
					if((line1 = bufferedReaderLogcat.readLine()) != null){
						if(!line1.equals("--------- beginning of /dev/log/main") &&
								!line1.equals("--------- beginning of /dev/log/system"));
							System.out.println(line1);
					}
				
				} catch (Exception e) {
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
			System.out.println("done");
		}
		public void start () {
		      System.out.println("Starting " );
		      if (m_Thread == null) {
		    	  m_Thread = new Thread (this);
		         m_Thread.start ();
		      }
		}
	}
}