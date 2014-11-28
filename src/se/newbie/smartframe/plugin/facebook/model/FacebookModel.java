package se.newbie.smartframe.plugin.facebook.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class FacebookModel {

	private List<FacebookAlbum> mAlbums = null;
	
	public void setAlbums(JSONObject aAlbums) {
		mAlbums = new ArrayList<FacebookAlbum>();
		if (aAlbums != null) {
			JSONArray list = aAlbums.optJSONArray("data");
			for (int i = 0; i < list.length(); i++) {
				JSONObject album = list.optJSONObject(i);
				if (album != null) {
					mAlbums.add(new FacebookAlbum(album));
				}
			}
		}
	}
	
	public List<FacebookAlbum> getAlbums() {
		return null;
	}
	
}
