package com.codeliner.myapplicationtest0;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherWidget extends AppWidgetProvider {
    private static final String ACTION_UPDATE = "com.example.yourapp.ACTION_UPDATE";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private static final String API_KEY = "1e7c2a80ffce996efc83dd30e83c70c0";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_UPDATE.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, WeatherWidget.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Инициализация Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherService weatherService = retrofit.create(WeatherService.class);

        // Запрос погоды по городу (например, "Moscow")
        new FetchWeatherTask(context, appWidgetManager, appWidgetId, weatherService).execute("Kazan");
    }

    private static class FetchWeatherTask extends AsyncTask<String, Void, WeatherResponse> {
        private Context context;
        private AppWidgetManager appWidgetManager;
        private int appWidgetId;
        private WeatherService weatherService;

        FetchWeatherTask(Context context, AppWidgetManager appWidgetManager, int appWidgetId, WeatherService weatherService) {
            this.context = context;
            this.appWidgetManager = appWidgetManager;
            this.appWidgetId = appWidgetId;
            this.weatherService = weatherService;
        }

        @Override
        protected WeatherResponse doInBackground(String... params) {
            String city = params[0];
            Call<WeatherResponse> call = weatherService.getWeatherByCity(city, API_KEY, "metric");
            try {
                return call.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(WeatherResponse weatherResponse) {
            if (weatherResponse != null) {
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);

                // Обновление виджета
                views.setTextViewText(R.id.widget_city, "Kazan");
                views.setTextViewText(R.id.widget_temperature, String.format("%s°C", weatherResponse.main.temp));
                views.setTextViewText(R.id.widget_conditions, weatherResponse.weather[0].description);

                String iconUrl = "https://openweathermap.org/img/w/" + weatherResponse.weather[0].icon + ".png";
                Picasso.get().load(iconUrl).into(views, R.id.widget_icon, new int[]{appWidgetId});

                appWidgetManager.updateAppWidget(appWidgetId, views);
            } else {
                Toast.makeText(context, "Failed to load weather data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
