package com.ectolus.SnagMgmtSystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.app.ProgressDialog;
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
 * Created by user on 02-04-2018.
 */

public class SnagAsyncHttpPost extends AsyncTask<String, Void, JSONObject>{

    public ProcessFinishInterface delegate = null;

    public SnagAsyncHttpPost(ProcessFinishInterface delegate){
        this.delegate = delegate;
    }


//    public SnagAsyncHttpPost(View.OnClickListener activity) {
//        progressDialog = new ProgressDialog((Context) activity);
//    }

    public SnagAsyncHttpPost(View.OnClickListener onClickListener) {

    }


    @Override
    protected void onPreExecute() {
//        progressDialog.setMessage("Saving...");
//        progressDialog.show();
    }


    @Override
    protected JSONObject doInBackground(String... params) {
        HttpClient client = new DefaultHttpClient();

        HttpPost post = new HttpPost(params[0]);

        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();

        Log.d("TAG", params[0] + "-" + params[1] + "-" + params[2] + "-" + params[3] + "-" + params[4] + "-" + params[5] + "-" + params[6] + "-" + params[7]);
        nameValuePair.add(new BasicNameValuePair("machineID", params[1]));
        nameValuePair.add(new BasicNameValuePair("category", params[2]));
        nameValuePair.add(new BasicNameValuePair("subCategory", params[3]));
        nameValuePair.add(new BasicNameValuePair("partName", params[4]));
        nameValuePair.add(new BasicNameValuePair("description", params[5]));
        nameValuePair.add(new BasicNameValuePair("inspector1UserName", params[6]));
        nameValuePair.add(new BasicNameValuePair("currentStatusOfSnag", params[7]));
        Log.d("description", params[5]);
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
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
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
//        if(progressDialog.isShowing()) {
//            progressDialog.dismiss();
        }
    }
}
