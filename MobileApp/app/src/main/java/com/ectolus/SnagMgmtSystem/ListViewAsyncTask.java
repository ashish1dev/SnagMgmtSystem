package com.ectolus.SnagMgmtSystem;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.ectolus.SnagMgmtSystem.SnagAsyncHttpPost.requestJSON;

/**
 * Created by user on 02-04-2018.
 */

public class ListViewAsyncTask extends AsyncTask<String, Void, JSONObject>{

    public ProcessFinishInterface delegate = null;

    public ListViewAsyncTask(ProcessFinishInterface delegate){
        this.delegate = delegate;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        HttpClient client = new DefaultHttpClient();

        HttpPost post = new HttpPost(params[0]);

        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();

        nameValuePair.add(new BasicNameValuePair("currentStatusOfSnag", params[1]));
        Log.d("params snag = ", params[1]);
        JSONObject json = null;
        String description;
        try {
            post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
            HttpResponse response = client.execute(post);
            String result = requestJSON(response);
            Log.d("result in listview", result);
            json = new JSONObject(result);
            Log.d("json = ",json.toString());
//            JSONArray nameArray = json.names();
//            JSONArray valArray = json.toJSONArray(nameArray);
//            for (int i = 0; i < valArray.length(); i++) {
//                Log.d("TAG", "" + i + " = " + nameArray.getString(i) + " = " + valArray.getString(i) + ";");
//                if (nameArray.getString(i).equals("snag")) {
//                    JSONObject json2 = new JSONObject(valArray.getString(i));
//                    JSONArray nameArray2 = json2.names();
//                    JSONArray valArray2 = json2.toJSONArray(nameArray2);
//
//                    for (int j = 0; j < valArray2.length(); j++) {
//                        if (nameArray2.getString(j).equals("description")) {
//                            description = valArray2.getString(j);
//                        }
//                        Log.i("snags from node", "" + j + " = " + nameArray2.getString(j) + " = " + valArray2.getString(j) + ";");
//                    }
//                }
//
//            }
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

    @Override
    protected void onPostExecute(JSONObject result) {
        try {
            delegate.processFinish(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
