package com.raxixor.jdaddons.util;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class FinderUtil {
    
    private final static Pattern DISCORD_ID = Pattern.compile("\\d{17,20}"); // ID
    private final static Pattern FULL_USER_REF = Pattern.compile("(.{2,32})\\s*#(\\d{4})"); // $1 Username, $2 Discriminator
    private final static Pattern USER_MENTION = Pattern.compile("<@!?(\\d{17,20})>"); // $1 ID
    private final static Pattern CHANNEL_MENTION = Pattern.compile("<#(\\d{17,20})>"); // $1 ID
    private final static Pattern ROLE_MENTION = Pattern.compile("<@&(\\d{17,20})>"); // $1 ID
    
    public static List<User> findUsers(String query, JDA jda) {
        Matcher userMention = USER_MENTION.matcher(query);
        Matcher fullRefMatch = FULL_USER_REF.matcher(query);
        if (userMention.matches()) {
            User user = jda.getUserById(userMention.group(1));
            if (user != null) return Collections.singletonList(user);
        } else if (fullRefMatch.matches()) {
            String lowerName = fullRefMatch.group(1).toLowerCase();
            String discrim = fullRefMatch.group(2);
            List<User> users = jda.getUsers().stream()
                    .filter(user -> user.getName().toLowerCase().equals(lowerName)
                            && user.getDiscriminator().equals(discrim))
                    .collect(Collectors.toList());
            if (!users.isEmpty()) return users;
        } else if (DISCORD_ID.matcher(query).matches()) {
            User user = jda.getUserById(query);
            if (user != null) return Collections.singletonList(user);
        }
        ArrayList<User> exact = new ArrayList<>();
        ArrayList<User> wrongCase = new ArrayList<>();
        ArrayList<User> startsWith = new ArrayList<>();
        ArrayList<User> contains = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        jda.getUsers().forEach(user -> {
            String name = user.getName();
            if (name.equals(query)) exact.add(user);
            else if (name.equalsIgnoreCase(query) && exact.isEmpty()) wrongCase.add(user);
            else if (name.toLowerCase().startsWith(lowerQuery) && wrongCase.isEmpty()) startsWith.add(user);
            else if (name.toLowerCase().contains(lowerQuery) && startsWith.isEmpty()) contains.add(user);
        });
        if (!exact.isEmpty()) return exact;
        if (!wrongCase.isEmpty()) return wrongCase;
        if (!startsWith.isEmpty()) return startsWith;
        return contains;
    }
}
