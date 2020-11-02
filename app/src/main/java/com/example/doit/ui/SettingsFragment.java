package com.example.doit.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.example.doit.R;
import com.example.doit.model.LocalHelper;

public class SettingsFragment extends PreferenceFragmentCompat {
    private ListPreference listPreference;
    private LocalHelper localHelper;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        assert view!= null;
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        localHelper = new LocalHelper(getContext());
        listPreference = findPreference("language");
        PreferenceManager.setDefaultValues(getActivity(), R.xml.settings_preferences, false);

        Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(((String)newValue).equals("en")) {
                    localHelper.setLocale("en");
                }
                else if(((String)newValue).equals("he")) {
                    localHelper.setLocale("he");
                }

                localHelper.saveLocale((String)newValue);
                Intent refresh = new Intent(getActivity(), MainActivity.class);
                getActivity().finish();
                Toast.makeText(getActivity(), getResources().getString(R.string.language_changed), Toast.LENGTH_SHORT).show();
                startActivity(refresh);
                return true;
            }
        };

        listPreference.setOnPreferenceChangeListener(listener);
    }

//    public void setLocale(String lang) {
//        Locale myLocale = new Locale(lang);
//        Resources res = getResources();
//        DisplayMetrics dm = res.getDisplayMetrics();
//        Configuration conf = res.getConfiguration();
//        conf.locale = myLocale;
//        res.updateConfiguration(conf, dm);
//        Intent refresh = new Intent(getActivity(), MainActivity.class);
//        getActivity().finish();
//        Toast.makeText(getActivity(), getResources().getString(R.string.language_changed), Toast.LENGTH_SHORT).show();
//        startActivity(refresh);
//    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
////        final Preference changePassword = findPreference("changePassword");
////        changePassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
////            @Override
////            public boolean onPreferenceClick(Preference preference) {
////                startActivity(new Intent(getContext(), ChangePasswordActivity.class));
////                return false;
////            }
////        });
//    }
}