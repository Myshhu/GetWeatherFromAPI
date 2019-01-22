package com.example.myshh.getweatherfromapi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new GetJSON(this).execute();
    }

    public void btnClick(View v){
        new GetJSON(this).execute();
    }
}
