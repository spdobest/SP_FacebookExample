package sptecch.sp_facebookexample.fb;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import sptecch.sp_facebookexample.R;
import sptecch.sp_facebookexample.utils.ImagePicker;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MainActivity";
    CallbackManager callbackManager;

    AppCompatButton buttonPostMessage;
    AppCompatButton buttonPostImage;
    AppCompatButton buttonPostVideo;

    int IMAGE_FROM_CAMERA = 1;
    int IMAGE_FROM_GALLERY = 2;
    String img_path = null;
    File imageFile;

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookSDKInitialize();
        setContentView(R.layout.activity_main1);
        buttonPostMessage = (AppCompatButton) findViewById(R.id.buttonPostMessage);
        buttonPostImage = (AppCompatButton) findViewById(R.id.buttonPostImage);
        buttonPostVideo = (AppCompatButton) findViewById(R.id.buttonPostVideo);

        buttonPostMessage.setOnClickListener(this);
        buttonPostImage.setOnClickListener(this);
        buttonPostVideo.setOnClickListener(this);

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        getLoginDetails(loginButton);
    }

    /*
     * Register a callback function with LoginButton to respond to the login result.
     */
    protected void getLoginDetails(LoginButton login_button){

        // Callback registration
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult login_result) {

                String userId = login_result.getAccessToken().getUserId();



                getFriendsList(userId);


               /*GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(
                        login_result.getAccessToken(),
                        //AccessToken.getCurrentAccessToken(),
                        "/me/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                Intent intent = new Intent(MainActivity.this,FriendsList.class);
                                try {
                                    JSONArray rawName = response.getJSONObject().getJSONArray("data");
                                    intent.putExtra("jsondata", rawName.toString());
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                ).executeAsync();*/

            }

            @Override
            public void onCancel() {
                // code for cancellation
            }

            @Override
            public void onError(FacebookException exception) {
                //  code to handle error
            }
        });
    }



    protected void facebookSDKInitialize() {

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }


    private void getFriendsList(String userId){
        /* make the API call */

        Bundle params = new Bundle();
        params.putString("uid", userId);

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/"+userId/*{user-id}*/+"/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.i(TAG, "onCompleted: "+response);
            /* handle the result */
                    }
                }
        ).executeAsync();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonPostMessage :

                break;
            case R.id.buttonPostImage :


                Intent intentPickImage = new Intent(this, ImagePicker.class);
                intentPickImage.putExtra("pick_from",IMAGE_FROM_GALLERY);
                startActivityForResult(intentPickImage,IMAGE_FROM_GALLERY);


                break;
            case R.id.buttonPostVideo :
                Uri videoFileUri = null;
                ShareVideo video = new ShareVideo.Builder()
                        .setLocalUrl(videoFileUri)
                        .build();
                ShareVideoContent content = new ShareVideoContent.Builder()
                        .setVideo(video)
                        .build();
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       if(requestCode == IMAGE_FROM_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
           imageUri = selectedImageUri;
            img_path = saveImage(getApplicationContext(), selectedImageUri);
           try {
               Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
               uploadImage(bitmap);
           } catch (IOException e) {
               e.printStackTrace();
           }


        } else if (requestCode == IMAGE_FROM_CAMERA && resultCode == RESULT_OK/* && data != null*/) {
           imageUri = data.getData();
        }
        else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String saveImage(Context context, Uri uri) {
        Bitmap finalBitmap;
        File file = null;
        try {
            finalBitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
            File folder = new File(Environment.getExternalStorageDirectory().toString() + "/Clipwiser");
            if (!folder.exists()) {
                folder.mkdir();
            }
            file = new File(folder.getAbsolutePath() + "/Image_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg");
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void uploadImage(Bitmap bitmap){
//        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
    }

}