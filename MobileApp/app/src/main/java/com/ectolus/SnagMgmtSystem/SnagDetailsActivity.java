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
    Button accept;

    private static String url = "http://e197c729.ngrok.io/snag/updateCurrentStatus";
    String snag_id = null;
    String machine_id = null;
    String Description = null;
    String Category = null;
    String SubCategory = null;
    String PartName = null;


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

        accept = (Button) findViewById(R.id.btn_accept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start the qr scan activity
                AcceptForm();
            }
        });
    }

    public void AcceptForm() {
        SharedPreferences mPrefs = getSharedPreferences("USER_PREFERENCES", Context.MODE_PRIVATE);
        String functionalOperator = mPrefs.getString("userName", null);
        String snagType = "RESOLVED";
        new ChangeStatusAsyncTask(this).execute(new String[]{url, snag_id, functionalOperator, snagType});
    }

    @Override
    public void processFinish(JSONObject output) throws JSONException {
        Log.d("output ProcessFinish = ", output.toString());
        String status = getStatus(output);
        if (status != null && status.equals("success")) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            // Setting Dialog Title
            alertDialog.setTitle("Status");

            // Setting Dialog Message
            alertDialog.setMessage("Snag Accepted");

            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    Intent intent = new Intent(getApplication(), ListViewActivity.class);
                    startActivity(intent);
                }
            });
            alertDialog.show();

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
}
