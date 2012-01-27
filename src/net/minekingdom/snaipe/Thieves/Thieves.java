package net.minekingdom.snaipe.Thieves;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.server.MobEffect;
import net.minecraft.server.MobEffectList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.inventory.InventoryClickEvent;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.player.SpoutPlayer;

public class Thieves extends JavaPlugin implements Listener {
    
    private static Logger logger = Logger.getLogger("Minecraft");
    
    private File main;
    private FileConfiguration config;

    private final HashMap<Player, Player> stealingPlayers = new HashMap<Player, Player>();
    private boolean isTheftEnabled = true;
    
    private List<String> activeWorlds = new ArrayList<String>();
    private int stunTime;
    private int detectChance;
    private int theftRange;
    private int detectRadius;
    
    public static String enabled;
    public static String disabled;
    
    public static String youHaveBeenDiscovered;
    public static String victimIsTooFar;
    public static String thiefSpotted;

    public void onEnable() 
    {
        config = this.getConfig();
        main = new File(this.getDataFolder() + File.separator + "config.yml");
        loadConfig();
        
        getServer().getPluginManager().registerEvents(this, this);
        
        log(getDescription().getName() + " version " + getDescription().getVersion() + " is enabled!");
    }
    
    public void onDisable() 
    {
    	for ( Player thief : stealingPlayers.keySet() )
    	{
    		SpoutPlayer splayer = SpoutManager.getPlayer(thief);
            splayer.closeActiveWindow();
    	}
        stealingPlayers.clear();
    }
    
     public static void log(Level level, String msg, Object... arg)
    {
        logger.log(level, new StringBuilder().append("[Thieves] ").append(MessageFormat.format(msg, arg)).toString());
    }

    public static void log(String msg, Object... arg)
    {
        log(Level.INFO, msg, arg);
    }

