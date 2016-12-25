/*
Copyright 2015 Alex Florescu
Copyright 2014 Yahoo Inc.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.example.user.testvision;

import android.app.Activity;
import android.app.IntentService;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.hardware.camera2.CameraManager;
import android.widget.TextView;
import android.widget.Toast;

import org.florescu.android.rangeseekbar.RangeSeekBar;

public class RangeBarTest extends Activity {

    EditText highInputTextH;
    EditText lowInputTextH;
    EditText highInputTextS;
    EditText lowInputTextS;
    EditText highInputTextV;
    EditText lowInputTextV;
    EditText exposureInputText;
    RangeSeekBar rangeBarH;
    RangeSeekBar rangeBarS;
    RangeSeekBar rangeBarV;
    RangeSeekBar rangeBarExposure;
    int lowValueH = 56;
    int highValueH = 74;
    int lowValueS = 12;
    int highValueS = 252;
    int lowValueV = 1;
    int highValueV = 233;
    int lowExposureValue = 10;
    int highExposureValue = 100;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // Seek bar for which we will set text color in code

        configueRangeSeekBars();

        configureTextViews();
    }

    public void configueRangeSeekBars(){
        rangeBarH = (RangeSeekBar) findViewById(R.id.rangebarH);
        rangeBarH.setTextAboveThumbsColorResource(android.R.color.holo_blue_bright);
        rangeBarH.setSelectedMinValue(lowValueH);
        rangeBarH.setSelectedMaxValue(highValueH);
        rangeBarH.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                lowValueH = (int)minValue;
                highValueH = (int)maxValue;
                lowInputTextH.setText(((Integer)lowValueH).toString());
                highInputTextH.setText(((Integer)highValueH).toString());
                Log.e("hgf", "H Value react changed to: Min: " + lowValueH + " Max: " + highValueH);
            }
        });

        rangeBarS = (RangeSeekBar) findViewById(R.id.rangebarS);
        rangeBarS.setTextAboveThumbsColorResource(android.R.color.holo_blue_bright);
        rangeBarS.setSelectedMinValue(lowValueS);
        rangeBarS.setSelectedMaxValue(highValueS);
        rangeBarS.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                lowValueS = (int)minValue;
                highValueS = (int)maxValue;
                lowInputTextS.setText(((Integer)lowValueS).toString());
                highInputTextS.setText(((Integer)highValueS).toString());
                Log.e("hgf", "S Value react changed to: Min: " + lowValueS + " Max: " + highValueS);
            }
        });

        rangeBarV = (RangeSeekBar) findViewById(R.id.rangebarV);
        rangeBarV.setTextAboveThumbsColorResource(android.R.color.holo_blue_bright);
        rangeBarV.setSelectedMinValue(lowValueV);
        rangeBarV.setSelectedMaxValue(highValueV);
        rangeBarV.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                lowValueV = (int)minValue;
                highValueV = (int)maxValue;
                lowInputTextV.setText(((Integer)lowValueV).toString());
                highInputTextV.setText(((Integer)highValueV).toString());
                Log.e("hgf", "V Value react changed to: Min: " + lowValueV + " Max: " + highValueV);
            }
        });

        rangeBarExposure = (RangeSeekBar) findViewById(R.id.rangebarExposure);
        rangeBarExposure.setTextAboveThumbsColorResource(android.R.color.holo_blue_bright);
        rangeBarExposure.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                Log.e("hgf", "Exposure react changed to: " + maxValue);
                lowExposureValue = (int)minValue;
                highExposureValue = (int)maxValue;
                exposureInputText.setText(((Integer)highExposureValue).toString());
                Log.e("hgf", "Exposure Value changed to : " + highValueV);
            }
        });
    }

    public void configureTextViews(){
        lowInputTextH  = (EditText) findViewById(R.id.LowInputH);
        lowInputTextH.setFilters(new InputFilter[]{new InputFilterMinMax(0, 255)});
        lowInputTextH.setText(((Integer)lowValueH).toString());
        lowInputTextH.setImeOptions(EditorInfo.IME_ACTION_DONE);
        lowInputTextH.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(v.getText().toString().equals("")){
                        Toast.makeText(RangeBarTest.this, "Please enter text", Toast.LENGTH_LONG).show();
                        return true;
                    }
                    if(Integer.parseInt(v.getText().toString()) > highValueH){
                        Toast.makeText(RangeBarTest.this, "Unable to save value, the input value is above the High H Value", Toast.LENGTH_LONG).show();
                        lowInputTextH.setText(((Integer)lowValueH).toString());
                    } else {
                        lowValueH = Integer.parseInt(v.getText().toString());
                        rangeBarH.setSelectedMinValue(lowValueH);
                    }
                }
                return false;
            }
        });

        highInputTextH = (EditText) findViewById(R.id.HighInputH);
        highInputTextH.setFilters(new InputFilter[]{new InputFilterMinMax(0, 255)});
        highInputTextH.setText(((Integer)highValueH).toString());
        highInputTextH.setImeOptions(EditorInfo.IME_ACTION_DONE);
        highInputTextH.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(v.getText().toString().equals("")){
                        Toast.makeText(RangeBarTest.this, "Please enter text", Toast.LENGTH_LONG).show();
                        return true;
                    }
                    if(Integer.parseInt(v.getText().toString()) < lowValueH){
                        Toast.makeText(RangeBarTest.this, "Unable to save value, the input value is below the Low H Value", Toast.LENGTH_LONG).show();
                        highInputTextH.setText(((Integer)highValueH).toString());
                    } else {
                        highValueH = Integer.parseInt(v.getText().toString());
                        rangeBarH.setSelectedMaxValue(highValueH);
                    }
                }
                return false;
            }
        });
        lowInputTextS  = (EditText) findViewById(R.id.LowInputS);
        lowInputTextS.setFilters(new InputFilter[]{new InputFilterMinMax(0, 255)});
        lowInputTextS.setText(((Integer)lowValueS).toString());
        lowInputTextS.setImeOptions(EditorInfo.IME_ACTION_DONE);
        lowInputTextS.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(v.getText().toString().equals("")){
                        Toast.makeText(RangeBarTest.this, "Please enter text", Toast.LENGTH_LONG).show();
                        return true;
                    }
                    if(Integer.parseInt(v.getText().toString()) > highValueS){
                        Toast.makeText(RangeBarTest.this, "Unable to save value, the input value is above the High S Value", Toast.LENGTH_LONG).show();
                        lowInputTextS.setText(((Integer)lowValueS).toString());
                    } else {
                        lowValueS = Integer.parseInt(v.getText().toString());
                        rangeBarS.setSelectedMinValue(lowValueS);
                    }
                }
                return false;
            }
        });
        highInputTextS = (EditText) findViewById(R.id.HighInputS);
        highInputTextS.setFilters(new InputFilter[]{new InputFilterMinMax(0, 255)});
        highInputTextS.setText(((Integer)highValueS).toString());
        highInputTextS.setImeOptions(EditorInfo.IME_ACTION_DONE);
        highInputTextS.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(v.getText().toString().equals("")){
                        Toast.makeText(RangeBarTest.this, "Please enter text", Toast.LENGTH_LONG).show();
                        return true;
                    }
                    if(Integer.parseInt(v.getText().toString()) < lowValueS){
                        Toast.makeText(RangeBarTest.this, "Unable to save value, the input value is below the Low S Value", Toast.LENGTH_LONG).show();
                        highInputTextS.setText(((Integer)highValueS).toString());
                    } else {
                        highValueS = Integer.parseInt(v.getText().toString());
                        rangeBarS.setSelectedMaxValue(highValueS);
                    }
                }
                return false;
            }
        });
        lowInputTextV  = (EditText) findViewById(R.id.LowInputV);
        lowInputTextV.setFilters(new InputFilter[]{new InputFilterMinMax(0, 255)});
        lowInputTextV.setText(((Integer)lowValueV).toString());
        lowInputTextV.setImeOptions(EditorInfo.IME_ACTION_DONE);
        lowInputTextV.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(v.getText().toString().equals("")){
                        Toast.makeText(RangeBarTest.this, "Please enter text", Toast.LENGTH_LONG).show();
                        return true;
                    }
                    if(Integer.parseInt(v.getText().toString()) > highValueV){
                        Toast.makeText(RangeBarTest.this, "Unable to save value, the input value is above the High V Value", Toast.LENGTH_LONG).show();
                        lowInputTextV.setText(((Integer)lowValueV).toString());
                    } else {
                        lowValueV = Integer.parseInt(v.getText().toString());
                        rangeBarV.setSelectedMinValue(lowValueV);
                    }
                }
                return false;
            }
        });
        highInputTextV = (EditText) findViewById(R.id.HighInputV);
        highInputTextV.setFilters(new InputFilter[]{new InputFilterMinMax(0, 255)});
        highInputTextV.setText(((Integer)highValueV).toString());
        highInputTextV.setImeOptions(EditorInfo.IME_ACTION_DONE);
        highInputTextV.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(v.getText().toString().equals("")){
                        Toast.makeText(RangeBarTest.this, "Please enter text", Toast.LENGTH_LONG).show();
                        return true;
                    }
                    if(Integer.parseInt(v.getText().toString()) < lowValueV){
                        Toast.makeText(RangeBarTest.this, "Unable to save value, the input value is below the Low V Value", Toast.LENGTH_LONG).show();
                        highInputTextV.setText(((Integer)highValueV).toString());
                    } else {
                        highValueV = Integer.parseInt(v.getText().toString());
                        rangeBarV.setSelectedMaxValue(highValueV);
                    }
                }
                return false;
            }
        });
        exposureInputText = (EditText) findViewById(R.id.ExposureInput);
        exposureInputText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 255)});
        exposureInputText.setText(((Integer)highExposureValue).toString());
        exposureInputText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        exposureInputText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(v.getText().toString().equals("")){
                        Toast.makeText(RangeBarTest.this, "Please enter text", Toast.LENGTH_LONG).show();
                        return true;
                    }
                    if(Integer.parseInt(v.getText().toString()) < lowExposureValue){
                        Toast.makeText(RangeBarTest.this, "Unable to save value, the input value is below the minimum exposure level", Toast.LENGTH_LONG).show();
                        exposureInputText.setText(((Integer)highExposureValue).toString());
                    } else {
                        highExposureValue = Integer.parseInt(v.getText().toString());
                        rangeBarExposure.setSelectedMaxValue(highExposureValue);
                    }
                }
                return false;
            }
        });
    }

    public class InputFilterMinMax implements InputFilter {
        private int min, max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public InputFilterMinMax(String min, String max) {
            this.min = Integer.parseInt(min);
            this.max = Integer.parseInt(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) { }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }
}