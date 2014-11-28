package se.newbie.smartframe.plugin.facebook.model;

import org.json.JSONObject;

public class FacebookAlbum {
	private JSONObject mAlbum;
	
	public FacebookAlbum(JSONObject aAlbum) {
		mAlbum = aAlbum;
	}
	
	public int getId() {
		return mAlbum.optInt("id");
	}
	
	public String getName() {
		return mAlbum.optString("name");
	}
}
