package com.raxixor.jdaddons.command;

import net.dv8tion.jda.api.events.*;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EventWaiter implements EventListener {
    
    private final HashMap<Class<?>, List<WaitingEvent>> waitEvents;
    private final ScheduledExecutorService threadPool;
    
    public EventWaiter() {
        this.waitEvents = new HashMap<>();
        this.threadPool = Executors.newSingleThreadScheduledExecutor();
    }
    
    public <T extends Event> void waitForEvent(Class<T> classType, Predicate<T> condition, Consumer<T> action) {
        waitForEvent(classType, condition, action, -1, null, null);
    }
    
    public <T extends Event> void waitForEvent(Class<T> classType, Predicate<T> condition, Consumer<T> action,
                                               long timeout, TimeUnit unit, Runnable timeoutAction) {
        List<WaitingEvent> list;
        if (waitEvents.containsKey(classType))
            list = waitEvents.get(classType);
        else {
            list = new ArrayList<>();
            waitEvents.put(classType, list);
        }
        
        WaitingEvent we = new WaitingEvent<>(condition, action);
        list.add(we);
        if (timeout > 0 && unit != null) {
            threadPool.schedule(() -> {
                if (list.remove(we) && timeoutAction != null)
                    timeoutAction.run();
            }, timeout, unit);
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    @SubscribeEvent
    public void onEvent(@NotNull GenericEvent event) {
        Class c = event.getClass();
        while (c.getSuperclass() != null) {
            if (waitEvents.containsKey(c)) {
                List<WaitingEvent> list = waitEvents.get(c);
                List<WaitingEvent> ulist = new ArrayList<>(list);
                list.removeAll(ulist.stream().filter(i -> i.attempt(event)).collect(Collectors.toList()));
            }
            
            if (event instanceof ShutdownEvent)
                threadPool.shutdown();
            c = c.getSuperclass();
        }
    }
    
    private class WaitingEvent<T extends GenericEvent> {
        final Predicate<T> condition;
        final Consumer<T> action;
        
        WaitingEvent(Predicate<T> condition, Consumer<T> action) {
            this.condition = condition;
            this.action = action;
        }
        
        boolean attempt(T event) {
            if (condition.test(event)) {
                action.accept(event);
                return true;
            }
            return false;
        }
    }
}
