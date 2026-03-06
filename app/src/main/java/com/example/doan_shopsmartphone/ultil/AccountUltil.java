package com.example.doan_shopsmartphone.ultil;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.doan_shopsmartphone.model.User;


public class AccountUltil {
    public static String TOKEN = "";
    public static String BEARER = "Bearer ";
    public static User USER;

    public static String getToken(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences("ACCOUNT", Context.MODE_PRIVATE);
        return prefs.getString("TOKEN", "");
    }

    public static void saveToken(Context context, String token) {
        SharedPreferences prefs =
                context.getSharedPreferences("ACCOUNT", Context.MODE_PRIVATE);
        prefs.edit().putString("TOKEN", token).apply();
    }
}
