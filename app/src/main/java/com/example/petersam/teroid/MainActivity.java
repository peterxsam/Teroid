package com.example.petersam.teroid;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    //inisiasi variabel
    Button fleksi, ekstensi, deviasiRadial, deviasiUlnar, pronasi, supinasi, OK;
    public static File data = null;
    public static final String fileData = ("Riwayat terapi.txt");
    EditText editText;
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //pembuatan file & folder untuk menyimpan data
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
        //fungsi jika button dipencet
        OK = (Button) findViewById(R.id.buttonOK);
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeNama();
                Toast.makeText(getBaseContext(),"Tercatat, silakan pilih model gerakan",Toast.LENGTH_LONG).show();
            }
        });
        deviasiRadial = (Button) findViewById(R.id.buttonDeviasiRadial);
        deviasiRadial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DeviasiRadialActivity.class);
                startActivity(intent);
                finish();
            }
        });
        deviasiUlnar = (Button) findViewById(R.id.buttonDeviasiUlnar);
        deviasiUlnar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DeviasiUlnarActivity.class);
                startActivity(intent);
                finish();
            }
        });
        fleksi = (Button) findViewById(R.id.buttonFleksi);
        fleksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FleksiActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ekstensi = (Button) findViewById(R.id.buttonEkstensi);
        ekstensi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EkstensiActivity.class);
                startActivity(intent);
                finish();
            }
        });
        pronasi = (Button) findViewById(R.id.buttonPronasi);
        pronasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PronasiActivity.class);
                startActivity(intent);
                finish();
            }
        });
        supinasi = (Button) findViewById(R.id.buttonSupinasi);
        supinasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SupinasiActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    //fungsi untuk menyimpan nama pasien
    public void writeNama(){
        OutputStream fo;
        editText = (EditText) findViewById(R.id.textNama);
        String nama = editText.getText().toString();
        byte[] bnama = nama.getBytes();
        String newLine = "\n";
        byte[] bnewLine = newLine.getBytes();
        Date date = new Date();
        String sdate = dateFormat.format(date);
        byte[] bdate = sdate.getBytes();
        String pasien = "Pasien: ";
        byte[] bpasien = pasien.getBytes();
        try {
            fo = new FileOutputStream(data, true);
            fo.write(bnewLine);
            fo.write(bpasien);
            fo.write(bnama);
            fo.write(bnewLine);
            fo.write(bdate);
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        this.finish();
    }
}
