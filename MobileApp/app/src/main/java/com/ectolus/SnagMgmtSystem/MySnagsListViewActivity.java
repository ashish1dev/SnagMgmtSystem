package com.ectolus.SnagMgmtSystem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MySnagsListViewActivity extends AppCompatActivity implements ProcessFinishInterface {
    String userType, userName;
    private static String url = SplashActivity.DOMAIN + "/snag/getMySnags";
    ProgressDialog progressDialog;
    private ListView lv;
    ArrayList<HashMap<String, String>> snagList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_snags_list_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lv = (ListView) findViewById(R.id.mySnagList);

        SharedPreferences mPrefs = getSharedPreferences("USER_PREFERENCES", Context.MODE_PRIVATE);
        userType = mPrefs.getString("userType", null);
        userName = mPrefs.getString("userName", null);

        new MySnagsAsyncHttpPost(this).execute(new String[]{url, userType, userName});

        progressDialog = new ProgressDialog(MySnagsListViewActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        snagList = new ArrayList<>();
    }

    @Override
    public void processFinish(JSONObject output) throws JSONException {
        Log.d("output in listview", output.toString());
        String snagID = null, description = null;
        String status = getStatus(output);
        if (status != null && status.equals("success")) {
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            progressDialog.dismiss();

                        }
                    }, 50);
            JSONArray snags = output.getJSONArray("snags");
            Log.d("snags in listview", snags.toString());

            for (int i = 0; i < snags.length(); i++) {
                JSONObject sn = snags.getJSONObject(i);
                snagID = sn.getString("snagID");
                description = sn.getString("description");
                Log.d("sn from listview = ", sn.toString());

                HashMap dataHM = new HashMap<String, String>();
                dataHM.put("snagID", snagID);
                dataHM.put("description", description);
                Log.d("dataHM in mySnags", dataHM.toString());
                snagList.add(dataHM);
            }


            ListAdapter adapter = new SimpleAdapter(
                    MySnagsListViewActivity.this, snagList,
                    R.layout.list_my_snags_items, new String[]{"snagID", "description"},
                    new int[]{R.id.snagID, R.id.description});

            lv.setAdapter(adapter);
        }
        else if (status != null && status.equals("noSnagsFound")) {
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                        }
                    }, 500);
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            // Setting Dialog Title
            alertDialog.setTitle("Response");

            // Setting Dialog Message
            alertDialog.setMessage("No Snag Found");

            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                }
            });

            alertDialog.show();
        }
    }

    public String getStatus(JSONObject st) throws JSONException {
        String status = null;
        JSONArray nameArray = st.names();
        JSONArray valArray = st.toJSONArray(nameArray);
        for (int i = 0; i < valArray.length(); i++) {
            if (nameArray.getString(i).equals("status")) {
                status = valArray.getString(i);
                Log.d("TAG status ", status);
            }
        }
        return status;
    }
}
