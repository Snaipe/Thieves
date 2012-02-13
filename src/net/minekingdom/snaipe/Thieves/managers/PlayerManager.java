package net.minekingdom.snaipe.Thieves.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import net.minekingdom.snaipe.Thieves.ThievesPlayer;
import net.minekingdom.snaipe.Thieves.Thieves;

public class PlayerManager {
    
    private final Thieves plugin;
    private final HashMap<ThievesPlayer, ThievesPlayer> stealingPlayers = new HashMap<ThievesPlayer, ThievesPlayer>();
    private final HashMap<Player, ThievesPlayer> onlinePlayers = new HashMap<Player, ThievesPlayer>();

    public PlayerManager()
    {
        plugin = Thieves.getInstance();
    }
    
    public void addPlayer(Player player)
    {
        ThievesPlayer tplayer = plugin.getSettingManager().loadPlayer(player);
        
        onlinePlayers.put(player, tplayer);
    }
    
    public void removePlayer(Player player)
    {
        plugin.getSettingManager().savePlayer(onlinePlayers.get(player));
        
        onlinePlayers.remove(player);
    }
    
    public void saveAllPlayers()
    {
        for ( ThievesPlayer player : onlinePlayers.values() )
        {    
            plugin.getSettingManager().savePlayer(player);
        }
    }
    
    public void reloadAllPlayers()
    {
        saveAllPlayers();
        for ( ThievesPlayer player : onlinePlayers.values() )
        {    
            plugin.getSettingManager().loadPlayer(player);
        }
    }

    public HashMap<ThievesPlayer, ThievesPlayer> getStealingPlayers() 
    {
        return stealingPlayers;
    }
    
    public void removeThief(ThievesPlayer player)
    {
        stealingPlayers.remove(player);
    }
    
    public boolean isThief(ThievesPlayer player)
    {
        return stealingPlayers.containsKey(player);
    }

    public ThievesPlayer getTarget(ThievesPlayer player) 
    {
        return stealingPlayers.get(player);
    }

    public void removeAllThieves() 
    {
        for ( ThievesPlayer thief : stealingPlayers.keySet() )
        {
            SpoutPlayer splayer = SpoutManager.getPlayer(thief);
            splayer.closeActiveWindow();
        }
        stealingPlayers.clear();
    }
    
    public void addThief(ThievesPlayer thief, ThievesPlayer target)
    {
        stealingPlayers.put(thief, target);
    }

    public boolean isStealed(ThievesPlayer player) 
    {
        return stealingPlayers.containsValue(player);
    }
    
    public ThievesPlayer getThiefFromTarget(Player target)
    {
        ThievesPlayer thief = null;
        
        for ( Entry<ThievesPlayer, ThievesPlayer> couple : stealingPlayers.entrySet() )
        {
            if ( couple.getValue().getDisplayName().equalsIgnoreCase(target.getDisplayName()) )
            {
                thief = couple.getKey();
                break;
            }
        }
        
        return thief;
    }
    
    public List<ThievesPlayer> getNearbyThieves(Player player)
    {
        List<ThievesPlayer> thieves = new ArrayList<ThievesPlayer>();
        int detectRadius = plugin.getSettingManager().getDetectRadius();
        
        for ( Entity entity : player.getNearbyEntities(detectRadius, detectRadius, detectRadius) )
        {
            if ( entity instanceof Player )
            {
                ThievesPlayer nearbyPlayer = plugin.getPlayerManager().getPlayer((Player) entity);
                
                if ( isThief(nearbyPlayer) )
                    thieves.add(nearbyPlayer);
            }
        }
        
        return thieves;
    }

    public ThievesPlayer getPlayer(Player player) 
    {
        return onlinePlayers.get(player);
    }
}