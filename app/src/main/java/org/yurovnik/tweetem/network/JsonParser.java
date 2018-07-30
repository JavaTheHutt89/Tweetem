package org.yurovnik.tweetem.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.yurovnik.tweetem.pojo.Tweet;
import org.yurovnik.tweetem.pojo.User;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

public class JsonParser {

    private final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Tweet.class, new TweetDeserializer())
            .create();

    public Collection<User> getUsers(String response) {
        Type userCollectionType = new TypeToken<Collection<User>>(){}.getType();
        return GSON.fromJson(response, userCollectionType);
    }

    public Collection<Tweet> getTweets(String response){
        Type tweetsCollectionType = new TypeToken<Collection<Tweet>>(){}.getType();
        return GSON.fromJson(response, tweetsCollectionType);
    }

    private String getTweetImageUrl(JSONObject tweetJson) throws JSONException{
        JSONObject entities = tweetJson.getJSONObject("entities");
        JSONArray mediaArray = entities.has("media")? entities.getJSONArray("media"): null;
        JSONObject firstMedia = mediaArray != null? mediaArray.getJSONObject(0): null;

        return firstMedia != null? firstMedia.getString("media_url"): null;
    }

    public User getUser(String response){
        return GSON.fromJson(response, User.class);
    }

}

