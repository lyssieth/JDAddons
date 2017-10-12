package com.raxixor.jdaddons.menu.buttonmenu;

import com.raxixor.jdaddons.command.EventWaiter;
import com.raxixor.jdaddons.menu.Menu;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.requests.RestAction;

import java.awt.Color;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ButtonMenu extends Menu {
    private final Color color;
    private final String text;
    private final String description;
    private final List<String> choices;
    private final Consumer<ReactionEmote> action;
    private final Runnable cancel;
    
    protected ButtonMenu(EventWaiter waiter, Set<User> users, Set<Role> roles,
                         long timeout, TimeUnit unit, Color color, String text,
                         String description, List<String> choices,
                         Consumer<ReactionEmote> action, Runnable cancel) {
        super(waiter, users, roles, timeout, unit);
        this.color = color;
        this.text = text;
        this.description = description;
        this.choices = choices;
        this.action = action;
        this.cancel = cancel;
    }
    
    @Override
    public void display(MessageChannel channel) {
        initialize(channel.sendMessage(getMessage()));
    }
    
    @Override
    public void display(Message message) {
        initialize(message.editMessage(getMessage()));
    }
    
    private void initialize(RestAction<Message> ra) {
        ra.queue(m -> {
            for (int i = 0; i < choices.size(); i++) {
                Emote emote;
                try {
                    emote = m.getJDA().getEmoteById(choices.get(i));
                } catch (Exception e) {
                    emote = null;
                }
                RestAction<Void> r = emote == null ?
                        m.addReaction(choices.get(i)) :
                        m.addReaction(emote);
                if (i + 1 < choices.size())
                    r.queue();
                else
                    r.queue(v -> {
                        waiter.waitForEvent(MessageReactionAddEvent.class, event -> {
                            if (!event.getMessageId().equals(m.getId()))
                                return false;
                            String re = event.getReaction().getEmote().isEmote()
                                    ? event.getReaction().getEmote().getId()
                                    : event.getReaction().getEmote().getName();
                            if (!choices.contains(re))
                                return false;
                            return isValidUser(event);
                        }, (MessageReactionAddEvent event ) -> {
                            m.delete().queue();
                            action.accept(event.getReaction().getEmote());
                        }, timeout, unit, cancel);
                    });
            }
        });
    }
    
    private Message getMessage() {
        MessageBuilder m = new MessageBuilder();
        if (text != null)
            m.append(text);
        if (description != null)
            m.setEmbed(new EmbedBuilder().setColor(color).setDescription(description).build());
        return m.build();
    }
}
