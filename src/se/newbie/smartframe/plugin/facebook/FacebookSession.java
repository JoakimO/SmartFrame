package se.newbie.smartframe.plugin.facebook;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class FacebookSession {
	private SharedPreferences mSharedPreferences = null;
	private Editor mEditor = null;
	
	private static final String SHARED_PREFERENCES_NAME = "FacebookSharedPreferences";
	
	private static final String API_ACCESS_TOKEN = "access_token";
	
	public FacebookSession(Context aContext) {
		mSharedPreferences = aContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();
	}
	
	public String getAccessToken() {
		return mSharedPreferences.getString(API_ACCESS_TOKEN, null);
	}
		
	
	public void storeSession(String aAccessToken) {
		mEditor.putString(API_ACCESS_TOKEN, aAccessToken);
		mEditor.commit();
	}
	
	public void reset() {
		mEditor.putString(API_ACCESS_TOKEN, null);
		mEditor.commit();		
	}
	
	public boolean hasAccessToken() {
		return getAccessToken() != null ? true : false;
	}

}
