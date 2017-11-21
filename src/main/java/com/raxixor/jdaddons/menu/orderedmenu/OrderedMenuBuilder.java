package com.raxixor.jdaddons.menu.orderedmenu;

import com.raxixor.jdaddons.menu.MenuBuilder;

import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class OrderedMenuBuilder extends MenuBuilder<OrderedMenuBuilder, OrderedMenu> {
    private Color color;
    private String text;
    private String description;
    private final List<String> choices = new LinkedList<>();
    private Consumer<Integer> action;
    private Runnable cancel = () -> {};
    private boolean useLetters = false;
    private boolean allowTypedInput = true;
    private boolean addCancel = false;
    
    @Override
    public OrderedMenu build() {
        if(waiter==null)
            throw new IllegalArgumentException("Must set an EventWaiter");
        if(choices.isEmpty())
            throw new IllegalArgumentException("Must have at least one choice");
        if(choices.size()>10)
            throw new IllegalArgumentException("Must have no more than ten choices");
        if(action==null)
            throw new IllegalArgumentException("Must provide an action consumer");
        if(text==null && description==null)
            throw new IllegalArgumentException("Either text or description must be set");
        return new OrderedMenu(waiter,users,roles,timeout,unit,color,text,description,choices,
                action,cancel,useLetters,allowTypedInput,addCancel);
    }
    
    @Override
    public OrderedMenuBuilder setColor(Color color) {
        this.color = color;
        return this;
    }
    
    public OrderedMenuBuilder useLetters() {
        this.useLetters = true;
        return this;
    }
    
    public OrderedMenuBuilder useNumbers() {
        this.useLetters = false;
        return this;
    }
    
    public OrderedMenuBuilder allowTextInput(boolean allow) {
        this.allowTypedInput = allow;
        return this;
    }
    
    public OrderedMenuBuilder useCancelButton(boolean use) {
        this.addCancel = use;
        return this;
    }
    
    public OrderedMenuBuilder setText(String text) {
        this.text = text;
        return this;
    }
    
    public OrderedMenuBuilder setDescription(String description) {
        this.description = description;
        return this;
    }
    
    public OrderedMenuBuilder setAction(Consumer<Integer> action) {
        this.action = action;
        return this;
    }
    
    public OrderedMenuBuilder setCancel(Runnable cancel) {
        this.cancel = cancel;
        return this;
    }
    
    public OrderedMenuBuilder addChoices(String... choices) {
        this.choices.addAll(Arrays.asList(choices));
        return this;
    }
    
    public OrderedMenuBuilder setChoices(String... choices) {
        this.choices.clear();
        this.choices.addAll(Arrays.asList(choices));
        return this;
    }
}
