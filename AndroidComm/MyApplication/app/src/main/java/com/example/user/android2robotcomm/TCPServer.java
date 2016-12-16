package com.example.user.android2robotcomm;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.*;
import java.net.*;

class TCPServer extends AsyncTask<String, Void, TCPServer>
{
    TextView t;
    @Override
    protected TCPServer doInBackground(String... params) {
        try {
            Log.i("Sa", "started");
            String clientSentence;
            String capitalizedSentence;
            ServerSocket welcomeSocket = new ServerSocket(6789);

            Socket connectionSocket = welcomeSocket.accept();

            BufferedReader inFromClient =
                    new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            outToClient.writeBytes("Hello");
            clientSentence = inFromClient.readLine();

            capitalizedSentence = clientSentence.toUpperCase() + '\n';
            outToClient.writeBytes(capitalizedSentence);

        }catch(Exception e){
            Log.e("error", e.toString());
        }
        return null;
    }

    public TCPServer(TextView t)
    {

        Log.i("Sas", "stadrted");
    }
}