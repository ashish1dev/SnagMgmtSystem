package com.ectolus.SnagMgmtSystem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity implements AsyncHttpPost.AsyncResponse {
    private static final String TAG = "LoginActivity";

    @BindView(R.id.input_username)
    EditText _UsernameText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;

    ProgressDialog progressDialog;

    private static String url = "http://f162828c.ngrok.io/usermobile/authenticateMobileUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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


        new AsyncHttpPost(this).execute(new String[]{url, username, password});
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

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
//        finish();
    }

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
        String firstname = null;
        String lastname = null;
        String username = null;
        String usertype = null;
        String id = null;
        JSONArray nameArray = usr.names();
        JSONArray valArray = usr.toJSONArray(nameArray);
        for (int i = 0; i < valArray.length(); i++) {
            if (nameArray.getString(i).equals("user")) {
                JSONObject json2 = new JSONObject(valArray.getString(i));
                JSONArray nameArray2 = json2.names();
                JSONArray valArray2 = json2.toJSONArray(nameArray2);
                for (int j = 0; j < valArray2.length(); j++) {
                    if (nameArray2.getString(j).equals("firstname")) {
                        firstname = valArray2.getString(j);
                    }
                    if (nameArray2.getString(j).equals("lastname")) {
                        lastname = valArray2.getString(j);
                    }
                    if (nameArray2.getString(j).equals("username")) {
                        username = valArray2.getString(j);
                    }
                    if (nameArray2.getString(j).equals("usertype")) {
                        usertype = valArray2.getString(j);
                    }
                    if (nameArray2.getString(j).equals("_id")) {
                        id = valArray2.getString(j);
                    }
                    Log.i("TAG", "" + j + " = " + nameArray2.getString(j) + " = " + valArray2.getString(j) + ";");
                }

                Log.d("TAG firstname ", firstname);
                Log.d("TAG lastname ", lastname);
                Log.d("TAG username", username);
                Log.d("TAG usertype", usertype);
                Log.d("TAG id", id);
            }
            SharedPreferences users;
            users = getSharedPreferences("USER_PREFERENCES", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = users.edit();
            editor.putString("id", id);
            editor.putString("firstname", firstname);
            editor.putString("lastname", lastname);
            editor.putString("username", username);
            editor.putString("usertype", usertype);
            editor.commit();

            String username1 = users.getString("username", null);
            Log.d("check", String.valueOf(username1));

        }
        if (firstname != null && lastname != null && username != null && usertype != null && id != null) {
            return true;
        } else {
            return false;
        }
    }
}