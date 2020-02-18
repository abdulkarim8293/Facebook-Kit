package com.abdulkarim.facebookkit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private ImageView imageView;
    private TextView userName,userEmail;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.login_button);
        imageView = findViewById(R.id.profileImageView);
        userName = findViewById(R.id.userNameTextView);
        userEmail = findViewById(R.id.userEmailTextView);

        callbackManager = CallbackManager.Factory.create();

        checkLoginStatus();

        loginButton.setReadPermissions(Arrays.asList("email","public_profile"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        callbackManager.onActivityResult(requestCode,resultCode,data);

        super.onActivityResult(requestCode, resultCode, data);
    }

    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            if (currentAccessToken == null){

                userEmail.setText("");
                userName.setText("");
                imageView.setImageResource(0);

                Toast.makeText(MainActivity.this, "Log out ", Toast.LENGTH_SHORT).show();
            }else {
                getUserProfileInfo(currentAccessToken);
            }
        }
    };

    private void getUserProfileInfo(AccessToken accessToken){

        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");

                    String image_url = "https://graph.facebook.com/"+id+"/picture?type=normal";
                    userEmail.setText(email);
                    userName.setText(first_name+" "+last_name);
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.dontAnimate();

                    Glide.with(MainActivity.this).load(image_url).into(imageView);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("fields","first_name,last_name,email,id");
        request.setParameters(bundle);
        request.executeAsync();
    }

    private void checkLoginStatus(){

        if (AccessToken.getCurrentAccessToken()!= null){

            getUserProfileInfo(AccessToken.getCurrentAccessToken());
        }

    }
}
