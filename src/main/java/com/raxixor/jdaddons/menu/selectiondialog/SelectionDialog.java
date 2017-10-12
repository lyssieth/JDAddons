package com.raxixor.jdaddons.menu.selectiondialog;

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

import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public class SelectionDialog extends Menu {
    
    private final List<String> choices;
    private final String leftEnd, rightEnd;
    private final String defaultLeft, defaultRight;
    private final Function<Integer, Color> color;
    private final boolean loop;
    private final Function<Integer, String> text;
    private final Consumer<Integer> success;
    private final Runnable cancel;
    
    public static final String UP     = "\uD83D\uDD3C";
    public static final String DOWN   = "\uD83D\uDD3D";
    public static final String SELECT = "\u2705";
    public static final String CANCEL = "\u274E";
    
    protected SelectionDialog(EventWaiter waiter, Set<User> users, Set<Role> roles, long timeout, TimeUnit unit,
                              List<String> choices, String leftEnd, String rightEnd, String defaultLeft,
                              String defaultRight, Function<Integer, Color> color, boolean loop,
                              Consumer<Integer> success, Runnable cancel, Function<Integer, String> text) {
        super(waiter, users, roles, timeout, unit);
        this.choices = choices;
        this.leftEnd = leftEnd;
        this.rightEnd = rightEnd;
        this.defaultLeft = defaultLeft;
        this.defaultRight = defaultRight;
        this.color = color;
        this.loop = loop;
        this.success = success;
        this.cancel = cancel;
        this.text = text;
    }
    
    @Override
    public void display(MessageChannel chan) {
        showDialog(chan, 1);
    }
    
    @Override
    public void display(Message msg) {
        showDialog(msg, 1);
    }
    
    public void showDialog(MessageChannel channel, int selection) {
        selection = (selection < 1) ? 1 : ((selection > choices.size()) ? choices.size() : selection);
        Message msg = render(selection);
        initialize(channel.sendMessage(msg), selection);
    }
    
    public void showDialog(Message msg, int selection) {
        selection = (selection < 1) ? 1 : ((selection > choices.size()) ? choices.size() : selection);
        Message m = render(selection);
        initialize(msg.editMessage(m), selection);
    }
    
    private void initialize(RestAction<Message> action, int selection) {
        action.queue(m -> {
            if (choices.size() > 1) {
                m.addReaction(UP).queue();
                m.addReaction(SELECT).queue();
                m.addReaction(CANCEL).queue();
                m.addReaction(DOWN).queue(v -> selectionDialog(m, selection), v -> selectionDialog(m, selection));
            } else {
                m.addReaction(SELECT).queue();
                m.addReaction(CANCEL).queue(v -> selectionDialog(m, selection), v -> selectionDialog(m, selection));
            }
        });
    }
    
    private void selectionDialog(Message message, int selection) {
        waiter.waitForEvent(MessageReactionAddEvent.class, event -> {
            if (!event.getMessageId().equals(message.getId()))
                return false;
            if (!(UP.equals(event.getReaction().getEmote().getName())
                    || DOWN.equals(event.getReaction().getEmote().getName())
                    || CANCEL.equals(event.getReaction().getEmote().getName())
                    || SELECT.equals(event.getReaction().getEmote().getName())))
                return false;
            return isValidUser(event);
        }, event -> {
            int newSelection = selection;
            switch (event.getReaction().getEmote().getName()) {
                case UP:
                    if (newSelection > 1)
                        newSelection--;
                    else if (loop)
                        newSelection = choices.size();
                    break;
                case DOWN:
                    if (newSelection < choices.size())
                        newSelection++;
                    else if (loop)
                        newSelection = 1;
                    break;
                case CANCEL:
                    message.delete().queue();
                    cancel.run();
                    return;
                case SELECT:
                    message.delete().queue();
                    success.accept(selection);
                    return;
            }
            try { event.getReaction().removeReaction(event.getUser()).queue(); } catch (PermissionException ignored) { }
            int n = newSelection;
            message.editMessage(render(n)).queue(m ->
                    selectionDialog(m, n));
        }, timeout, unit, () -> {
            message.delete().queue(); cancel.run();
        });
    }
    
    private Message render(int selection) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < choices.size(); i++)
            if (i + 1 == selection)
                sb.append("\n").append(leftEnd).append(choices.get(i)).append(rightEnd);
            else
                sb.append("\n").append(defaultLeft).append(choices.get(i)).append(defaultRight);
        MessageBuilder mb = new MessageBuilder();
        String content = text.apply(selection);
        if (content != null)
            mb.append(content);
        return mb.setEmbed(new EmbedBuilder().setColor(color.apply(selection)).setDescription(sb.toString()).build())
                .build();
    }
}
