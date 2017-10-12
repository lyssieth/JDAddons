package com.raxixor.jdaddons.menu.pagination;

import com.raxixor.jdaddons.command.EventWaiter;
import com.raxixor.jdaddons.menu.Menu;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.requests.RestAction;

import java.awt.Color;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class Paginator extends Menu {
    
    private final BiFunction<Integer, Integer, Color> color;
    private final BiFunction<Integer, Integer, String> text;
    private final int columns;
    private final int itemsPerPage;
    private final boolean showPageNumbers;
    private final boolean numberItems;
    private final List<String> strings;
    private final int pages;
    private final Consumer<Message> finalAction;
    private final boolean waitOnSinglePage;
    
    public static final String LEFT = "\u25C0";
    public static final String STOP = "\u23F9";
    public static final String RIGHT = "\u25B6";
    
    protected Paginator(EventWaiter waiter, Set<User> users, Set<Role> roles, long timeout, TimeUnit unit,
                        BiFunction<Integer, Integer, Color> color, BiFunction<Integer, Integer, String> text,
                        Consumer<Message> finalAction, int columns, int itemsPerPage, boolean showPageNumbers,
                        boolean numberItems, List<String> items, boolean waitOnSinglePage) {
        super(waiter, users, roles, timeout, unit);
        this.color = color;
        this.text = text;
        this.columns = columns;
        this.itemsPerPage = itemsPerPage;
        this.showPageNumbers = showPageNumbers;
        this.numberItems = numberItems;
        this.strings = items;
        this.pages = (int) Math.ceil((double) strings.size() / itemsPerPage);
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
        pageNum = (pageNum < 1) ? ((pageNum > pages) ? pages : 1) : 1;
        Message msg = renderPage(pageNum);
        initialize(channel.sendMessage(msg), pageNum);
    }
    
    public void paginate(Message message, int pageNum) {
        pageNum = (pageNum < 1) ? ((pageNum > pages) ? pages : 1) : 1;
        Message msg = renderPage(pageNum);
        initialize(message.editMessage(msg), pageNum);
    }
    
    private void initialize(RestAction<Message> action, int pageNum) {
        action.queue(m -> {
            if (pages > 1) {
                m.addReaction(LEFT).queue();
                m.addReaction(STOP).queue();
                m.addReaction(RIGHT).queue(v -> pagination(m ,pageNum), t -> pagination(m, pageNum));
            } else if (waitOnSinglePage)
                m.addReaction(STOP).queue(v -> pagination(m, pageNum), t -> pagination(m, pageNum));
            else
                finalAction.accept(m);
        });
    }
    
    private void pagination(Message message, int pageNum) {
        waiter.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent event) -> {
            if (!event.getMessageId().equals(message.getId()))
                return false;
            return (LEFT.equals(event.getReaction().getEmote().getName())
                    || STOP.equals(event.getReaction().getEmote().getName())
                    || RIGHT.equals(event.getReaction().getEmote().getName())) && isValidUser(event);
        }, event -> {
            int newPageNum = pageNum;
            switch (event.getReaction().getEmote().getName()) {
                case LEFT:  if (newPageNum > 1) newPageNum--; break;
                case RIGHT: if (newPageNum < pages) newPageNum++; break;
                case STOP: finalAction.accept(message); return;
            }
            try { event.getReaction().removeReaction(event.getUser()).queue(); } catch(PermissionException ignored) {}
            int n = newPageNum;
            message.editMessage(renderPage(newPageNum)).queue(m ->{
                pagination(m, n);
            });
        }, timeout, unit, () -> finalAction.accept(message));
    }
    
    private Message renderPage(int pageNum) {
        MessageBuilder mbuilder = new MessageBuilder();
        EmbedBuilder ebuilder = new EmbedBuilder();
        int start = (pageNum - 1) * itemsPerPage;
        int end = strings.size() < pageNum * itemsPerPage ? strings.size() : pageNum * itemsPerPage;
        switch (columns) {
            case 1:
                StringBuilder sbuilder = new StringBuilder();
                for (int i = start; i < end; i++)
                    sbuilder.append("\n").append(numberItems ? "`" + (i + 1) + ".` " : "").append(strings.get(i));
                ebuilder.setDescription(sbuilder.toString());
                break;
            default:
                int per = (int) Math.ceil((double) (end - start) / columns);
                for (int k = 0; k < columns; k++) {
                    StringBuilder strbuilder = new StringBuilder();
                    for (int i = start + k * per; i < end && i < start + (k + 1) * per; i++)
                        strbuilder.append("\n").append(numberItems ? "`" + (i + 1) + ".` " : "").append(strings.get(i));
                    ebuilder.addField("", strbuilder.toString(), true);
                }
        }
        
        ebuilder.setColor(color.apply(pageNum, pages));
        if (showPageNumbers)
            ebuilder.setFooter("Page " + pageNum + "/" + pages, null);
        mbuilder.setEmbed(ebuilder.build());
        if (text != null)
            mbuilder.append(text.apply(pageNum, pages));
        return mbuilder.build();
    }
}
