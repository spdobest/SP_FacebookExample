package sptecch.sp_facebookexample.social;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import sptecch.sp_facebookexample.R;
import sptecch.sp_facebookexample.utils.Constants;


/**
 * Created by Craftsvilla on 28/7/16.
 */
public class GoogleSignInManager implements GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = "GoogleSignInManager";

    private static GoogleSignInManager mGoogleInstance;
    private        GoogleApiClient     mGoogleApiClient;

    private GoogleSignInManager(Context ctx){
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder( GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestProfile()
//                .requestScopes(new RestrictTo.Scope( "https://www.googleapis.com/auth/userinfo.profile"))
                .requestServerAuthCode( ctx.getString( R.string.google_server_client_id), false)
                .requestIdToken(ctx.getString(R.string.google_server_client_id))
                .build();

        mGoogleApiClient=new GoogleApiClient.Builder( ctx)
                .enableAutoManage( (FragmentActivity )ctx, this)
                .addApi( Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public static GoogleSignInManager getInstance(Context context){
        if(mGoogleInstance==null) {
            mGoogleInstance = new GoogleSignInManager(context);
        }
        return mGoogleInstance;
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed: "+connectionResult.getErrorMessage());
    }

    public void signIn(DialogFragment dialogFragment) {
        Intent intent= Auth.GoogleSignInApi.getSignInIntent( mGoogleApiClient);
        dialogFragment.startActivityForResult( intent, Constants.ActivityRequestCodes.GOOGLE_SIGN_IN);
    }

    public void signIn(AppCompatActivity context) {
        Intent intent= Auth.GoogleSignInApi.getSignInIntent( mGoogleApiClient);
        context.startActivityForResult(intent, Constants.ActivityRequestCodes.GOOGLE_SIGN_IN);
    }

    public interface OnGoogleSignInListener {
        void onGoogleTokenReceived(String token);
        void onGoogleFailed();
    }

    public void onTokenReceive(GoogleSignInResult result, OnGoogleSignInListener googleSignInListener){
        if (result.isSuccess()) {
            GoogleSignInAccount googleSignInAccount = result.getSignInAccount();
            String              token               = googleSignInAccount.getIdToken();
            String              name                = googleSignInAccount.getDisplayName();
            Log.i(TAG, "onActivityResult: " + "token=" + token + "\nname=" + name);
            googleSignInListener.onGoogleTokenReceived(token);
        } else {
            Log.i(TAG, "onActivityResult: FAIL");
            googleSignInListener.onGoogleFailed();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data, OnGoogleSignInListener listener) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent( data);
        if (result.isSuccess()) {

            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //String userId = acct.getServerAuthCode();
            String token = acct.getIdToken();
            Log.i(TAG, "onActivityResult: Google Signin success "+token +" email "+acct.getEmail()+" name "+acct.getDisplayName());
            listener.onGoogleTokenReceived(token);

        } else {
            Log.i(TAG, "onActivityResult: GOogle Signin success ");
            // Signed out, show unauthenticated UI.
            listener.onGoogleFailed();
        }


    }

}