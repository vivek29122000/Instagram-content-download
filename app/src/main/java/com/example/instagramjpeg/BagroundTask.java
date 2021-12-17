package com.example.instagramjpeg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class BagroundTask extends AsyncTask<Void, Void, Void> {
    String url;
    ArrayList<String> list;
    ArrayList<String> list1;
    ArrayList<Boolean> isVideo;
    Context c;

    String TAG = "viveksingh";

    boolean b = false;

    String profilepic, username, fullname, followers;

    BagroundTask(Context c, String url) {
        this.url = url;
        this.c = c;
        list = new ArrayList<>();
        list1 = new ArrayList<>();
        isVideo = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:35.0) Gecko/20100101 Firefox/35.0").get();
            Elements elements = doc.getElementsByTag("script");

            String jsonstr = null;

            for(Element e: elements) {
                for(DataNode node: e.dataNodes()) {
                    String s = node.getWholeData();
                    if(s.startsWith("window._sharedData =")) {
                        jsonstr = s.substring(21, s.length()-1);
                    }
                }
            }
//            System.out.println(jsonstr);
            Log.i("instaimg", jsonstr);

            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(jsonstr);
            JSONObject entrydata = (JSONObject) object.get("entry_data");
            JSONArray postpage = (JSONArray) entrydata.get("PostPage");

            JSONObject obj = (JSONObject) postpage.get(0);
            JSONObject graphql = (JSONObject) obj.get("graphql");
            JSONObject shortcode_media = (JSONObject) graphql.get("shortcode_media");


//            OWNER DETAILS-------------------------------------------------------------------------

            JSONObject owner = (JSONObject) shortcode_media.get("owner");
            System.out.println(owner.get("profile_pic_url"));
            System.out.println(owner.get("username"));
            System.out.println(owner.get("full_name"));
            JSONObject follower = (JSONObject) owner.get("edge_followed_by");
            System.out.println(follower.get("count").toString());

            profilepic = (String) owner.get("profile_pic_url");
            username = (String) owner.get("username");
            fullname = (String) owner.get("full_name");
            followers = follower.get("count").toString();
//            OWNER DETAILS END---------------------------------------------------------------------

            JSONArray display_resources = (JSONArray) shortcode_media.get("display_resources");
            JSONObject obj2 = (JSONObject) display_resources.get(0);

//			LINK OF FIRST IMAGE
//			for(Object o: display_resources) {
//				JSONObject obj3 = (JSONObject) o;
//				System.out.println(obj3.get("src"));
//			}
//			boolean b = (boolean) shortcode_media.get("is_video");
//			System.out.println(b);


//            IF POST HAS ONLY ONE MEDIA FILE-----------------------------------------------------
            JSONObject edge_sidecar_to_children = (JSONObject) shortcode_media.get("edge_sidecar_to_children");
            if(edge_sidecar_to_children == null) {

                if((boolean) shortcode_media.get("is_video")) {
                    list.add((String) shortcode_media.get("video_url"));
                    isVideo.add(true);
                    list1.add("0");
                    Log.i(TAG, "video url: " + (String) shortcode_media.get("video_url"));
                }
                else {
                    for (Object o : display_resources) {
                        JSONObject obj3 = (JSONObject) o;
                        System.out.println(obj3.get("src"));
                        isVideo.add(false);
                        list.add((String) obj3.get("src"));
                        list1.add(obj3.get("config_width").toString());
                        Log.i(TAG, "image url: " + (String) shortcode_media.get("video_url") + "  quality: " + obj3.get("config_width").toString());
                    }
                }

            }
//            --------------------------------------------------------------------------------------
//            IF POST HAS MULTIPLE MEDIA FILE-------------------------------------------------------
            else {

                JSONArray edges = (JSONArray) edge_sidecar_to_children.get("edges");
                int len = edges.size();

                for (Object o : edges) {
                    JSONObject obj3 = (JSONObject) o;
                    JSONObject node = (JSONObject) obj3.get("node");
                    if((boolean) node.get("is_video")) {
                        list.add((String) node.get("video_url"));
                        list1.add("0");
                        isVideo.add(true);
                        Log.i(TAG, "video url: " + (String) node.get("video_url"));
                        continue;
                    }
                    JSONArray disp_res = (JSONArray) node.get("display_resources");
                    for (Object ob : disp_res) {
                        JSONObject obj4 = (JSONObject) ob;
                        Log.i("instaimg", (String) obj4.get("src"));
                        list.add((String) obj4.get("src"));
                        list1.add(obj4.get("config_width").toString());
                        isVideo.add(false);
//                        Log.i(TAG, "image url: " + (String) shortcode_media.get("video_url") + "  quality: " + obj3.get("config_width").toString());
                    }
                    Log.i("instaimg", "  ");
                }
            }
//          MULTIPLE MEDIA FILE END---------------------------------------------------------------------------

        } catch(IllegalStateException | ExceptionInInitializerError e3) {
            b = true;
        } catch(FileNotFoundException e2) {
            b = true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if(b) {
            Toast.makeText(c, "Some Error Ocurred\n   Please Try Again", Toast.LENGTH_SHORT).show();
            ScrapActivity.dialog.dismiss();
        } else {
            Recycler_Adapter adapter = new Recycler_Adapter(c, list, list1, isVideo);
            Picasso.with(c).load(profilepic).into(ScrapActivity.im);
            ScrapActivity.tv1.setText(username);
            ScrapActivity.tv2.setText(fullname);
            ScrapActivity.tv3.setText(followers);
            ScrapActivity.dialog.dismiss();
            ScrapActivity.rv.setAdapter(adapter);
        }
    }
}
