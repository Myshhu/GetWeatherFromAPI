package com.example.myshh.getweatherfromapi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText etCityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCityName = findViewById(R.id.etCityName);

        new GetJSON(this, etCityName.getText().toString()).execute();
    }

    public void btnClick(View v){
        new GetJSON(this, etCityName.getText().toString()).execute();
    }
}
