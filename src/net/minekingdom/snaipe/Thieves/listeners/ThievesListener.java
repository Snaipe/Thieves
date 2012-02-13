package net.minekingdom.snaipe.Thieves.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.minekingdom.snaipe.Thieves.Language;
import net.minekingdom.snaipe.Thieves.Thieves;
import net.minekingdom.snaipe.Thieves.ThievesPlayer;
import net.minekingdom.snaipe.Thieves.events.ItemStealEvent;

public class ThievesListener implements Listener {
	
	private final Thieves plugin;
	
	public ThievesListener()
	{
		plugin = Thieves.getInstance();
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onItemSteal(ItemStealEvent event)
	{
		final ThievesPlayer target = event.getTarget();
		
		int rand = (int)(Math.random()*100) + 1;

        if ( rand < Math.abs(plugin.getSettingManager().getDetectChance()) % 100 )
        {
            target.sendMessage(ChatColor.RED + Language.thiefSpotted);
        }
        
        target.updateInventory();
	}
}