package com.raxixor.jdaddons.menu.embedpaginator;

import com.raxixor.jdaddons.command.EventWaiter;
import com.raxixor.jdaddons.menu.Menu;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@SuppressWarnings("SimplifiableIfStatement")
public class EmbedPaginator extends Menu {
    
    private final BiFunction<Integer, Integer, String> text;
    private final List<MessageEmbed> embeds;
    private final int pages;
    private final Consumer<Message> finalAction;
    private final boolean waitOnSinglePage;
    private final boolean loop;
    
    public static final String LEFT = "\u25C0";
    public static final String STOP = "\u23F9";
    public static final String RIGHT = "\u25B6";
    
    protected EmbedPaginator(EventWaiter waiter, Set<User> users, Set<Role> roles, long timeout, TimeUnit unit,
                             BiFunction<Integer, Integer, String> text,
                             Consumer<Message> finalAction, List<MessageEmbed> embeds, boolean waitOnSinglePage, boolean loop) {
        super(waiter, users, roles, timeout, unit);
        this.text = text;
        this.embeds = embeds;
        this.pages = embeds.size();
        this.finalAction = finalAction;
        this.waitOnSinglePage = waitOnSinglePage;
        this.loop = loop;
    }
    
    @Override
    public void display(MessageChannel channel) {
        paginate(channel, 1);
    }
    
    @Override
    public void display(Message message) {
        paginate(message, 1);
    }
    
    public void paginate(Message message, int pageNum) {
        pageNum = (pageNum < 1) ? 1 : ((pageNum > pages) ? pages : pageNum);
        Message msg = render(pageNum);
        initialize(message.editMessage(msg), pageNum);
    }
    
    public void paginate(MessageChannel channel, int pageNum) {
        pageNum = (pageNum < 1) ? 1 : ((pageNum > pages) ? pages : pageNum);
        Message msg = render(pageNum);
        initialize(channel.sendMessage(msg), pageNum);
    }
    
    private void initialize(RestAction<Message> action, int pageNum) {
        action.queue(m -> {
            if (pages > 1) {
                m.addReaction(LEFT).queue();
                m.addReaction(STOP).queue();
                m.addReaction(RIGHT).queue(v ->
                        pagination(m, pageNum), t ->
                        pagination(m, pageNum));
            } else if (waitOnSinglePage)
                m.addReaction(STOP).queue(v ->
                        pagination(m, pageNum), t ->
                        pagination(m, pageNum));
            else
                finalAction.accept(m);
        });
    }
    
    private void pagination(Message message, int pageNum) {
        waiter.waitForEvent(MessageReactionAddEvent.class,
                (MessageReactionAddEvent event) -> {
            
            if (!event.getMessageId().equals(message.getId()))
                return false;
            return (LEFT.equals(event.getReactionEmote().getName())
                    || STOP.equals(event.getReactionEmote().getName())
                    || RIGHT.equals(event.getReactionEmote().getName()))
                    && isValidUser(event);
            
                }, event -> {
            
            int newPageNum = pageNum;
            switch (event.getReactionEmote().getName()) {
                case LEFT:
                    if (newPageNum > 1)
                        newPageNum--;
                    else if (loop)
                        newPageNum = pages;
                    break;
                case RIGHT:
                    if (newPageNum < pages)
                        newPageNum++;
                    else if (loop)
                        newPageNum = 1;
                    break;
                case STOP:
                    finalAction.accept(message);
                    return;
            }
            try {
                event.getReaction().removeReaction(event.getUser()).queue();
            } catch (PermissionException ignored) { }
            int n = newPageNum;
            message.editMessage(render(n)).queue(m -> pagination(m, n));
            
                }, timeout, unit, () -> finalAction.accept(message));
    }
    
    private Message render(int page) {
        MessageBuilder mb = new MessageBuilder();
        mb.setEmbed(embeds.get(page));
        mb.append(text.apply(page, pages));
        return mb.build();
    }
}
