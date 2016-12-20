package org.usfirst.frc.team360.robot;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * AdbBridge interfaces to an Android Debug Bridge (adb) binary, which is needed
 * to communicate to Android devices over USB.
 *
 * adb binary provided by https://github.com/Spectrum3847/RIOdroid
 */
public class AdbBridge {

    public AdbBridge() {
    }

    public Process runCommand(String args) {
        Runtime r = Runtime.getRuntime();
        Process p;
        try {
        	//p = new ProcessBuilder("/bin/bash", args).start();
        	p = r.exec(args);
        } catch (IOException e) {
        	System.out.println("AdbBridge: Could not run command " + args);
            e.printStackTrace();
            return null;
        }
        return p;
    }

    public Process startLogcat(){
    	runCommand("adb logcat -c");
    	Process ret = runCommand("adb logcat Sas:W *:F");
    	return ret;
    }
    public Process startLogcatAtDevice(String device){
    	runCommand("adb -s " + device + " logcat -c");
    	Process ret = runCommand("adb -s " + device + " logcat Sas:W *:F");
    	return ret;
    }
    public Process getDevices(){
   		Process ret = runCommand("adb devices");
    	return ret;
    }
    public void start() { 
   		System.out.println("Starting adb");
   		runCommand("adb start-server");
    }

    public void stop() {
        System.out.println("Stopping adb");
        runCommand("adb kill-server");
    }

    public void restartAdb() {
        System.out.println("Restarting adb");
        stop();
        start();
    }
    public void removePortForward(int local_port) {
    	Process ret = runCommand("adb forward --remove tcp:" + local_port);
    } 
    public Process removePortForward(String device, int local_port) {
    	Process ret = runCommand("adb -s " + device + " forward --remove tcp:" + local_port);
    	return ret;
    }
    public void portForward(int local_port, int remote_port) {
    	Process ret = runCommand("adb forward tcp:" + local_port + " tcp:" + remote_port);
    }
    public Process portForward(String device, int local_port, int remote_port) {
    	Process ret = runCommand("adb -s " + device + " forward tcp:" + local_port + " tcp:" + remote_port);
    	return ret;
    }

    public void reversePortForward(int remote_port, int local_port) {
        runCommand("adb reverse tcp:" + remote_port + " tcp:" + local_port);
    }

    public void restartApp() {
        System.out.println("Restarting app");
        runCommand("shell am force-stop com.team254.cheezdroid \\; "
                + "am start com.team254.cheezdroid/com.team254.cheezdroid.VisionTrackerActivity");
    }
}