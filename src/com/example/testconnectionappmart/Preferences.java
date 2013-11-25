package com.example.testconnectionappmart;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
	
	private static SharedPreferences mprefs; 
	
	private Preferences(){
		
	}
	
	public static SharedPreferences getSingleton(Activity activity){
		
		if(mprefs == null)
			mprefs = PreferenceManager.getDefaultSharedPreferences(activity);
		
		return mprefs;
	}

}
