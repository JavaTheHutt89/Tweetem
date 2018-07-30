package org.yurovnik.tweetem.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.yurovnik.tweetem.R;
import org.yurovnik.tweetem.adapter.UsersAdapter;
import org.yurovnik.tweetem.network.HttpClient;
import org.yurovnik.tweetem.pojo.User;

import java.io.IOException;
import java.util.Collection;

public class SearchUsersActivity extends AppCompatActivity{
    UsersAdapter usersAdapter;
    RecyclerView usersRecyclerView;
    private Toolbar toolbar;
    private EditText queryEditText;
    private Button searchButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    HttpClient httpClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);
        initRecyclerView();

        toolbar = findViewById(R.id.toolBar);
        queryEditText = toolbar.findViewById(R.id.search_edit_text);
        searchButton = toolbar.findViewById(R.id.search_button);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        searchButton.setOnClickListener(view -> searchUsers());

        queryEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH){
                searchUsers();
                return true;
            }
            return false;
        });

        swipeRefreshLayout.setOnRefreshListener(this::searchUsers);

        httpClient = new HttpClient();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initRecyclerView(){
        usersRecyclerView = findViewById(R.id.users_recycler_view);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        UsersAdapter.onUserClickListener onUserClickListener = user -> {
            Intent intent = new Intent(SearchUsersActivity.this, UserInfoActivity.class);
            intent.putExtra(UserInfoActivity.USER_ID, user.getId());
            startActivity(intent);
        };

        usersAdapter = new UsersAdapter(onUserClickListener);
        usersRecyclerView.setAdapter(usersAdapter);
    }

    private void searchUsers(){
        usersAdapter.clearItems();
        final String query = queryEditText.getText().toString();
        if (query.length()==0){
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this,R.string.not_enough_symbols_msg, Toast.LENGTH_LONG).show();
            return;
        }
        new SearchUsersAsyncTask().execute(query);
    }


    @SuppressLint("StaticFieldLeak")
    private class SearchUsersAsyncTask extends AsyncTask<String,Integer,Collection<User>>{

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Collection<User> doInBackground(String... strings) {
            try {
                String query = strings[0];
                return httpClient.readUsers(query);
            } catch (IOException | JSONException e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Collection<User> users) {
            swipeRefreshLayout.setRefreshing(false);
            if (users != null) {
                usersAdapter.clearItems();
                usersAdapter.setItems(users);
            } else {
                Toast.makeText(SearchUsersActivity.this, R.string.loading_error_msg, Toast.LENGTH_LONG).show();
            }
        }
    }

}
