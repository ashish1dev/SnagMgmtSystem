package com.ectolus.SnagMgmtSystem;

/**
 * Created by user on 23-03-2018.
 */

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;



public class AsyncHttpPost extends AsyncTask<String, Void, JSONObject> {


    public interface AsyncResponse {
        void processFinish(JSONObject output) throws JSONException;
    }

    public AsyncResponse delegate = null;

    public AsyncHttpPost(AsyncResponse delegate){
        this.delegate = delegate;
    }


    @Override
    protected void onPreExecute() {}

    @Override
    protected JSONObject doInBackground(String... params) {

        HttpClient client = new DefaultHttpClient();

        HttpPost post = new HttpPost(params[0]);// in

        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        String status = null, firstname = null, lastname = null;

        Log.d("TAG", params[0] + "-" + params[1] + "-" + params[2]);
        nameValuePair.add(new BasicNameValuePair("username", params[1]));
        nameValuePair.add(new BasicNameValuePair("password", params[2]));
        JSONObject json = null;
        try {
            post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
            HttpResponse response = client.execute(post);
            String result = requestJSON(response);
            json = new JSONObject(result);

//            Log.d("json", String.valueOf(json));
//            JSONArray nameArray = json.names();
//            JSONArray valArray = json.toJSONArray(nameArray);
//            for (int i = 0; i < valArray.length(); i++) {
//                Log.i("TAG", "" + i + " = " + nameArray.getString(i) + " = " + valArray.getString(i) + ";");
//
//                if (nameArray.getString(i).equals("status")) {
//                    status = valArray.getString(i);
//                    Log.i("TAG status ", status);
//                }
//
//                if (isJSONValid(valArray.getString(i))) {
//                    JSONObject json2 = new JSONObject(valArray.getString(i));
//                    Log.d("json", String.valueOf(json2));
//                    JSONArray nameArray2 = json2.names();
//                    JSONArray valArray2 = json2.toJSONArray(nameArray2);
//                    for (int j = 0; j < valArray2.length(); j++) {
//                        if (nameArray2.getString(j).equals("firstname")) {
//                            firstname = valArray2.getString(j);
//                        }
//                        if (nameArray2.getString(j).equals("lastname")) {
//                            lastname = valArray2.getString(j);
//                        }
//                        Log.i("TAG", "" + j + " = " + nameArray2.getString(j) + " = " + valArray2.getString(j) + ";");
//                    }
//
//                    Log.i("TAG firstname ", firstname);
//                    Log.i("TAG lastname ", lastname);
//                }
//
//                return json;
//
//            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static String requestJSON(HttpResponse response){
        String result = "";
        try{
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null){
                str.append(line + "\n");
            }
            in.close();
            result = str.toString();
        }catch(Exception ex){
            result = "Error";
        }
        return result;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        try {
            delegate.processFinish(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}



