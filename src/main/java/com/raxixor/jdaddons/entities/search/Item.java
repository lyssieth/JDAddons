package com.raxixor.jdaddons.entities.search;

public class Item {
    
    private String title;
    private String link;
    private String snippet;
    private String formattedUrl;
    
    public Item() { }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getLink() {
        return link;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    
    public String getSnippet() {
        return snippet;
    }
    
    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }
    
    public String getFormattedUrl() {
        return formattedUrl;
    }
    
    public void setFormattedUrl(String formattedUrl) {
        this.formattedUrl = formattedUrl;
    }
}
