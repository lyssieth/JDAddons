package com.raxixor.jdaddons.command.single;

import com.raxixor.jdaddons.entities.EmoteLevel;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.requests.RestAction;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.List;

public abstract class Command {
    
    /**
     * Executes a command with the given trigger and arguments.
     *
     * @param trig The message that triggered the command
     * @param args The arguments provided
     */
    public abstract void execute(Message trig, List<String> args);
    
    public CommandDescription getDescription() {
        return getClass().getAnnotation(CommandDescription.class);
    }
    
    public String getName() {
        return getDescription().name();
    }
    
    public String getAttributeValueFromKey(String key) {
        if (!hasAttribute(key)) return null;
        CommandAttribute attr = Arrays.stream(getDescription().attributes())
                .filter(ca -> ca.key().equals(key))
                .findFirst().orElse(null);
        return attr != null ? attr.value() : null;
    }
    
    public boolean hasAttribute(String key) {
        return Arrays.stream(getDescription().attributes())
                .anyMatch(ca -> ca.key().equals(key));
    }
    
    protected RestAction<Message> reply(@NonNull Message trig, String msg) {
        return trig.getChannel().sendMessage(msg);
    }
    
    protected RestAction<Message> reply(@NonNull Message trig, String msg, EmoteLevel e) {
        e = (e != null) ? e : EmoteLevel.INFO;
        return reply(trig, String.format("%s | %s", e, msg));
    }
    
    protected RestAction<Message> reply(@NonNull Message trig, MessageEmbed embed) {
        return trig.getChannel().sendMessage(embed);
    }
    
    protected RestAction<Message> reply(@NonNull Message trig, Message msg) {
        return trig.getChannel().sendMessage(msg);
    }
    
    protected RestAction<Message> reply(@NonNull MessageChannel chan, String msg) {
        return chan.sendMessage(msg);
    }
    
    protected RestAction<Message> reply(@NonNull MessageChannel chan, String msg, EmoteLevel e) {
        e = (e != null) ? e : EmoteLevel.INFO;
        return reply(chan, String.format("%s | %s", e, msg));
    }
    
    protected RestAction<Message> reply(@NonNull MessageChannel chan, MessageEmbed embed) {
        return chan.sendMessage(embed);
    }
    
    protected RestAction<Message> reply(@NonNull MessageChannel chan, Message msg) {
        return chan.sendMessage(msg);
    }
}
