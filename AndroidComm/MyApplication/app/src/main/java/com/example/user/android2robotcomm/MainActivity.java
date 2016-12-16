package com.example.user.android2robotcomm;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.*;
import android.app.admin.DevicePolicyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.Socket;

public class MainActivity extends AppCompatActivity  {
    RobotConnection rc;
    public TextView t;
    int i = 0;
    TCPServer ts;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t = (TextView) findViewById(R.id.thing);
        final Button b = (Button) findViewById(R.id.button_send);
        b.setOnClickListener(new View.OnClickListener(){
          public void onClick(View v){

              Log.e("Sas", "hello");
              try {
                  t.setText(((Integer)i).toString());
                  i++;
                  if(i==1) {
                      ts = new TCPServer(t);
                     // ts.doInBackground(null);
                  }
              }catch (Exception e){

              }
          }
        });
         rc = new RobotConnection();
        rc.start();
    }

    @Override
    public void onPause()
    {
        super.onPause();

    }

    @Override
    public void onResume()
    {
        super.onResume();

    }
    private void enableDeviceAdmin() {
        DevicePolicyManager manager =
                (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = OurDeviceAdmin.getComponentName(this);

        if (!manager.isAdminActive(componentName)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            startActivityForResult(intent, 0);
            return;
        }
    }
    public void onDestroy() {
        super.onDestroy();

    }
}
