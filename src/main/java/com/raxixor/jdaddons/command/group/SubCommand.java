package com.raxixor.jdaddons.command.group;

import com.raxixor.jdaddons.command.single.CommandAttribute;
import com.raxixor.jdaddons.entities.EmoteLevel;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.requests.restaction.MessageAction;

import java.util.Arrays;
import java.util.List;

public abstract class SubCommand {
    
    private CommandGroup group;
    
    /**
     * Executes a sub-command with the given trigger and arguments.
     *
     * @param trig The message that triggered the sub-command
     * @param args The arguments provided
     */
    public abstract void execute(Message trig, List<String> args);
    
    public SubCommandDescription getDescription() {
        return getClass().getAnnotation(SubCommandDescription.class);
    }
    
    public CommandGroup getGroup() {
        return group;
    }
    
    public void setGroup(CommandGroup group) {
        this.group = group;
    }
    
    public String getAttributeValueFromKey(String key) {
        if (!hasAttribute(key));
        CommandAttribute attr = Arrays.stream(getDescription().attributes())
                .filter(ca -> ca.key().equals(key))
                .findFirst().orElse(null);
        
        return attr != null ? attr.value() : null;
    }
    
    public boolean hasAttribute(String key) {
        return Arrays.stream(getDescription().attributes())
                .anyMatch(ca -> ca.key().equals(key));
    }
    
    protected MessageAction reply(Message trig, String msg) {
        return trig.getChannel().sendMessage(msg);
    }
    
    protected MessageAction reply(Message trig, String msg, EmoteLevel e) {
        e = e != null ? e : EmoteLevel.INFO;
        return reply(trig, String.format("%s | %s", e, msg));
    }
    
    protected MessageAction reply(Message trig, MessageEmbed embed) {
        return trig.getChannel().sendMessage(embed);
    }
    
    protected MessageAction reply(Message trig, Message msg) {
        return trig.getChannel().sendMessage(msg);
    }
    
    protected MessageAction reply(MessageChannel chan, String msg) {
        return chan.sendMessage(msg);
    }
    
    protected MessageAction reply(MessageChannel chan, String msg, EmoteLevel e) {
        e = e != null ? e : EmoteLevel.INFO;
        return reply(chan, String.format("%s | %s", e, msg));
    }
    
    protected MessageAction reply(MessageChannel chan, MessageEmbed embed) {
        return chan.sendMessage(embed);
    }
    
    protected MessageAction reply(MessageChannel chan, Message msg) {
        return chan.sendMessage(msg);
    }
}
