package com.example.myapplication;

import org.json.JSONException;

public interface OnDownloadedWeather {
    void onDownload(String jsonString) throws JSONException;
}
