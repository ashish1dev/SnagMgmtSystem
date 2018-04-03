package com.ectolus.SnagMgmtSystem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddSnagActivity extends AppCompatActivity implements  ProcessFinishInterface{
    private static final String TAG = "AddSnagActivity";

    private static String url = "http://89dfe9f9.ngrok.io/snag/add";

    Button btnSubmit;
    TextInputLayout machineID_layout, description_layout;
    Spinner categorySpinner, subcategorySpinner, partnameSpinner;
    EditText machineID_ET,descriptionET;
    ProgressDialog progressDialog;
    String currentStatusOfSnag, inspector1UserName;
    private Context thisActivityContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_snag);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String machine_id = getIntent().getStringExtra("machineID");
        Log.d("machineID in snag", machine_id);
        machineID_ET = (EditText) findViewById(R.id.machineID);
        machineID_ET.setText(machine_id);
        machineID_layout = (TextInputLayout) findViewById(R.id.machineID_layout);
        machineID_layout.setEnabled(false);
        description_layout = (TextInputLayout) findViewById(R.id.description_layout);
        description_layout.setCounterMaxLength(120);
        description_layout.setCounterEnabled(true);
        descriptionET = (EditText) findViewById(R.id.description);
        categorySpinner = (Spinner) findViewById(R.id.category);
        subcategorySpinner = (Spinner) findViewById(R.id.subCategory);
        partnameSpinner = (Spinner) findViewById(R.id.partname);
        SharedPreferences mPrefs = getSharedPreferences("USER_PREFERENCES", Context.MODE_PRIVATE);
        inspector1UserName = mPrefs.getString("userName", null);
        currentStatusOfSnag = "REPORTED";
        btnSubmit = (Button) findViewById(R.id.btn_submit);

        thisActivityContext = getApplicationContext();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               submitForm();
            }
        });
    }

    public void submitForm(){
        new SnagAsyncHttpPost(this).execute(new String[]{url, machineID_ET.getText().toString(), categorySpinner.getSelectedItem().toString(), subcategorySpinner.getSelectedItem().toString(), partnameSpinner.getSelectedItem().toString(), descriptionET.getText().toString(), inspector1UserName, currentStatusOfSnag});
        progressDialog = new ProgressDialog(AddSnagActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Saving...");
        progressDialog.show();
    }

    @Override
    public void processFinish(JSONObject output) throws JSONException {
        Log.d("output ProcessFinish = ", output.toString());
        String status = getStatus(output);
        String snagID = getSnagID(output);
        if (status != null && status.equals("success")) {
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            progressDialog.dismiss();

                        }
                    }, 3000);
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            // Setting Dialog Title
            alertDialog.setTitle("Alert Dialog");

            // Setting Dialog Message
            alertDialog.setMessage("Snag added. SnagID is " + snagID);

            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    Intent intent = new Intent(getApplication(), ListViewActivity.class);
                    startActivity(intent);
                }
            });

            alertDialog.show();
        } else if (status != null && status.equals("failed")) {
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                        }
                    }, 1000);
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            // Setting Dialog Title
            alertDialog.setTitle("Alert Dialog");

            // Setting Dialog Message
            alertDialog.setMessage("Saving Failed...");

            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                }
            });

            alertDialog.show();
//        }
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

    public String getSnagID(JSONObject st) throws JSONException {
        String snagID = null;
        JSONArray nameArray = st.names();
        JSONArray valArray = st.toJSONArray(nameArray);
        for (int i = 0; i < valArray.length(); i++) {
            if (nameArray.getString(i).equals("snagID")) {
                snagID = valArray.getString(i);
                Log.d("TAG snagID ", snagID);
            }
        }
        return snagID;
    }
}
