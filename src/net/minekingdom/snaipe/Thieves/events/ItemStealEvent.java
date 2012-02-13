package net.minekingdom.snaipe.Thieves.events;

import net.minekingdom.snaipe.Thieves.ThievesPlayer;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("serial")
public class ItemStealEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	
	private boolean cancelled;
	
	private ItemStack item;
	private ThievesPlayer thief;
	private ThievesPlayer target;
	
	public ItemStealEvent(ThievesPlayer thief, ThievesPlayer target, ItemStack item)
	{
		this.item = item;
		this.thief = thief;
		this.target = target;
	}

    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }

	@Override
	public boolean isCancelled() 
	{
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) 
	{
		this.cancelled = cancelled;
	}

	public ThievesPlayer getTarget() 
	{
		return target;
	}
	
	public ThievesPlayer getThief() 
	{
		return thief;
	}
	
	public ItemStack getItem()
	{
		return item;
	}
	
}