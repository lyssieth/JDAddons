package com.raxixor.jdaddons.entities.search;

import java.util.List;

public class Result {
    
    private SearchInformation searchInformation;
    private List<Item> items;
    
    public Result() { }
    
    public List<Item> getItems() {
        return items;
    }
    
    public void setItems(List<Item> items) {
        this.items = items;
    }
    
    public SearchInformation getSearchInformation() {
        return searchInformation;
    }
    
    public void setSearchInformation(SearchInformation searchInformation) {
        this.searchInformation = searchInformation;
    }
}
