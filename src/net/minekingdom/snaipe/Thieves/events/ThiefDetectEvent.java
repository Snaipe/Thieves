package net.minekingdom.snaipe.Thieves.events;

import net.minekingdom.snaipe.Thieves.ThievesPlayer;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@SuppressWarnings("serial")
public class ThiefDetectEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private ThievesPlayer thief;
    private ThievesPlayer target;
    private ThievesPlayer detector;
    
    public ThiefDetectEvent(ThievesPlayer thief, ThievesPlayer target, ThievesPlayer detector)
    {
    	this.thief = thief;
    	this.target = target;
    	this.detector = detector;
    }

	public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public ThievesPlayer getTarget() 
    {
        return target;
    }
    
    public ThievesPlayer getThief() 
    {
        return thief;
    }
    
    public ThievesPlayer getDetector() 
    {
        return detector;
    }
}