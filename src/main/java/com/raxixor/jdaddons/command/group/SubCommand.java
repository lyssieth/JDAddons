package com.raxixor.jdaddons.command.group;

import com.raxixor.jdaddons.entities.EmoteLevel;
import com.raxixor.jdaddons.util.FormatUtil;
import com.sun.istack.internal.NotNull;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.requests.RestAction;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public abstract class SubCommand {
    
    public abstract void execute(Message trig, List<String> args);
    
    public SubCommandDescription getDescription() {
        return getClass().getAnnotation(SubCommandDescription.class);
    }
    
    public String getName() {
        return getDescription().name();
    }
    
    public String[] getTriggers() {
        return getDescription().triggers();
    }
    
    public int getArgs() {
        return getDescription().args();
    }
    
    public boolean respondToBots() {
        return getDescription().respondToBots();
    }
    
    public String getAttributeValueFromKey(String key) {
        if (!hasAttribute(key)) return null;
        return Arrays.stream(getDescription().attributes())
                .filter(ca -> ca.key().equals(key))
                .findFirst().get().value();
    }
    
    public boolean hasAttribute(String key) {
        return Arrays.stream(getDescription().attributes())
                .anyMatch(ca -> ca.key().equals(key));
    }
    
    protected RestAction<Message> reply(@NotNull Message trig, String msg) {
        trig.getChannel().sendTyping().queue();
        return trig.getChannel().sendMessage(msg);
    }
    
    protected RestAction<Message> reply(@NotNull Message trig, String msg, EmoteLevel e) {
        e = e != null ? e : EmoteLevel.INFO;
        return reply(trig, String.format("%s | %s", e, msg));
    }
    
    protected RestAction<Message> reply(@NotNull Message trig, MessageEmbed embed) {
        trig.getChannel().sendTyping().queue();
        return trig.getChannel().sendMessage(embed);
    }
    
    protected RestAction<Message> reply(@NotNull Message trig, Message msg) {
        return trig.getChannel().sendMessage(msg);
    }
    
    protected RestAction<Message> reply(@NotNull MessageChannel chan, String msg) {
        chan.sendTyping().queue();
        return chan.sendMessage(msg);
    }
    
    protected RestAction<Message> reply(@NotNull MessageChannel chan, String msg, EmoteLevel e) {
        e = e != null ? e : EmoteLevel.INFO;
        return reply(chan, String.format("%s | %s", e, msg));
    }
    
    protected RestAction<Message> reply(@NotNull MessageChannel chan, MessageEmbed embed) {
        chan.sendTyping().queue();
        return chan.sendMessage(embed);
    }
    
    protected RestAction<Message> reply(@NotNull MessageChannel chan, Message msg) {
        return chan.sendMessage(msg);
    }
    
    protected EmbedBuilder getBaseEmbed(@NotNull Color color, String footer) {
        EmbedBuilder eb = new EmbedBuilder();
        
        return eb.setColor(color).setFooter(footer, null);
    }
    
    protected EmbedBuilder getBaseEmbed(@NotNull Color color, String footer, User author) {
        String eff = author.getEffectiveAvatarUrl();
        return getBaseEmbed(color, footer).setAuthor(FormatUtil.formatUser(author),
                eff, eff);
    }
    
    protected EmbedBuilder getBaseEmbed(@NotNull Color color, @SuppressWarnings("SameParameterValue") String footer, Member author) {
        String eff = author.getUser().getEffectiveAvatarUrl();
        return getBaseEmbed(color, footer).setAuthor(FormatUtil.formatMember(author),
                eff, eff);
    }
}
