package se.newbie.smartframe.model;

import android.content.Context;
import android.view.View;

/**
 * A frame represent a view into the component.
 */
public interface IPresentation {
	public View createLayout(Context aContext);
}
