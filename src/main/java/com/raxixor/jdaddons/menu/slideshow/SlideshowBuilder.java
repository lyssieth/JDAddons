package com.raxixor.jdaddons.menu.slideshow;

import com.raxixor.jdaddons.menu.MenuBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class SlideshowBuilder extends MenuBuilder<SlideshowBuilder, Slideshow> {
    
    private BiFunction<Integer, Integer, Color> color = (page, pages) -> null;
    private BiFunction<Integer, Integer, String> text = (page, pages) -> null;
    private BiFunction<Integer, Integer, String> desc = (page, pages) -> null;
    private Consumer<Message> finalAction = m -> m.delete().queue();
    private boolean showPageNumbers = true;
    private boolean waitOnSinglePage = false;
    
    private final List<String> strings = new LinkedList<>();
    
    @Override
    public Slideshow build() {
        if (waiter == null)
            throw new IllegalArgumentException("Must set an EventWaiter.");
        if (strings.isEmpty())
            throw new IllegalArgumentException("Must include at least one item to paginate.");
        return new Slideshow(waiter, users, roles, timeout, unit, color, text, desc, finalAction, showPageNumbers,
                strings, waitOnSinglePage);
    }
    
    @Override
    public SlideshowBuilder setColor(Color color) {
        this.color = (i0, i1) -> color;
        return this;
    }
    
    public SlideshowBuilder setColor(BiFunction<Integer,Integer,Color> colorBiFunction) {
        this.color = colorBiFunction;
        return this;
    }

    public SlideshowBuilder setText(String text) {
        this.text = (i0, i1) -> text;
        return this;
    }

    public SlideshowBuilder setText(BiFunction<Integer,Integer,String> textBiFunction) {
        this.text = textBiFunction;
        return this;
    }
    
    public SlideshowBuilder setDescription(String description) {
        this.desc = (i0, i1) -> description;
        return this;
    }

    public SlideshowBuilder setDescription(BiFunction<Integer,Integer,String> descriptionBiFunction) {
        this.desc = descriptionBiFunction;
        return this;
    }

    public SlideshowBuilder setFinalAction(Consumer<Message> finalAction) {
        this.finalAction = finalAction;
        return this;
    }

    public SlideshowBuilder showPageNumbers(boolean show) {
        this.showPageNumbers = show;
        return this;
    }

    public SlideshowBuilder waitOnSinglePage(boolean wait) {
        this.waitOnSinglePage = wait;
        return this;
    }

    public SlideshowBuilder addItems(String... items) {
        strings.addAll(Arrays.asList(items));
        return this;
    }
    
    public SlideshowBuilder setUrls(String... items) {
        strings.clear();
        strings.addAll(Arrays.asList(items));
        return this;
    }
}
