package net.minekingdom.snaipe.Thieves;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.server.MobEffect;
import net.minecraft.server.MobEffectList;

import org.bukkit.Achievement;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class ThievesPlayer implements Player {
    
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
	    		if ( hasPermission("thieves.level." + Integer.valueOf(i)) )
	    			return i;
	    	}
	    	
	    	return 1;
    	}
    	else
    	{
    		return getThiefLevel(getWorld());
    	}
    }

    public int getThiefLevel(World world)
    {
        return thiefLevels.get(world);
    }
    
    public void incrementThiefLevel()
    {
    	incrementThiefLevel(getWorld());
    }
    
    public void incrementThiefLevel(World world)
    {
        thiefLevels.put(world, thiefLevels.get(world) + 1);
    }
    
    public void addThiefExperience(int exp)
    {
    	addThiefExperience(getWorld(), exp);
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
    		return getThiefExperience(getWorld());
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

        ((CraftPlayer)player).getHandle().a(inventory);
    }
    
    public boolean canSeePlayer(Player target)
    {
        Vector vector = getEyeLocation().getDirection();
        
        Vector difference = new Vector(target.getLocation().getX() - getLocation().getX(), 0, target.getLocation().getZ() - getLocation().getZ());
        float angle = difference.angle(vector);
        
        if ( angle < Math.PI / 3 )
            return true;
        
        return false;
    }
    
    public boolean isTargetWithinRange(Player target)
    {
        return target.getLocation().distance(getLocation()) < Thieves.getInstance().getSettingManager().getTheftRange();
    }
    
    public void closeWindow()
    {
        SpoutPlayer splayer = SpoutManager.getPlayer(player);
        splayer.closeActiveWindow();
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
		return getMaxItemWealth(getWorld());
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
	
	/*
	 * Default bukkit implementation.
	 */

    @Override
    public GameMode getGameMode() 
    {
        return player.getGameMode();
    }

    @Override
    public PlayerInventory getInventory() 
    {
        return player.getInventory();
    }

    @Override
    public ItemStack getItemInHand() 
    {
        return player.getItemInHand();
    }

    @Override
    public String getName() 
    {
        return player.getName();
    }

    @Override
    public int getSleepTicks() 
    {
        return player.getSleepTicks();
    }

    @Override
    public boolean isSleeping() 
    {
        return player.isSleeping();
    }

    @Override
    public void setGameMode(GameMode arg0) 
    {
        player.setGameMode(arg0);
    }

    @Override
    public void setItemInHand(ItemStack arg0) 
    {
        player.setItemInHand(arg0);
    }

    @Override
    public void damage(int arg0) 
    {
        player.damage(arg0);
    }

    @Override
    public void damage(int arg0, Entity arg1) 
    {
        player.damage(arg0, arg1);
    }

    @Override
    public double getEyeHeight() 
    {
        return player.getEyeHeight();
    }

    @Override
    public double getEyeHeight(boolean arg0) 
    {
        return player.getEyeHeight(arg0);
    }

    @Override
    public Location getEyeLocation() 
    {
        return player.getEyeLocation();
    }

    @Override
    public int getHealth() 
    {
        return player.getHealth();
    }

    @Override
    public Player getKiller() 
    {
        return player.getKiller();
    }

    @Override
    public int getLastDamage() 
    {
        return player.getLastDamage();
    }

    @Override
    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> arg0, int arg1) 
    {
        return player.getLastTwoTargetBlocks(arg0, arg1);
    }

    @Override
    public List<Block> getLineOfSight(HashSet<Byte> arg0, int arg1) 
    {
        return player.getLineOfSight(arg0, arg1);
    }

    @Override
    public int getMaxHealth() 
    {
        return player.getMaxHealth();
    }

    @Override
    public int getMaximumAir() 
    {
        return player.getMaximumAir();
    }

    @Override
    public int getMaximumNoDamageTicks() 
    {
        return player.getMaximumNoDamageTicks();
    }

    @Override
    public int getNoDamageTicks() 
    {
        return player.getNoDamageTicks();
    }

    @Override
    public int getRemainingAir() 
    {
        return player.getRemainingAir();
    }

    @Override
    public Block getTargetBlock(HashSet<Byte> arg0, int arg1) 
    {
        return player.getTargetBlock(arg0, arg1);
    }

    @Override
    public Vehicle getVehicle() 
    {
        return player.getVehicle();
    }

    @Override
    public boolean isInsideVehicle() 
    {
        return player.isInsideVehicle();
    }

    @Override
    public boolean leaveVehicle() 
    {
        return player.leaveVehicle();
    }

    @Override
    public void setHealth(int arg0) 
    {
        player.setHealth(arg0);
    }

    @Override
    public void setLastDamage(int arg0) 
    {
        player.setLastDamage(arg0);
    }

    @Override
    public void setMaximumAir(int arg0) 
    {
        player.setMaximumAir(arg0);
    }

    @Override
    public void setMaximumNoDamageTicks(int arg0) 
    {
        player.setMaximumNoDamageTicks(arg0);
    }

    @Override
    public void setNoDamageTicks(int arg0) 
    {
        player.setNoDamageTicks(arg0);
    }

    @Override
    public void setRemainingAir(int arg0) 
    {
        player.setRemainingAir(arg0);
    }

    @Override
    public Arrow shootArrow() 
    {
        return player.shootArrow();
    }

    @Override
    public Egg throwEgg() 
    {
        return player.throwEgg();
    }

    @Override
    public Snowball throwSnowball() 
    {
        return player.throwSnowball();
    }

    @Override
    public boolean eject() 
    {
        return player.eject();
    }

    @Override
    public int getEntityId() 
    {
        return player.getEntityId();
    }

    @Override
    public float getFallDistance() 
    {
        return player.getFallDistance();
    }

    @Override
    public int getFireTicks() 
    {
        return player.getFireTicks();
    }

    @Override
    public EntityDamageEvent getLastDamageCause() 
    {
        return player.getLastDamageCause();
    }

    @Override
    public Location getLocation() 
    {
        return player.getLocation();
    }

    @Override
    public int getMaxFireTicks() 
    {
        return player.getMaxFireTicks();
    }

    @Override
    public List<Entity> getNearbyEntities(double arg0, double arg1, double arg2) 
    {
        return player.getNearbyEntities(arg0, arg1, arg2);
    }

    @Override
    public Entity getPassenger() 
    {
        return player.getPassenger();
    }

    @Override
    public Server getServer() 
    {
        return player.getServer();
    }

    @Override
    public int getTicksLived() 
    {
        return player.getTicksLived();
    }

    @Override
    public UUID getUniqueId() 
    {
        return player.getUniqueId();
    }

    @Override
    public Vector getVelocity() 
    {
        return player.getVelocity();
    }

    @Override
    public World getWorld() 
    {
        return player.getWorld();
    }

    @Override
    public boolean isDead() 
    {
        return player.isDead();
    }

    @Override
    public boolean isEmpty() 
    {
        return player.isEmpty();
    }

    @Override
    public void playEffect(EntityEffect arg0) 
    {
        player.playEffect(arg0);
    }

    @Override
    public void remove() 
    {
        player.remove();
    }

    @Override
    public void setFallDistance(float arg0) 
    {
        player.setFallDistance(arg0);
    }

    @Override
    public void setFireTicks(int arg0) 
    {
        player.setFireTicks(arg0);
    }

    @Override
    public void setLastDamageCause(EntityDamageEvent arg0) 
    {
        player.setLastDamageCause(arg0);
    }

    @Override
    public boolean setPassenger(Entity arg0) 
    {
        return player.setPassenger(arg0);
    }

    @Override
    public void setTicksLived(int arg0) 
    {
        player.setTicksLived(arg0);
    }

    @Override
    public void setVelocity(Vector arg0) 
    {
        player.setVelocity(arg0);
    }

    @Override
    public boolean teleport(Location arg0) 
    {
        return player.teleport(arg0);
    }

    @Override
    public boolean teleport(Entity arg0) 
    {
        return player.teleport(arg0);
    }

    @Override
    public boolean teleport(Location arg0, TeleportCause arg1) 
    {
        return player.teleport(arg0, arg1);
    }

    @Override
    public boolean teleport(Entity arg0, TeleportCause arg1) 
    {
        return player.teleport(arg0, arg1);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0) 
    {
        return player.addAttachment(arg0);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0, int arg1) 
    {
        return player.addAttachment(arg0, arg1);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0, String arg1,
            boolean arg2) 
    {
        return player.addAttachment(arg0, arg1, arg2);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0, String arg1,
            boolean arg2, int arg3) 
    {
        return player.addAttachment(arg0, arg1, arg2, arg3);
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() 
    {
        return player.getEffectivePermissions();
    }

    @Override
    public boolean hasPermission(String arg0) 
    {
        return player.hasPermission(arg0);
    }

    @Override
    public boolean hasPermission(Permission arg0) 
    {
        return player.hasPermission(arg0);
    }

    @Override
    public boolean isPermissionSet(String arg0) 
    {
        return player.isPermissionSet(arg0);
    }

    @Override
    public boolean isPermissionSet(Permission arg0) 
    {
        return player.isPermissionSet(arg0);
    }

    @Override
    public void recalculatePermissions() 
    {
        player.recalculatePermissions();
    }

    @Override
    public void removeAttachment(PermissionAttachment arg0) 
    {
        player.removeAttachment(arg0);
    }

    @Override
    public boolean isOp() 
    {
        return player.isOp();
    }

    @Override
    public void setOp(boolean arg0) 
    {
        player.setOp(arg0);
    }

    @Override
    public void sendMessage(String arg0) 
    {
        player.sendMessage(arg0);
    }

    @Override
    public long getFirstPlayed() 
    {
        return player.getFirstPlayed();
    }

    @Override
    public long getLastPlayed() 
    {
        return player.getLastPlayed();
    }

    @Override
    public Player getPlayer() 
    {
        return player.getPlayer();
    }

    @Override
    public boolean hasPlayedBefore() 
    {
        return player.hasPlayedBefore();
    }

    @Override
    public boolean isBanned() 
    {
        return player.isBanned();
    }

    @Override
    public boolean isOnline() 
    {
        return player.isOnline();
    }

    @Override
    public boolean isWhitelisted() 
    {
        return player.isWhitelisted();
    }

    @Override
    public void setBanned(boolean arg0) 
    {
        player.setBanned(arg0);
    }

    @Override
    public void setWhitelisted(boolean arg0) 
    {
        player.setWhitelisted(arg0);
    }

    @Override
    public Map<String, Object> serialize() 
    {
        return player.serialize();
    }

    @Override
    public Set<String> getListeningPluginChannels() 
    {
        return player.getListeningPluginChannels();
    }

    @Override
    public void sendPluginMessage(Plugin arg0, String arg1, byte[] arg2) 
    {
        player.sendPluginMessage(arg0, arg1, arg2);
    }

    @Override
    public void awardAchievement(Achievement arg0) 
    {
        player.awardAchievement(arg0);
    }

    @Override
    public void chat(String arg0) 
    {
        player.chat(arg0);
    }

    @Override
    public InetSocketAddress getAddress() 
    {
        return player.getAddress();
    }

    @Override
    public boolean getAllowFlight() 
    {
        return player.getAllowFlight();
    }

    @Override
    public Location getBedSpawnLocation() 
    {
        return player.getBedSpawnLocation();
    }

    @Override
    public Location getCompassTarget() 
    {
        return player.getCompassTarget();
    }

    @Override
    public String getDisplayName() 
    {
        return player.getDisplayName();
    }

    @Override
    public float getExhaustion() 
    {
        return player.getExhaustion();
    }

    @Override
    public float getExp() 
    {
        return player.getExp();
    }

    @Deprecated
    @Override
    public int getExperience() 
    {
        return player.getExperience();
    }

    @Override
    public int getFoodLevel() 
    {
        return player.getFoodLevel();
    }

    @Override
    public int getLevel() 
    {
        return player.getLevel();
    }

    @Override
    public String getPlayerListName() 
    {
        return player.getPlayerListName();
    }

    @Override
    public long getPlayerTime() 
    {
        return player.getPlayerTime();
    }

    @Override
    public long getPlayerTimeOffset() 
    {
        return player.getPlayerTimeOffset();
    }

    @Override
    public float getSaturation() 
    {
        return player.getSaturation();
    }

    @Override
    public int getTotalExperience() 
    {
        return player.getTotalExperience();
    }

    @Override
    public void giveExp(int arg0) 
    {
        player.giveExp(arg0);
    }

    @Override
    public void incrementStatistic(Statistic arg0) 
    {
        player.incrementStatistic(arg0);
    }

    @Override
    public void incrementStatistic(Statistic arg0, int arg1) 
    {
        player.incrementStatistic(arg0, arg1);
    }

    @Override
    public void incrementStatistic(Statistic arg0, Material arg1) 
    {
        player.incrementStatistic(arg0, arg1);
    }

    @Override
    public void incrementStatistic(Statistic arg0, Material arg1, int arg2) 
    {
        player.incrementStatistic(arg0, arg1, arg2);
    }

    @Override
    public boolean isPlayerTimeRelative() 
    {
        return player.isPlayerTimeRelative();
    }

    @Override
    public boolean isSleepingIgnored() 
    {
        return player.isSleepingIgnored();
    }

    @Override
    public boolean isSneaking() 
    {
        return player.isSneaking();
    }

    @Override
    public boolean isSprinting() 
    {
        return player.isSprinting();
    }

    @Override
    public void kickPlayer(String arg0) 
    {
        player.kickPlayer(arg0);
    }

    @Override
    public void loadData() 
    {
        player.loadData();
    }

    @Override
    public boolean performCommand(String arg0) 
    {
        return player.performCommand(arg0);
    }

    @Override
    public void playEffect(Location arg0, Effect arg1, int arg2) 
    {
        player.playEffect(arg0, arg1, arg2);
    }

    @Override
    public void playNote(Location arg0, byte arg1, byte arg2) 
    {
        player.playNote(arg0, arg1, arg2);
    }

    @Override
    public void playNote(Location arg0, Instrument arg1, Note arg2) 
    {
        player.playNote(arg0, arg1, arg2);
    }

    @Override
    public void resetPlayerTime() 
    {
        player.resetPlayerTime();
    }

    @Override
    public void saveData() 
    {
        player.saveData();
    }

    @Override
    public void sendBlockChange(Location arg0, Material arg1, byte arg2) 
    {
        player.sendBlockChange(arg0, arg1, arg2);
    }

    @Override
    public void sendBlockChange(Location arg0, int arg1, byte arg2) 
    {
        player.sendBlockChange(arg0, arg1, arg2);
    }

    @Override
    public boolean sendChunkChange(Location arg0, int arg1, int arg2, int arg3,
            byte[] arg4) 
    {
        return player.sendChunkChange(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public void sendMap(MapView arg0) 
    {
        player.sendMap(arg0);
    }

    @Override
    public void sendRawMessage(String arg0) 
    {
        player.sendRawMessage(arg0);
    }

    @Override
    public void setAllowFlight(boolean arg0) 
    {
        player.setAllowFlight(arg0);
    }

    @Override
    public void setBedSpawnLocation(Location arg0) 
    {
        player.setBedSpawnLocation(arg0);
    }

    @Override
    public void setCompassTarget(Location arg0) 
    {
        player.setCompassTarget(arg0);
    }

    @Override
    public void setDisplayName(String arg0) 
    {
        player.setDisplayName(arg0);
    }

    @Override
    public void setExhaustion(float arg0) 
    {
        player.setExhaustion(arg0);
    }

    @Override
    public void setExp(float arg0) 
    {
        player.setExp(arg0);
    }

    @Deprecated
    @Override
    public void setExperience(int arg0) 
    {
        player.setExperience(arg0);
    }

    @Override
    public void setFoodLevel(int arg0) 
    {
        player.setFoodLevel(arg0);
    }

    @Override
    public void setLevel(int arg0) 
    {
        player.setLevel(arg0);
    }

    @Override
    public void setPlayerListName(String arg0) 
    {
        player.setPlayerListName(arg0);
    }

    @Override
    public void setPlayerTime(long arg0, boolean arg1) 
    {
        player.setPlayerTime(arg0, arg1);
    }

    @Override
    public void setSaturation(float arg0) 
    {
        player.setSaturation(arg0);
    }

    @Override
    public void setSleepingIgnored(boolean arg0) 
    {
        player.setSleepingIgnored(arg0);
    }

    @Override
    public void setSneaking(boolean arg0) 
    {
        player.setSneaking(arg0);
    }

    @Override
    public void setSprinting(boolean arg0) 
    {
        player.setSprinting(arg0);
    }

    @Override
    public void setTotalExperience(int arg0) 
    {
        player.setTotalExperience(arg0);
    }

    @Deprecated
    @Override
    public void updateInventory() 
    {
        player.updateInventory();
    }

	@Override
	public boolean addPotionEffect(PotionEffect arg0) 
	{
		return player.addPotionEffect(arg0);
	}

	@Override
	public boolean addPotionEffect(PotionEffect arg0, boolean arg1) 
	{
		return player.addPotionEffect(arg0, arg1);
	}

	@Override
	public boolean addPotionEffects(Collection<PotionEffect> arg0) 
	{
		return player.addPotionEffects(arg0);
	}

	@Override
	public Collection<PotionEffect> getActivePotionEffects() 
	{
		return player.getActivePotionEffects();
	}

	@Override
	public boolean hasPotionEffect(PotionEffectType arg0) 
	{
		return player.hasPotionEffect(arg0);
	}

	@Override
	public void removePotionEffect(PotionEffectType arg0) 
	{
		player.removePotionEffect(arg0);
	}

	@Override
	public boolean canSee(Player arg0) 
	{
		return player.canSee(arg0);
	}

	@Override
	public void hidePlayer(Player arg0) 
	{
		player.hidePlayer(arg0);
	}

	@Override
	public void showPlayer(Player arg0) {
		player.showPlayer(arg0);
	}
}