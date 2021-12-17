package com.example.instagramjpeg;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class BackgroundVideotask extends AsyncTask<String, Void, Void> {

    Context c;
    AlertDialog dialog;

    public BackgroundVideotask(Context c) {
        this.c = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Toast.makeText(c, "onPreExecute", Toast.LENGTH_SHORT).show();

        dialog = new AlertDialog.Builder(c)
                .setTitle("Downloading Content")
                .setMessage("Please wait until download finishes")
                .setNegativeButton("Cancel", (dialog1, which) -> {
                    cancel(true);
                }).create();
        dialog.show();

    }

    @Override
    protected Void doInBackground(String... strings) {

        try {
            URL url = new URL(strings[0]);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.connect();

            File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File vd = new File(f, "Instagram.vdo");
            if(!vd.exists()) {
                vd.mkdir();
            }
            File file = new File(vd, System.currentTimeMillis()+".mp4");
            file.createNewFile();

            InputStream in = con.getInputStream();

            FileOutputStream out = new FileOutputStream(file);

            byte[] data = new byte[1024];
            int len;
            while((len = in.read(data)) > 0) {
                out.write(data, 0, len);
            }
//            for(; (len = in.read(data, 0, 1024)) > 0;){
//                out.write(data, 0, len);
//            }

            out.close();

        } catch (MalformedURLException | ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        dialog.dismiss();
    }
}
