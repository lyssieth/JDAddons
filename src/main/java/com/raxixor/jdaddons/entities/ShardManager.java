package com.raxixor.jdaddons.entities;

import com.raxixor.jdaddons.util.SafeIdUtil;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.*;

import java.util.*;

public class ShardManager {
    
    private List<JDA> shards;
    private static ShardManager instance;
    
    private ShardManager() {
        this.shards = new ArrayList<>();
    }
    
    public static ShardManager getInstance() {
        if (instance == null)
            instance = new ShardManager();
        return instance;
    }
    
    public Set<JDA> getShards() {
        return new HashSet<>(shards);
    }
    
    public JDA getShard(int id) {
        if (isValid(id))
            return shards.get(id);
        return null;
    }
    
    public void addShard(JDA shard) {
        if (!hasShard(shard))
            shards.add(shard);
    }
    
    public User getUser(long id) throws IllegalArgumentException {
        return getUser(Long.toString(id));
    }
    
    public User getUser(String id) {
        if (SafeIdUtil.checkId(id)) {
            for (JDA shard : shards) {
                User user = shard.getUserById(id);
                if (user != null)
                    return user;
            }
            return null;
        } else
            throw new IllegalArgumentException("Provided ID is not a valid ID.");
    }
    
    public Guild getGuild(long id) throws IllegalArgumentException {
        return getGuild(Long.toString(id));
    }
    
    public Guild getGuild(String id) {
        if (SafeIdUtil.checkId(id)) {
            for (JDA shard : shards) {
                Guild guild = shard.getGuildById(id);
                if (guild != null)
                    return guild;
            }
            return null;
        } else
            throw new IllegalArgumentException("Provided ID is not a valid ID.");
    }
    
    public void setAvatar(Icon icon) {
        for (JDA shard : shards)
            shard.getSelfUser().getManager().setAvatar(icon).queue();
    }
    
    public void setGame(Game game) {
        for (JDA shard : shards)
            shard.getPresence().setGame(game);
    }
    
    public void setOnlineStatus(OnlineStatus status) {
        for (JDA shard : shards)
            shard.getPresence().setStatus(status);
    }
    
    public boolean hasShardWithId(int id) {
        if (isValid(id))
            return shards.get(id) != null;
        return false;
    }
    
    public boolean hasShard(JDA shard) {
        return shards.contains(shard);
    }
    
    public int indexOf(JDA shard) {
        if (hasShard(shard))
            return shards.indexOf(shard);
        return -1;
    }
    
    private boolean isValid(int s) {
        return s >= 0 && s < shards.size();
    }
}
