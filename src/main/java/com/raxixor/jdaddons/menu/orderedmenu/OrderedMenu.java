package com.raxixor.jdaddons.menu.orderedmenu;

import com.raxixor.jdaddons.command.EventWaiter;
import com.raxixor.jdaddons.menu.Menu;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class OrderedMenu extends Menu {
    private final Color color;
    private final String text;
    private final String description;
    private final List<String> choices;
    private final Consumer<Integer> action;
    private final Runnable cancel;
    private final boolean useLetters;
    private final boolean allowTypedInput;
    private final boolean useCancel;
    
    public final static String[] NUMBERS = new String[]{"1\u20E3","2\u20E3","3\u20E3",
            "4\u20E3","5\u20E3","6\u20E3","7\u20E3","8\u20E3","9\u20E3", "\uD83D\uDD1F"};
    public final static String[] LETTERS = new String[]{"\uD83C\uDDE6","\uD83C\uDDE7","\uD83C\uDDE8",
            "\uD83C\uDDE9","\uD83C\uDDEA","\uD83C\uDDEB","\uD83C\uDDEC","\uD83C\uDDED","\uD83C\uDDEE","\uD83C\uDDEF"};
    public final static String CANCEL = "\u274C";
    
    protected OrderedMenu(EventWaiter waiter, Set<User> users, Set<Role> roles, long timeout, TimeUnit unit,
                          Color color, String text, String description, List<String> choices, Consumer<Integer> action,
                          Runnable cancel, boolean useLetters, boolean allowTypedInput, boolean useCancel) {
        super(waiter, users, roles, timeout, unit);
        this.color = color;
        this.text = text;
        this.description = description;
        this.choices = choices;
        this.action = action;
        this.cancel = cancel;
        this.useLetters = useLetters;
        this.allowTypedInput = allowTypedInput;
        this.useCancel = useCancel;
    }
    
    @Override
    public void display(MessageChannel channel) {
        if (channel.getType() == ChannelType.TEXT && !allowTypedInput && !PermissionUtil.checkPermission(
                (TextChannel) channel, ((TextChannel)channel).getGuild().getSelfMember(),
                Permission.MESSAGE_ADD_REACTION))
            throw new PermissionException("Must be able to add reactions if not allowing typed input!");
        initialize(channel.sendMessage(getMessage()));
    }
    
    @Override
    public void display(Message message) {
        if (message.getChannelType() == ChannelType.TEXT && !allowTypedInput && !PermissionUtil.checkPermission(
                message.getTextChannel(), message.getGuild().getSelfMember(), Permission.MESSAGE_ADD_REACTION))
            throw new PermissionException("Must be able to add reactions if not allowing typed input!");
        initialize(message.editMessage(getMessage()));
    }
    
    private void initialize(RestAction<Message> ra) {
        ra.queue(m -> {
            try {
                for (int i = 1; i <= choices.size(); i++) {
                    if (i < choices.size())
                        m.addReaction(getEmoji(i)).queue();
                    else {
                        RestAction<Void> re = m.addReaction(getEmoji(i));
                        if (useCancel) {
                            re.queue();
                            re = m.addReaction(CANCEL);
                        }
                        re.queue(v -> {
                            if (allowTypedInput)
                                waitGeneric(m);
                            else
                                waitReactionOnly(m);
                        });
                    }
                }
            } catch (PermissionException ex) {
                if (allowTypedInput)
                    waitGeneric(m);
                else
                    waitReactionOnly(m);
            }
        });
    }
    
    private void waitGeneric(Message m) {
        waiter.waitForEvent(Event.class, e -> {
            if (e instanceof MessageReactionAddEvent)
                return isValidReaction(m, (MessageReactionAddEvent) e);
            if (e instanceof MessageReceivedEvent)
                return isValidMessage(m, (MessageReceivedEvent) e);
            return false;
        }, e -> {
            m.delete().queue();
            if (e instanceof MessageReactionAddEvent) {
                MessageReactionAddEvent event = (MessageReactionAddEvent) e;
                if (event.getReactionEmote().getName().equals(CANCEL))
                    cancel.run();
                else
                    action.accept(getNumber(event.getReactionEmote().getName()));
            } else if (e instanceof MessageReceivedEvent) {
                MessageReceivedEvent event = (MessageReceivedEvent) e;
                int num = getMessageNumber(event.getMessage().getContentRaw());
                if (num < 0 || num > choices.size())
                    cancel.run();
                else
                    action.accept(num);
            }
        }, timeout, unit, cancel);
    }
    
    private void waitReactionOnly(Message m) {
        waiter.waitForEvent(MessageReactionAddEvent.class, e -> isValidReaction(m, e), e -> {
            m.delete().queue();
            if (e.getReactionEmote().getName().equals(CANCEL))
                cancel.run();
            else
                action.accept(getNumber(e.getReactionEmote().getName()));
        }, timeout, unit, cancel);
    }
    
    private Message getMessage() {
        MessageBuilder m = new MessageBuilder();
        if (text != null) m.append(text);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < choices.size(); i++)
            sb.append("\n").append(getEmoji(i + 1)).append(" ").append(choices.get(i));
        m.setEmbed(new EmbedBuilder().setColor(color)
                .setDescription(description == null ? sb.toString() : description + sb.toString()).build());
        return m.build();
    }
    
    private boolean isValidReaction(Message m, MessageReactionAddEvent e) {
        return e.getChannel().equals(m.getChannel()) && isValidUser(e);
    }
    
    private boolean isValidMessage(Message m, MessageReceivedEvent e) {
        return e.getChannel().equals(m.getChannel()) && isValidUser(e);
    }
    
    private String getEmoji(int number) {
        if (useLetters)
            return LETTERS[number-1];
        else
            return NUMBERS[number-1];
    }
    
    private int getNumber(String emoji) {
        String[] array = useLetters ? LETTERS : NUMBERS;
        for (int i = 0; i < array.length; i++)
            if (array[i].equals(emoji))
                return i+1;
        return -1;
    }
    
    private int getMessageNumber(String message) {
        if (useLetters)
            //noinspection SpellCheckingInspection
            return message.length() == 1 ? " abcdefghij".indexOf(message.toLowerCase()) : -1;
        else {
            if (message.length() == 1)
                return " 123456789".indexOf(message);
            return message.equals(10 + "") ? 10 : -1;
        }
    }
}
