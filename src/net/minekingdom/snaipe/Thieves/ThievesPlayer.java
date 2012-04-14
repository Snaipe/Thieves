package net.minekingdom.snaipe.Thieves;

import java.util.Date;
import java.util.HashMap;

import net.minecraft.server.MobEffect;
import net.minecraft.server.MobEffectList;

import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ThievesPlayer {
    
    private Player player;
    private HashMap<World, Integer> thiefLevels = new HashMap<World, Integer>();
    private HashMap<World, Long> thiefExperience = new HashMap<World, Long>();
    private long cooldown = 0;
    private int itemWealth = 0;
    
    public ThievesPlayer(Player player)
    {
        this.player = player;
    }
    
    public ThievesPlayer(Player player, HashMap<World, Integer> thiefLevels, HashMap<World, Long> thiefExperience) 
    {
        this.player = player;
        this.thiefLevels = thiefLevels;
        this.thiefExperience = thiefExperience;
    }
    
    public int getThiefLevel()
    {
        if ( Thieves.getInstance().getSettingManager().isPermissionLevels() )
        {
            for ( int i = 10; i > 0; i--)
            {
                if ( player.hasPermission("thieves.level." + Integer.valueOf(i)) )
                    return i;
            }
            
            return 1;
        }
        else
        {
            return getThiefLevel(player.getWorld());
        }
    }

    public int getThiefLevel(World world)
    {
        return thiefLevels.get(world);
    }
    
    public void incrementThiefLevel()
    {
        incrementThiefLevel(player.getWorld());
    }
    
    public void incrementThiefLevel(World world)
    {
        thiefLevels.put(world, thiefLevels.get(world) + 1);
    }
    
    public void addThiefExperience(int exp)
    {
        addThiefExperience(player.getWorld(), exp);
    }
    
    public void addThiefExperience(World world, int exp)
    {
        Long experience = thiefExperience.get(world);
        if ( experience != null )
        {
            thiefExperience.remove(world);
            thiefExperience.put(world, experience + exp);
        }
        else
        {
            thiefExperience.put(world, (long) exp);
        }
    }
    
    public long getThiefExperience()
    {
        if ( !Thieves.getInstance().getSettingManager().isPermissionLevels() )
        {
            return getThiefExperience(player.getWorld());
        }
        else
        {
            return 0;
        }
    }
    
    public long getThiefExperience(World world)
    {
        Long experience = thiefExperience.get(world);
        if ( experience != null )
            return experience;
        else
            return 0;
    }
    
    public void startStealing(ThievesPlayer target)
    {
        ThievesInventory inventory = new ThievesInventory(((CraftPlayer)target.getPlayer()).getHandle().inventory, ((CraftPlayer)target.getPlayer()).getHandle());

        ((CraftPlayer)player).getHandle().openContainer(inventory);
    }
    
    public Player getPlayer()
    {
        return player;
    }

    public boolean canSeePlayer(Player target)
    {
        Vector vector = player.getEyeLocation().getDirection();
        
        Vector difference = new Vector(target.getLocation().getX() - player.getLocation().getX(), 0, target.getLocation().getZ() - player.getLocation().getZ());
        float angle = difference.angle(vector);
        
        if ( angle < Math.PI / 3 )
            return true;
        
        return false;
    }
    
    public boolean isTargetWithinRange(Player target)
    {
        return target.getLocation().distance(player.getLocation()) < Thieves.getInstance().getSettingManager().getTheftRange();
    }
    
    public void closeWindow()
    {
        player.closeInventory();
    }
    
    public void stun(int seconds)
    {
        MobEffect slow = new MobEffect(MobEffectList.SLOWER_MOVEMENT.getId(), (int)(20 * seconds), 10);
        
        ((CraftPlayer)player).getHandle().addEffect(slow);
    }
    

    public long getCooldown() 
    {
        return cooldown - (new Date()).getTime();
    }
    
    public void setCooldown(int cooldown)
    {
        this.cooldown = (new Date()).getTime() + cooldown*1000;
    }
    
    public int getMaxItemWealth()
    {
        return getMaxItemWealth(player.getWorld());
    }
    
    public int getMaxItemWealth(World world)
    {
        return 10*thiefLevels.get(world);
    }
    
    public void addItemToWealth(ItemStack item)
    {
        itemWealth += ItemValues.valueOf(item.getType());
    }
    
    public void setItemWealth(int wealth)
    {
        itemWealth = wealth;
    }
    
    public int getItemWealth()
    {
        return itemWealth;
    }

    public boolean canLevelUp()
    {
        return getThiefExperience() > Math.ceil(100*Math.pow(1.6681, getThiefLevel()));
    }
}