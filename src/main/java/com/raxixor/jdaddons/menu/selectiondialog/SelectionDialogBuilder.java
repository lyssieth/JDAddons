package com.raxixor.jdaddons.menu.selectiondialog;

import com.raxixor.jdaddons.menu.MenuBuilder;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class SelectionDialogBuilder extends MenuBuilder<SelectionDialogBuilder, SelectionDialog> {
    
    private final List<String> choices = new LinkedList<>();
    private String leftEnd = "";
    private String rightEnd = "";
    private String defaultLeft = "";
    private String defaultRight = "";
    private Function<Integer, Color> color = i -> null;
    private boolean loop = true;
    private Function<Integer, String> text = i -> null;
    private Consumer<Integer> success;
    private Runnable cancel = () -> {};
    
    @Override
    public SelectionDialog build() {
        if (waiter == null)
            throw new IllegalArgumentException("Must set an EventWaiter.");
        if (choices.isEmpty())
            throw new IllegalArgumentException("Must have at least one choice.");
        if (success == null)
            throw new IllegalArgumentException("Must provide a selection consumer.");
        return new SelectionDialog(waiter, users, roles, timeout, unit, choices, leftEnd, rightEnd, defaultLeft,
                defaultRight, color, loop, success, cancel, text);
    }
    
    @Override
    public SelectionDialogBuilder setColor(Color color) {
        this.color = i -> color;
        return this;
    }
    
    public SelectionDialogBuilder setColor(Function<Integer, Color> color ) {
        this.color = color;
        return this;
    }
    
    public SelectionDialogBuilder setText(String text) {
        this.text = i -> text;
        return this;
    }
    
    public SelectionDialogBuilder setText(Function<Integer, String> text) {
        this.text = text;
        return this;
    }
    
    public SelectionDialogBuilder setSelectedEnds(String left, String right) {
        this.leftEnd = left;
        this.rightEnd = right;
        return this;
    }
    
    public SelectionDialogBuilder setDefaultEnds(String left, String right) {
        this.defaultLeft = left;
        this.defaultRight = right;
        return this;
    }
    
    public SelectionDialogBuilder useLooping(boolean loop) {
        this.loop = loop;
        return this;
    }
    
    public SelectionDialogBuilder setSelectionConsumer(Consumer<Integer> selection) {
        this.success = selection;
        return this;
    }
    
    public SelectionDialogBuilder setCanceledRunnable(Runnable cancel) {
        this.cancel = cancel;
        return this;
    }
    
    public SelectionDialogBuilder clearChoices() {
        this.choices.clear();
        return this;
    }
    
    public SelectionDialogBuilder setChoices(String... choices) {
        this.choices.clear();
        this.choices.addAll(Arrays.asList(choices));
        return this;
    }
    
    public SelectionDialogBuilder addChoices(String... choices) {
        this.choices.addAll(Arrays.asList(choices));
        return this;
    }
}
