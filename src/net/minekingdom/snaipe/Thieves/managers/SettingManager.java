package net.minekingdom.snaipe.Thieves.managers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.minekingdom.snaipe.Thieves.ItemValues;
import net.minekingdom.snaipe.Thieves.Language;
import net.minekingdom.snaipe.Thieves.Thieves;
import net.minekingdom.snaipe.Thieves.ThievesPlayer;

public class SettingManager {
    
    private final Thieves plugin;
    
    private FileConfiguration config;
    private File main;
    
    private List<String> activeWorlds = new ArrayList<String>();
    
    private boolean canStealHotBar;
    private boolean canStealInNonPvPArea;
    
    private int detectChance;
    private int detectRadius;
    private int stunTime;
    private int theftRange;

    

    public SettingManager()
    {
        plugin = Thieves.getInstance();
        
        config = plugin.getConfig();
        main = new File(plugin.getDataFolder() + File.separator + "config.yml");
        loadConfig();
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
        
        File dataDir = new File(plugin.getDataFolder() + File.separator + "Data");
        
        if (!dataDir.exists())
            dataDir.mkdir();
        
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
        canStealHotBar = config.getBoolean("general.can-steal-in-hotbar", true);
        canStealInNonPvPArea = config.getBoolean("general.can-steal-in-non-pvp-area", false);
        
        Language.init(config);
        ItemValues.init();
    
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
    
    public ThievesPlayer loadPlayer(Player player)
    {
        File playerDataFile = new File(plugin.getDataFolder() + File.separator + "Data" + File.separator + player.getName() + ".yml");
        
        YamlConfiguration playerData = new YamlConfiguration();
        
        try 
        {
            playerData.load(playerDataFile);
        } 
        catch (FileNotFoundException e) 
        {
            HashMap<World, Integer> map = new HashMap<World, Integer>();
            for ( String world : activeWorlds )
            {
                map.put(plugin.getServer().getWorld(world), 1);
            }
            return new ThievesPlayer(player, map);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            HashMap<World, Integer> map = new HashMap<World, Integer>();
            for ( String world : activeWorlds )
            {
                map.put(plugin.getServer().getWorld(world), 1);
            }
            return new ThievesPlayer(player, map);
        } 
        catch (InvalidConfigurationException e) 
        {
            e.printStackTrace();
            HashMap<World, Integer> map = new HashMap<World, Integer>();
            for ( String world : activeWorlds )
            {
                map.put(plugin.getServer().getWorld(world), 1);
            }
            return new ThievesPlayer(player, map);
        }
        
        HashMap<World, Integer> levels = new HashMap<World, Integer>();
        for ( String world : activeWorlds )
        {
            int level = playerData.getInt("levels." + world, 1);
            levels.put(plugin.getServer().getWorld(world), level);
        }
        
        return new ThievesPlayer(player, levels);
    }
    
    public void savePlayer(ThievesPlayer player)
    {
        File playerDataFile = new File(plugin.getDataFolder() + File.separator + "Data" + File.separator + player.getName() + ".yml");
        
        YamlConfiguration playerData = new YamlConfiguration();
        
        try 
        {
            if (!playerDataFile.exists())
            {
                playerDataFile.createNewFile();
            }
            
            playerData.load(playerDataFile);
            
            for ( String world : activeWorlds )
            {
                if ( plugin.getServer().getWorld(world) == null )
                    continue;
                
                playerData.set("level." + world, player.getThiefLevel(plugin.getServer().getWorld(world)));
            }
            
            playerData.save(playerDataFile);
            
        } 
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
            return;
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            return;
        } 
        catch (InvalidConfigurationException e) 
        {
            e.printStackTrace();
            return;
        }
    }

    public boolean canStealHotBar() 
    {
        return canStealHotBar;
    }

    public int getDetectChance() 
    {
        return detectChance;
    }
    
    public int getDetectRadius()
    {
        return detectRadius;
    }
    
    public int getStunTime()
    {
        return stunTime;
    }
    
    public int getTheftRange()
    {
        return theftRange;
    }
    
    public List<String> getActiveWorlds()
    {
        return activeWorlds;
    }
    
    public boolean isActiveWorld(World world)
    {
        return activeWorlds.contains(world.getName());
    }

    public boolean canStealInNonPvPArea() 
    {
        return canStealInNonPvPArea;
    }
}