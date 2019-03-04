package com.example.petersam.teroid;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DeviasiRadialActivity extends AppCompatActivity implements SensorEventListener{

    SensorManager sensorManager;
    Sensor sensor;
    TextView teksCounter, teksFrekuensi, teksLGS, teksWaktu;
    boolean stopFlag = false;
    boolean startFlag = false;
    boolean onceTime = true;
    boolean sekaliSaja = true;
    float []akselerasi = new float[3];
    float batasA = (float) 3.25;
    float batasB = (float) 1.5;
    float deltaY = 0;
    float deltaYMaks = 0;
    float lastY;
    float rerataYMaks = 0;
    float frekuensi;
    float LGS;
    int stepCount = 0;
    int highFlag = 0;
    int lowFlag = 1;
    int zona = 0;
    int seconds, minutes, milliseconds;
    long milliSecondTime, startTime, timeBuff, updateTime = 0L;
    Handler handler;
    public static File data = null;
    public static final String fileData = ("Riwayat terapi.txt");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deviasi_radial);

        String namaFolder = "/Data Terapi";
        String direktori = Environment.getExternalStorageDirectory().toString();
        File newFolder = new File(direktori + namaFolder);
        newFolder.mkdir();
        data = new File(direktori + namaFolder +"/"+ fileData);
        if (!data.exists()){
            try {
                data.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        teksFrekuensi = (TextView) findViewById(R.id.teksFrekuensi);
        teksLGS = (TextView) findViewById(R.id.teksLGS);
        teksWaktu = (TextView) findViewById(R.id.textWaktu);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        handler = new Handler();
        Button mulai = (Button) findViewById(R.id.buttonMulai);
        mulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFlag = true;
                startTime = SystemClock.uptimeMillis();
                handler.postDelayed(runnable, 0);
                Toast.makeText(getBaseContext(),"Silakan lakukan gerakan",Toast.LENGTH_LONG).show();
            }
        });
        Button stop = (Button) findViewById(R.id.buttonStop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopFlag = true;
                timeBuff += milliSecondTime;
                handler.removeCallbacks(runnable);
                Toast.makeText(getBaseContext(),"berhenti",Toast.LENGTH_SHORT).show();
            }
        });
        Button menu = (Button) findViewById(R.id.buttonMenu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeviasiRadialActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Button simpan = (Button) findViewById(R.id.buttonSimpan);
        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpanData();
                Toast.makeText(getBaseContext(),"Menyimpan data",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void simpanData(){
        OutputStream fo;
        String gerakan = "Deviasi Radial";
        byte[] bgerakan = gerakan.getBytes();
        String cacah = "-jumlah gerakan: ";
        byte[] bcacah = cacah.getBytes();
        String sLGS = "-LGS(derajat)  : ";
        byte[] bLGS = sLGS.getBytes();
        String sfrekuensi = "-frekuensi(Hz) : ";
        byte[] bfrekuensi = sfrekuensi.getBytes();
        String cacahVal = String.valueOf(stepCount);
        byte[] bcacahVal = cacahVal.getBytes();
        String LGSVal = teksLGS.getText().toString();
        byte[] bLGSVal = LGSVal.getBytes();
        String frekuensiVal = teksFrekuensi.getText().toString();
        byte[] bfrekuensiVal = frekuensiVal.getBytes();
        String newLine = "\n";
        byte[] bnewLine = newLine.getBytes();

        try {
            fo = new FileOutputStream(data, true);
            fo.write(bnewLine);
            fo.write(bgerakan);
            fo.write(bnewLine);
            fo.write(bcacah);
            fo.write(bcacahVal);
            fo.write(bnewLine);
            fo.write(bLGS);
            fo.write(bLGSVal);
            fo.write(bnewLine);
            fo.write(bfrekuensi);
            fo.write(bfrekuensiVal);
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            milliSecondTime = SystemClock.uptimeMillis() - startTime;
            updateTime = timeBuff + milliSecondTime;
            seconds = (int)(updateTime/1000);
            minutes = seconds/60;
            seconds = seconds % 60;
            milliseconds = (int)(updateTime % 1000);
            teksWaktu.setText("" + minutes +":"+ String.format("%02d",seconds)+":"+ String.format("%03d",milliseconds));
            handler.postDelayed(this, 0);
        }
    };
    public void onResume(){
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (startFlag){
            akselerasi[0] = sensorEvent.values[0];
            akselerasi[1] = sensorEvent.values[1];
            akselerasi[2] = sensorEvent.values[2];
            getCounter();
            getRerataYMaks();
        }
    }
    //fungsi untuk menghitung jumlah gerakan
    public void getCounter(){ //berhasil
        if (!stopFlag && akselerasi[1]>batasA && highFlag==0 && lowFlag==1){
            stepCount = stepCount+1;
            teksCounter = (TextView) findViewById(R.id.textCounter);
            teksCounter.setText(String.valueOf(stepCount));
            highFlag = 1;
            lowFlag = 0;
            zona = 2;
        }
        else if (!stopFlag && akselerasi[1]<batasA && highFlag==1){
            highFlag = 0;
            zona = 3;
        }
        else if(!stopFlag && akselerasi[1]<batasB){
            lowFlag = 1;
            zona = 0;
        }
        else if (!stopFlag && akselerasi[1]>batasB && lowFlag==1){
            zona = 1;
        }
    }

    //fungsi untuk menghitung LGS
    public void getRerataYMaks(){ //berhasil
        deltaY = Math.abs(lastY - akselerasi[1]);

        switch (zona){
            case 1:
                deltaYMaks = 0;
                sekaliSaja = true;
                break;
            case 2:
                if (deltaY>deltaYMaks){
                    deltaYMaks = deltaY;
                }
                break;
            case 3:
                while (onceTime){
                    rerataYMaks = deltaYMaks;
                    onceTime = false;
                }
                while (sekaliSaja){
                    rerataYMaks = (rerataYMaks + deltaYMaks)/2;
                    LGS = (float) ((rerataYMaks - 0.0732)/0.129);

                    //pernyataan untuk menghitung frekuensi gerakan
                    frekuensi = 1 / (((float)minutes*60 + (float)seconds) / (float)stepCount);
                    teksFrekuensi.setText(String.format("%.2f",frekuensi));
                    sekaliSaja = false;
                }
                teksLGS.setText(String.format("%.1f", LGS));
                break;
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
