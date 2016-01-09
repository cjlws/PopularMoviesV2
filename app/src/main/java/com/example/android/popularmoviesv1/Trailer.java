package com.example.android.popularmoviesv1;

import org.json.JSONException;
import org.json.JSONObject;

public class Trailer {

    private String id;
    private String name;
    private String site;
    private String type;
    private String key;

    public Trailer(){
        // Empty Constructor
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public Trailer(JSONObject trailer) throws JSONException {
        this.id = trailer.getString("id");
        this.name = trailer.getString("name");
        this.site = trailer.getString("site");
        this.type = trailer.getString("type");
        this.key = trailer.getString("key");
    }
}
