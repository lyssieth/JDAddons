package com.raxixor.jdaddons.examples;

import com.raxixor.jdaddons.command.Command;
import com.raxixor.jdaddons.command.CommandHandler;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ExampleMessageListener extends ListenerAdapter {
    
    private final CommandHandler handler;
    
    public ExampleMessageListener(CommandHandler handler) {
        this.handler = handler;
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String content = event.getMessage().getRawContent(); // Gets the Message's raw content.
        
        if (!content.startsWith("yourPrefix")) return; // Or do whatever you want to do.
        
        content = content.replaceFirst("yourPrefix", ""); // Removes prefix.
        String first = content.split(" ")[0]; // Gets the first word from the content.
    
        Command cmd = handler.findCommand(first.trim()); // Tries to find a command using the first word.
        
        if (handler.isCommand(cmd)) {
            // If the found command is in fact a command.
            handler.execute(cmd, event.getMessage(), content.replaceFirst(first.trim(), "").trim()); // Execute
        }
    }
}
