package com.raxixor.jdaddons.menu.slideshow;

import com.raxixor.jdaddons.command.EventWaiter;
import com.raxixor.jdaddons.menu.Menu;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.requests.RestAction;

import java.awt.Color;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class Slideshow extends Menu {
    
    private final BiFunction<Integer, Integer, Color> color;
    private final BiFunction<Integer, Integer, String> text;
    private final BiFunction<Integer, Integer, String> description;
    private final boolean showPageNumbers;
    private final List<String> urls;
    private final Consumer<Message> finalAction;
    private final boolean waitOnSinglePage;
    
    public static final String LEFT = "\u25C0";
    public static final String STOP = "\u23F9";
    public static final String RIGHT = "\u25B6";
    
    protected Slideshow(EventWaiter waiter, Set<User> users, Set<Role> roles, long timeout, TimeUnit unit,
                        BiFunction<Integer, Integer, Color> color, BiFunction<Integer, Integer, String> text,
                        BiFunction<Integer, Integer, String> description, Consumer<Message> finalAction,
                        boolean showPageNumbers, List<String> items, boolean waitOnSinglePage) {
        super(waiter, users, roles, timeout, unit);
        this.color = color;
        this.text = text;
        this.description = description;
        this.showPageNumbers = showPageNumbers;
        this.urls = items;
        this.finalAction = finalAction;
        this.waitOnSinglePage = waitOnSinglePage;
    }
    
    @Override
    public void display(MessageChannel channel) {
        paginate(channel, 1);
    }
    
    @Override
    public void display(Message message) {
        paginate(message, 1);
    }
    
    public void paginate(MessageChannel channel, int pageNum) {
        pageNum = (pageNum < 1) ? 1 : ((pageNum > urls.size()) ? urls.size() : pageNum);
        Message msg = renderPage(pageNum);
        initialize(channel.sendMessage(msg), pageNum);
    }
    
    public void paginate(Message message, int pageNum) {
        pageNum = (pageNum < 1) ? 1 : ((pageNum > urls.size()) ? urls.size() : pageNum);
        Message msg = renderPage(pageNum);
        initialize(message.editMessage(msg), pageNum);
    }
    
    private void initialize(RestAction<Message> action, int pageNum) {
        action.queue(m -> {
            if (urls.size() > 1) {
                m.addReaction(LEFT).queue();
                m.addReaction(STOP).queue();
                m.addReaction(RIGHT).queue(v -> pagination(m, pageNum), v -> pagination(m, pageNum));
            } else if (waitOnSinglePage)
                m.addReaction(STOP).queue(v -> pagination(m, pageNum), v -> pagination(m, pageNum));
            else
                finalAction.accept(m);
        });
    }
    
    private void pagination(Message message, int pageNum) {
        waiter.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent event) -> event.getMessageId().equals(message.getId()) &&
                (LEFT.equals(event.getReactionEmote().getName()) ||
                        STOP.equals(event.getReactionEmote().getName()) ||
                        RIGHT.equals(event.getReactionEmote().getName())) &&
                isValidUser(event),
                event -> {
            int newPageNum = pageNum;
            switch(event.getReactionEmote().getName()) {
                case LEFT: if (newPageNum > 1) newPageNum--; break;
                case RIGHT: if (newPageNum < urls.size()) newPageNum++; break;
                case STOP: finalAction.accept(message); return;
            }
            try { event.getReaction().removeReaction(event.getUser()).queue(); } catch (PermissionException ignored) { }
            int n = newPageNum;
            message.editMessage(renderPage(newPageNum)).queue(m -> pagination(m, n));
        }, timeout, unit, () -> finalAction.accept(message));
    }
    
    private Message renderPage(int pageNum) {
        MessageBuilder m = new MessageBuilder();
        EmbedBuilder e = new EmbedBuilder();
        e.setImage(urls.get(pageNum - 1));
        e.setColor(color.apply(pageNum, urls.size()));
        e.setDescription(description.apply(pageNum, urls.size()));
        if (showPageNumbers)
            e.setFooter("Image " + pageNum + "/" + urls.size(), null);
        m.setEmbed(e.build());
        if (text != null)
            m.append(text.apply(pageNum, urls.size()));
        return m.build();
    }
}
