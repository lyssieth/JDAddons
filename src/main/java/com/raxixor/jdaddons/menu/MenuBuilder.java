package com.raxixor.jdaddons.menu;

import com.raxixor.jdaddons.command.EventWaiter;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unchecked")
public abstract class MenuBuilder<T extends MenuBuilder<T, V>, V extends Menu> {
    protected EventWaiter waiter;
    protected final Set<User> users = new HashSet<>();
    protected final Set<Role> roles = new HashSet<>();
    protected long timeout = 1;
    protected TimeUnit unit = TimeUnit.MINUTES;
    
    public abstract V build();
    
    public abstract T setColor(Color color);
    
    public final T setEventWaiter(EventWaiter waiter) {
        this.waiter = waiter;
        return (T) this;
    }
    
    public final T addUsers(User... users) {
        this.users.addAll(Arrays.asList(users));
        return (T) this;
    }
    
    public final T setUsers(User... users) {
        this.users.clear();
        this.users.addAll(Arrays.asList(users));
        return (T) this;
    }
    
    public final T addRoles(Role... roles) {
        this.roles.addAll(Arrays.asList(roles));
        return (T) this;
    }
    
    public final T setRoles(Role... roles) {
        this.roles.clear();
        this.roles.addAll(Arrays.asList(roles));
        return (T) this;
    }
    
    public final T setTimeout(long timeout, TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
        return (T) this;
    }
}