    public void loadConfig()
    {
        boolean exists = (main).exists();

        if (exists)
        {
            try
            {
                config.options().copyDefaults(true);
                config.load(main);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            config.options().copyDefaults(true);
        }
        
        List<String> defaults = new ArrayList<String>();
        defaults.add("world");

        for ( Object o : config.getList("worlds", defaults) )
        {
            activeWorlds.add(o.toString());
        }
        
        stunTime = config.getInt("general.stun-time", 5);
        detectChance = config.getInt("general.chance-of-discovery", 30);
        theftRange = config.getInt("general.theft-range", 4);
        detectRadius = config.getInt("general.detection-radius", 4);
        
        enabled = config.getString("language.enabled", "Theft have been enabled on the server.");
        disabled = config.getString("language.disabled", "Theft have been disabled on the server.");
        
        youHaveBeenDiscovered = config.getString("language.you-have-been-discovered", "You have been discovered, and the victim stunned you with anger.");
        victimIsTooFar = config.getString("language.victim-is-too-far", "The victim is too far from you.");
        thiefSpotted = config.getString("language.thief-spotted", "You feel that something is moving inside your pocket.");

        save();
    }

    public void save()
    {
        try
        {
            config.save(main);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
    {
        if (command.getName().equals("thieves"))
        {
            Player player = null;
            
            if (sender instanceof Player)
                player = (Player) sender;
            
            if (args.length > 0)
            {
                if (args[0].equals("toggle"))
                {
                    if (isTheftEnabled)
                    {
                        isTheftEnabled = false;
                        
                        for ( Player thief : stealingPlayers.keySet() )
                    	{
                    		SpoutPlayer splayer = SpoutManager.getPlayer(thief);
                            splayer.closeActiveWindow();
                    	}
                        stealingPlayers.clear();
                        
                        if (player != null)
                            if ( player.hasPermission("thieves.toggle") ) //TODO: thieves.toggle
                            	player.sendMessage(ChatColor.GREEN + "[Thieves] " + Thieves.disabled);
                        else
                            log(Thieves.disabled);
                         
                        return true;
                    }
                    else
                    {
                        isTheftEnabled = true;
                        if (player != null)
                            if ( player.hasPermission("thieves.toggle") ) //TODO: thieves.toggle
                            	player.sendMessage(ChatColor.GREEN + "[Thieves] " + Thieves.enabled);
                        else
                            log(Thieves.enabled);
                        
                        return true;
                    }
                }
                
                if (args[0].equals("reload"))
                {
                	for ( Player thief : stealingPlayers.keySet() )
                	{
                		SpoutPlayer splayer = SpoutManager.getPlayer(thief);
                        splayer.closeActiveWindow();
                	}
                    stealingPlayers.clear();
                	loadConfig();
                	
                    if (player != null)
                        if ( player.hasPermission("thieves.toggle") ) //TODO: thieves.reload
                        	player.sendMessage(ChatColor.GREEN + "[Thieves] Configuration reloaded.");
                    else
                        log("Configuration reloaded.");
                    
                    return true;
                }
            }
        }
        return false;
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if ( event.isCancelled() )
            return;
        
        final Player player = event.getPlayer();

        if (player == null)
        {
            return;
        }

        if ( activeWorlds.contains(player.getWorld().getName()) )
        {
        	Player thief = null;
        	
            if ( stealingPlayers.containsValue(player) )
            {
                for ( Entry<Player, Player> couple : stealingPlayers.entrySet() )
                {
                    if ( !couple.getValue().equals(player) )
                        continue;
                    
                    thief = couple.getKey();
                }
                
                if ( thief == null )
                    return;
                
                if ( player.getLocation().distance(thief.getLocation()) > theftRange )
                {
                    SpoutPlayer splayer = SpoutManager.getPlayer(thief);
                    splayer.closeActiveWindow();
                }
            }
            else
            {
            	for ( Entity entity : player.getNearbyEntities(detectRadius, detectRadius, detectRadius) )
            	{
            		if ( entity instanceof Player )
                	{
            			Player nearbyPlayer = (Player) entity;
            			
            			if ( stealingPlayers.containsKey(nearbyPlayer) )
            			{
            				thief = nearbyPlayer;
            				break;
            			}
            			
                	}
            	}
            	
            	if ( thief == null )
                    return;
            }
                	
        	Vector vector = player.getEyeLocation().getDirection();
            
            Vector difference = new Vector(thief.getLocation().getX() - player.getLocation().getX(), 0, thief.getLocation().getZ() - player.getLocation().getZ());
            float angle = difference.angle(vector);
            
            if ( angle <= Math.PI / 3 )
            {
            	thief.sendMessage(ChatColor.RED + Thieves.youHaveBeenDiscovered);
                
                MobEffect slow = new MobEffect(MobEffectList.SLOWER_MOVEMENT.getId(), (int)(20 * stunTime), 10);
                
                ((CraftPlayer)thief).getHandle().addEffect(slow);
                
                SpoutPlayer splayer = SpoutManager.getPlayer(thief);
                splayer.closeActiveWindow();
            }

            	
        }
    }
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
    {
        final Player player = event.getPlayer();
        final Entity entity = event.getRightClicked();

        if (player == null || entity == null)
            return;
        
        if ( activeWorlds.contains(player.getWorld().getName()) )
        {
            if ( isTheftEnabled && player.hasPermission("thieves.steal") ) //TODO: thieves.steal
            {
                if ( !player.isSneaking() )
                    return;
                
                if ( entity instanceof Player )
                {
                    SpoutPlayer splayer = SpoutManager.getPlayer(player);
                    Player target = (Player) entity;
                    
                    if ( target.hasPermission("thieves.protected") ) //TODO: thieves.protected
                        return;
                    
                    Vector vector = target.getEyeLocation().getDirection();
                    
                    Vector difference = new Vector(player.getLocation().getX() - target.getLocation().getX(), 0, player.getLocation().getZ() - target.getLocation().getZ());
                    float angle = difference.angle(vector);
                    
                    if ( angle > Math.PI / 3 )
                    {
                        Inventory inv = (Inventory) target.getInventory();
                        splayer.openInventoryWindow(inv);
                        stealingPlayers.put(player, target);
                    }    
                }
            }
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        final Player player = event.getPlayer();
        final Player target = stealingPlayers.get(player);
        
        if ( target != null )
        {
            int rand = (int)(Math.random()*100) + 1;

            if ( rand < Math.abs(detectChance) % 100 )
            {
                target.sendMessage(ChatColor.RED + Thieves.thiefSpotted);
            }
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event)
    {
        if (event.isCancelled())
            return;
        
        final Player player = event.getPlayer();
        if ( player != null )
            if ( stealingPlayers.containsKey(player) )
                stealingPlayers.remove(player);
    }
}