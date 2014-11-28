package se.newbie.smartframe.components;

import se.newbie.smartframe.R;
import se.newbie.smartframe.SmartFrameApplication;
import se.newbie.smartframe.model.IModel;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DebugFrameView extends FrameLayout implements IModel.IPresentationChangeListener, IModel.IModelChangeListener {
	private final static String TAG = DebugFrameView.class.getCanonicalName();
	
	private TextView mQueue = null; 
	private LinearLayout mRoot = null;
	
	public DebugFrameView(Context aContext) {
		super(aContext);
		init(aContext);
	}
	
	public DebugFrameView(Context aContext, AttributeSet aAttrs) {
		super(aContext, aAttrs);
		init(aContext);
	}
	
	public DebugFrameView(Context aContext, AttributeSet aAttrs, int aDefStyle) {
		super(aContext, aAttrs, aDefStyle);
		init(aContext);
	}
	
	public void init(Context aContext) {
		
		mRoot = new LinearLayout(aContext);
		super.addView(mRoot);
		
		LayoutInflater inflater = (LayoutInflater)aContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View debugView = inflater.inflate(R.layout.debug_frame, null);
		
		mQueue = (TextView)debugView.findViewById(R.id.debug_frame_queue_value_text_view);
		
		super.addView(debugView);
	}
	
	@Override
	protected void onAttachedToWindow () {
		Log.d(TAG, "onAttached");
		IModel model = SmartFrameApplication.getInstance().getModel();
		model.registerPresentationChangeListener(this);
		model.registerModelChangeListener(this);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		Log.d(TAG, "onDetached");
		IModel model = SmartFrameApplication.getInstance().getModel();
		model.removePresentationChangeListener(this);
		model.removeModelChangeListener(this);
		
	}
	
	@Override
	public void onPresentationChangeEvent(final IModel aModel) {
		update(aModel);
	}

	@Override
	public void onModelChangeEvent(final IModel aModel) {
		update(aModel);
	}
	
	private void update(final IModel aModel) {
		this.post(new Runnable() {
			@Override
			public void run() {
				update(aModel);
				mQueue.setText("" + aModel.getPresentationSize());
				mQueue.invalidate();
				
			}
		});		
	}
	
	/*@Override
	public void addView(View aView) {
		mRoot.addView(aView);
	}*/
}
