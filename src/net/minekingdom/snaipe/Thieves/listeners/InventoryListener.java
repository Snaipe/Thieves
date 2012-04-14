package net.minekingdom.snaipe.Thieves.listeners;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

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
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        if ( event.isCancelled() )
            return;

        if ( !(event.getWhoClicked() instanceof Player) )
            return;
        
        final ThievesPlayer thief = plugin.getPlayerManager().getPlayer((Player) event.getWhoClicked());
        final ThievesPlayer target = plugin.getPlayerManager().getTarget(thief);
        
        if ( target != null )
        {
            
            final int slot = event.getRawSlot();
            
            if ( event.isShiftClick() )
            {
                event.setCancelled(true);
                return;
            }
            
            if ( slot == -999 )
            {
                event.setCancelled(true);
                return;
            }
            
            int containerMaxSlot = Thieves.getInstance().getSettingManager().canStealHotBar() ? 35 : 26 ;
            
            if ( slot > containerMaxSlot )
                return;
            
            if ( event.getCursor().getAmount() != 0 )
            {
                event.setCancelled(true);
                return;
            }

            if ( event.getCurrentItem().getAmount() == 0 )
                return;
            
            final ItemStack item = event.getCurrentItem();
            
            if ( thief.getMaxItemWealth() < thief.getItemWealth() + ItemValues.valueOf(item.getType()) )
            {
                thief.getPlayer().sendMessage(ChatColor.RED + Language.cannotStealMore);
                event.setCancelled(true);
                return;
            }
            
            Map<Enchantment, Integer> enchantments = item.getEnchantments();
            double enchantmentMultiplier = 1D;
            
            for( Enchantment enchantment : enchantments.keySet() )
            {
            	enchantmentMultiplier += plugin.getSettingManager().getEnchantmentUnitMultiplier()*item.getEnchantmentLevel(enchantment);
            }
            
            double rand = (double) (Math.random()*100 + 1);
            boolean successful = rand <= 100*( 1D - ((double) ItemValues.valueOf(item.getType())*enchantmentMultiplier ) / ((double) (thief.getThiefLevel() + 9) ));
            
            ItemStealEvent stealEvent = new ItemStealEvent(thief, target, event.getCurrentItem(), successful);
            plugin.getServer().getPluginManager().callEvent(stealEvent);
            
            if ( stealEvent.isCancelled() )
            {
                event.setCancelled(true);
                return;
            }
            else
            {
                thief.addItemToWealth(item);
                
                if ( !stealEvent.isSuccessful() )
                {
                    target.getPlayer().sendMessage(ChatColor.RED + Language.thiefSpotted);
                    event.setCancelled(true);
                    return;
                }
                
                event.setResult(Result.ALLOW);
                
                thief.addThiefExperience(ItemValues.valueOf(item.getType()));
                
                if ( thief.canLevelUp() )
                {
                    thief.getPlayer().sendMessage(ChatColor.RED + Language.levelUp);
                    thief.incrementThiefLevel();
                }
                
                ItemStack cursor = item.clone();
                          cursor.setAmount(1);
                
                if ( item.getAmount() > 1 )
                    item.setAmount(item.getAmount() - 1);
                else
                    event.setCurrentItem(null);
                
                event.setCursor(cursor);
            }
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event)
    {
        final Player player = (Player) event.getPlayer();
        if ( player != null )
        {
            ThievesPlayer thief = plugin.getPlayerManager().getPlayer(player);
            
            if ( plugin.getPlayerManager().isThief(thief) )
            {
                plugin.getPlayerManager().removeThief(thief);
                thief.setCooldown(plugin.getSettingManager().getCooldown());
                thief.setItemWealth(0);
            }
        }
            
    }
}