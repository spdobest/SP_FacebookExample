package sptecch.sp_facebookexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import sptecch.sp_facebookexample.social.FacebookManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,FacebookManager.OnFacebookLoginSuccessListener{
    private static final String TAG = "MainActivity";
    AppCompatButton loginButton;
    CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();

        loginButton = (AppCompatButton) findViewById(R.id.buttonFacebookLOgin);

        loginButton.setOnClickListener(this);

    }
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFacebookLoginSuccess(String accessToken) {
        Log.i(TAG, "onFacebookLoginSuccess: "+accessToken);
    }

    @Override
    public void onFacebookFailed() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonFacebookLOgin :
                FacebookManager.getInstance(MainActivity.this).login(MainActivity.this,this);
                break;
        }
    }
}
