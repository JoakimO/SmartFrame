package se.newbie.smartframe;

import java.util.List;

import se.newbie.smartframe.model.IHasPresentationProviderState;
import se.newbie.smartframe.model.IModel;
import se.newbie.smartframe.model.IPresentationProvider;
import se.newbie.smartframe.model.IPresentationProviderState;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;

public class SmartFrameActivity extends Activity {
	private final static String TAG = SmartFrameActivity.class.getCanonicalName();
	private final static String PRESENTATION_PROVIDER_STATES_KEY = "presentation_provider_states";
	
	private SparseArray<IPresentationProviderState> mPresentationProviderStates;
	private View mAnchorView = null;
	private int mHideFlags = 0;
	
	private final BroadcastReceiver mActivityReciver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (IModel.ACTION_CLOSE.equals(intent.getAction())) {
				finish();
			}
		}
	};	
	
	
	@Override
	protected void onCreate(Bundle aSavedInstanceState) {
		super.onCreate(aSavedInstanceState);
		Log.v(TAG, "onCreate");
		
				
		if (aSavedInstanceState != null) {
			mPresentationProviderStates = aSavedInstanceState.getSparseParcelableArray(PRESENTATION_PROVIDER_STATES_KEY);
			Log.v(TAG, "Found " + mPresentationProviderStates.size() + " stored states.");
		} else {
			mPresentationProviderStates = new SparseArray<IPresentationProviderState>();
		}
		
		SmartFrameApplication.getInstance().init(this);
		
		
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		
		
		
		setContentView(R.layout.activity_smart_frame);
		
		mAnchorView = findViewById(R.id.activity_anchor_view);
		
		getActionBar().hide();
		mHideFlags = View.SYSTEM_UI_FLAG_LOW_PROFILE;
		mHideFlags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		mHideFlags |= View.SYSTEM_UI_FLAG_FULLSCREEN;
		mHideFlags |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
		mHideFlags |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;		
		
		mAnchorView.setSystemUiVisibility(mHideFlags);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_smart_frame, menu);
		return true;
	}

	@Override
    protected void onResume() {
		super.onResume();
		
		List<IPresentationProvider> providers = SmartFrameApplication.getInstance().getPresentationProviders();
		for (IPresentationProvider provider : providers) {
			IPresentationProviderState presentationProviderState = null;
			if (provider instanceof IHasPresentationProviderState) {
				presentationProviderState = mPresentationProviderStates.get(provider.getId());
			}
			provider.onResume(presentationProviderState);
		}
		SmartFrameApplication.getInstance().onResume();
		registerReceiver(mActivityReciver, new IntentFilter(IModel.ACTION_CLOSE));
	}

	@Override
    protected void onPause() {
		super.onPause();
		
		List<IPresentationProvider> providers = SmartFrameApplication.getInstance().getPresentationProviders();
		for (IPresentationProvider provider : providers) {
			provider.onPause();
			
			if (provider instanceof IHasPresentationProviderState) {
				mPresentationProviderStates.put(provider.getId(), ((IHasPresentationProviderState) provider).getPresentationProviderState());
			}
		}		
		SmartFrameApplication.getInstance().onPause();
		this.unregisterReceiver(mActivityReciver);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle aOutState) {
		super.onSaveInstanceState(aOutState);
		Log.v(TAG, "Saving states for " + mPresentationProviderStates.size() + " providers.");
		aOutState.putSparseParcelableArray(PRESENTATION_PROVIDER_STATES_KEY, mPresentationProviderStates);
	}	
}
