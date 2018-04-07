package com.ectolus.SnagMgmtSystem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SnagDetailsActivity extends AppCompatActivity implements ProcessFinishInterface {

    EditText snagID, machineID, description, category, subCategory, partName;
    Button btnAccept;

    private static String url = SplashActivity.DOMAIN + "/snag/updateCurrentStatus";
    String snag_id = null;
    String machine_id = null;
    String Description = null;
    String Category = null;
    String SubCategory = null;
    String PartName = null;
    String status, userName,userType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snag_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        snag_id = getIntent().getStringExtra("snagID");
        machine_id = getIntent().getStringExtra("machineID");
        Description = getIntent().getStringExtra("description");
        Category = getIntent().getStringExtra("category");
        SubCategory = getIntent().getStringExtra("subCategory");
        PartName = getIntent().getStringExtra("partName");

        snagID = (EditText) findViewById(R.id.edittext_SnagID);
        snagID.setText(snag_id);
        snagID.setEnabled(false);
        machineID = (EditText) findViewById(R.id.edittext_MachineID);
        machineID.setText(machine_id);
        machineID.setEnabled(false);
        description = (EditText) findViewById(R.id.edittext_Description);
        description.setText(Description);
        description.setEnabled(false);
        category = (EditText) findViewById(R.id.edittext_Category);
        category.setText(Category);
        category.setEnabled(false);
        subCategory = (EditText) findViewById(R.id.edittext_SubCategory);
        subCategory.setText(SubCategory);
        subCategory.setEnabled(false);
        partName = (EditText) findViewById(R.id.edittext_PartName);
        partName.setText(PartName);
        partName.setEnabled(false);

        SharedPreferences mPrefs = getSharedPreferences("USER_PREFERENCES", Context.MODE_PRIVATE);
        userName = mPrefs.getString("userName", null);
        userType = mPrefs.getString("userType", null);

        btnAccept = (Button) findViewById(R.id.btn_accept);
        if(userType.equals("FUNCTIONAL_OPERATOR")){
            btnAccept.setText("RESOLVED");
        }

        if(userType.equals("INSPECTOR2")){
            btnAccept.setText("REVIEWED");
        }

        if(userType.equals("INSPECTOR3")){
            btnAccept.setText("CLOSED");
        }

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start the qr scan activity
                AcceptForm();
            }
        });
    }

    public void AcceptForm() {

        if(userType.equals("FUNCTIONAL_OPERATOR")) {
            status = "RESOLVED";
            new ChangeStatusAsyncTask(this).execute(new String[]{url, snag_id, userName, userType, status});
        }

        if(userType.equals("INSPECTOR2")) {
            status = "REVIEWED";
            new ChangeStatusAsyncTask(this).execute(new String[]{url, snag_id, userName, userType, status});
        }

        if(userType.equals("INSPECTOR3")) {
            status = "CLOSED";
            new ChangeStatusAsyncTask(this).execute(new String[]{url, snag_id, userName, userType, status});
        }
    }

    @Override
    public void processFinish(JSONObject output) throws JSONException {
        Log.d("output ProcessFinish = ", output.toString());
        String status = getStatus(output);
        String fetchCurrentStatus = getCurrentStatus(output);
        if (status != null && status.equals("success")) {
            if(fetchCurrentStatus != null && fetchCurrentStatus.equals("RESOLVED")) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                // Setting Dialog Title
                alertDialog.setTitle("Status");

                // Setting Dialog Message
                alertDialog.setMessage("Snag RESOLVED");

                // Setting OK Button
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        Intent intent = new Intent(getApplication(), ListViewActivity.class);
                        startActivity(intent);
                    }
                });
                alertDialog.show();
            }

            else if(fetchCurrentStatus != null && fetchCurrentStatus.equals("REVIEWED")) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                // Setting Dialog Title
                alertDialog.setTitle("Status");

                // Setting Dialog Message
                alertDialog.setMessage("Snag REVIEWED");

                // Setting OK Button
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        Intent intent = new Intent(getApplication(), ListViewActivity.class);
                        startActivity(intent);
                    }
                });
                alertDialog.show();
            }

            else if(fetchCurrentStatus != null && fetchCurrentStatus.equals("CLOSED")) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                // Setting Dialog Title
                alertDialog.setTitle("Status");

                // Setting Dialog Message
                alertDialog.setMessage("Snag CLOSED");

                // Setting OK Button
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        Intent intent = new Intent(getApplication(), ListViewActivity.class);
                        startActivity(intent);
                    }
                });
                alertDialog.show();
            }

        } else if (status != null && status.equals("noSnagFound")) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            // Setting Dialog Title
            alertDialog.setTitle("Status");

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

    public String getCurrentStatus(JSONObject st) throws JSONException {
        Log.d("st in getCurrentStatus", st.toString());
        String fetchCurrentStatus = null;
        JSONArray nameArray = st.names();
        Log.d("nameArray in func", nameArray.toString());
        JSONArray valArray = st.toJSONArray(nameArray);
        Log.d("valArray in func", valArray.toString());
        for (int i = 0; i < valArray.length(); i++) {
            if (nameArray.getString(i).equals("fetchCurrentStatus")) {
                fetchCurrentStatus = valArray.getString(i);
                Log.d("TAG currentStatus ", fetchCurrentStatus);
            }
        }
        return fetchCurrentStatus;
    }
}
