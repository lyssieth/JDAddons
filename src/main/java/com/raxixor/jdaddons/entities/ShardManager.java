package com.raxixor.jdaddons.entities;

import com.raxixor.jdaddons.util.SafeIdUtil;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.*;

import java.util.*;

public class ShardManager {
    
    private final HashMap<Integer, JDA> shards;
    private static ShardManager instance;
    
    private ShardManager() {
        this.shards = new HashMap<>();
    }
    
    /**
     * Gets the current instance of the ShardManager, or creates one.
     *
     * @return The current instance
     */
    public static ShardManager getInstance() {
        if (instance == null)
            instance = new ShardManager();
        return instance;
    }
    
    /**
     * Gets all the entries from the list.
     *
     * @return All the entries.
     */
    public Set<Map.Entry<Integer, JDA>> getEntries() {
        return shards.entrySet();
    }
    
    /**
     * Gets all the indexes from the list
     *
     * @return All of the indexes
     */
    public Set<Integer> getIndexes() {
        return shards.keySet();
    }
    
    /**
     * Gets all the Shards from the list.
     *
     * @return All of the Shards
     */
    public Set<JDA> getShards() {
        return new HashSet<>(shards.values());
    }
    
    /**
     * Gets a Shard from the list, using the index
     *
     * @param index Index of the Shard
     * @return The Shard, or null
     */
    public JDA getShard(int index) {
        if (isValid(index))
            return shards.get(index);
        return null;
    }
    
    /**
     * Adds a Shard to the list, if it doesn't already exist
     *
     * @param shard Shard to add
     */
    public void addShard(JDA shard) {
        if (!hasShard(shard))
            shards.put(shard.getShardInfo().getShardId(), shard);
    }
    
    /**
     * Same as the String version
     *
     * @param id The ID of the User
     * @return The User, or null
     * @throws IllegalArgumentException If the provided ID isn't a valid Discord ID.
     */
    public User getUser(long id) throws IllegalArgumentException{
        return getUser(Long.toString(id));
    }
    
    /**
     * Gets a User from the shards
     *
     * @param id ID of the User
     * @return The User, or null
     * @throws IllegalArgumentException If the provided ID isn't a valid Discord ID.
     */
    public User getUser(String id) throws IllegalArgumentException {
        if (SafeIdUtil.checkId(id)) {
            for (JDA shard : shards.values()) {
                User user = shard.getUserById(id);
                if (user != null)
                    return user;
            }
            return null;
        } else
            throw new IllegalArgumentException("Provided ID is not a valid Discord ID.");
    }
    
    /**
     * Same as the String version
     *
     * @param id ID of the Guild
     * @return The Guild, or null
     * @throws IllegalArgumentException If the provided ID isn't a valid Discord ID.
     */
    public Guild getGuild(long id) throws IllegalArgumentException {
        return getGuild(Long.toString(id));
    }
    
    /**
     * Gets a Guild from the shards
     *
     * @param id ID of the Guild
     * @return The Guild, or null
     * @throws IllegalArgumentException If the provided ID isn't a valid Discord ID.
     */
    public Guild getGuild(String id) throws IllegalArgumentException {
        if (SafeIdUtil.checkId(id)) {
            for (JDA shard : shards.values()) {
                Guild guild = shard.getGuildById(id);
                if (guild != null)
                    return guild;
            }
            return null;
        } else
            throw new IllegalArgumentException("Provided ID is not a valid Discord ID");
    }
    /**
     * Sets the Avatar of all shards
     *
     * @param icon Avatar to set it to
     */
    public void setAvatar(Icon icon) {
        for (JDA shard : shards.values())
            shard.getSelfUser().getManager().setAvatar(icon).queue();
    }
    
    /**
     * Sets the Game of all shards
     *
     * @param game Game to set it to
     */
    public void setGame(Game game) {
        for (JDA shard : shards.values())
            shard.getPresence().setGame(game);
    }
    
    /**
     * Sets the OnlineStatus of all shards
     *
     * @param status Status to set it to
     */
    public void setOnlineStatus(OnlineStatus status) {
        for (JDA shard : shards.values())
            shard.getPresence().setStatus(status);
    }
    
    /**
     * Checks whether a Shard with the provided index exists.
     *
     * @param index Index of the Shard
     * @return True, if it exists and isn't null
     */
    public boolean hasShardWithId(int index) {
        if (isValid(index))
            return shards.get(index) != null;
        return false;
    }
    
    /**
     * Checks whether the ShardManager has the shard
     *
     * @param shard Shard to check for
     * @return True, if the ShardManager has the shard.
     */
    public boolean hasShard(JDA shard) {
        return shards.containsValue(shard);
    }
    
    /**
     * Gets the index of a Shard, or -1 if it isn't registered.
     *
     * @param shard The shard to get the index of
     * @return The index, or -1
     */
    public int indexOf(JDA shard) {
        int index = shard.getShardInfo().getShardId();
        if (shards.get(index) != null)
            return index;
        return -1;
    }
    
    private boolean isValid(int s) {
        return s >= 0 && s < shards.size();
    }
}
