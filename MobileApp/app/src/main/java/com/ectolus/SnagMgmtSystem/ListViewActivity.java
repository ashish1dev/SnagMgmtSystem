package com.ectolus.SnagMgmtSystem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListViewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ProcessFinishInterface {

    private static String url = "http://ae55f07b.ngrok.io/snag/getSnagsBySnagType";
    ProgressDialog progressDialog;
    private ListView lv;
    ArrayList<HashMap<String, String>> snagList;
    String snagType= null;
    TextView nav_fullName, nav_userType;
    String userType, firstName, lastName, fullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences mPrefs = getSharedPreferences("USER_PREFERENCES", Context.MODE_PRIVATE);
        userType = mPrefs.getString("userType", null);
        firstName = mPrefs.getString("firstName", null);
        lastName = mPrefs.getString("lastName", null);
        fullName = firstName + " " + " " + lastName;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Intent intent = new Intent(getApplication(), ScanBtnActivity.class);
                startActivity(intent);
            }
        });

        snagList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);

        if(userType.equals("FUNCTIONAL_OPERATOR")){
            snagType= "REPORTED";
            new ListViewAsyncTask(this).execute(new String[]{url,snagType});
        }

        if(userType.equals("INSPECTOR2")){
            snagType= "RESOLVED";
            new ListViewAsyncTask(this).execute(new String[]{url,snagType});
        }

        if(userType.equals("INSPECTOR3")){
            snagType= "REVIEWED";
            new ListViewAsyncTask(this).execute(new String[]{url,snagType});
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        nav_fullName = (TextView) headerView.findViewById(R.id.textView_fullName);
        nav_fullName.setText(fullName);
        nav_userType = (TextView) headerView.findViewById(R.id.textView_userType);
        nav_userType.setText(userType);
        navigationView.setNavigationItemSelectedListener(this);

        //progress dialog will start here
        if(!userType.equals("INSPECTOR1")) {
            progressDialog = new ProgressDialog(ListViewActivity.this, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.listview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void processFinish(JSONObject output) throws JSONException {
        String snagID = null, machineID = null, description = null, category = null, subCategory = null, partName = null;
        final HashMap<Integer, JSONObject> snagHM = new HashMap<Integer, JSONObject>();
        String status = getStatus(output);
        if (status != null && status.equals("success")) {
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            progressDialog.dismiss();

                        }
                    }, 50);
            JSONArray snags = output.getJSONArray("snag");

            for (int i = 0; i < snags.length(); i++) {
                JSONObject sn = snags.getJSONObject(i);
                snagID = sn.getString("snagID");
                description = sn.getString("description");
                machineID = sn.getString("machineID");
                category = sn.getString("categoryID");
                subCategory = sn.getString("subCategoryID");
                partName = sn.getString("partNameID");
                Log.d("sn from listview = ",sn.toString());
                snagHM.put(i, sn);


                HashMap dataHM= new HashMap<String, String>();
                dataHM.put("snagID",snagID);
                dataHM.put("description",description);
                snagList.add(dataHM);
            }


            ListAdapter adapter = new SimpleAdapter(
                    ListViewActivity.this, snagList,
                    R.layout.list_snag_items, new String[]{"snagID", "description"},
                    new int[]{R.id.snagID, R.id.description});

            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Log.d("position in adapter = ", position+"");
                    Log.d("item json = ", snagHM.get(position).toString());
                    JSONObject snagJSON = snagHM.get(position);
                    JSONArray nameArray = snagJSON.names();
                    try {
                        Intent intent = new Intent(ListViewActivity.this, SnagDetailsActivity.class);
                        JSONArray valArray = snagJSON.toJSONArray(nameArray);
                        Log.d("ValArray = ",valArray.toString());
                        for (int i = 0; i < valArray.length(); i++) {
                            if (nameArray.getString(i).equals("snagID")) {
                                String snagID = valArray.getString(i);
                                Log.d("TAG snagID = ", snagID);
                                intent.putExtra("snagID", snagID);
                            }
                            if (nameArray.getString(i).equals("machineID")) {
                                String machineID = valArray.getString(i);
                                Log.d("TAG machineID = ", machineID);
                                intent.putExtra("machineID", machineID);
                            }
                            if (nameArray.getString(i).equals("description")) {
                                String description = valArray.getString(i);
                                Log.d("TAG description = ", description);
                                intent.putExtra("description", description);
                            }
                            if (nameArray.getString(i).equals("categoryID")) {
                                String category = valArray.getString(i);
                                Log.d("TAG category = ", category);
                                intent.putExtra("category", category);
                            }
                            if (nameArray.getString(i).equals("subCategoryID")) {
                                String subCategory = valArray.getString(i);
                                Log.d("TAG subCategory = ", subCategory);
                                intent.putExtra("subCategory", subCategory);
                            }
                            if (nameArray.getString(i).equals("partNameID")) {
                                String partName = valArray.getString(i);
                                Log.d("TAG partName = ", partName);
                                intent.putExtra("partName", partName);
                            }

                        }
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
        else if (status != null && status.equals("snagsNotFound")) {
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

