package net.minekingdom.snaipe.Thieves.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.inventory.InventoryClickEvent;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;

import net.minekingdom.snaipe.Thieves.ItemValues;
import net.minekingdom.snaipe.Thieves.Language;
import net.minekingdom.snaipe.Thieves.ThievesPlayer;
import net.minekingdom.snaipe.Thieves.Thieves;
import net.minekingdom.snaipe.Thieves.events.ItemStealEvent;

public class InventoryListener implements Listener {
    
    private final Thieves plugin;
    
    public InventoryListener()
    {
        plugin = Thieves.getInstance();
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
    	if ( event.isCancelled() || event.getItem() == null )
    		return;
    	
        if ( event.getPlayer() == null )
            return;
        
        final ThievesPlayer thief = plugin.getPlayerManager().getPlayer(event.getPlayer());
        
        if ( thief == null )
            return;

        final ThievesPlayer target = plugin.getPlayerManager().getTarget(thief);
        
        if ( target != null )
        {
            ItemStealEvent stealEvent = new ItemStealEvent(thief, target, event.getCursor());
            plugin.getServer().getPluginManager().callEvent(stealEvent);
            
            if ( stealEvent.isCancelled() )
            {
                event.setCancelled(true);
                return;
            }
            else
            {
                int rand = (int)(Math.random()*100) + 1;

                if ( rand < 1 - ItemValues.valueOf(event.getItem().getType()) / ( thief.getThiefLevel(thief.getWorld()) + 9 ) )
                {
                    target.sendMessage(ChatColor.RED + Language.thiefSpotted);
                    event.setCancelled(true);
                    return;
                }
            }
        }
        
        target.updateInventory();
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event)
    {
        if (event.isCancelled())
            return;
        
        final Player player = event.getPlayer();
        if ( player != null )
        {
            ThievesPlayer thief = plugin.getPlayerManager().getPlayer(player);
            
            if ( plugin.getPlayerManager().isThief(thief) )
                plugin.getPlayerManager().removeThief(thief);
        }
            
    }
}