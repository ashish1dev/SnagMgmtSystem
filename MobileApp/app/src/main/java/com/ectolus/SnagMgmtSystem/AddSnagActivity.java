package com.ectolus.SnagMgmtSystem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.RequestQueue;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class AddSnagActivity extends AppCompatActivity implements  ProcessFinishInterface, SpinnerAsyncTask.AsyncResult {
    private static final String TAG = "AddSnagActivity";

    private static String url = SplashActivity.DOMAIN + "/snag/add";
    private static String url_Parts = SplashActivity.DOMAIN + "/snag/listAllCategorySubCategoryParts";

    Button btnSubmit;
    TextInputLayout machineID_layout, description_layout;
    Spinner categorySpinner, subcategorySpinner, partsSpinner;
    EditText machineID_ET, descriptionET;
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
        categorySpinner = (Spinner) findViewById(R.id.spinner_Category);
        subcategorySpinner = (Spinner) findViewById(R.id.spinner_subCategory);
        partsSpinner = (Spinner) findViewById(R.id.spinner_Parts);
        SharedPreferences mPrefs = getSharedPreferences("USER_PREFERENCES", Context.MODE_PRIVATE);
        inspector1UserName = mPrefs.getString("userName", null);
        currentStatusOfSnag = "REPORTED";

        new SpinnerAsyncTask(this).execute(new String[]{url_Parts});

        btnSubmit = (Button) findViewById(R.id.btn_submit);

        thisActivityContext = getApplicationContext();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
    }

    public void submitForm() {
        new SnagAsyncHttpPost(this).execute(new String[]{url, machineID_ET.getText().toString(), categorySpinner.getSelectedItem().toString(), subcategorySpinner.getSelectedItem().toString(), partsSpinner.getSelectedItem().toString(), descriptionET.getText().toString(), inspector1UserName, currentStatusOfSnag});
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

    @Override
    public void onProcessFinish(JSONObject output) throws JSONException {
        String[] machineCategoryArray = getMachineCategory(output);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, machineCategoryArray);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        String[] machineSubCategoryArray = getMachineSubCategroy(output);
        ArrayAdapter<String> subCategoryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, machineSubCategoryArray);
        subCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subcategorySpinner.setAdapter(subCategoryAdapter);

        String[] partNameArray = getPartName(output);
        ArrayAdapter<String> PartsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, partNameArray);
        PartsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        partsSpinner.setAdapter(PartsAdapter);
    }

    public String[] getPartName(JSONObject st) throws JSONException {
        String partName = null;
        String[] partNameArray = null;
        JSONArray nameArray = st.names();
        JSONArray valArray = st.toJSONArray(nameArray);
        for (int i = 0; i < valArray.length(); i++) {
            if (nameArray.getString(i).equals("result")) {
                JSONObject json2 = new JSONObject(valArray.getString(i));
                Log.d("show json2 = ", json2.toString());

                JSONArray nameArray2 = json2.names();
                JSONArray valArray2 = json2.toJSONArray(nameArray2);

                Log.d("valArray2", valArray2.toString());

                JSONArray partsArray = valArray2.getJSONArray(2);
                partNameArray = new String[partsArray.length()];
                for (int j = 0; j < partsArray.length(); ++j) {
                    JSONObject parts;
                    parts = partsArray.getJSONObject(j);
                    partName = parts.getString("partName");
                    Log.d("partName = ", partName);
                    partNameArray[j] = partName;
                    Log.d("partNameArray", "Parts " +partNameArray[j]);
                }

            }
        }
        return partNameArray;
    }

    public String[] getMachineCategory(JSONObject st) throws JSONException {
        String machineCategory = null;
        String[] machineCategoryArray = null;
        JSONArray nameArray = st.names();
        JSONArray valArray = st.toJSONArray(nameArray);
        for (int i = 0; i < valArray.length(); i++) {
            if (nameArray.getString(i).equals("result")) {
                JSONObject json2 = new JSONObject(valArray.getString(i));
                Log.d("show json2 = ", json2.toString());

                JSONArray nameArray2 = json2.names();
                JSONArray valArray2 = json2.toJSONArray(nameArray2);

                Log.d("valArray2", valArray2.toString());

                JSONArray categoryArray = valArray2.getJSONArray(0);
                machineCategoryArray = new String[categoryArray.length()];
                for (int j = 0; j < categoryArray.length(); ++j) {
                    JSONObject category;
                    category = categoryArray.getJSONObject(j);
                    machineCategory = category.getString("machineCategory");
                    Log.d("partName = ", machineCategory);
                    machineCategoryArray[j] = machineCategory;
                    Log.d("machineCategoryArray", "machineCategory " +machineCategoryArray[j]);
                }

            }
        }
        return machineCategoryArray;
    }

    public String[] getMachineSubCategroy(JSONObject st) throws JSONException {
        String machineSubCategory = null;
        String[] machineSubCategoryArray = null;
        JSONArray nameArray = st.names();
        JSONArray valArray = st.toJSONArray(nameArray);
        for (int i = 0; i < valArray.length(); i++) {
            if (nameArray.getString(i).equals("result")) {
                JSONObject json2 = new JSONObject(valArray.getString(i));
                Log.d("show json2 = ", json2.toString());

                JSONArray nameArray2 = json2.names();
                JSONArray valArray2 = json2.toJSONArray(nameArray2);

                Log.d("valArray2", valArray2.toString());

                JSONArray subCategoryArray = valArray2.getJSONArray(1);
                machineSubCategoryArray = new String[subCategoryArray.length()];
                for (int j = 0; j < subCategoryArray.length(); ++j) {
                    JSONObject subCategory;
                    subCategory = subCategoryArray.getJSONObject(j);
                    machineSubCategory = subCategory.getString("machineSubCategory");
                    Log.d("partName = ", machineSubCategory);
                    machineSubCategoryArray[j] = machineSubCategory;
                    Log.d("partNameArray", "Parts " +machineSubCategoryArray[j]);
                }

            }
        }
        return machineSubCategoryArray;
    }
}
