package com.raxixor.jdaddons.command;

import com.raxixor.jdaddons.entities.EmoteLevel;
import com.raxixor.jdaddons.util.FormatUtil;
import com.sun.istack.internal.NotNull;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.awt.*;
import java.util.Arrays;

public interface Command {
    
    void execute(Message trig, String s);
    
    default CommandDescription getDescription() {
        return getClass()
                .getAnnotation(CommandDescription.class);
    }
    
    default String getName() {
        return getDescription().name();
    }
    
    default SimpleLog getLog() {
        return SimpleLog.getLog(getClass().getSimpleName());
    }
    
    default String getAttributeValueFromKey(String key) {
        if (!hasAttribute(key)) return null;
        return Arrays.stream(getDescription().attributes())
                .filter(ca -> ca.key().equals(key)).findFirst().get().value();
    }
    
    default boolean hasAttribute(String key) {
        return Arrays.stream(getDescription().attributes())
               .anyMatch(ca -> ca.key().equals(key));
    }
    
    default void reply(@NotNull Message trig, String msg) {
        trig.getChannel().sendTyping().queue(v ->
                trig.getChannel().sendMessage(msg).queue());
    }
    
    default void reply(@NotNull Message trig, String msg, EmoteLevel e) {
        e = e != null ? e : EmoteLevel.INFO;
        reply(trig, String.format("%s | %s", e, msg));
    }
    
    default void reply(@NotNull Message trig, MessageEmbed embed) {
        trig.getChannel().sendTyping().queue(v ->
                trig.getChannel().sendMessage(embed).queue());
    }
    
    default void reply(@NotNull MessageChannel chan, String msg) {
        chan.sendTyping().queue(v -> chan.sendMessage(msg).queue());
    }
    
    default void reply(@NotNull MessageChannel chan, String msg, EmoteLevel e) {
        e = e != null ? e : EmoteLevel.INFO;
        reply(chan, String.format("%s | %s", e, msg));
    }
    
    default void reply(@NotNull MessageChannel chan, MessageEmbed embed) {
        chan.sendTyping().queue(v -> chan.sendMessage(embed).queue());
    }
    
    default Message replyReturn(@NotNull Message trig, String msg)
            throws RateLimitedException {
        trig.getChannel().sendTyping().queue();
        return trig.getChannel().sendMessage(msg).complete(true);
    }
    
    default Message replyReturn(@NotNull Message trig, String msg, EmoteLevel e)
            throws RateLimitedException {
        e = e != null ? e : EmoteLevel.INFO;
        return replyReturn(trig, String.format("%s | %s", e, msg));
    }
    
    default Message replyReturn(@NotNull Message trig, MessageEmbed embed)
            throws RateLimitedException {
        trig.getChannel().sendTyping().queue();
        return trig.getChannel().sendMessage(embed).complete(true);
    }
    
    default Message replyReturn(@NotNull MessageChannel chan, String msg)
            throws RateLimitedException {
        chan.sendTyping().queue();
        return chan.sendMessage(msg).complete(true);
    }
    
    default Message replyReturn(@NotNull MessageChannel chan, String msg, EmoteLevel e)
            throws RateLimitedException {
        e = e != null ? e : EmoteLevel.INFO;
        return replyReturn(chan, String.format("%s | %s", e, msg));
    }
    
    default Message replyReturn(@NotNull MessageChannel chan, MessageEmbed embed)
            throws RateLimitedException {
        chan.sendTyping().queue();
        return chan.sendMessage(embed).complete(true);
    }
    
    default EmbedBuilder getBaseEmbed(@NotNull Color color, String footer) {
        EmbedBuilder eb = new EmbedBuilder();
        
        return eb.setColor(color).setFooter(footer, null);
    }
    
    default EmbedBuilder getBaseEmbed(@NotNull Color color, String footer,
                                      User author) {
        String eff = author.getEffectiveAvatarUrl();
        return getBaseEmbed(color, footer).setAuthor(FormatUtil.formatUser(author),
                eff, eff);
    }
    
    default EmbedBuilder getBaseEmbed(@NotNull Color color, String footer,
                                      Member author) {
        String eff = author.getUser().getEffectiveAvatarUrl();
        return getBaseEmbed(color, footer).setAuthor(FormatUtil.formatMember(author),
                eff, eff);
    }
}
