package org.instk.logger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

public class SensorPref extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Some of the following values could also be read from the pref file.
		Intent mintent=getIntent();
		int nsen=mintent.getIntExtra("nsen", 0);
		String[] sname=mintent.getStringArrayExtra("snames");
		int srate=mintent.getIntExtra("srate", 0);
		int[] actsen=mintent.getIntArrayExtra("act_sen");
		
		//Pref strings
		CharSequence[] rateprefs={"SENSOR_DELAY_UI",
								  "SENSOR_DELAY_NORMAL",
								  "SENSOR_DELAY_GAME",
								  "SENSOR_DELAY_FASTEST"};
		CharSequence[] ratekeys={Integer.toString(SensorManager.SENSOR_DELAY_UI),
								 Integer.toString(SensorManager.SENSOR_DELAY_NORMAL),
								 Integer.toString(SensorManager.SENSOR_DELAY_GAME),
								 Integer.toString(SensorManager.SENSOR_DELAY_FASTEST),};
		
		
		//Create the preference list
		//Root
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
        
        //Rate preference
        PreferenceCategory RatePrefCat = new PreferenceCategory(this);
        RatePrefCat.setTitle("Choose the Rate");
        root.addPreference(RatePrefCat);
        
        ListPreference listPref = new ListPreference(this);
        listPref.setEntries(rateprefs);
        listPref.setEntryValues(ratekeys);
        listPref.setDialogTitle("Choose a Sensor Rate");
        listPref.setKey("rate_preference");
        listPref.setTitle("Rate Selection");
        listPref.setSummary("Sensor output frequency");
        RatePrefCat.addPreference(listPref);
		
        
        
        //Active Sensor Selection
        PreferenceCategory ActSenCat = new PreferenceCategory(this);
        ActSenCat.setTitle("Select the Active Sensors");
        root.addPreference(ActSenCat);
        
        for (int i=0;i<nsen;i++) {
        	CheckBoxPreference togglePref = new CheckBoxPreference(this);
        	togglePref.setTitle(sname[i]);
        	togglePref.setKey(sname[i]);
        	ActSenCat.addPreference(togglePref);
        }
        
        //Set the activity content
        setPreferenceScreen(root);
	}
}
