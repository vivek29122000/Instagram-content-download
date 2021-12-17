package com.example.instagramjpeg;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class Recycler_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context c;
    ArrayList<String> list;
    ArrayList<String> list1;
    ArrayList<Boolean> isVideo;

    int image = 0;
    int video = 1;

    Recycler_Adapter(Context c, ArrayList<String> list, ArrayList<String> list1, ArrayList<Boolean> isVideo) {
        this.c = c;
        this.list = list;
        this.list1 = list1;
        this.isVideo = isVideo;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if(viewType == image) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_image_layout, parent, false);
            return new Holder(v);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_video_layout, parent, false);
            return new VideoHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder hold, int position) {
        if(!isVideo.get(position)) {
            Holder holder = (Holder) hold;

            Picasso.with(c).load(list.get(position)).placeholder(R.drawable.ic_baseline_error_outline_24).into(holder.im);
            (holder).tv.setText(String.valueOf(list1.get(position)) + "p");

            (holder).b.setOnClickListener(v -> {
                (holder).b.setBackgroundColor(c.getColor(R.color.teal_200));
                Toast.makeText(c, "Saved", Toast.LENGTH_SHORT).show();
                BitmapDrawable draw = (BitmapDrawable) (
                        holder).im.getDrawable();
                Bitmap bmp = draw.getBitmap();
                try {
                    saveImage(bmp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            VideoHolder holder = (VideoHolder) hold;
            holder.vd.setVideoURI(Uri.parse(list.get(position)));
            holder.vd.start();
            holder.b.setOnClickListener(v -> {
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(list.get(position)));
//                i.setPackage("com.android.chrome");
//                c.startActivity(i);

                saveVideo(list.get(position));
//                BackgroundVideotask task = new BackgroundVideotask(c);
//                task.execute(list.get(position));
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return isVideo.get(position)?video:image;
    }

    private void saveImage(Bitmap bitmap) throws IOException {

        File publicFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File myImg = new File(publicFile, "Instagram.jpeg");
        if(!myImg.exists()) {
            myImg.mkdir();
        }

        File img = new File(myImg, System.currentTimeMillis() + ".jpg");
        img.createNewFile();

        FileOutputStream stream = new FileOutputStream(img);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        stream.flush();
        stream.close();

        Intent i = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        i.setData(Uri.fromFile(img));
        c.sendBroadcast(i);

    }

    private void saveVideo(String url) {
        DownloadManager manager = (DownloadManager) c.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));

//        req.setDestinationInExternalFilesDir(c, Environment.DIRECTORY_PICTURES, "/instagramvideo/" + System.currentTimeMillis()+".jpg");

        req.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "/Instagram.video/" + System.currentTimeMillis()+".mp4");
        manager.enqueue(req);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Holder extends RecyclerView.ViewHolder{

        ImageButton b;
        ImageView im;
        TextView tv;

        public Holder(@NonNull View itemView) {
            super(itemView);
            b = itemView.findViewById(R.id.b);
            im = itemView.findViewById(R.id.im);
            tv = itemView.findViewById(R.id.tv);
        }
    }

    class VideoHolder extends RecyclerView.ViewHolder {
        ImageButton b;
        VideoView vd;
        TextView tv;
        public VideoHolder(@NonNull View itemView) {
            super(itemView);
            b = itemView.findViewById(R.id.b);
            vd = itemView.findViewById(R.id.vd);
            tv = itemView.findViewById(R.id.tv);
        }
    }
}
