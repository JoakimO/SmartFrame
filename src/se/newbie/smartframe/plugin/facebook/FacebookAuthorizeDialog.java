package se.newbie.smartframe.plugin.facebook;

import java.util.Map;

import se.newbie.smartframe.R;
import se.newbie.smartframe.util.HttpUtil;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class FacebookAuthorizeDialog extends Dialog {
	private final static String TAG = FacebookAuthorizeDialog.class.getCanonicalName();
	
	private String mUrl = null;
	private String mCallbackUri = null;
	private IOAuthDialogListener mListener = null;
	private WebView mWebView = null;
	
	public FacebookAuthorizeDialog(Context aContext, String aUrl, String aCallbackUri, IOAuthDialogListener aListener) {
		super(aContext);
		
		mUrl = aUrl;
		mCallbackUri = aCallbackUri;
		mListener = aListener;		
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle aSavedInstanceState) {
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		this.setContentView(R.layout.facebook_authorize);
		
		mWebView = (WebView)findViewById(R.id.facebook_authorize_web_view);
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setWebViewClient(new OAuthWebViewClient(mListener, this, mCallbackUri));
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl(mUrl);
		
	}
	
	private class OAuthWebViewClient extends WebViewClient {
		private IOAuthDialogListener mListener = null;
		private Dialog mDialog = null;
		private String mCallbackUri = null;
		
		public OAuthWebViewClient(IOAuthDialogListener aListener, Dialog aDialog, String aCallbackUri) {
			mListener = aListener;
			mDialog = aDialog;
			mCallbackUri = aCallbackUri;
		}
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView aView, String aUrl) {
			Log.d(TAG, "shouldOverrideUrlLoading : " + aUrl);

			if (aUrl.startsWith(mCallbackUri)) {
				
				Map<String, String> parameters = HttpUtil.parseQueryString(aUrl);
				mListener.onComplete(parameters.get("access_token"));
				mDialog.dismiss();
				return true;
			}
			return false;
		}

		@Override
		public void onReceivedError(WebView aView, int aErrorCode, String aDescription, String aFailingUrl) {
			super.onReceivedError(aView, aErrorCode, aDescription, aFailingUrl);
			Log.d(TAG, "onReceivedError : " + aDescription);
			mListener.onError();
			mDialog.dismiss();
		}

		@Override
		public void onPageStarted(WebView aView, String aUrl, Bitmap aFavicon) {
			super.onPageStarted(aView, aUrl, aFavicon);
			Log.d(TAG, "onPageStarted : " + aUrl);
		}

		@Override
		public void onPageFinished(WebView aView, String aUrl) {
			super.onPageFinished(aView, aUrl);
			Log.d(TAG, "onPageFinished : " + aUrl);
		}		
	}
	
	public interface IOAuthDialogListener {
		public void onComplete(String aToken);
		public void onError();
	}
	
}
