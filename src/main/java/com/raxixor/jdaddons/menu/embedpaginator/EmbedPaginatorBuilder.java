package com.raxixor.jdaddons.menu.embedpaginator;

import com.raxixor.jdaddons.menu.MenuBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class EmbedPaginatorBuilder extends MenuBuilder<EmbedPaginatorBuilder, EmbedPaginator> {
    
    private BiFunction<Integer, Integer, String> text = (page, pages) -> null;
    private Consumer<Message> finalAction = m -> m.delete().queue();
    private boolean waitOnSinglePage = false;
    private boolean loop = false;
    
    private final List<MessageEmbed> embeds = new LinkedList<>();
    
    @Override
    public EmbedPaginator build() {
        if (waiter == null)
            throw new IllegalArgumentException("Must set an EventWaiter.");
        if (embeds.isEmpty())
            throw new IllegalArgumentException("Must include at least one item to paginate.");
        
        return new EmbedPaginator(waiter, users, roles, timeout, unit, text, finalAction, embeds, waitOnSinglePage, loop);
    }
    
    @Override
    @Deprecated
    public EmbedPaginatorBuilder setColor(Color color) {
        return this;
    }
    
    public EmbedPaginatorBuilder setText(String text) {
        this.text = (i0, i1) -> text;
        return this;
    }
    
    public EmbedPaginatorBuilder setText(BiFunction<Integer, Integer, String> text) {
        this.text = text;
        return this;
    }
    
    public EmbedPaginatorBuilder setFinalAction(Consumer<Message> finalAction) {
        this.finalAction = finalAction;
        return this;
    }
    
    public EmbedPaginatorBuilder waitOnSinglePage(boolean wait) {
        this.waitOnSinglePage = wait;
        return this;
    }
    
    public EmbedPaginatorBuilder loop(boolean loop) {
        this.loop = loop;
        return this;
    }
    
    public EmbedPaginatorBuilder clearItems() {
        embeds.clear();
        return this;
    }
    
    public EmbedPaginatorBuilder addItems(MessageEmbed... emb) {
        embeds.addAll(Arrays.asList(emb));
        return this;
    }
    
    public EmbedPaginatorBuilder setItems(MessageEmbed... emb) {
        embeds.clear();
        embeds.addAll(Arrays.asList(emb));
        return this;
    }
}
