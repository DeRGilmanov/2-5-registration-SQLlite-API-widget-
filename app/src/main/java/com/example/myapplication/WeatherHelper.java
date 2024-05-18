package com.example.myapplication;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WeatherHelper {
    // Ключ для доступа к API OpenWeatherMap
    private final String key = "3b7d395eb97dc26959e2f95f1f84b3ee";
    // Интерфейс для передачи загруженных данных о погоде
    OnDownloadedWeather onDownloadedWeather;

    private Context context; // Контекст приложения

    public WeatherHelper(Context context) {
        // Установка контекста
        this.context = context;
    }

    public void setOnDownloadedWeather(OnDownloadedWeather onDownloadedWeather){
        // Установка обработчика для передачи загруженных данных
        this.onDownloadedWeather = onDownloadedWeather;
    }

    public void getWeatherByGPS() {
        Log.d("weather_helper", "Вызван getWeatherByGPS"); // Вывод сообщения в лог о вызове метода

        // Получение менеджера местоположения
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // Создание слушателя для получения обновлений местоположения
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Получение широты и долготы
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Log.d("weather_helper", "latitude: " + latitude); // Вывод широты в лог
                Log.d("weather_helper", "longitude: " + longitude); // Вывод долготы в лог
                // Получение погоды по широте и долготе
                getWeather(latitude, longitude);
                // Снятие прослушивателя, так как больше не нужно отслеживать местоположение
                locationManager.removeUpdates(this);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };

        try {
            // Запрос обновлений местоположения через GPS
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch(SecurityException e) {
            e.printStackTrace(); // Вывод сообщения об ошибке в лог
        }
    }


    public void getWeather(double latitudem, double longitude){
        // Формирование URL для получения погоды по координатам
        String url = "https://api.openweathermap.org/data/2.5/weather?lat="+ latitudem+"&lon="+longitude+"&appid=" + key + "&units=metric&lang=ru";
        // Загрузка погоды по URL
        new DownloadWeatherTask().execute(url);
    }
    public void getWeather(String city){
        // Формирование URL для получения погоды по названию города
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric&lang=ru";
        // Загрузка погоды по URL
        new DownloadWeatherTask().execute(url);
    }

    private class DownloadWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            String surl = params[0]; // Получение URL из параметров
            URL url; // Объявление переменной для объекта URL
            StringBuilder builder = new StringBuilder(); // Объявление переменной для построения строки

            try {
                // Создание объекта URL
                url = new URL(surl);
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {// Чтение ответа сервера построчно
                    String str;
                    while ((str = bufferedReader.readLine()) != null) {
                        builder.append(str); // Добавление строки в StringBuilder
                    }
                } catch (IOException e) {
                    e.printStackTrace(); // Вывод сообщения об ошибке в лог
                }
            } catch (MalformedURLException e) {
                e.printStackTrace(); // Вывод сообщения об ошибке в лог
            }

            // Возвращение полученной строки
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            try{
                // Передача загруженных данных обработчику
                onDownloadedWeather.onDownload(result);
            } catch (Exception ex){
                ex.printStackTrace(); // Вывод сообщения об ошибке в лог
            }
        }
    }
}