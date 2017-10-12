package com.raxixor.jdaddons.entities;

public class Wildcard {
    
    private String template;
    
    public Wildcard(String template) {
        this.template = template;
    }
    
    public String replace(String orig, Object r) {
        return orig.replace(template, r.toString());
    }
    
    public String replaceFirst(String orig, Object r) {
        return orig.replaceFirst(template, r.toString());
    }
    
    public String replaceAll(String orig, Object r) {
        return orig.replaceAll(template, r.toString());
    }
    
    public void setTemplate(String template) {
        this.template = template;
    }
    
    public String getTemplate() {
        return this.template;
    }
    
    @Override
    public String toString() {
        return this.template;
    }
}
