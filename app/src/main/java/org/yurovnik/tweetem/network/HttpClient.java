package org.yurovnik.tweetem.network;

import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.oauth.OAuth1aHeaders;

import org.json.JSONException;
import org.yurovnik.tweetem.pojo.Tweet;
import org.yurovnik.tweetem.pojo.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;

public class HttpClient {
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String GET = "GET";
    private final static String EXTENDED_MODE = "&tweet_mode=extended";

    private final JsonParser  jsonParser;

    public HttpClient() {
        this.jsonParser = new JsonParser();
    }

    public Collection<Tweet> readTweets(long userid) throws IOException, JSONException{
        String requestUrl = "https://api.twitter.com/1.1/statuses/user_timeline.json?user_id=" + userid + EXTENDED_MODE;
        String response = getRespones(requestUrl);

        return jsonParser.getTweets(response);
    }

    public User readUserInfo(long userid) throws IOException, JSONException{
        String requestUrl = "https://api.twitter.com/1.1/users/show.json?user_id=" + userid;
        String response = getRespones(requestUrl);

        return jsonParser.getUser(response);
    }

    public Collection<User> readUsers(String query) throws IOException, JSONException{
        String requestUrl = "https://api.twitter.com/1.1/users/search.json?q=" + query;
        String encodeUrl = requestUrl.replaceAll(" ", "%20");

        String response = getRespones(encodeUrl);

        return jsonParser.getUsers(response);
    }


    private String getRespones(String requestUrl) throws IOException {

        //инициализируем подключение к серверу
        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        //добавление информации о авторизации
        String authHeader = getAuthHeader(requestUrl);
        connection.setRequestProperty(HEADER_AUTHORIZATION,authHeader);

        //подключение к серверу
        connection.connect();

        //считывание входного потока от сервера
        InputStream in;
        int status = connection.getResponseCode();

        if (status != HttpURLConnection.HTTP_OK){
            in = connection.getErrorStream();
        } else {
            in = connection.getInputStream();
        }
        return convertStreamToSting(in);
    }


    private String convertStreamToSting(InputStream stream) throws IOException{
        String line;
        BufferedReader reader = new BufferedReader( new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();

        while ((line = reader.readLine()) !=  null){
            builder.append(line).append("\n");
        }
        stream.close();

        return builder.toString();
    }

    private String getAuthHeader(String url){
        TwitterAuthConfig authConfig = TwitterCore.getInstance().getAuthConfig();
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        return new OAuth1aHeaders().getAuthorizationHeader(authConfig,
                session.getAuthToken(),null,GET,url,null);
    }
}
