package com.ectolus.SnagMgmtSystem;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements ProcessFinishInterface{
    private static final String TAG = "LoginActivity";

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;

    @BindView(R.id.input_username)
    EditText _UsernameText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;

    ProgressDialog progressDialog;

    private static String url = "http://e197c729.ngrok.io/usermobile/authenticateMobileUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                    Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

                // MY_PERMISSIONS_REQUEST_CAMERA is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void login() {
        Log.d(TAG, "LoginActivity");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();


        String username = _UsernameText.getText().toString();
        String password = _passwordText.getText().toString();


        new LoginAsyncHttpPost(this).execute(new String[]{url, username, password});
//        new android.os.Handler().postDelayed(
//                new Runnable() {
//                    public void run() {
//                        // On complete call either onLoginSuccess or onLoginFailed
//                        onLoginSuccess();
//                        // onLoginFailed();
//                        progressDialog.dismiss();
//                    }
//                }, 3000);
    }


    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

//    public void onLoginSuccess() {
//        _loginButton.setEnabled(true);
//        finish();
//    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "LoginActivity failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _UsernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty() || username.length() < 6) {
            _UsernameText.setError("enter a valid username");
            valid = false;
        } else {
            _UsernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6) {
            _passwordText.setError("enter 6 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    @Override
    public void processFinish(JSONObject output) throws JSONException {
        String status = getStatus(output);
        if (status != null && status.equals("success")) {
            setUserPrefrences(output);
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            progressDialog.dismiss();

                        }
                    }, 3000);
            Intent i = new Intent(getApplication(), ListViewActivity.class);
            startActivity(i);
        } else if (status != null && status.equals("noUserFound")) {
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
            alertDialog.setMessage("Wrong Username or Password");

            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    Toast.makeText(getApplicationContext(), "Enter Corrent username and password", Toast.LENGTH_SHORT).show();
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


    public boolean setUserPrefrences(JSONObject usr) throws JSONException {
        String firstName = null;
        String lastName = null;
        String userName = null;
        String userType = null;
        String id = null;
        JSONArray nameArray = usr.names();
        JSONArray valArray = usr.toJSONArray(nameArray);
        for (int i = 0; i < valArray.length(); i++) {
            if (nameArray.getString(i).equals("user")) {
                JSONObject json2 = new JSONObject(valArray.getString(i));
                JSONArray nameArray2 = json2.names();
                JSONArray valArray2 = json2.toJSONArray(nameArray2);
                for (int j = 0; j < valArray2.length(); j++) {
                    if (nameArray2.getString(j).equals("firstName")) {
                        firstName = valArray2.getString(j);
                    }
                    if (nameArray2.getString(j).equals("lastName")) {
                        lastName = valArray2.getString(j);
                    }
                    if (nameArray2.getString(j).equals("userName")) {
                        userName = valArray2.getString(j);
                    }
                    if (nameArray2.getString(j).equals("userType")) {
                        userType = valArray2.getString(j);
                    }
                    if (nameArray2.getString(j).equals("_id")) {
                        id = valArray2.getString(j);
                    }
                    Log.i("TAG", "" + j + " = " + nameArray2.getString(j) + " = " + valArray2.getString(j) + ";");
                }

                Log.d("TAG firstname ", firstName);
                Log.d("TAG lastname ", lastName);
                Log.d("TAG username", userName);
                Log.d("TAG usertype", userType);
                Log.d("TAG id", id);
            }
            SharedPreferences users;
            users = getSharedPreferences("USER_PREFERENCES", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = users.edit();
            editor.putString("id", id);
            editor.putString("firstName", firstName);
            editor.putString("lastName", lastName);
            editor.putString("userName", userName);
            editor.putString("userType", userType);
            editor.commit();

            String username1 = users.getString("userName", null);
            Log.d("check", String.valueOf(username1));

        }
        if (firstName != null && lastName != null && userName != null && userType != null && id != null) {
            return true;
        } else {
            return false;
        }
    }
}