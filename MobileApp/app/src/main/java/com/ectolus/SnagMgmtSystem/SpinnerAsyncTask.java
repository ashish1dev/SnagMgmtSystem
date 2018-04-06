package com.ectolus.SnagMgmtSystem;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by user on 06-04-2018.
 */

public class SpinnerAsyncTask extends AsyncTask<String, Void, JSONObject> {
    public interface AsyncResult {
        void onProcessFinish(JSONObject output) throws JSONException;
    }

    public AsyncResult delegate = null;

    public SpinnerAsyncTask(AsyncResult delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
    }

    String status, partName;

    @Override
    protected JSONObject doInBackground(String... params) {
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(params[0]);

        JSONObject json = null;

        try {
            HttpResponse response = client.execute(get);
            String result = requestJSON(response);
            Log.d("requestJSON", result);
            json = new JSONObject(result);
            Log.d("json in spinnerAsync", json.toString());
//            JSONArray nameArray = json.names();
//            JSONArray valArray = json.toJSONArray(nameArray);
//            for (int i = 0; i < valArray.length(); i++) {
//                Log.d("value of i ", i + "");
//                Log.d("nameArray", nameArray.toString());
//                Log.d("valArray", valArray.toString());
//                if (nameArray.getString(i).equals("status")) {
//                    status = valArray.getString(i);
//                    Log.d("TAG status ", status);
//                } else if (nameArray.getString(i).equals("result")) {
//                    JSONObject json2 = new JSONObject(valArray.getString(i));
//                    JSONArray nameArray2 = json2.names();
//                    JSONArray valArray2 = json2.toJSONArray(nameArray2);
//                    for (int j = 0; j < valArray2.length(); j++) {
//                        if (nameArray2.getString(j).equals("parts")) {
//                            JSONObject json3 = new JSONObject(valArray2.getString(j));
//                            JSONArray nameArray3 = json3.names();
//                            JSONArray valArray3 = json3.toJSONArray(nameArray3);
//                            for (int k = 0; k < valArray3.length(); k++) {
//                                if (nameArray3.getString(j).equals("partname")) {
//                                    partName = valArray.getString(k);
//                                    Log.d("TAG partName = ", partName);
//                                }
//                            }
//                        }
//
//                    }
//                }
//            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }


    public static String requestJSON(HttpResponse response) {
        String result = "";
        try {
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                str.append(line + "\n");
            }
            in.close();
            result = str.toString();
        } catch (Exception ex) {
            result = "Error";
        }

        return result;
    }

    protected void onPostExecute(JSONObject result) {
        Log.d("onPostExecute", result.toString());
        try {
            delegate.onProcessFinish(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
