package com.raxixor.jdaddons.entities;

import net.dv8tion.jda.core.entities.Message;

import java.util.*;
import java.util.function.Consumer;

public class MessageList implements Iterable<Message> {
    
    private final List<Message> messages;
    
    public MessageList() {
        this.messages = new ArrayList<>();
    }
    
    public void add(Message message) {
        if (!messages.contains(message))
            messages.add(message);
        else
            throw new IllegalArgumentException("Provided message is already contained within the list.");
    }
    
    public void add(int index, Message message) {
        if (!messages.contains(message) && isValidIndex(index))
            messages.add(index, message);
        else if (!isValidIndex(index))
            throw new IndexOutOfBoundsException("Provided index is out of bounds.");
        else if (messages.contains(message))
            throw new IllegalArgumentException("Provided message is already contained within the list.");
    }
    
    public Message replace(int index, Message newMessage) {
        if (!messages.contains(newMessage) && isValidIndex(index)) {
            Message old = messages.get(index);
            
            messages.remove(index);
            messages.add(index, newMessage);
            return old;
        } else if (!isValidIndex(index))
            throw new IndexOutOfBoundsException("Provided index is out of bounds.");
        else if (messages.contains(newMessage))
            throw new IllegalArgumentException("Provided message is already contained within the list.");
        else
            throw new IllegalArgumentException("Something is illegal.");
    }
    
    public Message get(int index) {
        if (isValidIndex(index)) {
            Message msg = messages.get(index);
            
            if (msg != null)
                return msg;
            return null;
        } else
            throw new IndexOutOfBoundsException("Provided index is out of bounds.");
    }
    
    public Set<Message> subSet(int start) {
        if (!isValidIndex(start))
            throw new IndexOutOfBoundsException("Provided start index is out of bounds.");
        
        List<Message> sub = messages.subList(start, messages.size());
        return new HashSet<>(sub);
    }
    
    public Set<Message> subSet(int start, int end) {
        if (!isValidIndex(start) && !isValidIndex(end))
            throw new IndexOutOfBoundsException("Both provided indexes are out of bounds.");
        if (!isValidIndex(start))
            throw new IndexOutOfBoundsException("Provided start index is out of bounds.");
        if (!isValidIndex(end))
            throw new IndexOutOfBoundsException("Provided end index is out of bounds.");
        
        List<Message> sub = messages.subList(start, end);
        return new HashSet<>(sub);
    }
    
    public void forEach(Consumer<? super Message> action) {
        for (Message msg : messages)
            action.accept(msg);
    }
    
    public void clearForEach(Consumer<? super Message> action) {
        for (Message msg : messages) {
            action.accept(msg);
            messages.remove(msg);
        }
    }
    
    public void clear() {
        messages.clear();
    }
    
    public Set<Message> getMessages() {
        return new HashSet<>(messages);
    }
    
    public int size() {
        return messages.size();
    }
    
    public boolean isEmpty() {
        if (messages.size() == 1)
            return messages.get(0) == null;
        return messages.isEmpty();
    }
    
    public boolean hasMessageAtIndex(int index) {
        if (isValidIndex(index)) {
            Message message = messages.get(index);
            
            return message != null;
        }
        return false;
    }
    
    public boolean isValidIndex(int index) {
        return !(index > messages.size()) && index >= 0;
    }
    
    @SuppressWarnings("NullableProblems")
    @Override
    public Iterator<Message> iterator() {
        return messages.iterator();
    }
}
