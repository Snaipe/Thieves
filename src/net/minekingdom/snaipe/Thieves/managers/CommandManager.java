package net.minekingdom.snaipe.Thieves.managers;

import net.minekingdom.snaipe.Thieves.Language;
import net.minekingdom.snaipe.Thieves.Thieves;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandManager implements CommandExecutor {
    
    public final Thieves plugin;
    
    public CommandManager()
    {
        plugin = Thieves.getInstance();
    }

    @Override
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
                    if (player != null)
                    {
                        if ( player.hasPermission("thieves.toggle") ) //TODO: thieves.toggle
                        {
                            if (Thieves.isTheftEnabled)
                            {
                                Thieves.isTheftEnabled = false;
                                plugin.getPlayerManager().removeAllThieves();
                                
                                player.sendMessage(ChatColor.GREEN + "[Thieves] " + Language.disabled);
                            }
                            else
                            {
                                Thieves.isTheftEnabled = true;
                                
                                player.sendMessage(ChatColor.GREEN + "[Thieves] " + Language.enabled);
                            }
                        }
                    }
                    else
                    {
                        if (Thieves.isTheftEnabled)
                        {
                            Thieves.isTheftEnabled = false;
                            plugin.getPlayerManager().removeAllThieves();
                            
                            Thieves.log(Language.disabled);
                        }
                        else
                        {
                            Thieves.isTheftEnabled = true;
                            
                            Thieves.log(Language.enabled);
                        }
                    }
                    
                    return true;
                }
                
                if (args[0].equals("reload"))
                {
                    
                    
                    if (player != null)
                    {
                        if ( player.hasPermission("thieves.reload") ) //TODO: thieves.reload
                        {
                            plugin.getPlayerManager().removeAllThieves();
                            plugin.getSettingManager().loadConfig();
                            
                            player.sendMessage(ChatColor.GREEN + "[Thieves] Configuration reloaded.");
                        }
                    }
                    else
                    {
                        plugin.getPlayerManager().removeAllThieves();
                        plugin.getSettingManager().loadConfig();
                        
                        Thieves.log("Configuration reloaded.");
                    }
                    
                    return true;
                }
            }
        }
        return false;
    }
    
}