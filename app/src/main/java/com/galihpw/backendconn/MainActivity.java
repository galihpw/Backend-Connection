package com.galihpw.backendconn;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    TextView tvHasil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvHasil = (TextView) findViewById(R.id.textView);
    }

    private class AmbilData extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... strUrl) {
            Log.v("yw", "mulai ambil data");
            String hasil="";
            //ambil data dari internet
            InputStream inStream = null;
            int len = 500; //buffer
            try {
                URL url = new URL(strUrl[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //timeout
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);


                conn.setRequestMethod("GET");
                conn.connect();
                int response = conn.getResponseCode();
                inStream = conn.getInputStream();  //ambil stream data


                //konversi stream ke string
                Reader r = null;
                r = new InputStreamReader(inStream, "UTF-8");
                BufferedReader bfr = new BufferedReader(r);
                String s;
                StringBuilder sb = new StringBuilder();
                s = bfr.readLine();
                while (s != null) {
                    sb.append(s);
                    s = bfr.readLine(); //baca per baris
                }
                hasil = sb.toString().trim();
                /*char[] buffer = new char[len];
                r.read(buffer);
                hasil  =  new String(buffer);*/

                JSONObject jsonObj = new JSONObject(hasil);
                String kind = jsonObj.getString("kind");
                JSONObject jo = jsonObj.getJSONObject("volumeInfo");
                JSONArray ja = jo.getJSONArray("industryIdentifiers");

                //loop jsonarray
                for (int i = 0; i < ja.length(); i++) {
                    /*JSONObject jo2 = ja.getJSONObject(i);
                    Log.v("yw", "jo2:" + jo2.getString("type"));*/
                    String judul = jo.getString("title");
                    String authors = jo.getString("authors");
                    String desc = jo.getString("description");
                    Log.v("yw", "Judul: " + judul + " | Authors: " + authors + " | Description: " + desc);
                    hasil = "Judul: " + judul + "\nAuthors: " + authors + "\nDescription: " + desc;
                }
            } catch (JSONException e) {
                    e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return hasil;
        }


        protected void onPostExecute(String result) {
            tvHasil.setText(result);
        }
    }

    public void onClick(View v) {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new AmbilData().execute("https://www.googleapis.com/books/v1/volumes/yZ1APgAACAAJ/"); //url jadi parameter
        } else {
            // tampilkan error
            Toast t = Toast.makeText( getApplicationContext(), "Tidak ada koneksi!",Toast.LENGTH_LONG);
            t.show();
        }
    }
}
