package net.minekingdom.snaipe.Thieves;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class ItemValues {

    private static Thieves plugin;
    
    private static HashMap<Material, Integer> itemLevels = new HashMap<Material, Integer>();
    
    public static void init()
    {
        plugin = Thieves.getInstance();
        load();
    }
    
    public static void load()
    {
        if ( !itemLevels.isEmpty())
            itemLevels.clear();
        
        File itemConfigFile = new File(plugin.getDataFolder() + File.separator + "items.yml");
        
        YamlConfiguration itemConfig = new YamlConfiguration();
        
        boolean exists = itemConfigFile.exists();
        
        try
        {
            if (!exists)
            {
                exists = Thieves.writeRessource("/items.yml", itemConfigFile.getPath());
            }
            
            if (exists)
            {
                itemConfig.load(itemConfigFile);
                
                for ( String itemName : itemConfig.getConfigurationSection("items").getKeys(false) )
                {
                    if ( Material.matchMaterial(itemName) == null )
                        continue;
                    
                    Integer level = itemConfig.getInt("items." + itemName);
                    
                    if ( level != null)
                        itemLevels.put(Material.matchMaterial(itemName), level);
                }
            }
        } 
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
        catch (InvalidConfigurationException e) 
        {
            e.printStackTrace();
        }
    }
    
    public static int valueOf(Material material)
    {
        Integer level = itemLevels.get(material);
        
        if ( level != null )
            return level;
        else
            return 1;
    }
}