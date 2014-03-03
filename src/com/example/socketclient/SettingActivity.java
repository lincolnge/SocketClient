package com.example.socketclient;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/*
 * 
 */
public class SettingActivity extends PreferenceActivity {
  @SuppressWarnings("deprecation")
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.setting);
  }
}
