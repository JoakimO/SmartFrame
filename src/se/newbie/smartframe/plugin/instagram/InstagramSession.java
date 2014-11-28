package se.newbie.smartframe.plugin.instagram;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class InstagramSession {
	private SharedPreferences mSharedPreferences = null;
	private Editor mEditor = null;
	
	private static final String SHARED_PREFERENCES_NAME = "InstagramSharedPreferences";
	
	private static final String API_ID = "id";
	private static final String API_NAME = "name";
	private static final String API_USERNAME = "username";
	private static final String API_ACCESS_TOKEN = "access_token";
	
	public InstagramSession(Context aContext) {
		mSharedPreferences = aContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();
	}
	
	public String getId() {
		return mSharedPreferences.getString(API_ID, null);
	}	

	public String getName() {
		return mSharedPreferences.getString(API_NAME, null);
	}	
	
	public String getUsername() {
		return mSharedPreferences.getString(API_USERNAME, null);
	}
	
	public String getAccessToken() {
		return mSharedPreferences.getString(API_ACCESS_TOKEN, null);
	}		
	
	public void storeSession(String aAccessToken, String aId, String aName, String aUserName) {
		mEditor.putString(API_ID, aId);
		mEditor.putString(API_NAME, aName);
		mEditor.putString(API_USERNAME, aUserName);
		mEditor.putString(API_ACCESS_TOKEN, aAccessToken);
		mEditor.commit();
	}
	
	public void reset() {
		mEditor.putString(API_ID, null);
		mEditor.putString(API_NAME, null);
		mEditor.putString(API_USERNAME, null);
		mEditor.putString(API_ACCESS_TOKEN, null);
		mEditor.commit();		
	}
	
	public boolean hasAccessToken() {
		return getAccessToken() != null ? true : false;
	}

}
