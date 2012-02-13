package net.minekingdom.snaipe.Thieves;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import net.minekingdom.snaipe.Thieves.managers.*;
import net.minekingdom.snaipe.Thieves.listeners.*;

public class Thieves extends JavaPlugin {
    
	private static Thieves instance;
	
    private static Logger logger = Logger.getLogger("Minecraft");
    
    private PlayerListener playerListener;
    private InventoryListener inventoryListener;
    private ThievesListener thievesListener;
    
    private SettingManager settingManager;
    private PlayerManager playerManager;
    private CommandManager commandManager;

    public static boolean isTheftEnabled = true;
    
    public static Thieves getInstance()
    {
    	return instance;
    }
    
    public void onEnable() 
    {
    	instance = this;
    	
        settingManager = new SettingManager();
        playerManager = new PlayerManager();
        commandManager = new CommandManager();
    
        playerListener = new PlayerListener();
        inventoryListener = new InventoryListener();
        thievesListener = new ThievesListener();

        getServer().getPluginManager().registerEvents(inventoryListener, this);
        getServer().getPluginManager().registerEvents(playerListener, this);
        getServer().getPluginManager().registerEvents(thievesListener, this);

        getCommand("thieves").setExecutor(commandManager);
        
        log(getDescription().getName() + " version " + getDescription().getVersion() + " is enabled!");
    }
    
    public void onDisable() 
    { 
    	playerManager.saveAllPlayers();
    }
    
    public static void log(Level level, String msg, Object... arg)
    {
        logger.log(level, new StringBuilder().append("[Thieves] ").append(MessageFormat.format(msg, arg)).toString());
    }

    public static void log(String msg, Object... arg)
    {
        log(Level.INFO, msg, arg);
    }
    
    public SettingManager getSettingManager()
    {
    	return settingManager;
    }
    
    public PlayerManager getPlayerManager()
    {
    	return playerManager;
    }
}