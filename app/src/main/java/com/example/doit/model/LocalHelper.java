package com.example.doit.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.doit.R;
import com.example.doit.ui.MainActivity;
import com.example.doit.ui.SettingsFragment;

import java.util.Locale;

public class LocalHelper {
    private Activity activity;

    public LocalHelper(Activity activity) {
        this.activity = activity;
    }

    public void saveLocale(String lang) {
        String langPref = "Language";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();
    }

    public void loadLocale() {
        String language = getLocale();
        if(language.equals("he"))
            activity.getWindow().getDecorView().setLayoutDirection(
                    language.equals("he") ?
                            View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);

        setLocale(language);
    }

    public void setLocale(String lang) {

        Locale locale = new Locale(lang);
        Configuration config = new Configuration();
        config.locale = locale;
        Locale.setDefault(locale);

        activity.getResources().updateConfiguration(config, activity.getResources().getDisplayMetrics());
    }

    public String getLocale() {
        String langPref = "Language";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        return prefs.getString(langPref, "he");
    }
}
