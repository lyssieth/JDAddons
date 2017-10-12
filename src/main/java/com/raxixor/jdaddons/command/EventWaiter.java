package com.raxixor.jdaddons.command;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EventWaiter implements EventListener {
    
    private final HashMap<Class<?>, List<WaitingEvent>> waitEvents;
    private final ScheduledExecutorService threadpool;
    
    public EventWaiter() {
        waitEvents = new HashMap<>();
        threadpool = Executors.newSingleThreadScheduledExecutor();
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
            threadpool.schedule(() -> {
                if (list.remove(we) && timeoutAction != null)
                    timeoutAction.run();
            }, timeout, unit);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public final void onEvent(Event event) {
        Class c = event.getClass();
        while (c.getSuperclass() != null) {
            if (waitEvents.containsKey(c)) {
                List<WaitingEvent> list = waitEvents.get(c);
                List<WaitingEvent> ulst = new ArrayList<>(list);
                list.removeAll(ulst.stream().filter(i -> i.attempt(event)).collect(Collectors.toList()));
            }
            if (event instanceof ShutdownEvent)
                threadpool.shutdown();
            c = c.getSuperclass();
        }
    }
    
    private class WaitingEvent<T extends Event> {
        final Predicate<T> condition;
        final Consumer<T> action;
        
        WaitingEvent(Predicate<T> condition, Consumer<T> action) {
            this.condition = condition;
            this.action = action;
        }
        
        boolean attempt(T event) {
            if (condition.test(event)){
                action.accept(event);
                return true;
            } return false;
        }
    }
}
