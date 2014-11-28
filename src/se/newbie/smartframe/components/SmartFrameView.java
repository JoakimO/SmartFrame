package se.newbie.smartframe.components;

import se.newbie.smartframe.SmartFrameApplication;
import se.newbie.smartframe.model.IModel;
import se.newbie.smartframe.model.IPresentation;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

public class SmartFrameView extends FrameLayout implements IModel.IPresentationChangeListener {
	private final static String TAG = SmartFrameView.class.getCanonicalName();
	
	
	public SmartFrameView(Context aContext) {
		super(aContext);
	}
	
	public SmartFrameView(Context aContext, AttributeSet aAttrs) {
		super(aContext, aAttrs);
	}
	
	public SmartFrameView(Context aContext, AttributeSet aAttrs, int aDefStyle) {
		super(aContext, aAttrs, aDefStyle);
	}
	
	@Override
	protected void onAttachedToWindow () {
		Log.d(TAG, "onAttached");
		IModel model = SmartFrameApplication.getInstance().getModel();
		model.registerPresentationChangeListener(this);		
		
		final IPresentation presentation = SmartFrameApplication.getInstance().getModel().getCurrentPresentation();
		if (presentation != null) {
			removeAllViews();
			addView(presentation.createLayout(SmartFrameApplication.getInstance().getActivity()));
		}		
		
		
	}
	
	@Override
	protected void onDetachedFromWindow() {
		Log.d(TAG, "onDetached");
		IModel model = SmartFrameApplication.getInstance().getModel();
		model.removePresentationChangeListener(this);		
	}

	
	private ObjectAnimator addAnimation(View aView, String aPropertyName, float aStart, float aEnd, long aDuration, Animator.AnimatorListener aListener) {
		ObjectAnimator anim = ObjectAnimator.ofFloat(aView, aPropertyName, aStart, aEnd);
		anim.setDuration(aDuration);
		if (aListener != null) {
			anim.addListener(aListener);
		}		
		return anim;
	}
	
	@Override
	public void onPresentationChangeEvent(IModel aModel) {
		Log.d(TAG, "onPresentationChange");
		
		final IPresentation presentation = aModel.getCurrentPresentation();
		if (presentation != null) {
			
			this.post(new Runnable() {
		        public void run() {
					//removeAllViews();
					
					View view = presentation.createLayout(SmartFrameApplication.getInstance().getActivity());
					final View child = (getChildCount() > 0) ? getChildAt(0) : null;
					
					addView(view);
					
					ObjectAnimator newAnim = addAnimation(view, "translationX", getWidth(), 0, 500, null);
					if (child != null) {
						ObjectAnimator childAnim = addAnimation(child, "translationX", 0, -getWidth(), 500, new Animator.AnimatorListener() {
					        public void onAnimationStart(Animator animation) {}
					        public void onAnimationEnd(Animator animation) {
					        	if (child != null) {
					        		Log.v(TAG, "removeChild");
					        		removeView(child);
					        	}
					        }
					        public void onAnimationCancel(Animator animation) {}
					        public void onAnimationRepeat(Animator animation) {}
					    });
						childAnim.start();
					}	    
				    newAnim.start();
				    
					
					
					

		        }
		    });
			
		}
	}
}
