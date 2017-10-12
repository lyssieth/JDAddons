package com.raxixor.jdaddons.menu.buttonmenu;

import com.raxixor.jdaddons.command.EventWaiter;
import com.raxixor.jdaddons.menu.Menu;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

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
}
