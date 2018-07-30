package org.yurovnik.tweetem;

import android.app.Application;

import com.twitter.sdk.android.core.Twitter;

public class TweetemApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Twitter.initialize(this);
    }
}
