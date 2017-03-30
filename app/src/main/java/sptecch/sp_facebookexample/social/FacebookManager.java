package sptecch.sp_facebookexample.social;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

/**
 * Created by root on 8/11/16.
 */
public class FacebookManager {

	private static final String TAG = FacebookManager.class.getSimpleName();

	private static FacebookManager  facebookManagerInatsnce;
    private CallbackManager mCallbackManager;
	private        OnFacebookLoginSuccessListener mOnFacebookLoginSuccessListener;

	//EMPTY CONSTRUCTOR
	private FacebookManager() {

	}

	private FacebookManager( Context mContext ) {
		FacebookSdk.sdkInitialize( mContext.getApplicationContext() );
		mCallbackManager = CallbackManager.Factory.create();
		initFacebookCallback();
	}

	// SINGLETON CLASS INITIALIZATION
	public static FacebookManager getInstance( Context context ) {
		if ( facebookManagerInatsnce == null ) {
			facebookManagerInatsnce = new FacebookManager( context );
		}

		return facebookManagerInatsnce;
	}

	private void initFacebookCallback() {
		LoginManager.getInstance().registerCallback( mCallbackManager, new FacebookCallback< LoginResult >() {
			@Override
			public void onSuccess( LoginResult loginResult ) {
				Log.i( TAG, "onSuccess : token " + loginResult.getAccessToken().getToken() );
				mOnFacebookLoginSuccessListener.onFacebookLoginSuccess( loginResult.getAccessToken().getToken() );
			}

			@Override
			public void onCancel() {
				mOnFacebookLoginSuccessListener.onFacebookFailed();
			}

			@Override
			public void onError( FacebookException error ) {
				Log.i( TAG, "onError: facebook " + error.getMessage() );
			}
		} );
	}

	// CALLBACK METHOD
	public void login( Fragment fragment, OnFacebookLoginSuccessListener listener ) {
		mOnFacebookLoginSuccessListener = listener;
		LoginManager.getInstance().logInWithReadPermissions( fragment, Arrays.asList( "email" ) );
	}

	// login from activity
	// CALLBACK METHOD
	public void login( AppCompatActivity appCompatActivity, OnFacebookLoginSuccessListener listener ) {
		mOnFacebookLoginSuccessListener = listener;
		LoginManager.getInstance().logInWithReadPermissions( appCompatActivity, Arrays.asList( "email" ) );
	}

	public void onActivityResult( int requestCode, int resultCode, Intent data ) {
		mCallbackManager.onActivityResult( requestCode, resultCode, data );
	}

	// INTERFACE FOR FACEBOOK LOGIN RESPONSE CALLBACK
	public interface OnFacebookLoginSuccessListener {
		void onFacebookLoginSuccess(String accessToken);

		void onFacebookFailed();
	}

}