package se.newbie.smartframe.model.impl;

import java.util.HashMap;

import se.newbie.smartframe.model.IPresentationProviderState;
import android.os.Parcel;
import android.os.Parcelable;

public class SimplePresentationProviderState implements IPresentationProviderState {
	private HashMap<String, String> mMap;
	
	public SimplePresentationProviderState() {
		mMap = new HashMap<String, String>();
	}

	public SimplePresentationProviderState(Parcel aIn) {
		mMap = new HashMap<String, String>();
		readFromParcel(aIn);
	}	
	
	public static final Parcelable.Creator<SimplePresentationProviderState> CREATOR = new Parcelable.Creator<SimplePresentationProviderState>() {
		public SimplePresentationProviderState createFromParcel(Parcel aIn) {
			return new SimplePresentationProviderState(aIn);
		}

		public SimplePresentationProviderState[] newArray(int aSize) {
			return new SimplePresentationProviderState[aSize];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel aDest, int aFlags) {
		aDest.writeInt(mMap.size());
		for (String s : mMap.keySet()) {
			aDest.writeString(s);
			aDest.writeString(mMap.get(s));
		}
	}

	public void readFromParcel(Parcel aIn) {
		int count = aIn.readInt();
		for (int i = 0; i < count; i++) {
			mMap.put(aIn.readString(), aIn.readString());
		}
	}

	public String get(String aKey) {
		return mMap.get(aKey);
	}

	public void put(String aKey, String aValue) {
		mMap.put(aKey, aValue);
	}	
}
