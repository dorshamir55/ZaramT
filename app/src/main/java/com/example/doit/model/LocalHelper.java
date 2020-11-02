package com.example.doit.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.doit.R;
import com.example.doit.ui.MainActivity;
import com.example.doit.ui.SettingsFragment;

import java.util.Locale;

public class LocalHelper {
    private static Context context;

    public LocalHelper(Context context) {
        this.context = context;
    }

    public static void saveLocale(String lang) {
        String langPref = "Language";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();
    }

    public static void loadLocale() {
        String langPref = "Language";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String language = prefs.getString(langPref, "he");
        setLocale(language);
    }

    public static void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
}
