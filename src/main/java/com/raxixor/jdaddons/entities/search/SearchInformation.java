package com.raxixor.jdaddons.entities.search;

public class SearchInformation {
    
    private float searchTime;
    private String totalResults;
    
    public SearchInformation() { }
    
    public float getSearchTime() {
        return searchTime;
    }
    
    public void setSearchTime(float searchTime) {
        this.searchTime = searchTime;
    }
    
    public String getTotalResults() {
        return totalResults;
    }
    
    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }
}
