package org.yurovnik.tweetem.pojo;

import com.google.gson.annotations.SerializedName;

public class Tweet {
    @SerializedName("id")
    private long id;

    @SerializedName("user")
    private User user;

    @SerializedName("created_at")
    private String creationDate;

    @SerializedName("full_text")
    private String text;

    @SerializedName("retweet_count")
    private long retweetCount;

    @SerializedName("favorite_count")
    private long favouriteCount;

    private String imageUrl;

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getText() {
        return text;
    }

    public long getRetweetCount() {
        return retweetCount;
    }

    public long getFavouriteCount() {
        return favouriteCount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tweet tweet = (Tweet) o;

        if (id != tweet.id) return false;
        if (retweetCount != tweet.retweetCount) return false;
        if (favouriteCount != tweet.favouriteCount) return false;
        if (!user.equals(tweet.user)) return false;
        if (!creationDate.equals(tweet.creationDate)) return false;
        if (!text.equals(tweet.text)) return false;
        return imageUrl != null ? imageUrl.equals(tweet.imageUrl) : tweet.imageUrl == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + user.hashCode();
        result = 31 * result + creationDate.hashCode();
        result = 31 * result + text.hashCode();
        result = 31 * result + (int) (retweetCount ^ (retweetCount >>> 32));
        result = 31 * result + (int) (favouriteCount ^ (favouriteCount >>> 32));
        result = 31 * result + (imageUrl != null ? imageUrl.hashCode() : 0);
        return result;
    }
}

