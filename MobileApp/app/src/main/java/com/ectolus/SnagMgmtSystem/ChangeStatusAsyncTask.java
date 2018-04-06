package com.ectolus.SnagMgmtSystem;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by user on 04-04-2018.
 */

public class ChangeStatusAsyncTask  extends AsyncTask<String, Void, JSONObject> {
    public ProcessFinishInterface delegate = null;

    public ChangeStatusAsyncTask(ProcessFinishInterface delegate){
        this.delegate = delegate;
    }


    @Override
    protected void onPreExecute() {
    }


    @Override
    protected JSONObject doInBackground(String... params) {
        HttpClient client = new DefaultHttpClient();

        HttpPost post = new HttpPost(params[0]);// in

        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();

        Log.i("TAG in currentstatus", params[0] + "-" + params[1] + "-" + params[2] + "-" + params[3]);
        nameValuePair.add(new BasicNameValuePair("snagID", params[1]));
        nameValuePair.add(new BasicNameValuePair("userName", params[2]));
        nameValuePair.add(new BasicNameValuePair("userType", params[3]));
        nameValuePair.add(new BasicNameValuePair("currentStatusOfSnag", params[4]));
        JSONObject json = null;
        try {
            post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
            HttpResponse response = client.execute(post);
            String result = requestJSON(response);
            Log.d("requestJSON", result);
            json = new JSONObject(result);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
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

    protected void onPostExecute(JSONObject result) {
        try {
            Log.d("onPostExecute", result.toString());
            delegate.processFinish(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

