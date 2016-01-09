package com.example.android.popularmoviesv1;

import org.json.JSONException;
import org.json.JSONObject;

public class Review {

    private String id;
    private String author;
    private String content;

    public Review(){
        // Empty Constructor
    }

    public String getId(){
        return id;
    }

    public String getAuthor(){
        return author;
    }

    public String getContent(){
        return content;
    }

    public Review(JSONObject review) throws JSONException {
        this.id = review.getString("id");
        this.author = review.getString("author");
        this.content = review.getString("content");
    }


}
