package com.example.instagramjpeg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.net.NetworkInterface;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    EditText et;
    ImageButton b;
    ImageButton cb;
    ConnectivityManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et = findViewById(R.id.et);
        b = findViewById(R.id.ib);
        cb = findViewById(R.id.cb);

        manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
        }

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!checkUrl(s.toString())) {
                    et.setText("");
                    et.setHint("Invalid Url");
                    et.setHintTextColor(getColor(R.color.red));
                } else {
                    et.setHint("Enter Url");
                    et.setHintTextColor(getColor(R.color.purple_500));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        b.setOnClickListener(view -> {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
            } else if(manager.getActiveNetworkInfo() == null) {
                Toast.makeText(this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
            } else {
                String url = et.getText().toString().trim();
                if (checkUrl(url)) {
                    Intent i = new Intent(this, ScrapActivity.class);
                    i.putExtra("url", url);
//                    Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
                    startActivity(i);

                } else et.setError("Invalid url");
            }
        });

        cb.setOnClickListener(v -> {
            et.setText("");
        });

    }

    private boolean checkUrl(String s) {

        if(s.startsWith("https://")) return true;

        return false;
    }

}