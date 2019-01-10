package com.raxixor.jdaddons.menu.buttonmenu;

import com.raxixor.jdaddons.menu.MenuBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;

import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class ButtonMenuBuilder extends MenuBuilder<ButtonMenuBuilder, ButtonMenu> {
    
    private Color color;
    private String text;
    private String description;
    private final List<String> choices = new LinkedList<>();
    private Consumer<ReactionEmote> action;
    private Runnable cancel = () -> {};
    
    @Override
    public ButtonMenu build() {
        if (waiter == null)
            throw new IllegalArgumentException("Must set an EventWaiter.");
        if (choices.isEmpty())
            throw new IllegalArgumentException("Must have at least one choice.");
        if (action == null)
            throw new IllegalArgumentException("Must provide an action consumer.");
        if (text == null && description == null)
            throw new IllegalArgumentException("Either text or description must be set.");
        return new ButtonMenu(waiter, users, roles, timeout, unit, color, text, description, choices, action, cancel);
    }
    
    @Override
    public ButtonMenuBuilder setColor(Color color) {
        this.color = color;
        return this;
    }
    
    public ButtonMenuBuilder setText(String text) {
        this.text = text;
        return this;
    }
    
    public ButtonMenuBuilder setDescription(String description) {
        this.description = description;
        return this;
    }
    
    public ButtonMenuBuilder setAction(Consumer<ReactionEmote> action) {
        this.action = action;
        return this;
    }
    
    public ButtonMenuBuilder setCancel(Runnable cancel) {
        this.cancel = cancel;
        return this;
    }
    
    public ButtonMenuBuilder addChoices(String... emojis) {
        this.choices.addAll(Arrays.asList(emojis));
        return this;
    }
    
    public ButtonMenuBuilder addChoices(Emote... emojis) {
        Arrays.stream(emojis).map(ISnowflake::getId).forEach(this.choices::add);
        return this;
    }
    
    public ButtonMenuBuilder setChoices(String... emojis) {
        this.choices.clear();
        this.choices.addAll(Arrays.asList(emojis));
        return this;
    }
    
    public ButtonMenuBuilder setChoices(Emote... emojis) {
        this.choices.clear();
        Arrays.stream(emojis).map(ISnowflake::getId).forEach(this.choices::add);
        return this;
    }
}
