package org.usfirst.frc.team360.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team360.JSON.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This controls all vision actions, including vision updates, capture, and
 * interfacing with the Android phone with Android Debug Bridge. It also stores
 * all VisionUpdates (from the Android phone) and contains methods to add
 * to/prune the VisionUpdate list. Much like the subsystems, outside methods get
 * the VisionServer instance (there is only one VisionServer) instead of
 * creating new VisionServer instances.
 * 
 * @see VisionUpdate.java
 */

public class VisionServer implements Runnable {

    private static VisionServer s_instance = null;
    private ServerSocket m_server_socket;
    private boolean m_running = true;
    private int m_port;
    AdbBridge adb = new AdbBridge();
    double lastMessageReceivedTime = 0;
    private boolean m_use_java_time = false;

    private ArrayList<ServerThread> serverThreads = new ArrayList<>();
    private volatile boolean mWantsAppRestart = false;

    public static VisionServer getInstance() {
        if (s_instance == null) {
            s_instance = new VisionServer(8254);
        }
        return s_instance;
    }

    private boolean mIsConnect = false;

    public boolean isConnected() {
        return mIsConnect;
    }

    public void requestAppRestart() {
        mWantsAppRestart = true;
    }

    protected class ServerThread implements Runnable {
        private Socket m_socket;

        public ServerThread(Socket socket) {
            m_socket = socket;

SmartDashboard.putBoolean("stat", m_socket.isConnected());
        }

        public void send(String  message) {
            String toSend = message + "\n";
            //if (m_socket != null && m_socket.isConnected()) {
            if (m_socket != null || m_socket.isConnected()) {
                try {
                    OutputStream os = m_socket.getOutputStream();
                    os.write(toSend.getBytes());
                } catch (IOException e) {
                	SmartDashboard.putString("cobn","VisionServer: Could not send data to socket");
                }
            }
        }

        public void handleMessage(OffWireMessage message, double timestamp) {
            SmartDashboard.putString("hi", "recieved Message");
        }

        public boolean isAlive() {
        	return m_socket != null || m_socket.isConnected() && !m_socket.isClosed();

        	//return m_socket != null && m_socket.isConnected() && !m_socket.isClosed();
        }

        @Override
        public void run() {
        	SmartDashboard.putBoolean("stat", m_socket.isConnected());
        	try{
                SmartDashboard.putString("hsi", "sd Message");
            if (m_socket == null) {
                return;
            }
            try {
                InputStream is = m_socket.getInputStream();
                byte[] buffer = new byte[2048];
                int read;
                while (m_socket.isConnected() && (read = is.read(buffer)) != -1) {
                    double timestamp = getTimestamp();
                    lastMessageReceivedTime = timestamp;
                    String messageRaw = new String(buffer, 0, read);
                    String[] messages = messageRaw.split("\n");
                    for (String message : messages) {
                        OffWireMessage parsedMessage = new OffWireMessage(message);
                        if (parsedMessage.isValid()) {
                            handleMessage(parsedMessage, timestamp);
                        }
                    }
                    
                }
                SmartDashboard.putString("hi", "Socket disconnected");
            } catch (IOException e) {
            	SmartDashboard.putString("cofdn","Could not talk to socket");
            }
            if (m_socket != null) {
                try {
                    m_socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        	}catch(Exception e){
        		
        	}
        }
    }

    /**
     * Instantializes the VisionServer and connects to ADB via the specified
     * port.
     * 
     * @param Port
     */
    private VisionServer(int port) {
        try {
            adb = new AdbBridge();
            m_port = port;
            m_server_socket = new ServerSocket(port);
            adb.start();
            adb.portForward(port, port);
            try {
                String useJavaTime = System.getenv("USE_JAVA_TIME");
                m_use_java_time = "true".equals(useJavaTime);
            } catch (NullPointerException e) {
                m_use_java_time = false;
            }
            SmartDashboard.putDouble("das", 5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(this).start();
        new Thread(new AppMaintainanceThread()).start();
    }

    public void restartAdb() {
        adb.restartAdb();
        adb.reversePortForward(m_port, m_port);
    }


    @Override
    public void run() {

        SmartDashboard.putString("ServerThread", "starting");
        while (m_running) {
            SmartDashboard.putString("ServerThread", "running");
            try {
                SmartDashboard.putString("ServerThread", "trying");
                Socket p = m_server_socket.accept();
                SmartDashboard.putString("ServerThread", "accepting");
                ServerThread s = new ServerThread(p);
                SmartDashboard.putString("ServerThread", "creating");
                new Thread(s).start();
                serverThreads.add(s);
                SmartDashboard.putString("ServerThread", "finished");
            } catch (IOException e) {
                SmartDashboard.putString("ServerThread", "Issue accepting socket connection!");
            } finally {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            SmartDashboard.putBoolean("ew", true);
        }
    }

    private class AppMaintainanceThread implements Runnable {
  
		@Override
		public void run() {
			try{
			while (true) {
                if (getTimestamp() - lastMessageReceivedTime > .1) {
                    // camera disconnected
                    adb.reversePortForward(m_port, m_port);
                    SmartDashboard.putString("AppMaintainanceThread", "dissconnect");
                    mIsConnect = false;
                } else {
                	SmartDashboard.putString("AppMaintainanceThread", "connected");
                    mIsConnect = true;
                }
                if (mWantsAppRestart) {
                    adb.restartApp();
                    mWantsAppRestart = false;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
			}catch(Exception e){
				
			}
		}
    }

    private double getTimestamp() {
        if (m_use_java_time) {
            return System.currentTimeMillis();
        } else {
            return Timer.getFPGATimestamp();
        }
    }
}