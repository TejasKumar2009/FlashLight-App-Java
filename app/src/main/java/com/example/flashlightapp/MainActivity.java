package com.example.flashlightapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.View;
import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private CameraManager mCameraManager;
    private String mCameraId;
    ImageView btn_on_off;
    Button autoflashbtn;
    boolean isFlashOn = false;
    boolean isAutoflash = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_on_off = findViewById(R.id.btn_on_off);
        autoflashbtn = findViewById(R.id.autoflashbtn);

        autoflashbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAutoflash){
                    isAutoflash = false;
                    autoflashbtn.setText("Enable Auto-Flashlight");
                } else{
                    isAutoflash = true;
                    autoflashbtn.setText("Disable Auto-Flashlight");
                }
            }
        });
            SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

            if (lightSensor != null) {
                sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Toast.makeText(this, "Light Sensor is not Available !", Toast.LENGTH_SHORT).show();
            }


        boolean isFlashAvailable = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);

        if (!isFlashAvailable) {
            Toast.makeText(this, "Flashlight not Available !", Toast.LENGTH_SHORT).show();
        }

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


        btn_on_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFlashOn) {
                    btn_on_off.setImageResource(R.drawable.on);
                    switchFlashLight(isFlashOn);
                    isFlashOn = true;
                } else{
                    btn_on_off.setImageResource(R.drawable.off);
                    switchFlashLight(isFlashOn);
                    isFlashOn = false;
                }
            }
        });
    }


    public void switchFlashLight(boolean status) {
        try {
            mCameraManager.setTorchMode(mCameraId, status);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType()==Sensor.TYPE_LIGHT && isAutoflash){
            if (sensorEvent.values[0]<=30){
                btn_on_off.setImageResource(R.drawable.on);
                switchFlashLight(true);
                isFlashOn = true;
            }
            else{
                btn_on_off.setImageResource(R.drawable.off);
                switchFlashLight(false);
                isFlashOn = false;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
}