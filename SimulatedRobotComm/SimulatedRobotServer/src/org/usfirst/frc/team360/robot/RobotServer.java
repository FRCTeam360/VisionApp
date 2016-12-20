package org.usfirst.frc.team360.robot;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import org.usfirst.frc.team360.robot.*;

public class RobotServer {
	AdbBridge bridge;
	String line;
	String line2;
	Process devicesShell;
	//Process logcatShell;
	BufferedReader bufferedReaderDevices;
	ArrayList<PhoneConnection> phoneConnections;
	Thread devices;
	Thread write;
	boolean shouldRun;
	int localPortInUse = 3601;
	int phonePort= 3600;
	ArrayList<String> activeDevices;
	static RobotServer robotServer;
	public RobotServer(){
		robotServer = this;
		phoneConnections = new ArrayList<PhoneConnection>();
		activeDevices = new ArrayList<String>();
		shouldRun = true;
		bridge = new AdbBridge();
		bridge.start();
		devices = new Thread(new DevicesReader());
		devices.start();
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				shouldRun = false;
			}
		});
	 }
	
	public static RobotServer getInstance(){
		if(robotServer == null){
			robotServer = new RobotServer();
		}
		return robotServer;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		robotServer = new RobotServer(); 	
	}
	
	protected class DevicesReader implements Runnable{
		public DevicesReader(){
			 
		}
		ArrayList<String> foundDevices = new ArrayList<String>();
		boolean found;
		public void run() {
			// TODO Auto-generated method stub
			String line1;
			while(shouldRun){
				try {
					devicesShell = bridge.getDevices();
					foundDevices.clear();
					if(devicesShell != null){
						bufferedReaderDevices = new BufferedReader(new InputStreamReader(devicesShell.getInputStream()));	
					}	
					while((line1 = bufferedReaderDevices.readLine()) != null){
						found = false;
						if(!line1.equals("List of devices attached")){// eliminate the first line because it passes the next test case but we don't care about this string
							if(line1.contains("device")){// if the line is one we care about
								line1 = line1.substring(0, line1.indexOf("device"));// isolate the device name
								foundDevices.add(line1);
								for(String s : activeDevices){// search through the list of active devices
									if(line1.equals(s)){// if the device is in the list of active threads
										found = true;// if the device has been found already then dont create a new reader
									}
								}
								if(!found){// if the device isn't already active
									System.out.println(line1);// print the name of the found device
									activeDevices.add(line1);// add the device name to the list of active names
									phoneConnections.add(new PhoneConnection(line1, localPortInUse++, phonePort));
								}
							}
						}
					}
					if(activeDevices.size() > 0 && foundDevices.size() > 0){
						for(String activeDevice : activeDevices){
							boolean didFindDevice = false;
							for(String foundDevice : foundDevices){
								if(foundDevice.equals(activeDevice)){
									didFindDevice = true;
								}
							}
							if(didFindDevice == false){
								System.out.println("deleted");
								activeDevices.remove(activeDevice);
								for(PhoneConnection phone : phoneConnections){
									if(phone.getDevice().equals(activeDevice)){
										phone.shutDown();
										phoneConnections.remove(phone);
									}
								}
							}
						}
					} else if(foundDevices.size() == 0){
						activeDevices.clear();
						for(PhoneConnection phone : phoneConnections){
							phone.shutDown();
						}
						phoneConnections.clear();
					}
					Thread.sleep(200);// sleep the thread so it only runs every 1/4 seconds
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println(e.toString());
				}
			}
		}
	}

}
