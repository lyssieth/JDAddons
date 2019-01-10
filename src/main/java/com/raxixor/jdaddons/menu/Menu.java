package com.raxixor.jdaddons.menu;

import com.raxixor.jdaddons.command.EventWaiter;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class Menu {
    protected final EventWaiter waiter;
    protected final Set<User> users;
    protected final Set<Role> roles;
    protected final long timeout;
    protected final TimeUnit unit;
    
    protected Menu(EventWaiter waiter, Set<User> users, Set<Role> roles, long timeout, TimeUnit unit) {
        this.waiter = waiter;
        this.users = users;
        this.roles = roles;
        this.timeout = timeout;
        this.unit = unit;
    }
    
    public abstract void display(MessageChannel channel);
    
    public abstract void display(Message message);
    
    protected boolean isValidUser(MessageReactionAddEvent event) {
        if (event.getUser().isBot())
            return false;
        if (users.isEmpty() && roles.isEmpty())
            return true;
        if (users.contains(event.getUser()))
            return true;
        if (!(event.getChannel() instanceof TextChannel))
            return false;
        Member m = ((TextChannel)event.getChannel()).getGuild().getMember(event.getUser());
        return m.getRoles().stream().anyMatch(roles::contains);
    }
    
    protected boolean isValidUser(MessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return false;
        if (users.isEmpty() && roles.isEmpty())
            return true;
        if (users.contains(event.getAuthor()))
            return true;
        if (!(event.getChannel() instanceof TextChannel))
            return false;
        return event.getMember().getRoles().stream().anyMatch(roles::contains);
    }
}
