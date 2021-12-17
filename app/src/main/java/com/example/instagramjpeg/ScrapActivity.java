package com.example.instagramjpeg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class ScrapActivity extends AppCompatActivity {

    static ArrayList<String> list;
    static RecyclerView rv;
    static AlertDialog dialog;

    static TextView tv1, tv2, tv3;
    static ImageView im;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrap);

        getSupportActionBar().hide();
        String url = getIntent().getStringExtra("url");

        rv = findViewById(R.id.rv);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        im = findViewById(R.id.im);

        BagroundTask task = new BagroundTask(this, url);

        dialog = new AlertDialog.Builder(this)
                .setTitle("Fetching Data")
                .setMessage("please wait......")
                .setCancelable(false)
                .create();
        dialog.show();

        task.execute();
//        Recycler_Adapter adapter = new Recycler_Adapter(this, list);
//        rv.setAdapter(adapter);


    }
}