package com.example.myshh.getweatherfromapi;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class GetJSON extends AsyncTask<Void, Void, Void> {

    Activity context;

    public GetJSON(Activity context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        TextView textViewTemp = context.findViewById(R.id.textViewTemperature);
        TextView textViewPressure = context.findViewById(R.id.textViewPressure);
        TextView textViewHumidity = context.findViewById(R.id.textViewHumidity);

        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=Poznan,pl&APPID=4871bda0d9f2f723cb0da219ef9f1a28");
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            StringBuilder result = new StringBuilder();

            while((line = bufferedReader.readLine()) != null){
                result.append(line).append("\n");
            }

            JSONObject object = new JSONObject(result.toString());
            textViewTemp.setText(object.getJSONObject("main").getString("temp") + "\n");
            textViewPressure.setText(object.getJSONObject("main").getString("pressure") + "\n");
            System.out.println(object.getJSONObject("main").getString("pressure"));
            textViewHumidity.setText(object.getJSONObject("main").getString("humidity") + "\n");
            System.out.println(object.getJSONObject("main").getString("humidity"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        context = null;
    }
}
