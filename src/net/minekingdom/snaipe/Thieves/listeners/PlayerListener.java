package net.minekingdom.snaipe.Thieves.listeners;

import java.util.List;

import net.minekingdom.snaipe.Thieves.Language;
import net.minekingdom.snaipe.Thieves.ThievesPlayer;
import net.minekingdom.snaipe.Thieves.Thieves;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    
    private final Thieves plugin;
    
    public PlayerListener()
    {
        plugin = Thieves.getInstance();
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if ( event.isCancelled() )
            return;

        if ( event.getPlayer() == null )
            return;

        final ThievesPlayer player = plugin.getPlayerManager().getPlayer(event.getPlayer());
        
        if ( plugin.getSettingManager().isActiveWorld(player.getWorld()) )
        {
            
            if ( plugin.getPlayerManager().isStealed(player) )
            {
                ThievesPlayer thief = plugin.getPlayerManager().getThiefFromTarget(event.getPlayer());
                
                if ( thief == null )
                    return;
                
                if ( !thief.isTargetWithinRange(player) )
                {
                    thief.closeWindow();
                }
                
                if ( player.canSeePlayer(thief) )
                {
                    thief.closeWindow();
                    plugin.getPlayerManager().removeThief(thief);
                    thief.stun(plugin.getSettingManager().getStunTime());
                    
                    thief.sendMessage(ChatColor.RED + Language.youHaveBeenDiscovered);
                }
            }
            else
            {
                List<ThievesPlayer> thieves = plugin.getPlayerManager().getNearbyThieves(player);
                
                if ( thieves == null )
                    return;
                
                for ( ThievesPlayer thief : thieves )
                {
                    if ( player.canSeePlayer(thief) )
                    {
                        thief.closeWindow();
                        plugin.getPlayerManager().removeThief(thief);
                        thief.stun(plugin.getSettingManager().getStunTime());
                        
                        thief.sendMessage(ChatColor.RED + Language.youHaveBeenDiscovered);
                    }
                }
            }    
        }
    }
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
    {
        if (event.isCancelled())
            return;
        
        if (event.getPlayer() == null)
            return;
        
        final ThievesPlayer thief = plugin.getPlayerManager().getPlayer(event.getPlayer());
        final Entity entity = event.getRightClicked();

        if (entity == null)
            return;
        
        if ( !plugin.getSettingManager().canStealInNonPvPArea() )
        {
            EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(thief.getPlayer(), entity, DamageCause.ENTITY_ATTACK, 1);
            plugin.getServer().getPluginManager().callEvent(damageEvent);
            
            if (damageEvent.isCancelled())
            {
                thief.sendMessage(ChatColor.RED + Language.youCannotStealInNonPvPArea);
                return;
            }
        }
        
        if ( plugin.getSettingManager().isActiveWorld(thief.getWorld()) )
        {
            if ( Thieves.isTheftEnabled && thief.hasPermission("thieves.steal") ) //TODO: thieves.steal
            {
                if ( !thief.isSneaking() )
                    return;
                
                if ( entity instanceof Player )
                {
                    ThievesPlayer target = plugin.getPlayerManager().getPlayer((Player) entity);
                    
                    if ( target.hasPermission("thieves.protected") )
                    {
                        thief.sendMessage(ChatColor.RED + Language.cannotRobThisPlayer);
                        return;
                    }
                    
                    if ( !target.canSeePlayer(thief) )
                    {
                        thief.startStealing(target);
                        plugin.getPlayerManager().addThief(thief, target);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {    
        final Player player = event.getPlayer();
        
        if ( player != null )
            plugin.getPlayerManager().addPlayer(player);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        final Player player = event.getPlayer();
        
        if ( player != null )
            plugin.getPlayerManager().removePlayer(player);
    }
    
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event)
    {
        final Player player = event.getPlayer();
        
        if ( player != null )
            plugin.getPlayerManager().removePlayer(player);
    }
}