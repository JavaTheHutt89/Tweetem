package org.yurovnik.tweetem.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.yurovnik.tweetem.R;
import org.yurovnik.tweetem.adapter.TweetAdapter;
import org.yurovnik.tweetem.network.HttpClient;
import org.yurovnik.tweetem.pojo.Tweet;
import org.yurovnik.tweetem.pojo.User;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public class UserInfoActivity extends AppCompatActivity {

    public final static String USER_ID = "userId";

    private Toolbar toolbar;
    private ImageView userImageView;
    private TextView nameTextView;
    private TextView nickTextView;
    private TextView descriptionTextView;
    private TextView locationTextView;
    private TextView followingTextView;
    private TextView followersTextView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView tweetsRecyclerView;
    private TweetAdapter tweetAdapter;

    private HttpClient httpClient;

    private int taskInProgressCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        final long userid = getIntent().getLongExtra(USER_ID,-1);

        userImageView = findViewById(R.id.profile_image_view);
        nameTextView = findViewById(R.id.author_name_text_view);
        nickTextView = findViewById(R.id.author_nick_text_view);
        descriptionTextView = findViewById(R.id.user_description_text_view);
        locationTextView = findViewById(R.id.user_location_text_view);
        followingTextView= findViewById(R.id.following_count_text_view);
        followersTextView = findViewById(R.id.followers_count_text_view);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            tweetAdapter.clearItems();
            loadUserInfo(userid);
            loadTweets(userid);
        });

        initRecyclerView();

        httpClient = new HttpClient();
        loadUserInfo(userid);
        loadTweets(userid);
    }

    private void setRefreshLayoutVisible(boolean visible){
        if (visible){
            taskInProgressCount++;
            if (taskInProgressCount == 1){swipeRefreshLayout.setRefreshing(true);}
        } else {
            taskInProgressCount--;
            if (taskInProgressCount == 0){swipeRefreshLayout.setRefreshing(false);}
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search){
            Intent intent = new Intent(this, SearchUsersActivity.class);
            startActivity(intent);
        }
        return true;
    }

    private void loadUserInfo(final long userid){
        new UserInfoAsyncTask().execute(userid);
    }

    private void displayUserInfo(User user) {
        Picasso.with(this).load(user.getImageUrl()).into(userImageView);
        nameTextView.setText(user.getName());
        nickTextView.setText(user.getNick());
        descriptionTextView.setText(user.getDescription());
        locationTextView.setText(user.getLocation());

        String followingCount = String.valueOf(user.getFollowingCount());
        followingTextView.setText(followingCount);
        String followersCount = String.valueOf(user.getFollowersCount());
        followersTextView.setText(followersCount);

        getSupportActionBar().setTitle(user.getName());
    }

    private void initRecyclerView(){
        tweetsRecyclerView = findViewById(R.id.tweets_recycler_view);
        ViewCompat.setNestedScrollingEnabled(tweetsRecyclerView, false);
        tweetsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        tweetsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        tweetAdapter = new TweetAdapter();
        tweetsRecyclerView.setAdapter(tweetAdapter);

    }

    private void loadTweets(final long userid){
        new TweetsAsyncTask().execute(userid);
    }
    @SuppressLint("StaticFieldLeak")
    private class UserInfoAsyncTask extends AsyncTask<Long,Integer,User>{

        @Override
        protected void onPreExecute() {
            setRefreshLayoutVisible(true);
        }

        @Override
        protected User doInBackground(Long... longs) {
            try {
                Long userid = longs[0];
                return httpClient.readUserInfo(userid);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(User user) {
            setRefreshLayoutVisible(false);
            if (user != null){
                displayUserInfo(user);
            }else {
                Toast.makeText(UserInfoActivity.this,R.string.loading_error_msg,Toast.LENGTH_LONG).show();
            }

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class TweetsAsyncTask extends AsyncTask<Long, Integer, Collection<Tweet>> {

        @Override
        protected void onPreExecute() {
            setRefreshLayoutVisible(true);
        }

        @Override
        protected Collection<Tweet> doInBackground(Long... longs) {
            try {
                long userid = longs[0];
                return httpClient.readTweets(userid);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return  null;
            }
        }

        @Override
        protected void onPostExecute(Collection<Tweet> tweets) {
            setRefreshLayoutVisible(false);
            if (tweets != null) {
                tweetAdapter.setItems(tweets);
            } else {
                Toast.makeText(UserInfoActivity.this, R.string.loading_error_msg, Toast.LENGTH_LONG).show();
            }

        }
    }
}
