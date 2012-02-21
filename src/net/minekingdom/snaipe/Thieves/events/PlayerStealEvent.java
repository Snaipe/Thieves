package net.minekingdom.snaipe.Thieves.events;

import net.minekingdom.snaipe.Thieves.ThievesPlayer;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerStealEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    
    private boolean cancelled;
    
    private ThievesPlayer thief;
    private ThievesPlayer target;
    
    public PlayerStealEvent(ThievesPlayer thief, ThievesPlayer target)
    {
        this.thief = thief;
        this.target = target;
        cancelled = false;
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
    
}