package com.example.myshh.getweatherfromapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetJSON extends AsyncTask<Void, Integer, Void> {

    private WeakReference<Activity> context;
    private String cityName;
    private JSONObject object;

    private WeakReference<TextView> textViewTemp;
    private WeakReference<TextView> textViewPressure;
    private WeakReference<TextView> textViewHumidity;
    private WeakReference<TextView> textViewCityName;
    private WeakReference<TextView> textViewWind;
    private WeakReference<TextView> textViewSky;
    private WeakReference<ImageView> imageViewSky;

    private Bitmap bitmap;

    GetJSON(Activity context, String name) {
        this.context = new WeakReference<>(context);
        this.cityName = name;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        HttpURLConnection connection;
        BufferedReader bufferedReader;
        textViewTemp = new WeakReference<>(context.get().findViewById(R.id.textViewTemperature));
        //textViewTemp = context.get().findViewById(R.id.textViewTemperature);
        textViewPressure = new WeakReference<>(context.get().findViewById(R.id.textViewPressure));
        textViewHumidity = new WeakReference<>(context.get().findViewById(R.id.textViewHumidity));
        textViewCityName = new WeakReference<>(context.get().findViewById(R.id.textViewCityName));
        textViewWind = new WeakReference<>(context.get().findViewById(R.id.textViewWind));
        textViewSky = new WeakReference<>(context.get().findViewById(R.id.textViewSky));
        imageViewSky = new WeakReference<>(context.get().findViewById(R.id.imageViewSky));

        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&APPID=4871bda0d9f2f723cb0da219ef9f1a28");
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            //Success
            if(connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                StringBuilder result = new StringBuilder();

                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line).append("\n");
                }

                object = new JSONObject(result.toString());
                publishProgress(200);

            } else if(connection.getResponseCode() == 404){
                publishProgress(404);
            } else {
                publishProgress(connection.getResponseCode());
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        //Error dialog
        if(values[0] == 404) {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(context.get(), android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(context.get());
            }
            builder.setTitle("Error").setMessage("City not found").show();
            Toast.makeText(context.get(), "City not found", Toast.LENGTH_LONG).show();
        }

        else if (values[0] == 200) {
            //Update textViews
            try {
                //Convert from K to C
                double temperature = Math.round((Double.parseDouble(object.getJSONObject("main").getString("temp")) - 273.15)*100.0)/100.0;
                String windHeading = "null";
                if (object.getJSONObject("wind").has("deg")) {
                    windHeading = windHeadingToString(Double.parseDouble(object.getJSONObject("wind").getString("deg")));
                }
                textViewTemp.get().setText(String.format("%s °C", Double.toString(temperature)));
                textViewPressure.get().setText(String.format("%s hPa", object.getJSONObject("main").getString("pressure")));
                textViewHumidity.get().setText(String.format("%s%%", object.getJSONObject("main").getString("humidity")));
                textViewCityName.get().setText(String.format("%s, %s", object.getString("name"), object.getJSONObject("sys").getString("country")));
                textViewWind.get().setText(String.format("%s km/h, %s", object.getJSONObject("wind").getString("speed"), windHeading));
                textViewSky.get().setText(object.getJSONArray("weather").getJSONObject(0).getString("description").toUpperCase());
                new Thread(()->{
                    try {
                        bitmap = BitmapFactory.decodeStream((InputStream) new URL("http://openweathermap.org/img/w/" +
                                object.getJSONArray("weather").getJSONObject(0).getString("icon") + ".png").getContent());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    context.get().runOnUiThread(() -> imageViewSky.get().setImageBitmap(bitmap));
                }).start();
                System.out.println(object.getJSONObject("main").getString("humidity"));
                System.out.println(object.getJSONObject("main").getString("pressure"));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context.get(), "Error", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context.get(), Integer.toString(values[0]), Toast.LENGTH_LONG).show();
        }
    }

    private String windHeadingToString(double x)
    {
        String directions[] = {"N", "NE", "E", "SE", "S", "SW", "W", "NW", "N"};
        return directions[ (int)Math.round((  (x % 360) / 45)) ];
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        context = null;
    }
}
