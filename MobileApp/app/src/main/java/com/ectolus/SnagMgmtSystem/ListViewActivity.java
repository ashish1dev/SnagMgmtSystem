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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListViewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ProcessFinishInterface {

    private static String url = "http://89dfe9f9.ngrok.io/snag/getSnagsBySnagType";
    ProgressDialog progressDialog;
    private ListView lv;
    ArrayList<HashMap<String, String>> snagList;
    String snagType= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences mPrefs = getSharedPreferences("USER_PREFERENCES", Context.MODE_PRIVATE);
        String userType = mPrefs.getString("userType", null);
        String userName = mPrefs.getString("userName", null);

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


        if(userType.equals("function operator")){
            snagType= "REPORTED";
            new ListViewAsyncTask(this).execute(new String[]{url,snagType});
        }

        if(userType.equals("inspector 2")){
            snagType= "RESOLVED";
            new ListViewAsyncTask(this).execute(new String[]{url,snagType});
        }

        if(userType.equals("inspector 3")){
            snagType= "REVIEWED";
            new ListViewAsyncTask(this).execute(new String[]{url,snagType});
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //progress dialog will start here
        if(!userType.equals("inspector 1")) {
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
        getMenuInflater().inflate(R.menu.main2, menu);
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
                category = sn.getString("category");
                subCategory = sn.getString("subCategory");
                partName = sn.getString("partName");
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
                        Intent intent = new Intent(ListViewActivity.this, SnagDetails.class);
                        JSONArray valArray = snagJSON.toJSONArray(nameArray);
                        Log.d("ValArray = ",valArray.toString());
                        for (int i = 0; i < valArray.length(); i++) {
                            if (nameArray.getString(i).equals("snagID")) {
                                String snagID = valArray.getString(i);
                                Log.d("TAG status ", snagID);
                                intent.putExtra("snagID", snagID);
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

