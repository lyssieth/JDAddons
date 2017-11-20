package com.raxixor.jdaddons.entities.search;

import com.google.gson.*;

public class Meta {
    
    private JsonObject json;
    private String jsonString;
    private Queries queries;
    
    public Meta(String json) {
        setJsonString(json);
        setQueries();
    }
    
    private JsonObject getJson() {
        return json;
    }
    
    private String getJsonString() {
        return jsonString;
    }
    
    private void setJsonString(String jsonString) {
        this.jsonString = jsonString;
        
        this.json = new JsonParser().parse(jsonString).getAsJsonObject();
    }
    
    private void setQueries() {
        this.queries = new Gson()
                .fromJson(getJson().get("queries"), Queries.class);
    }
    
    public Queries getQueries() {
        return queries;
    }
}
