package com.example.user.testvision;

import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.opencv.android.JavaCameraView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.*;
import org.opencv.imgproc.*;
import org.opencv.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// OpenCV Classes

public class Vision extends AppCompatActivity implements CvCameraViewListener2 {


    enum CameraOptions { DirectCameraInput, ResizedImage, HSVImage, ThresholdedImage, ContourImage, FinalImage};

    CameraOptions selectedView;

    public TextView robotConnectionStatus;
    Socket connectionSocket;
    ServerSocket welcomeSocket;
    boolean shouldRun;

    // Used for logging success or failure messages
    private static final String TAG = "OCVSample::Activity";

    // Loads camera view of OpenCV for us to use. This lets us see using OpenCV
    private CameraBridgeViewBase mOpenCvCameraView;

    // Used in Camera selection from menu (when implemented)
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemSwitchCamera = null;
    ArrayList mCountours;
    Mat mRgba;
    Mat mRgbaF;
    Mat mRgbaT;
    Mat mHSV;
    Mat finalImage;
    Mat mThresholded;
    Mat mHeir;

    Scalar mLowFilter, mHighFilter, mContourColor;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    public Vision() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

Camera mCam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mCam = Camera.open();
        Camera.Parameters params = mCam.getParameters();
        params.setExposureCompensation(params.getMinExposureCompensation());

        selectedView = CameraOptions.FinalImage;

        setContentView(R.layout.activity_vision);

        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.show_camera_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);




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
protected class AndroidServer extends Thread {
    DataOutputStream outToClient;
    String clientSentence = "";
    String capitalizedSentence;
    BufferedReader inFromClient;
    long timeSinceLastMessage = 0;

    public AndroidServer() {
        shouldRun = true;
    }

    public void run() {
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
        while (connectionSocket != null && !connectionSocket.isClosed() && connectionSocket.isConnected() && shouldRun &&
                System.currentTimeMillis() - timeSinceLastMessage < 333) {
            try {
                while (inFromClient.ready() && (clientSentence = inFromClient.readLine()) != null) {
                    if (clientSentence.equals("Knock Knock Request")) {
                        Log.e("Sas", "Recieved Knock Knock Request");
                        send(createWhosThereTag());
                        timeSinceLastMessage = System.currentTimeMillis();
                    } else if (clientSentence.equals("App Type Request")) {
                        Log.e("Sas", "Recieved App Type Request");
                        send(createInfoResponse());
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
        if (shouldRun) {
            try {
                if (shouldRun) {
                    Thread.sleep(20);
                }
            } catch (Exception e) {
                Log.e("Sas", "error " + e.toString());
            }
            run();
        }

    }

    public String createInfoResponse() {
        return createMessageTypeTag("phoneType") + createTaggedMessage("phoneType", "vision");
    }

    public String createWhosThereTag() {
        return createTaggedMessage("whosThere", "Whos There");
    }

    public String createMessageTypeTag(String messageType) {
        return createTaggedMessage("messageType", messageType);
    }

    public String createTaggedMessage(String tag, String message) {
        return "<" + tag + ">" + message + "</" + tag + ">";
    }

    public void send(String message) {
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
    public void onPause()
    {
        super.onPause();
        shouldRun = false;
        try{
            welcomeSocket.close();
            connectionSocket.close();
        } catch (Exception e) {
            Log.e("Sas", "error " + e.toString());
        }
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(!shouldRun) {
            Thread ServerThread = new AndroidServer();
            ServerThread.start();
            shouldRun = true;
        }
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        shouldRun = false;
        try{
            welcomeSocket.close();
            connectionSocket.close();
        } catch (Exception e) {
            Log.e("Sas", "error " + e.toString());
        }
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        // if (mDrawerToggle.onOptionsItemSelected(item)) {
        //     return true;
        // }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.Direct_Camera_Input:
                selectedView = CameraOptions.DirectCameraInput;
                break;
            //case R.id.Rotated_Image:
            //    selectedView = CameraOptions.RotatedImage;
            //    break;
            case R.id.Resized_Image:
                selectedView = CameraOptions.ResizedImage;
                break;
            case R.id.HSV_Image:
                selectedView = CameraOptions.HSVImage;
                break;
            case R.id.Thresholded_Image:
                selectedView = CameraOptions.ThresholdedImage;
                break;
            case R.id.Contour_Image:
                selectedView = CameraOptions.ContourImage;
                break;
            case R.id.Final_Image:
                selectedView = CameraOptions.FinalImage;
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        Log.e("Chosen View ", selectedView.toString());
        return true;
    }
    public void onCameraViewStarted(int width, int height) {

        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
        mRgbaT = new Mat(width, width, CvType.CV_8UC4);
        mHSV = new Mat(width, width, CvType.CV_8UC4);
        mThresholded = new Mat(width, width, CvType.CV_8UC4);
        mHeir = new Mat(width, width, CvType.CV_8UC4);
        finalImage = new Mat(width, width, CvType.CV_8UC4);
        mLowFilter = new Scalar(0, 0, 0);
        mHighFilter = new Scalar(110, 255, 185);
        mContourColor = new Scalar(124, 124, 124);
        mCountours = new ArrayList();
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        // TODO Auto-generated method stub
        mRgba = inputFrame.rgba();
        if(selectedView.equals(CameraOptions.DirectCameraInput)) {
            return mRgba;
        }
        // Rotate mRgba 90 degrees
        mCountours.clear();
        Imgproc.resize(mRgba, mRgbaF, mRgbaF.size(), 0,0, 0);
        if(selectedView.equals(CameraOptions.ResizedImage)){
            return mRgbaF;
        }
        Imgproc.cvtColor(mRgbaF, mHSV, Imgproc.COLOR_RGB2HSV);
        if(selectedView.equals(CameraOptions.HSVImage)){
            return mHSV;
        }
        Core.inRange(mHSV, mLowFilter, mHighFilter, mThresholded);
        if(selectedView.equals(CameraOptions.ThresholdedImage)){
            return mThresholded;
        }
        Imgproc.findContours(mThresholded, mCountours, mHeir, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        mThresholded.copyTo(finalImage);
        if(selectedView.equals(CameraOptions.ContourImage)){
            for(int i = 0; i < mCountours.size(); i++){
                Imgproc.drawContours(mThresholded, mCountours, i, mContourColor);
            }
            return mThresholded;
        }
        for(int i = 0; i < mCountours.size(); i++){
            Rect rec = Imgproc.boundingRect((MatOfPoint) mCountours.get(i));
            if(rec.width > 40 && rec.height > 40 && rec.width < 340 && rec.height< 250) {
                Imgproc.drawContours(finalImage, mCountours, i, mContourColor);
            }
        }
        return finalImage;
    }
}