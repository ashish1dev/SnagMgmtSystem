package com.ectolus.SnagMgmtSystem;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddSnagActivity extends AppCompatActivity {

    Button btn;
    TextInputLayout machineID_layout, description_layout;
    Spinner category, subCategory, partname;
    EditText machineID,description;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_snag);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String machine_id = getIntent().getStringExtra("machineID");

        machineID = (EditText) findViewById(R.id.machineID);
        machineID.setText(machine_id);
        machineID_layout = (TextInputLayout) findViewById(R.id.machineID_layout);
        machineID_layout.setEnabled(false);
        description_layout = (TextInputLayout) findViewById(R.id.description_layout);
        description_layout.setCounterMaxLength(120);
        description_layout.setCounterEnabled(true);
    }

}
