package com.raxixor.jdaddons.menu.pagination;

import com.raxixor.jdaddons.menu.MenuBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class PaginatorBuilder extends MenuBuilder<PaginatorBuilder, Paginator> {
    
    private BiFunction<Integer, Integer, Color> color = (page, pages) -> null;
    private BiFunction<Integer, Integer, String> text = (page, pages) -> null;
    private Consumer<Message> finalAction = m -> m.delete().queue();
    private int columns = 1;
    private int itemsPerPage = 12;
    private boolean showPageNumbers = true;
    private boolean numberItems = false;
    private boolean waitOnSinglePage = false;
    
    private final List<String> strings = new LinkedList<>();
    
    @Override
    public Paginator build() {
        if (waiter == null)
            throw new IllegalArgumentException("Must set an EventWaiter.");
        if (strings.isEmpty())
            throw new IllegalArgumentException("Must include at least one item to paginate.");
        return new Paginator(waiter, users, roles, timeout, unit, color, text, finalAction, columns, itemsPerPage,
                showPageNumbers, numberItems, strings, waitOnSinglePage);
    }
    
    @Override
    public PaginatorBuilder setColor(Color color) {
        this.color = (i0, i1) -> color;
        return this;
    }
    
    public PaginatorBuilder setColor(BiFunction<Integer, Integer, Color> color) {
        this.color = color;
        return this;
    }
    
    public PaginatorBuilder setText(String text) {
        this.text = (i0, i1) -> text;
        return this;
    }
    
    public PaginatorBuilder setText(BiFunction<Integer, Integer, String> text) {
        this.text = text;
        return this;
    }
    
    public PaginatorBuilder setFinalAction(Consumer<Message> finalAction) {
        this.finalAction = finalAction;
        return this;
    }
    
    public PaginatorBuilder setColumns(int columns) {
        if (columns < 1 || columns > 3)
            throw new IllegalArgumentException("Only 1-3 columns are supported.");
        this.columns = columns;
        return this;
    }
    
    public PaginatorBuilder setItemsPerPage(int num) {
        if (num < 1)
            throw new IllegalArgumentException("There must be at least one item per page.");
        this.itemsPerPage = num;
        return this;
    }
    
    public PaginatorBuilder showPageNumbers(boolean show) {
        this.showPageNumbers = show;
        return this;
    }
    
    public PaginatorBuilder useNumberedItems(boolean number) {
        this.numberItems = number;
        return this;
    }
    
    public PaginatorBuilder waitOnSinglePage(boolean wait) {
        this.waitOnSinglePage = wait;
        return this;
    }
    
    public PaginatorBuilder clearItems() {
        strings.clear();
        return this;
    }
    
    public PaginatorBuilder addItems(String... items) {
        strings.addAll(Arrays.asList(items));
        return this;
    }
    
    public PaginatorBuilder setItems(String... items) {
        strings.clear();
        strings.addAll(Arrays.asList(items));
        return this;
    }
}
