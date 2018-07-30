package org.yurovnik.tweetem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.yurovnik.tweetem.R;

public class AuthActivity extends AppCompatActivity {
    private TwitterLoginButton twitterLoginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        twitterLoginButton = findViewById(R.id.login_button);

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                long userID = result.data.getUserId();
                navigateToUserInfo(userID);
            }

            @Override
            public void failure(TwitterException exception) {

            }
        });
    }

    private void navigateToUserInfo(long userID){
        Intent intent = new Intent(this,UserInfoActivity.class);
        intent.putExtra(UserInfoActivity.USER_ID, userID);
        startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }
}
