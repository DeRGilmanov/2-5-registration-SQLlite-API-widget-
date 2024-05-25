package com.example.myapplication;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Implementation of App Widget functionality.
 */
public class Weather extends AppWidgetProvider {



    public static String city_name = "Москва";
    private static final String SYNC_CLICKED    = "automaticWidgetSyncButtonClick";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Создаем объект RemoteViews для управления виджетом
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather);





        Log.d("widget_test", "updateAppWidget вызван");


        // Обновляем виджет на рабочем столе
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        // Создаем интент для вызова самого себя с указанным действием
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        // Возвращаем PendingIntent для запуска интента
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        Log.d("widget_test", "onUpdate");


        // Создаем объект RemoteViews и указываем его разметку
        RemoteViews remoteViews;
        ComponentName watchWidget;

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.weather);
        watchWidget = new ComponentName(context, Weather.class);

        // Устанавливаем обработчик нажатия на кнопку обновления
        remoteViews.setOnClickPendingIntent(R.id.update_btn, getPendingSelfIntent(context, SYNC_CLICKED));
        appWidgetManager.updateAppWidget(watchWidget, remoteViews);

        // Создаем помощник для работы с погодой
        WeatherHelper weatherHelper = new WeatherHelper(context);

        for (int appWidgetId : appWidgetIds) {
            // Устанавливаем обработчик для получения загруженных данных о погоде
            weatherHelper.setOnDownloadedWeather(new OnDownloadedWeather() {
                @Override
                public void onDownload(String jsonString) throws JSONException {
                    // Вызываем метод для установки полученных данных в виджет
                    setWeather(jsonString, context);
                }
            });

            weatherHelper.getWeather(city_name);
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);

        if (SYNC_CLICKED.equals(intent.getAction())) {
            // Получаем интент действия нажатия на кнопку обновления
            WeatherHelper weatherHelper = new WeatherHelper(context);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            // Создаем объект RemoteViews и указываем его разметку
            RemoteViews remoteViews;
            ComponentName watchWidget;remoteViews = new RemoteViews(context.getPackageName(), R.layout.weather);
            watchWidget = new ComponentName(context, Weather.class);




            Log.d("widget_test", "Click");
            // Устанавливаем обработчик для получения загруженных данных о погоде
            weatherHelper.setOnDownloadedWeather(new OnDownloadedWeather() {
                @Override
                public void onDownload(String jsonString) throws JSONException {
                    // Вызываем метод для установки полученных данных в виджет
                    setWeather(jsonString, context.getApplicationContext());
                }
            });

            weatherHelper.getWeather(city_name);



            appWidgetManager.updateAppWidget(watchWidget, remoteViews);

        }
    }
    @Override
    public void onEnabled(Context context) {
        // Введите соответствующую функциональность при создании первого виджета.

        Log.d("widget_test", "onEnabled");

    }

    @Override
    public void onDisabled(Context context) {
        // Введите соответствующие функции на случай отключения последнего виджета.
    }

    public int spToPx(float sp, Context context) {
        // Конвертируем значение sp в пиксели
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
    public void setWeather(String jsonString, Context context) throws JSONException {



        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.weather);
        ComponentName thisWidget = new ComponentName(context, Weather.class);


        Log.d("widget_test", "setWeather вызван");
        // Получаем данные о погоде из переданной JSON-строки
        JSONObject jsonObject = new JSONObject(jsonString);

        // Получаем название города
        String city = jsonObject.getString("name");
        // Устанавливаем название города в виджет
        remoteViews.setTextViewText(R.id.widget_city, city);

        Log.d("widget_test", "city " + city);
        // Получаем температуру
        int temp_value = (int)Math.round(jsonObject.getJSONObject("main").getDouble("temp"));
        // Формируем строку с температурой
        String temp = (temp_value < 0 ? "-" : "") + temp_value + "°C";
        // Устанавливаем температуру в виджет
        remoteViews.setTextViewText(R.id.widget_temp, temp);

        // Получаем описание погоды
        String like = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
        // Приводим описание в красивый вид
        like = like.substring(0, 1).toUpperCase() + like.substring(1);
        // Устанавливаем описание погоды в виджет
        remoteViews.setTextViewText(R.id.widget_like, like);

        // Получаем иконку погоды
        String icon = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");
        // Формируем URL для загрузки иконки
        String iconUrl = "https://openweathermap.org/img/wn/" + icon +"@4x.png";

        // Загружаем иконку с помощью Glide
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(iconUrl)
                .into(new CustomTarget<Bitmap>(spToPx(52, context), spToPx(52, context)) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // Устанавливаем иконку в виджет
                        remoteViews.setImageViewBitmap(R.id.widget_img, resource);
                        // Обновляем виджет
                        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                        Log.d("test_download_image", "Размер: " + resource.getWidth());
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
        remoteViews.setTextViewText(R.id.last_update_time, Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE) + ":" + Calendar.getInstance().get(Calendar.SECOND));
        // Обновляем виджет
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

    public static void updateAllWidgets(final Context context,
                                        final int layoutResourceId,
                                        final Class< ? extends AppWidgetProvider> appWidgetClass)
    {
        Log.d("widget_test", "updateAllWidgets");
        // Создаем объект RemoteViews для управления виджетами
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), layoutResourceId);

        // Получаем менеджер виджетов
        AppWidgetManager manager = AppWidgetManager.getInstance(context);

        // Получаем список ID всех активных виджетов для данного класса виджета
        final int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(context, appWidgetClass));

        // Обновляем каждый виджет
        for (int i = 0; i < appWidgetIds.length; ++i)
        {
            manager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
    }
}
