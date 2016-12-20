package com.example.josh.robotsim;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.*;
import android.app.admin.DevicePolicyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    public TextView robotConnectionStatus;
    Socket connectionSocket;
    ServerSocket welcomeSocket;
    boolean shouldRun;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        robotConnectionStatus = (TextView) findViewById(R.id.robotConnectionDevice);
        final Button b = (Button) findViewById(R.id.button_send);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Log.e("Sas", encodeString("Vision", "y=2"));
            }
        });
        Thread ServerThread = new AndroidServer();
        ServerThread.start();
    }
    protected class AndroidServer extends Thread
    {
        DataOutputStream outToClient;
        String clientSentence = "";
        String capitalizedSentence;
        BufferedReader inFromClient;
        long timeSinceLastMessage = 0;
        public AndroidServer () {
            shouldRun = true;
        }
        public void run(){
            try {
                welcomeSocket = new ServerSocket(3600);
                welcomeSocket.setReuseAddress(true);
                connectionSocket = welcomeSocket.accept();
                connectionSocket.setReuseAddress(true);
                inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                timeSinceLastMessage = System.currentTimeMillis();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        robotConnectionStatus.setText("Robot Connection Status: Connected");
                    }
                });
            } catch (Exception e) {
                Log.e("Sas", "error " + e.toString());
            }
            while(connectionSocket != null && !connectionSocket.isClosed() && connectionSocket.isConnected() && shouldRun &&
                    System.currentTimeMillis() - timeSinceLastMessage < 333){
                try {
                    while(inFromClient.ready() && (clientSentence = inFromClient.readLine()) != null){
                        Log.e("Sas", clientSentence);
                        if(clientSentence.equals("Knock Knock")){
                            send("Whos There");
                            timeSinceLastMessage = System.currentTimeMillis();
                        }
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.e("Sas", "error " + e.toString());
                }
                try {
                    Thread.sleep(20);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            try {
                welcomeSocket.close();
                connectionSocket.close();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        robotConnectionStatus.setText("Robot Connection Status: Disconnected");
                    }
                });

            } catch (Exception e) {
                Log.e("Sas", "error " + e.toString());
            }
            Log.e("Sas", "done");
            if(shouldRun) {
                try{
                    if(shouldRun) {
                        Thread.sleep(20);
                    }
                } catch (Exception e) {
                    Log.e("Sas", "error " + e.toString());
                }
                run();
            }

        }
        public void send(String message){
            try {
                outToClient.writeBytes(message + '\n');
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e("Sas", e.toString());
            }
        }
    }
    public String encodeString(String tag, String message){
        return "@" + tag + "$" + message + "%";
    }

    @Override
    public void onPause() {
        super.onPause();
        shouldRun = false;
        try{
            welcomeSocket.close();
            connectionSocket.close();
        } catch (Exception e) {
            Log.e("Sas", "error " + e.toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!shouldRun) {
            Thread ServerThread = new AndroidServer();
            ServerThread.start();
            shouldRun = true;
        }
    }

    public void onDestroy() {
        shouldRun = false;
        try{
            welcomeSocket.close();
            connectionSocket.close();
        } catch (Exception e) {
            Log.e("Sas", "error " + e.toString());
        }
        super.onDestroy();
    }
}